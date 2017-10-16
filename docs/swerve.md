# Swerve Drive

It is designed to run on swerve drive hardware described in this [paper](https://www.chiefdelphi.com/media/papers/3375). The swerve drive is controlled using the [`SwerveDrive`][swerve] class. Derivation of inverse kinematic equations are from Ether's [Swerve Kinematics and Programming][ether].

The swerve-drive inverse kinematics algorithm will always calculate individual wheel angles as -0.5 to 0.5 rotations, measured clockwise with zero being the straight-ahead position. Wheel speed is calculated as 0 to 1 in the direction of the wheel angle. The [`Wheel`][swerve] class will calculate how to implement this angle and drive direction optimally for the azimuth and drive motors. In some cases it makes sense to reverse wheel direction to avoid rotating the wheel azimuth 180 degrees.

Hardware assumed by the [`Wheel`][swerve] class includes a CTRE magnetic encoder on the azimuth motor and no limits on wheel azimuth rotation. Azimuth Talons have an ID in the range 0-3 with corresponding drive Talon IDs in the range 10-13.

# Demonstration Robot

A demonstration of Third Coast API usage is in [`org.strykeforce.thirdcoast.robot`][robot]. This uses [GradleRIO] to build.

**Note:** you must edit the [`thirdcoast.toml`][toml] configuration file and provide Talon position-mode PID parameters for your hardware. This file will be copied from the deployed JAR file to `/home/lvuser/thirdcoast.toml` the first time you deploy and run the robot code. See the configuration file [documentation](talon) for more information.

Return to [main page](index).

[ether]: https://www.chiefdelphi.com/media/papers/2426
[gradlerio]: https://github.com/Open-RIO/GradleRIO
[robot]: https://github.com/strykeforce/thirdcoast/tree/master/robot
[swerve]: https://strykeforce.github.io/thirdcoast/javadoc/org/strykeforce/thirdcoast/swerve/package-summary.html
[toml]: https://github.com/strykeforce/thirdcoast/blob/master/core/src/main/resources/org/strykeforce/thirdcoast.toml
