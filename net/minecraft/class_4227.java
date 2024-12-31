package net.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class class_4227 {
	private final MinecraftClient field_20764;
	private final class_4216 field_20765;
	private float field_20766;

	public class_4227(class_4216 arg) {
		this.field_20765 = arg;
		this.field_20764 = MinecraftClient.getInstance();
	}

	public void method_19176(float f) {
		this.field_20766 += f;
		this.field_20765.method_19048(this.field_20764, MathHelper.sin(this.field_20766 * 0.001F) * 5.0F + 25.0F, -this.field_20766 * 0.1F);
		this.field_20764.field_19944.method_18293();
	}
}
