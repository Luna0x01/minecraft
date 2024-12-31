package net.minecraft.client.realms.gui;

public class DummyFetchRateLimiter implements FetchRateLimiter {
	@Override
	public void onRun() {
	}

	@Override
	public long getRemainingPeriod() {
		return 0L;
	}
}
