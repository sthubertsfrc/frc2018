/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4637.robot;

import java.util.concurrent.locks.ReentrantLock;

// import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
// import edu.wpi.first.wpilibj.interfaces.Gyro;
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
	Victor winch;
	// Gyro gyro = new ADXRS450_Gyro();
	
	//Drive Train declaration
	DriveControl drive = new DriveControl();
    Encoder leftEncoder = new Encoder(2, 3, false, Encoder.EncodingType.k4X);

	//Control declaration
	Joystick leftJoystick = new Joystick(0);
	Joystick rightJoystick = new Joystick(1);

	//Other variables
	boolean isSwitchPushed;
	boolean pull;
	boolean push;
	Solenoid solenoid2A;
	Solenoid solenoid2B;

	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	boolean releaseHook;
	boolean releaseHookIn;

	boolean releaseHook2;
	boolean releaseHookIn2;
	
	// Autonomous control variables
	double autoStartTime;
	double Kp = 0.04;
	double Kd = Kp / 10.0;
	
	String gameData;

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
		winch = new Victor(5);

		Solenoid solenoid2A = new Solenoid(4);
		Solenoid solenoid2B = new Solenoid(5);

		solenoid2A.set(pull);
		solenoid2B.set(push);

        // gyro.calibrate();
        
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

		double someval = SmartDashboard.getNumber("Starting Pos", 0.0);
		autoStartTime = Timer.getFPGATimestamp();
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		System.out.printf("Auto started at %f sec\n", autoStartTime);
		SmartDashboard.putString("Game Data", gameData);
		SmartDashboard.putNumber("Got setting", someval);
		// gyro.reset();
	}
	
	public double getCurrentAutonomousTime() {
		return Timer.getFPGATimestamp() - autoStartTime;
	}

	public void sleep(double seconds)
	{
		try {
			// Convert from seconds to ms for sleep function
			Thread.sleep((long)seconds*1000);
		} catch (InterruptedException e) {
			// Do nothing if interrupted (it shouln't be anyway)
		}
	}
	
	public void driveAtFixedAngle(double speed, double angle_ref)
	{
//		double err_angle = angle_ref - gyro.getAngle();
//		double err_rate = 0.0 - gyro.getRate();
//		double angleCorrection = Kp * err_angle + Kd * err_rate; // get current heading
//		
//		drive.moveAtAngleAndSpeed(angleCorrection, speed, false);
	}
	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		
		/************** Autonomous code starts here ********************************/
		double t = getCurrentAutonomousTime();
		if(t < 2.0) {
			drive.moveAtAngleAndSpeed(0, 0.50, false); // Start moving forward
		} else {
			drive.stop();
		}

		/************** Autonomous code end here ********************************/
	}

	public double getEncoderDistInch()
	{
		return leftEncoder.getDistance() * 18.5 / 2048.0;
	}
	
	public void teleopInit() {
		// Pull in hook arm by default
		try {
			shooter.releaseShooterClutch();
		} catch (InterruptedException e) {
		}
//		gyro.reset();
		leftEncoder.reset();
	}
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {

//		SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
//		SmartDashboard.putNumber("Gyro Rate", gyro.getRate());
		SmartDashboard.putNumber("Encoder", leftEncoder.getDistance());
		// Call this here just to update the smart dashboard
		SmartDashboard.putBoolean("Shooter Limit Switch State", shooter.limitSwitchTest());

		// Flip sign on Y axis so that forward on the stick is actually forward
		// Reduce the sensitivity slightly of drive input from stick
		drive.moveAtAngleAndSpeed(rightJoystick.getX()*.8, -rightJoystick.getY()*.9, true);

		handleArmControl(leftJoystick.getY(), leftJoystick.getRawButton(4));

		releaseHook2 = leftJoystick.getRawButton(6);
		releaseHookIn2 = leftJoystick.getRawButton(7);


		if (leftJoystick.getRawButton(11) == true){
			winch.set(1);
		}
		else 
			winch.set(0);


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
		// Extend the hook
		if (leftJoystick.getRawButton(1) == true){
			try {
				// KLUDGE controls the hook arm
				shooter.armShooterClutch();
			} catch (InterruptedException e) {
			}
			//LoadShooterRunnable reloader = new LoadShooterRunnable(shooter);
			//Thread load_thread = new Thread(reloader);
			//load_thread.start();
		}

		// Shoot! (and reload)
		// Retract the hook
		if (rightJoystick.getRawButton(1) == true){
			try {
				shooter.releaseShooterClutch();
				// KLUDGE controls the hook arm
			} catch (InterruptedException e) {
			}
			//LaunchShooterRunnable launcher = new LaunchShooterRunnable(shooter, inOut, 2.0);
			//Thread shoot_thread = new Thread(launcher);
			//shoot_thread.start();
		}	
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

		if (releaseHook) {

			solenoid2A.set(pull); //PCM sol. port 5
			solenoid2B.set(push);  //PCM sol. port 6

		} else if (releaseHookIn) {
			solenoid2B.set(pull); //PCM sol. port 5
			solenoid2A.set(push);  //PCM sol. port 6
		}

	}

	public void testInit() {
//		gyro.reset();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
//		SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
//		SmartDashboard.putNumber("Gyro Rate", gyro.getRate());
		SmartDashboard.putNumber("Encoder", leftEncoder.getDistance());
	}
}
