# Stryke Force Third Coast Java Libraries

This project consists of three libraries that are used by Team 2767 Stryke Force.

- **Swerve Drive** - software control of [Third Coast swerve drive modules](https://www.strykeforce.org/resources/).
  This code should generally work with swerve drives that use CTRE motor controllers and a CTRE magnetic encoder for
  azimuth position.
- **Telemetry** - provide real-time streaming telemetry information from a robot. Used with
  our [Grapher](https://github.com/strykeforce/grapher) LabView strip-chart recorder.
- **Health Check** - configure automated motor health checks for use by pit crew during competitions.

Other Stryke Force engineering resources are at [strykeforce.org](https://www.strykeforce.org/resources/).

## Installation

The Third Coast `vendordeps` file is at: http://packages.strykeforce.org/thirdcoast.json

To install, use **Install new libraries (online)** in VS Code or download manually to your project `vendordeps`
directory.

```
$ ./gradlew vendordep --url=http://packages.strykeforce.org/thirdcoast.json
```

## Swerve Drive

We have wrapped the FRC WPILib Swerve Drive kinematics and
odometry [classes](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/package-summary.html)
to work easily with our swerve module design and to facilitate use of the rest of the Third Coast libraries.

For example, here is how you can configure our swerve drive.

```java
public class SwerveDriveSubsystem extends SubsystemBase {

    private final SwerveDrive swerveDrive;

    public SwerveDriveSubsystem() { // Pretend to set up a swerve drive

        var moduleBuilder =
                new TalonSwerveModule.Builder()
                        .driveGearRatio(DriveConstants.kDriveGearRatio)
                        .wheelDiameterInches(DriveConstants.kWheelDiameterInches)
                        .driveMaximumMetersPerSecond(DriveConstants.kMaxSpeedMetersPerSecond);

        TalonSwerveModule[] swerveModules = new TalonSwerveModule[4];
        Translation2d[] wheelLocations = DriveConstants.getWheelLocationMeters();

        // initialize the swerve modules
        for (int i = 0; i < 4; i++) {
            var azimuthTalon = new TalonSRX(i);
            // configure azimuth Phoenix API settings...
            var driveTalon = new TalonFX(i + 10);
            // configure drive Phoenix API settings...

            swerveModules[i] =
                    moduleBuilder
                            .azimuthTalon(azimuthTalon)
                            .driveTalon(driveTalon)
                            .wheelLocationMeters(wheelLocations[i])
                            .build();

            swerveModules[i].loadAndSetAzimuthZeroReference();
        }

        // initialize the swerve drive with configured swerve modules
        swerveDrive = new SwerveDrive(swerveModules);
    }
}

```

In the simplest case, you can control this configured `SwerveDrive` in open-loop, for example during tele-operation.

```java
public class SwerveDriveSubsystem extends SubsystemBase {
    private final SwerveDrive swerveDrive;

    // ...

    public void drive(double vxMetersPerSecond, double vyMetersPerSecond, double omegaRadiansPerSecond, boolean isFieldOriented) {
        swerveDrive.drive(vxMetersPerSecond, vyMetersPerSecond, omegaRadiansPerSecond, isFieldOriented);
    }
}
```

## Telemetry

Our Telemetry library is used to instrument subsystems and stream measurements to
our [Grapher](https://github.com/strykeforce/grapher) LabView strip-chart recorder. We find it invaluable during the
season for many tasks, not the least of which is motor controller closed-loop tuning. During motor controller
closed-loop
tuning we often will stream telemetry while manually controlling the motors using
our `tct` [application](https://github.com/strykeforce/thirdcoast-tct).

Continuing on with our example from above, to instrument the `SwerveDriveSubsystem` class we subclass our
abstract `MeasureableSubsystem` instead of `SubsystemBase` and then implement the `getMeasures()` method.

```java
public class SwerveDriveSubsystem extends MeasurableSubsystem {
    private final SwerveDrive swerveDrive;
    // ...

    @Override
    public Set<Measure> getMeasures() {
        return Set.of(
                new Measure("Gyro Rotation2D(deg)", () -> swerveDrive.getHeading().getDegrees()),
                new Measure("Odometry X", () -> swerveDrive.getPoseMeters().getX()),
                new Measure("Odometry Y", () -> swerveDrive.getPoseMeters().getY()),
                // other measurements...
        );
    }
}
```

Each of the `Measure` objects supplied from `getMeasures()` are created with a name, optional description, and
a `DoubleSupplier` (typically a lambda expression) to provide the measured data.

During robot start-up, we register the instrumented subsystems with the Telemetry library. When you connect to the robot
using the Grapher application, you are presented with these subsystems and their measurements as options to view in the
strip-chart.

```java
public class RobotContainer {
    private final SwerveDriveSubsystem swerveDriveSubsystem;
    private final TelemetryService telemetryService = new TelemetryService(TelemetryController::new);

    public RobotContainer() {
        swerveDriveSubsystem.registerWith(telemetryService);
        // ...
    }
}
```

## Health Check

This system provides our pit team with the ability to define a set of pre-defined motor health checks that can be run
with the press of a button. It is intended for use with subsystems that contain Talon motor controllers.

Subsystems that contain motors to be health checked are annotated with `@HealthCheck` and optionally, annotations that
define the health check in more detail. There are three possible ways to configure a talon for testing:

- `@Timed` - runs the motor at the specified output for the specified amount of time.
- `@Position` - runs the motor at the specified output until the specified amount of encoder ticks have occurred.
- `@Follower` - the motor is configured to follow another Talon and therefor is not commanded, only measured.

This subsystem has some examples of Talons being tested in various ways.

```java
public class ExampleSubsystem extends SubsystemBase {

    private final TalonSRXConfiguration talonSRXConfiguration = new TalonSRXConfiguration();

    // default duration healthcheck at default output percentages in forward and reverse direction
    @HealthCheck
    private final TalonSRX talonOne = new TalonFX(1);

    // run each of 3 output percentages for 4 seconds each
    @HealthCheck
    @Timed(percentOutput = {0.5, 0.75, -0.25}, duration = 4.0)
    private final TalonFX talonTwo = new TalonFX(2);

    // run each of 4 output percentages until 20,000 encoder ticks have occurred
    @HealthCheck
    @Position(percentOutput = {0.25, -0.25, 0.5, -0.5}, encoderChange = 20_000)
    private final TalonSRX talonThree = new TalonFX(3);

    // follows talonTwo (device id = 2) so just take measurements
    @HealthCheck
    @Follow(leader = 2)
    private final TalonFX talonFour = new Talon(4);

    public ExampleSubsystem() {
        talonFour.follow(talonTwo);
    }

    // These Health Check lifecycle methods are all optional.

    @BeforeHealthCheck
    private boolean beforeHealthCheck() {
        // perform any set-up such as positioning subsystems on the robot to
        // allow clear running, or override Talon configurations temporarily
        // while testing. Called periodically by the robot loop while running,
        // return true when finished.
        return true;
    }

    @AfterHealthCheck
    private boolean afterHealthCheck() {
        // perform any tear-down such as position subsystems, or resetting
        // Talon configurations. Called periodically by the robot loop while
        // running, return true when finished.
        return true;
    }

}
```

The `@BeforeHealthCheck` and `@AfterHealthCheck` methods are run before and after the health checks respectively and are
called periodically by the robot loop while they are running. They should return `true` when finished. If there is more
than one of either, you can specify the order in a similar fashion to `@HealthCheck`.

If you annotate an instance of `SwerveDrive`, it will run a series of standard health checks on the azimuth and drive
Talons.

```java
public class SwerveDriveSubsystem extends SubsystemBase {
    @HealthCheck
    private final SwerveDrive swerveDrive;
    // ...
}
```

To run the health checks, pass the subsystems to test to the `HealthCheckCommand` and connect to a suitable button.

```java
public class RobotContainer {

    private final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
    private final SwerveDriveSubsystem swerveDriveSubsystem = new SwerveDriveSubsystem();

    public RobotContainer() {
        new Trigger(RobotController::getUserButton).onTrue(new HealthCheckCommand(exampleSubsystem, swerveDriveSubsystem));
        // ...
    }
}
```

After running the healthcheck, the JSON-formatted raw data is available at `http://10.27.67.2:2767/data` and can be
saved and analyzed with a tool of choice. For example, the data can be loaded into
a [Pandas](https://pandas.pydata.org/pandas-docs/stable/) dataframe for analysis and visualization using the following.

```python
import pandas as pd
import requests

r = requests.get("http://10.27.67.2:2767/data")
j = r.json()

meta = pd.DataFrame(j["meta"])
data = pd.DataFrame(j["data"])
df = pd.merge(meta, data, on="case", suffixes=('_set', '_measured'))
df. head(3)
```

<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>case</th>
      <th>case_uuid</th>
      <th>name</th>
      <th>talon_set</th>
      <th>type</th>
      <th>output</th>
      <th>duration</th>
      <th>datetime</th>
      <th>msec_elapsed</th>
      <th>talon_measured</th>
      <th>voltage</th>
      <th>position</th>
      <th>speed</th>
      <th>supply_current</th>
      <th>stator_current</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>0</td>
      <td>8efc0583-8f90-4ab8-9930-5403b1f51fb2</td>
      <td>DriveSubsystem</td>
      <td>0</td>
      <td>time</td>
      <td>0.25</td>
      <td>5000000</td>
      <td>2023-02-18 14:14:23.819275</td>
      <td>0</td>
      <td>0</td>
      <td>0.0</td>
      <td>0.0</td>
      <td>0.0</td>
      <td>0.125</td>
      <td>0.0</td>
    </tr>
    <tr>
      <th>1</th>
      <td>0</td>
      <td>8efc0583-8f90-4ab8-9930-5403b1f51fb2</td>
      <td>DriveSubsystem</td>
      <td>0</td>
      <td>time</td>
      <td>0.25</td>
      <td>5000000</td>
      <td>2023-02-18 14:14:23.819275</td>
      <td>7144</td>
      <td>0</td>
      <td>0.0</td>
      <td>0.0</td>
      <td>0.0</td>
      <td>0.125</td>
      <td>0.0</td>
    </tr>
    <tr>
      <th>2</th>
      <td>0</td>
      <td>8efc0583-8f90-4ab8-9930-5403b1f51fb2</td>
      <td>DriveSubsystem</td>
      <td>0</td>
      <td>time</td>
      <td>0.25</td>
      <td>5000000</td>
      <td>2023-02-18 14:14:23.819275</td>
      <td>13316</td>
      <td>0</td>
      <td>0.0</td>
      <td>0.0</td>
      <td>0.0</td>
      <td>0.125</td>
      <td>0.0</td>
    </tr>
  </tbody>
</table>
</div>
