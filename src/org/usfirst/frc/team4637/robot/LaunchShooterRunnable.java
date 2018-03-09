package org.usfirst.frc.team4637.robot;

import java.util.concurrent.TimeUnit;

public class LaunchShooterRunnable implements Runnable {

	private Shooter shooter;
	private long reloadTime_ms;

	public LaunchShooterRunnable(Shooter shooter_in, double reload_time_sec) {
		shooter = shooter_in;
		reloadTime_ms = (long)(reload_time_sec * 1000);
	}

	public void run() {
		try {
			if (!shooter.mutex.tryLock(500, TimeUnit.MILLISECONDS)) {
				return;
			}

			shooter.setWinchSpeed(0.0); // Don't release the clutch while the motor is spinning (reduce friction on the clutch)
			
			shooter.releaseShooterClutch();
			
			Thread.sleep(reloadTime_ms);
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
		
		LoadShooterRunnable reloader = new LoadShooterRunnable(shooter);
		Thread reload_thread = new Thread(reloader);
		reload_thread.start();
	}
}
