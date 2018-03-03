package org.usfirst.frc.team4637.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter {
	PWMTalonSRX shooterTalon1;
	PWMTalonSRX shooterTalon2;
	DigitalInput limitSwitch;
	DigitalInput di;
	SpeedControllerGroup winch;
	boolean isSwitchPushed;
	
	public Shooter(int talonPort1, int talonPort2, int switchPort) {
		di = new DigitalInput(switchPort);
		limitSwitch = di; 
		shooterTalon1 = new PWMTalonSRX(talonPort1);
		shooterTalon2 = new PWMTalonSRX(talonPort2);
		winch = new SpeedControllerGroup(shooterTalon1, shooterTalon2);
	}
	public boolean limitSwitchTest(){
		isSwitchPushed = limitSwitch.get();
		SmartDashboard.putBoolean("Limit Switch Pushed", isSwitchPushed);
		return isSwitchPushed;
	}
	
	public void Load(){
		winch.set(1);
	}
	public void StopLoader(){
		winch.set(0);
	}
}
