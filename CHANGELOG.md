# Changelog

All notable changes to EMF Magnetic Field Detector will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-01-21

### Added
- Initial release
- Real-time EMF detection using device's magnetic sensor
- Visual speedometer display showing EMF readings in µT
- Graph visualization for EMF history
- Magnetic field components (X, Y, Z) display
- Demo mode for testing without sensor
- Material Design 3 UI implementation
- About screen with app information
- Background scanning capability
- Peak value detection and display
- Sensor calibration support
- Toggle between Sensor and Demo modes

### Features
- Clean and minimal user interface
- Real-time updates of EMF readings
- Visual representation of field strength
- Component-wise magnetic field breakdown
- Historical data visualization
- No advertisements or tracking
- Works completely offline
- Minimal permissions required

### Technical Details
- Minimum SDK: Android 7.0 (API 24)
- Target SDK: Android 14 (API 34)
- Built with Jetpack Compose
- ProGuard optimization enabled
- F-Droid compliant build

### Known Issues
- Some devices may not have magnetic sensors
- Calibration may be required on first use
- Readings may vary between different device models

## Future Plans
- Add unit conversion (µT to mG)
- Export measurement data
- Custom themes
- Widget support
- Improved graph visualization
- Multiple language support
