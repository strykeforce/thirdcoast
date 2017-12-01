# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Calendar Versioning](http://calver.org/).

## [Unreleased]

### Added

- Added this changelog.

## [17.1.2] - 2017-11-27

### Fixed

- `tct` utility was not saving configuration for settings that had forward and reverse double values, for example **peak voltage**. #22

## [17.1.1] - 2017-11-27

### Added

- `tct` utility added **brake in neutral** configuration. #17
- Added `MotionMagicTalonConfiguration` to Talon provisioning and ability to control its **acceleration** and **cruise velocity** in `tct` utility. #20

### Changed

- `tct` utility allows digital inputs to be reconfigured as outputs. #13
- `tct` utility logging level increased to warn. #18

### Fixed

- **voltage ramp rate** was not configuring a new `TalonConfigurationBuilder` from an existing `TalonConfiguration`. #19

## [17.1.0] - 2017-11-16

### Added

- Released `tct` utility for Talons, digital outputs and servos.

[17.1.0]: https://github.com/strykeforce/thirdcoast/compare/v17.0.23...v17.1.0
[17.1.1]: https://github.com/strykeforce/thirdcoast/compare/v17.1.0...v17.1.1
[17.1.2]: https://github.com/strykeforce/thirdcoast/compare/v17.1.1...v17.1.2
[unreleased]: https://github.com/strykeforce/thirdcoast/compare/v17.1.2...develop
