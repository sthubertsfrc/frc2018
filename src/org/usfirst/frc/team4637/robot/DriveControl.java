package org.usfirst.frc.team4637.robot;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveControl {
	
	// NOTE hard-coded ports here for drive wheels
	Spark backLeft = new Spark(0);
	Spark frontLeft = new Spark(1);
	Spark backRight= new Spark(2);
	Spark frontRight = new Spark(3);
	
	SpeedControllerGroup leftDrive = new SpeedControllerGroup(frontLeft, backLeft);
	SpeedControllerGroup rightDrive = new SpeedControllerGroup (frontRight, backRight);
	DifferentialDrive myDrive = new DifferentialDrive (leftDrive, rightDrive);

	public void moveAtAngleAndSpeed(double driveAngle, double driveSpeed) {
		
		SmartDashboard.putNumber("Drive Angle", driveAngle);
		SmartDashboard.putNumber("Drive Speed", driveSpeed);
		
		myDrive.arcadeDrive(-driveSpeed, driveAngle, true);
	}
	
	public void stop() {
		moveAtAngleAndSpeed(0.0, 0.0);
	}
}
