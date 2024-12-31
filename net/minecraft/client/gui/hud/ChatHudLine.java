package net.minecraft.client.gui.hud;

import net.minecraft.text.Text;

public class ChatHudLine {
	private final int creationTick;
	private final Text text;
	private final int id;

	public ChatHudLine(int i, Text text, int j) {
		this.text = text;
		this.creationTick = i;
		this.id = j;
	}

	public Text getText() {
		return this.text;
	}

	public int getCreationTick() {
		return this.creationTick;
	}

	public int getId() {
		return this.id;
	}
}
