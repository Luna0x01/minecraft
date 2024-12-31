package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;

public abstract class LongRunningTask implements Runnable {
	protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

	public void setScreen(RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen) {
		this.longRunningMcoTaskScreen = realmsLongRunningMcoTaskScreen;
	}

	public void error(String string) {
		this.longRunningMcoTaskScreen.method_21290(string);
	}

	public void setTitle(String string) {
		this.longRunningMcoTaskScreen.setTitle(string);
	}

	public boolean aborted() {
		return this.longRunningMcoTaskScreen.aborted();
	}

	public void tick() {
	}

	public void init() {
	}

	public void abortTask() {
	}
}
