package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.math.Vec3d;

public class DiffuseLighting {
	private static final FloatBuffer field_910 = GlAllocationUtils.allocateFloatBuffer(4);
	private static final Vec3d field_5040 = new Vec3d(0.2F, 1.0, -0.7F).normalize();
	private static final Vec3d field_5041 = new Vec3d(-0.2F, 1.0, 0.7F).normalize();

	public static void disable() {
		GlStateManager.disableLighting();
		GlStateManager.disableLight(0);
		GlStateManager.disableLight(1);
		GlStateManager.disableColorMaterial();
	}

	public static void enableNormally() {
		GlStateManager.enableLighting();
		GlStateManager.enableLight(0);
		GlStateManager.enableLight(1);
		GlStateManager.enableColorMaterial();
		GlStateManager.colorMaterial(1032, 5634);
		GlStateManager.method_12281(16384, 4611, method_844(field_5040.x, field_5040.y, field_5040.z, 0.0));
		float f = 0.6F;
		GlStateManager.method_12281(16384, 4609, method_845(0.6F, 0.6F, 0.6F, 1.0F));
		GlStateManager.method_12281(16384, 4608, method_845(0.0F, 0.0F, 0.0F, 1.0F));
		GlStateManager.method_12281(16384, 4610, method_845(0.0F, 0.0F, 0.0F, 1.0F));
		GlStateManager.method_12281(16385, 4611, method_844(field_5041.x, field_5041.y, field_5041.z, 0.0));
		GlStateManager.method_12281(16385, 4609, method_845(0.6F, 0.6F, 0.6F, 1.0F));
		GlStateManager.method_12281(16385, 4608, method_845(0.0F, 0.0F, 0.0F, 1.0F));
		GlStateManager.method_12281(16385, 4610, method_845(0.0F, 0.0F, 0.0F, 1.0F));
		GlStateManager.shadeModel(7424);
		float g = 0.4F;
		GlStateManager.method_12282(2899, method_845(0.4F, 0.4F, 0.4F, 1.0F));
	}

	private static FloatBuffer method_844(double d, double e, double f, double g) {
		return method_845((float)d, (float)e, (float)f, (float)g);
	}

	public static FloatBuffer method_845(float f, float g, float h, float i) {
		field_910.clear();
		field_910.put(f).put(g).put(h).put(i);
		field_910.flip();
		return field_910;
	}

	public static void enable() {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
		enableNormally();
		GlStateManager.popMatrix();
	}
}
