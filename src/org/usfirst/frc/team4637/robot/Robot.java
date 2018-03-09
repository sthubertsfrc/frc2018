/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4637.robot;

import java.util.concurrent.locks.ReentrantLock;

import edu.wpi.first.wpilibj.IterativeRobot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	//Class declaration
	Shooter shooter;
	GrabberSolenoid grabberSolenoid;
	ArmAngleController positioner;
	IntakeOuttake inOut;

	//Drive Train declaration
	Spark backLeft = new Spark(0);
	Spark frontLeft = new Spark(1);
	Spark backRight= new Spark(2);
	Spark frontRight = new Spark(3);
	SpeedControllerGroup leftDrive = new SpeedControllerGroup(frontLeft, backLeft);
	SpeedControllerGroup rightDrive = new SpeedControllerGroup (frontRight, backRight);
	DifferentialDrive myDrive = new DifferentialDrive (leftDrive, rightDrive);

	//Control declaration
	Joystick leftJoystick = new Joystick(0);
	Joystick rightJoystick = new Joystick(1);


	//Other variables
	boolean isSwitchPushed;

	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);

		//variable initialization
		positioner = new ArmAngleController (1, 4, 8, 9);
		inOut = new IntakeOuttake (7, 6);
		grabberSolenoid = new GrabberSolenoid(0, 1);
		shooter = new Shooter(new ReentrantLock(), 8, 9, 1, 2, 3);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
		case kCustomAuto:
			// Put custom auto code here
			break;
		case kDefaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
	
		updateDriveMotors(rightJoystick.getX(), rightJoystick.getY());
		
		handleArmControl(leftJoystick.getY(), leftJoystick.getRawButton(4));

		// Handle grabber states
		if (leftJoystick.getRawButton(2) == true){
			inOut.intake();
		}
		else if (leftJoystick.getRawButton(3) == true){
			inOut.outtake();
		}
		else if (leftJoystick.getRawButton(5) == true){
			// "Spin" the box by running one side faster than the other
			inOut.spin();
		}
		else {
			// NOTE: only stop if neither button is pressed (otherwise outtake won't work)
			inOut.Stop();
		}
		
		// Push grabber Piston Out
		if (rightJoystick.getRawButton(8) == true){
			grabberSolenoid.pushOut1();
		}

		// Pull grabber piston in
		if (rightJoystick.getRawButton(9) == true){
			grabberSolenoid.pullIn1();
		}
		
		// Arm shooter
		if (leftJoystick.getRawButton(1) == true){
			LoadShooterRunnable reloader = new LoadShooterRunnable(shooter);
			Thread load_thread = new Thread(reloader);
			load_thread.start();
		}
		
		// Shoot! (and reload)
		if (rightJoystick.getRawButton(1) == true){
			LaunchShooterRunnable launcher = new LaunchShooterRunnable(shooter, inOut, 2.0);
			Thread shoot_thread = new Thread(launcher);
			shoot_thread.start();
		}	
	}

	private void updateDriveMotors(double driveAngle, double driveSpeed) {
		
		SmartDashboard.putNumber("Drive Angle", driveAngle);
		SmartDashboard.putNumber("Drive Speed", driveSpeed);
		
		myDrive.arcadeDrive(-driveSpeed, driveAngle, true);
	}

	private void handleArmControl(double armLiftVel, boolean auto_lift_active) {
		// Handle arm positioning (throttle arm motor based on left joystick Y axis)
		
		positioner.printControllerVariable();

		// Check if fast-positioning button is depressed, and override joystick input with maximum speed
		if (auto_lift_active == true){
			armLiftVel = 1.0;
		}
		
		SmartDashboard.putNumber("Arm Speed", armLiftVel);
		positioner.updateMotorSpeed(armLiftVel);
	}
	

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
