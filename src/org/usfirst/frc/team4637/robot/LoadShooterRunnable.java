package org.usfirst.frc.team4637.robot;

import java.util.concurrent.TimeUnit;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LoadShooterRunnable implements Runnable {

	private Shooter shooter;

	public LoadShooterRunnable(Shooter shooter_in) {
		// 
		shooter = shooter_in;
	}

	/** Sleep for the designated amount of time, but break out early if the limit switch trips */
	private boolean sleepWithLimitCheck(long sleep_ms) throws InterruptedException
	{
		final long refresh_time_ms = 50;
		while (sleep_ms > 0) {
			if (shooter.limitSwitchTest()) {
				return true;
			}
			Thread.sleep(Math.max(Math.min(sleep_ms, refresh_time_ms), 0));
			sleep_ms -= refresh_time_ms;
		}
		return false;
	}
	
	public void run() {
		try {
			if (!shooter.mutex.tryLock(500, TimeUnit.MILLISECONDS)) {
				return;
			}

			// ************** Load procedure starts here ***********************
			SmartDashboard.putString("ShooterStatus", "(1) Starting");
			shooter.setWinchSpeed(0.3);    // start spinning motors slowly to seat clutch
			sleepWithLimitCheck(100); // Wait just long enough for motors to start spinning before trying to engage the clutch

			shooter.armShooterClutch();  // Activate solenoid (while motor is spinning) to seat clutch
			SmartDashboard.putString("ShooterStatus", "(2) Clutch Engaged");
			if (!sleepWithLimitCheck(2000));  // Wait long enough for clutch to seat, but also check for limit switch contact in case it seats quickly
			{
				SmartDashboard.putString("ShooterStatus", "(3) Winding shooter arm");
				// NOTE: only ramp up speed and wait if limit switch hasn't already tripped
				shooter.setWinchSpeed(1.0); // Run at full speed while arming the mechanism
				sleepWithLimitCheck(10000); // Wait 10 seconds or until limit switch trips
			}

			shooter.setWinchSpeed(0.0); // Stop winch motor once the mechanism is fully sprung
			
			SmartDashboard.putString("ShooterStatus", "(4) Ready to Fire");
			// ************** Load procedure ends here ***********************
			
		} catch (InterruptedException e) {
			// Can't really recover other than to abort
			return;
		}
		finally {
			// ALWAYS release the mutex or it won't be able to arm again
			if (shooter.mutex.isLocked()) {
				shooter.mutex.unlock();
			}
		}
	}
}
