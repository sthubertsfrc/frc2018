package org.usfirst.frc.team4637.robot;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter {

	// Two motors used to drive the winch 
	private PWMTalonSRX shooterTalon1;
	private PWMTalonSRX shooterTalon2;

	// Controls both winch motors in unison (more convenient interface)
	private SpeedControllerGroup winch;

	// Winch limit switch (trips when shooter is fully drawn
	private DigitalInput limitSwitch;

	// Clutch solenoid to engage / disengage the puller motor from the shooter
	private DoubleSolenoid triggerClutch;

	ReentrantLock mutex;

	public Shooter(ReentrantLock shooterAccessMutex, int talonPort1, int talonPort2, int switchPort, int clutchForwardPort, int clutchReversePort) {
		mutex = shooterAccessMutex;
		limitSwitch = new DigitalInput(switchPort); 
		shooterTalon1 = new PWMTalonSRX(talonPort1);
		shooterTalon2 = new PWMTalonSRX(talonPort2);
		winch = new SpeedControllerGroup(shooterTalon1, shooterTalon2);
		triggerClutch = new DoubleSolenoid(clutchForwardPort, clutchReversePort);
	}

	public void armShooterClutch() throws InterruptedException {
		try {
			mutex.tryLock(0, TimeUnit.SECONDS);
			triggerClutch.set(DoubleSolenoid.Value.kForward);
		} finally {
			mutex.unlock();
		}

	}

	public void releaseShooterClutch() throws InterruptedException {
		try {
			mutex.tryLock(0, TimeUnit.SECONDS);;
			triggerClutch.set(DoubleSolenoid.Value.kReverse);
		} finally {
			mutex.unlock();
		}
	}
	
	public void setWinchSpeed(double speed) throws InterruptedException {
		try {
			mutex.tryLock(0, TimeUnit.SECONDS);
			winch.set(speed);
		} finally {
			mutex.unlock();
		}
	}

	public boolean limitSwitchTest(){
		boolean isSwitchPushed = limitSwitch.get();
		SmartDashboard.putBoolean("Limit Switch Pushed", isSwitchPushed);
		return isSwitchPushed;
	}
}

