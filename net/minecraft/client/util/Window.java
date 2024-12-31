package net.minecraft.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class Window {
	private final double scaledWidth;
	private final double scaledHeight;
	private int width;
	private int height;
	private int scaleFactor;

	public Window(MinecraftClient minecraftClient) {
		this.width = minecraftClient.width;
		this.height = minecraftClient.height;
		this.scaleFactor = 1;
		boolean bl = minecraftClient.forcesUnicodeFont();
		int i = minecraftClient.options.guiScale;
		if (i == 0) {
			i = 1000;
		}

		while (this.scaleFactor < i && this.width / (this.scaleFactor + 1) >= 320 && this.height / (this.scaleFactor + 1) >= 240) {
			this.scaleFactor++;
		}

		if (bl && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
			this.scaleFactor--;
		}

		this.scaledWidth = (double)this.width / (double)this.scaleFactor;
		this.scaledHeight = (double)this.height / (double)this.scaleFactor;
		this.width = MathHelper.ceil(this.scaledWidth);
		this.height = MathHelper.ceil(this.scaledHeight);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public double getScaledWidth() {
		return this.scaledWidth;
	}

	public double getScaledHeight() {
		return this.scaledHeight;
	}

	public int getScaleFactor() {
		return this.scaleFactor;
	}
}
