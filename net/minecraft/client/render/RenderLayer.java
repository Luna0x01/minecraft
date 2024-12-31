package net.minecraft.client.render;

public enum RenderLayer {
	SOLID("Solid"),
	CUTOUT_MIPPED("Mipped Cutout"),
	CUTOUT("Cutout"),
	TRANSLUCENT("Translucent");

	private final String name;

	private RenderLayer(String string2) {
		this.name = string2;
	}

	public String toString() {
		return this.name;
	}
}
