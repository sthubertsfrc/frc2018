/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4637.robot;

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
	Pneumatics pneumatics;
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
		pneumatics = new Pneumatics (0, 1, 2, 3);
		shooter = new Shooter (8, 9, 1);
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
		updateDriveMotors();
		handleArmControl();
		
		shooter.limitSwitchTest();
		isSwitchPushed = shooter.limitSwitchTest();

		//Outtake Command
		if (leftJoystick.getRawButton(2) ==  true){
			inOut.Outtake();
		}
		else{
			inOut.Stop();
		}
		
		//Loading the shooter
		if (leftJoystick.getRawButton(1) ==  true){
			shooter.Load();
			pneumatics.pushOut2();
		}

		//Limit Switch Fun
		if (isSwitchPushed == false){
			shooter.StopLoader();
		}

		//SHOOT!
		if(rightJoystick.getRawButton(1) == true){
			pneumatics.pullIn2();
		}

		//Intake Command
		if (leftJoystick.getRawButton(3) == true){
			inOut.Intake();
		}
		else{
			inOut.Stop();
		}

		//Push grabber Piston Out
		if (rightJoystick.getRawButton(8) == true){
			pneumatics.pushOut1();
		}

		//Pull grabber piston in
		if (rightJoystick.getRawButton(9) == true){
			pneumatics.pullIn1();
		}

		//Push shooter piston out
		if (leftJoystick.getRawButton(8) == true){
			pneumatics.pushOut2();
		}

		//Pull Shooter piston in
		if (leftJoystick.getRawButton(9) == true){
			pneumatics.pullIn2();
		}
	}

	private void updateDriveMotors() {
		double driveAngle = rightJoystick.getX();
		double driveSpeed = rightJoystick.getY();
		
		SmartDashboard.putNumber("Drive Angle", driveAngle);
		SmartDashboard.putNumber("Drive Speed", driveSpeed);
		
		myDrive.arcadeDrive(-driveSpeed, driveAngle, true);
	}

	private void handleArmControl() {
		// Handle arm positioning (throttle arm motor based on left joystick Y axis)
		double armLiftVel = leftJoystick.getY();
		
		positioner.printControllerVariable();

		// Check if fast-positioning button is depressed, and override joystick input with maximum speed
		if (leftJoystick.getRawButton(4) == true){
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
