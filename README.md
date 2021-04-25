# Stryke Force Third Coast Java Libraries

This project consists of three libraries that are used by Team 2767 Stryke Force.

-   **Swerve Drive** - software control of [Third Coast swerve drive modules](https://strykeforce.org/resources/#mecanical). This code should generally work with swerve drives that use CTRE motor controllers and a CTRE magnetic encoder for azimuth position.
-   **Telemetry** -  provide real-time streaming telemetry information from a robot. Used with our [Grapher](https://github.com/strykeforce/grapher) LabView strip-chart recorder.
-   **Health Check** - configure automated motor health checks for use by pit crew during competitions.


## Installation

The Third Coast `vendordeps` file is at: http://maven.strykeforce.org/thirdcoast.json

To install, use **Install new libraries (online)** in VS Code or download manually to your project `vendordeps` directory.

```
$ ./gradlew vendordep --url=http://maven.strykeforce.org/thirdcoast.json
```

See examples of usage in [thirdcoast-examples](https://github.com/strykeforce/thirdcoast-examples) repo and other engineering resources at [strykeforce.org](https://strykeforce.org/resources/).

# Hackbots 2021X Swerve Drive
Strykeforce have created some really good code and test-protected it with automated unit tests the run on every build.  They use TalonSRX for their azimuth (steering) motor controllers.  We are using CANSparkMAX.  Our exercise will be to refactor the Strykeforce API to accept a Java interface exposing the key TalonSRX behaviors, and then create both TalonSRX and CANSparkMax implementations.  The unit tests must be updated to prove we didn't break anything.  Then we will create a simple example similar to ThriftySwerve demonstrating the use of the CANSparkMax implementation.

Hackbots do not (yet) have a maven repo.  That part of the build had to be disabled.

Many thanks to 2767 Strykeforce.

## To Work With This
1. Clone this repository.
2. From VS Code, perform a WPILib: Build Robot Code
3. Open GitBash to this project's root directory
4. Run `gradlew publishToMavenLocal`
5. Run `ls -l ~/.m2/repository/org/strykeforce/2021X_thirdcoast/21.0.0/` to confirm all of the .jar, .module and .pom files published with the correct timestamp.  You will need to do this any time you need to fix a bug in this library for the 2021X_Thrifty_Server_Java project to "see" the fix.



