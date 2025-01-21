package com.samyak2403.emf_magneticfielddetector.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samyak2403.emf_magneticfielddetector.components.EMFGraph
import com.samyak2403.emf_magneticfielddetector.components.EMFSpeedometer
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private const val TAG = "HomeScreen"
private const val HISTORY_SIZE = 50
private const val CALIBRATION_SAMPLES = 10
private const val NOISE_THRESHOLD = 0.5f
private const val MAX_EMF_VALUE = 200f

data class MagneticValues(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val total: Float = 0f,
    val isCalibrated: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var magneticValues by remember { mutableStateOf(MagneticValues()) }
    var isDemoMode by remember { mutableStateOf(false) }
    var isEnabled by remember { mutableStateOf(true) }
    var sensorAvailable by remember { mutableStateOf(false) }
    var peakValue by remember { mutableStateOf(0f) }
    var historyValues by remember { mutableStateOf(listOf<Float>()) }
    var calibrationValues by remember { mutableStateOf(listOf<MagneticValues>()) }
    var baselineValues by remember { mutableStateOf(MagneticValues()) }

    val currentStrength by animateFloatAsState(
        targetValue = if (isEnabled) magneticValues.total else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    // Calibration effect
    LaunchedEffect(isEnabled) {
        if (isEnabled && !isDemoMode) {
            calibrationValues = emptyList()
        }
    }

    // Update peak value and history
    LaunchedEffect(magneticValues.total, isEnabled) {
        if (!isEnabled) return@LaunchedEffect
        
        if (magneticValues.total > peakValue) {
            peakValue = magneticValues.total
        }
        historyValues = (historyValues + magneticValues.total).takeLast(HISTORY_SIZE)
    }

    // Demo mode animation
    LaunchedEffect(isDemoMode, isEnabled) {
        if (isDemoMode && isEnabled) {
            var time = 0f
            while (true) {
                val x = (sin(time) + 1) * 25 + Random.nextFloat() * 10 - 5
                val y = (sin(time + 2) + 1) * 25 + Random.nextFloat() * 10 - 5
                val z = (sin(time + 4) + 1) * 25 + Random.nextFloat() * 10 - 5
                val total = sqrt(x * x + y * y + z * z)
                magneticValues = MagneticValues(x, y, z, total, true)
                time += 0.1f
                delay(100)
            }
        }
    }

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val magnetometer = remember {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorAvailable = sensor != null
        sensor
    }

    DisposableEffect(isDemoMode, isEnabled) {
        if (!isDemoMode && isEnabled && magnetometer != null) {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    
                    if (calibrationValues.size < CALIBRATION_SAMPLES) {
                        calibrationValues = calibrationValues + MagneticValues(x, y, z, 0f)
                        if (calibrationValues.size == CALIBRATION_SAMPLES) {
                            baselineValues = MagneticValues(
                                x = calibrationValues.map { it.x }.average().toFloat(),
                                y = calibrationValues.map { it.y }.average().toFloat(),
                                z = calibrationValues.map { it.z }.average().toFloat()
                            )
                        }
                        return
                    }

                    // Apply calibration and noise reduction
                    val calibratedX = abs(x - baselineValues.x)
                    val calibratedY = abs(y - baselineValues.y)
                    val calibratedZ = abs(z - baselineValues.z)
                    
                    val total = if (calibratedX > NOISE_THRESHOLD || 
                                  calibratedY > NOISE_THRESHOLD || 
                                  calibratedZ > NOISE_THRESHOLD) {
                        sqrt(calibratedX * calibratedX + 
                             calibratedY * calibratedY + 
                             calibratedZ * calibratedZ)
                    } else {
                        0f
                    }

                    magneticValues = MagneticValues(
                        calibratedX,
                        calibratedY,
                        calibratedZ,
                        total,
                        true
                    )
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    Log.d(TAG, "Sensor accuracy changed: $accuracy")
                }
            }

            sensorManager.registerListener(
                listener,
                magnetometer,
                SensorManager.SENSOR_DELAY_UI
            )

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        } else {
            onDispose { }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with Power, Mode Switch and Reset
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Power Button
            IconButton(
                onClick = { isEnabled = !isEnabled },
                modifier = Modifier
                    .background(
                        if (isEnabled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = if (isEnabled) "Turn Off" else "Turn On",
                    tint = Color.White
                )
            }

            Text(
                text = if (isDemoMode) "Demo Mode" else "Sensor Mode",
                style = MaterialTheme.typography.titleMedium,
                color = if (isDemoMode) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.secondary
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Reset Peak Button
                IconButton(
                    onClick = { peakValue = 0f },
                    enabled = isEnabled
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Peak",
                        tint = if (isEnabled) MaterialTheme.colorScheme.primary 
                              else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Mode Switch
                Switch(
                    checked = isDemoMode,
                    onCheckedChange = { isDemoMode = it },
                    enabled = isEnabled,
                    thumbContent = if (isDemoMode) {
                        {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize)
                            )
                        }
                    } else null
                )
            }
        }

        if (!sensorAvailable && !isDemoMode) {
            Text(
                text = "Magnetic sensor not available on this device",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        // Status Text
        if (!magneticValues.isCalibrated && !isDemoMode && isEnabled) {
            Text(
                text = "Calibrating sensor...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // EMF Speedometer
        EMFSpeedometer(
            value = currentStrength,
            maxValue = MAX_EMF_VALUE,
            modifier = Modifier
                .size(280.dp)
                .padding(16.dp),
            lowValueColor = Color(0xFF00C853),    // Material Green
            mediumValueColor = Color(0xFFFFD600), // Material Yellow
            highValueColor = Color(0xFFD50000),   // Material Red
            backgroundColor = MaterialTheme.colorScheme.surface,
            textColor = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // History Graph
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                EMFGraph(
                    values = historyValues,
                    modifier = Modifier.fillMaxSize(),
                    maxValue = MAX_EMF_VALUE
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // XYZ Values Display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Magnetic Field Components",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Peak: %.1f µT".format(peakValue),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AxisValue("X", magneticValues.x, MaterialTheme.colorScheme.primary)
                AxisValue("Y", magneticValues.y, MaterialTheme.colorScheme.secondary)
                AxisValue("Z", magneticValues.z, MaterialTheme.colorScheme.tertiary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total Strength Display
        Text(
            text = "Total Magnetic Field Strength",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "%.2f µT".format(magneticValues.total),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        if (isDemoMode && isEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Using simulated data",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun AxisValue(axis: String, value: Float, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(8.dp)
    ) {
        Text(
            text = axis,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
        Text(
            text = "%.1f".format(value),
            style = MaterialTheme.typography.bodyLarge,
            color = color
        )
    }
}
