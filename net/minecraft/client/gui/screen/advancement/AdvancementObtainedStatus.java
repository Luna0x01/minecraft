package net.minecraft.client.gui.screen.advancement;

public enum AdvancementObtainedStatus {
	field_2701(0),
	field_2699(1);

	private final int spriteIndex;

	private AdvancementObtainedStatus(int j) {
		this.spriteIndex = j;
	}

	public int getSpriteIndex() {
		return this.spriteIndex;
	}
}
