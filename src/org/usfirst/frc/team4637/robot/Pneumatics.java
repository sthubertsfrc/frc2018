package org.usfirst.frc.team4637.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Solenoid;

public class Pneumatics {
	//Compressor and Solenoid objects
	Compressor compressor;
	DoubleSolenoid solenoid1;
	DoubleSolenoid solenoid2;
	//Solenoid variables.
		int pneumaticChannelNumberSol1A;
		int pneumaticChannelNumberSol1B;
		
		int pneumaticChannelNumberSol2A;
		int pneumaticChannelNumberSol2B; 

		//Booleans for position of cylinder, the retracted position is true, released position is false.
		Value pull;
		Value push;
		
		public Pneumatics (int port1, int port2, int port3, int port4){
			
			solenoid1 = new DoubleSolenoid(port1, port2); 
	    	solenoid2 = new DoubleSolenoid(port3, port4);
			compressor = new Compressor();
			
		}
		
		public void pushOut1(){
			solenoid1.set(DoubleSolenoid.Value.kForward);
			
		}
		public void pullIn1(){
			solenoid1.set(DoubleSolenoid.Value.kReverse);
			
		}
		public void pushOut2(){
			solenoid2.set(DoubleSolenoid.Value.kForward);
			
		}
		public void pullIn2(){
			solenoid2.set(DoubleSolenoid.Value.kReverse);
			
		}
}
