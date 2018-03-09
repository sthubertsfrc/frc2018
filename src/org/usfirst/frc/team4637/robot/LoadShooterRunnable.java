package org.usfirst.frc.team4637.robot;

import java.util.concurrent.TimeUnit;

public class LoadShooterRunnable implements Runnable {

	private Shooter shooter;

	public LoadShooterRunnable(Shooter shooter_in) {
		// 
		shooter = shooter_in;
	}

	public void run() {
		try {
			if (!shooter.mutex.tryLock(500, TimeUnit.MILLISECONDS)) {
				return;
			}

			shooter.setWinchSpeed(0.3);    // start spinning motors slowly to seat clutch
			Thread.sleep(200); // Wait just long enough for motors to start spinning before trying to engage the clutch

			shooter.armShooterClutch();  // Activate solenoid (while motor is spinning) to seat clutch
			Thread.sleep(2000);  // Wait long enough for clutch to seat

			// Arm shooter w/ winch at full speed
			long t_initial = System.currentTimeMillis();
			final long winch_timeout = 10000; // milliseconds before winch shuts off regardless of limit switch
			long t_cutoff =  t_initial + winch_timeout; // Stop winch after this time

			shooter.setWinchSpeed(1.0);

			while (System.currentTimeMillis() < t_cutoff) {
				// WARNING: we don't even start looking for the limit switch until here, so make sure that previous lines don't take too long!
				if (shooter.limitSwitchTest()) {
					break;
				}
				Thread.sleep(50); 
			}
			shooter.setWinchSpeed(0.0);
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
