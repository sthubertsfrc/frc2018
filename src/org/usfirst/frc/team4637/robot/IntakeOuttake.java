package org.usfirst.frc.team4637.robot;

import edu.wpi.first.wpilibj.PWMVictorSPX;

public class IntakeOuttake {
	PWMVictorSPX leftVictor;
	PWMVictorSPX rightVictor;
	
	//Constructor
	public IntakeOuttake(int leftPort, int rightPort) {
			leftVictor = new PWMVictorSPX (leftPort);
			rightVictor = new PWMVictorSPX (rightPort);
	}

		//Methods
		public void Intake(){
			leftVictor.set(0.5);
			rightVictor.set(-0.5);
		}
		public void Outtake() {
		leftVictor.set(-0.5);
		rightVictor.set(0.5);
		}
		public void Stop(){
			leftVictor.set(0);
			rightVictor.set(0);
		}
	}