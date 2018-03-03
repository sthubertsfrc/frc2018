package org.usfirst.frc.team4637.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Position extends PIDSubsystem{
	Spark positionMotor;
	Potentiometer pot;
	AnalogInput ai;
	double posSpeed;


	//Constructor

	public Position (int potPort, int talonPort){
		super("Position", 0.01, 0.0, 0.001);// The constructor passes a name for the subsystem and the P, I and D constants that are used when computing the motor output
		setAbsoluteTolerance(0.02);
		getPIDController().setContinuous(false);
		setInputRange(0, 255);
		setOutputRange(-12, 12);

		ai = new AnalogInput(potPort);
		pot = new AnalogPotentiometer(ai, 255, 0);
		positionMotor = new Spark (talonPort);

	}

	//Methods	
	public void initDefaultCommand() {

	}
	public void motorTest(){
		positionMotor.set(-0.9);
	}
	public void motorTestDown(){
		positionMotor.set(0.9);
	}
	public void motorTestStop(){
		positionMotor.set(0);
	}

	protected double returnPIDInput() {
		double works = ai.getAverageVoltage() * 255.0 / 5.0; // returns the sensor value that is providing the feedback for the system
		SmartDashboard.putNumber("PIDInput", works);
		return works;
	}

	protected void usePIDOutput(double output) {
		SmartDashboard.putNumber("PIDOutput", output);
		positionMotor.pidWrite(output); // this is where the computed output value fromthe PIDController is applied to the motor
	}

	public void positionTest(){
		returnPIDInput();
	}

	public void potTest(){
		double degrees = pot.get();
		SmartDashboard.putNumber("Pot Degrees", degrees);

	}
	
	public void positionZero(){
		setSetpoint(127.5);
	}
	
	public void positionOne(){
		setSetpoint(153);

	}
	
	public void positionTwo(){
		setSetpoint(178.5);

	}
	
	public void positionThree(){
		setSetpoint(204);

	}
	
	public void freeMoveUp(){
		positionMotor.set(-1);

	}
	public void freeMoveDown(){
		positionMotor.set(1);


	}
}