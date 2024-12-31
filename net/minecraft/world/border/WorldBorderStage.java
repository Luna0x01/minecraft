package net.minecraft.world.border;

public enum WorldBorderStage {
	field_12754(4259712),
	field_12756(16724016),
	field_12753(2138367);

	private final int color;

	private WorldBorderStage(int j) {
		this.color = j;
	}

	public int getColor() {
		return this.color;
	}
}
