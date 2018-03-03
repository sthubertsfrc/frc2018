package org.usfirst.frc.team4637.robot;

import edu.wpi.first.wpilibj.PWMVictorSPX;

public class IntakeOuttake {
	PWMVictorSPX leftVictor;
	PWMVictorSPX rightVictor;
	double intakeSpeed;
	double outtakeSpeed;

	//Constructor
	public IntakeOuttake(int leftPort, int rightPort) {
		leftVictor = new PWMVictorSPX (leftPort);
		rightVictor = new PWMVictorSPX (rightPort);
		intakeSpeed = 0.8;
		outtakeSpeed = 0.8;
	}

	//Methods
	public void outtake(){
		leftVictor.set(outtakeSpeed);
		rightVictor.set(-outtakeSpeed);
	}
	public void intake() {
		leftVictor.set(-intakeSpeed);
		rightVictor.set(intakeSpeed);
	}
	public void spin() {
		leftVictor.set(-intakeSpeed * 0.5);
		rightVictor.set(intakeSpeed);
	}
	public void Stop(){
		leftVictor.set(0);
		rightVictor.set(0);
	}
}