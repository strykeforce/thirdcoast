# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Calendar Versioning](http://calver.org/).

## [Unreleased]

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

- `tct` utility was not saving configuration for settings that had forward and reverse double values, for example **peak voltage**.

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
[unreleased]: https://github.com/strykeforce/thirdcoast/compare/v17.1.3...develop
