package net.minecraft.client.gui.screen;

public interface StatsListener {
	String[] PROGRESS_BAR_STAGES = new String[]{"oooooo", "Oooooo", "oOoooo", "ooOooo", "oooOoo", "ooooOo", "oooooO"};

	void onStatsReady();
}
