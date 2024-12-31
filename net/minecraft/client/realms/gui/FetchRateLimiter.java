package net.minecraft.client.realms.gui;

public interface FetchRateLimiter {
	void onRun();

	long getRemainingPeriod();
}
