package org.usfirst.frc.team4637.robot;

import java.util.concurrent.TimeUnit;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LaunchShooterRunnable implements Runnable {

	private Shooter shooter;
	private IntakeOuttake grabber;
	private long reloadTime_ms;

	public LaunchShooterRunnable(Shooter shooter_in, IntakeOuttake grabber_in, double reload_time_sec) {
		shooter = shooter_in;
		grabber = grabber_in;
		reloadTime_ms = (long)(reload_time_sec * 1000);
	}

	/**
	 * "Runnable" method that is invoked when this is started in a new thread.
	 * This method MUST exit within a fixed amount of time to return control of the shooter. Avoid doing any blocking calls here (i.e. lock()).
	 */
	public void run() {
		try {
			if (!shooter.mutex.tryLock(500, TimeUnit.MILLISECONDS)) {
				return;
			}
			SmartDashboard.putString("ShooterStatus", "(5) Starting shot");
			// ************** Launch procedure starts here ***********************
			shooter.setWinchSpeed(0.0); // Don't release the clutch while the motor is spinning (reduce friction on the clutch)		
			shooter.releaseShooterClutch(); // SHOOT!
			Thread.sleep(500); // Delay to sync up solenoid and grabber wheels
			grabber.outtake();
			Thread.sleep(500); // Delay to sync up solenoid and grabber wheels
			grabber.Stop();
			Thread.sleep(reloadTime_ms); // Ensure that shot is away and stuff has stopped moving
			SmartDashboard.putString("ShooterStatus", "(6) Shot done");
			// ************** Launch procedure ends here *************************
			
		} catch (InterruptedException e) {
			// Minimum recover from interruption is not to do anything else and just return
			return;
		}
		finally {
			// ALWAYS release the mutex or it won't be able to arm again
			if (shooter.mutex.isLocked()) {
				shooter.mutex.unlock();
			}
		}
		
		// Re-load the shooter after shooting
		LoadShooterRunnable reloader = new LoadShooterRunnable(shooter);
		Thread reload_thread = new Thread(reloader);
		reload_thread.start();
	}
}
