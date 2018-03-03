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
	double speedCtrlGain;
	double armUpDirection;

	public ArmAngleController (int potPort, int talonPort, int lowerLimitPort, int upperLimitPort){
		super("Position", 0.01, 0.0, 0.001);// The constructor passes a name for the subsystem and the P, I and D constants that are used when computing the motor output
		setAbsoluteTolerance(0.02);
		getPIDController().setContinuous(false);
		setInputRange(-255, 255);
		setOutputRange(-1.0, 1.0);
		
		// NOTE This gain MUST be between -1.0 and 1.0
		// 
		speedCtrlGain = 0.5;
		armUpDirection = -1.0;
		
		ai = new AnalogInput(potPort);
		pot = new AnalogPotentiometer(ai, 255.0, 0);
		positionMotor = new Spark (talonPort);
		lowerLimit = new DigitalInput(lowerLimitPort);
		upperLimit = new DigitalInput(upperLimitPort);
	}

	protected double returnPIDInput() {
		double raw_angle = pot.get(); // returns the sensor value that is providing the feedback for the system
		return raw_angle;
	}

	protected void usePIDOutput(double output) {
		SmartDashboard.putNumber("PID Output", output);
		positionMotor.pidWrite(output); // this is where the computed output value fromthe PIDController is applied to the motor
	}

	public void printControllerVariable(){
		SmartDashboard.putNumber("Pot Degrees", pot.get());
		SmartDashboard.putNumber("PID Input (scaled)", returnPIDInput());
		SmartDashboard.putNumber("Pot Angle (scaled)", pot.get());
	}
	
	public int inLimits(){
		// NOTE: switches are normally closed (i.e. "on") when arm is within limits
		// This way, if anything fails (wire breaks, 
		//boolean inLowerLimit = lowerLimit.get();
		boolean inLowerLimit = true;
		boolean inUpperLimit = upperLimit.get();
		// SmartDashboard.putBoolean("Arm at Lower Limit", inLowerLimit);
		SmartDashboard.putBoolean("Arm at Upper Limit", inUpperLimit);
		
		// Need to know which limit we've hit so the speed controller can back off
		int arm_status = 0;
		if (!inUpperLimit) {
			arm_status = 1;
		} else if (!inLowerLimit) {
			arm_status = -1;
		} 
		SmartDashboard.putNumber("Arm Limit Status", arm_status);
		return arm_status;
	}
	
	public static double makePositive(double in)
	{
		return Math.max(in, 0.0);
	}
	
	public static double makeNegative(double in)
	{
		return Math.min(in, 0.0);
	}
	
	public static double applyBound(double in, double min_val, double max_val)
	{
		return Math.max(Math.min(in, max_val), min_val);
	}
	
	public void updateMotorSpeed(double speed)
	{
		double net_speed = speed;
		int limitState = inLimits();
		switch (limitState) {
			case -1: // At lower limit, speed has to be positive
				net_speed = makePositive(net_speed);
				break;
			case 1: // At upper limit, speed has to be negative
				net_speed = makeNegative(net_speed);
				break;
			case 0:
			default:
		}
		
		// Bound input speed based on controller limits and gain
		net_speed = applyBound(net_speed * speedCtrlGain * armUpDirection, -1.0, 1.0);
		SmartDashboard.putNumber("Net Arm Speed", net_speed);

		positionMotor.set(net_speed);
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
	
}