# Module healthcheck


A HealthCheck has a collection of TestGroups. The only TestGroup provided by ThirdCoast is the TalonGroup, a
 collection of Talon SRX motor controllers. The TestGroup has types of tests (e.g. the TalonGroup TestGroup has a
 TalonPositionTest and a TalonTimedTest) which set the Talons to specified voltage outputs or positions, log the data
 , and generate a portion of the HTML report. See a sample implementation of the TalonTimedTest at www.github.com/rkalnins/healthcheck-testbed

# Package org.strykeforce.thirdcoast.healthcheck

Provides a systems check service for use with CTRE Talon SRX motor controllers. Custom tests can be added season to
 season for any desired application.

# Package org.strykeforce.thirdcoast.healthcheck.tests

Talon position and timed tests and setting Talons to positions.

# Package org.strykeforce.thirdcoast.healthcheck.groups

Contains groups of Talons