package org.usfirst.frc.team4637.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;


public class GrabberSolenoid {
	
	// Solenoid to drive the grabber 
	DoubleSolenoid grabberSolenoid;
	Compressor compressor;

	public GrabberSolenoid (int grabberForwardPort, int grabberReversePort){
		grabberSolenoid = new DoubleSolenoid(grabberForwardPort, grabberReversePort);
		compressor = new Compressor();
	}

	public void pushOut1(){
		grabberSolenoid.set(DoubleSolenoid.Value.kForward);

	}
	public void pullIn1(){
		grabberSolenoid.set(DoubleSolenoid.Value.kReverse);

	}
}
