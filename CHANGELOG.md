# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Calendar Versioning](http://calver.org/).

## [18.3.1] - 2018-02-23

### Added

- Implement drive closed-loop gain scaling by subclassing Wheel with a custom implementation.
- Grapher support for PathFinder controller.
- Added methods to `SwerveDrive` to support azimuth belt slip correction.

### Changed

- Moved the `tct` command-line utility to its own repo and removed `sim` development tool.

## [18.3.0] - 2018-02-13

### Added

- Swerve drive `Wheel` class now has basic support for a pluggable implementation of drive wheel "driver" to allow different control strategies, for example, open-loop vs gain-scheduled closed-loop.
- Ultrasonic Rangefinder grapher telemetry `Item`.

### Changed

- Talons are now configured with Quad encoder by default if no other sensor selected.
- Gyro logging can be turned off in config file.
- Config file location is now passed in as a URL to allow embedding in JAR.

### Fixed

- Telemetry now reports lower 12 bits of absolute encoder position (pulse width position).

## [18.2.0] - 2018-02-03

### Changed

- Complete re-write of TalonSRX handling code to incorporate rest of Phoenix update. We now utilize profile slots and selecting the control mode through the Talon's `set()` method. Additionally, all Talon configuration is done at startup as Talon IDs are identified in the config file. Backwards incompatible changes to the config file format and API.

## [18.0.5] - 2018-01-24

### Changed

- Updated TalonSRX status frame rates for 2018.

## [18.0.4] - 2018-01-20

This version upgrades the library to be compatible with the 2018 Phoenix TalonSRX and WPI control system releases.

## [17.2.0] - 2018-01-05

### Added

- New "Default Status Frame Rates" and "Grapher (high speed) Status Frame Rates" commands for `tct` to set common configurations for Talons.
- Launch script `tct.sh` is in `tct/script`, copy to the same directory on the roboRIO you installed `tct.jar` to.
- Third Coast Java core library now published to [Bintray jcenter](https://bintray.com/strykeforce/maven/thirdcoast).
- Made swerve drive implementation support graphing and added gyro example measurements.

### Changed

- Talons are now set to the default set of frame rate when initialized by the Talon provisioner. See `StatusFrameRate` [javadoc](https://strykeforce.github.io/thirdcoast/javadoc/org/strykeforce/thirdcoast/talon/StatusFrameRate.html) for defaults.
- For better visibility and to better manage CAN bus utilization, we not longer adjust the Talon frame rates automatically when registering for Telemetry or when using `tct`.

## [17.1.4] - 2017-12-04

### Added

- Added Motion Magic data to grapher:

  - Motion Magic Acceleration
  - Motion Magic Trajectory Point Target Position
  - Motion Magic Trajectory Point Target Velocity
  - Motion Magic Cruise Velocity

### Changed

- Moved example robot into `org.team2767.thirdcoast` since it isn't part of the library.

## [17.1.3] - 2017-12-02

### Added

- Added this changelog.
- You can now use the `B` key in the `tct` utility to go back while running Talons, Servos or Digital Outputs.

### Changed

- Linefeeds added to `tct` utility menus to improve readability.

## [17.1.2] - 2017-11-27

### Fixed

- `tct` utility was not saving configuration for settings that had forwardLimits and reverseLimits double values, for example **peak voltage**.

## [17.1.1] - 2017-11-27

### Added

- `tct` utility added **brake in neutral** configuration.
- Added `MotionMagicTalonConfiguration` to Talon provisioning and ability to control its **acceleration** and **cruise velocity** in `tct` utility.

### Changed

- `tct` utility allows digital inputs to be reconfigured as outputs.
- `tct` utility logging level increased to warn.

### Fixed

- **voltage ramp rate** was not configuring a new `TalonConfigurationBuilder` from an existing `TalonConfiguration`.

## [17.1.0] - 2017-11-16

### Added

- Released `tct` utility for Talons, digital outputs and servos.

[17.1.0]: https://github.com/strykeforce/thirdcoast/compare/v17.0.23...v17.1.0
[17.1.1]: https://github.com/strykeforce/thirdcoast/compare/v17.1.0...v17.1.1
[17.1.2]: https://github.com/strykeforce/thirdcoast/compare/v17.1.1...v17.1.2
[17.1.3]: https://github.com/strykeforce/thirdcoast/compare/v17.1.2...v17.1.3
[17.1.4]: https://github.com/strykeforce/thirdcoast/compare/v17.1.3...v17.1.4
[17.2.0]: https://github.com/strykeforce/thirdcoast/compare/v17.1.4...v17.2.0
[18.0.4]: https://github.com/strykeforce/thirdcoast/compare/v17.2.0...v18.0.4
[18.0.5]: https://github.com/strykeforce/thirdcoast/compare/v18.0.4...v18.0.5
[18.2.0]: https://github.com/strykeforce/thirdcoast/compare/v18.0.5...v18.2.0
[18.3.0]: https://github.com/strykeforce/thirdcoast/compare/v18.2.0...v18.3.0
[18.3.1]: https://github.com/strykeforce/thirdcoast/compare/v18.3.0...v18.3.1
[unreleased]: https://github.com/strykeforce/thirdcoast/compare/v18.3.0...develop
