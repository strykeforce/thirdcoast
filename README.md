# Stryke Force Third Coast Java Libraries

This project consists of three libraries that are used by Team 2767 Stryke Force.

- **Swerve Drive** - software control of [Third Coast swerve drive modules](https://www.strykeforce.org/resources/). This code should generally work with swerve drives that use CTRE motor controllers and a CTRE magnetic encoder for azimuth position.
- **Telemetry** - provide real-time streaming telemetry information from a robot. Used with our [Grapher](https://github.com/strykeforce/grapher) LabView strip-chart recorder.
- **Health Check** - configure automated motor health checks for use by pit crew during competitions.

## Installation

The Third Coast `vendordeps` file is at: http://packages.strykeforce.org/thirdcoast.json

To install, use **Install new libraries (online)** in VS Code or download manually to your project `vendordeps` directory.

```
$ ./gradlew vendordep --url=http://packages.strykeforce.org/thirdcoast.json
```

See examples of usage in [thirdcoast-examples](https://github.com/strykeforce/thirdcoast-examples) repo and other engineering resources at [strykeforce.org](https://www.strykeforce.org/resources/).
