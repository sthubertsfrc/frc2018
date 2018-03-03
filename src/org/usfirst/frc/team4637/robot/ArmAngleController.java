package org.usfirst.frc.team4637.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;

public class ArmAngleController extends PIDSubsystem{
	Spark positionMotor;
	Potentiometer pot;
	AnalogInput ai;
	DigitalInput lowerLimit;
	DigitalInput upperLimit;
	double posSpeed;

	public ArmAngleController (int potPort, int talonPort, int lowerLimitPort, int upperLimitPort){
		super("Position", 0.01, 0.0, 0.001);// The constructor passes a name for the subsystem and the P, I and D constants that are used when computing the motor output
		setAbsoluteTolerance(0.02);
		getPIDController().setContinuous(false);
		setInputRange(-255, 255);
		setOutputRange(-2.0, 2.0);

		ai = new AnalogInput(potPort);
		pot = new AnalogPotentiometer(ai, 1.0, 0);
		positionMotor = new Spark (talonPort);
		lowerLimit = new DigitalInput(lowerLimitPort);
		upperLimit = new DigitalInput(upperLimitPort);
	}
	
	public void motorUp(){
		positionMotor.set(-0.1);
	}
	
	public void motorDown(){
		positionMotor.set(0.1);
	}
	
	public void motorStop(){
		positionMotor.set(0);
	}

	protected double returnPIDInput() {
		double raw_angle = pot.get(); // returns the sensor value that is providing the feedback for the system
		return raw_angle;
	}

	protected void usePIDOutput(double output) {
		SmartDashboard.putNumber("PID Output", output);
		positionMotor.pidWrite(output); // this is where the computed output value fromthe PIDController is applied to the motor
	}

	public void positionTest(){
		returnPIDInput();
	}

	public void printControllerVariable(){
		SmartDashboard.putNumber("Pot Degrees", pot.get());
		SmartDashboard.putNumber("PID Input (scaled)", returnPIDInput());
		SmartDashboard.putNumber("Pot Angle (scaled)", pot.get());
	}
	
	public boolean inLimits(){
		// NOTE: switches are normally closed (i.e. "on") when arm is within limits
		// This way, if anything fails (wire breaks, 
		// boolean inLowerLimit = lowerLimit.get();
		boolean inUpperLimit = upperLimit.get();
		// SmartDashboard.putBoolean("Arm at Lower Limit", inLowerLimit);
		SmartDashboard.putBoolean("Arm at Upper Limit", inUpperLimit);
		return inUpperLimit;
	}
	
	public void updateMotorSpeed(double speed)
	{
		double net_speed = 0.0;
		if (inLimits()) {
			net_speed = Math.max(Math.min(speed, 1.0), -1.0);
		}
		positionMotor.set(net_speed);
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
	
}