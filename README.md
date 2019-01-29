# Stryke Force Third Coast Java Libraries

[![Build Status](https://travis-ci.org/strykeforce/thirdcoast.svg?branch=master)](https://travis-ci.org/strykeforce/thirdcoast)

This project consists of three separate libraries that are reused by Team 2767 Stryke Force.

-   **Swerve Drive** - software control of [Third Coast swerve drive modules](https://strykeforce.org/resources/#mecanical).
-   **Telemetry** -  provide real-time streaming telemetry information from a robot. Used with our [Grapher](https://github.com/strykeforce/grapher) LabView strip-chart recorder.
-   **Deadeye** - roboRIO-side interface to our [Deadeye](https://github.com/strykeforce/deadeye) vision application.

The Talon SRX provision library was removed after the 2018 season. With the additions made to the CTRE Phoenix libraries during the summer of 2018, especially `TalonSRXConfiguration` and friends, we no longer need to maintain our separate library for this.

You can create pre-configured projects that use Third Coast with [cookiecutter-robot](https://github.com/strykeforce/cookiecutter-robot).

See examples of usage in [thirdcoast-examples](https://github.com/strykeforce/thirdcoast-examples) repo and other engineering resources at [strykeforce.org](https://strykeforce.org/resources/).

