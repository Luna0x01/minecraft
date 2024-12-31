package net.minecraft.client;

public interface WindowEventHandler {
	void onWindowFocusChanged(boolean bl);

	void updateDisplay(boolean bl);

	void onResolutionChanged();
}
