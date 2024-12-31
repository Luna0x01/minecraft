package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class class_4240 extends class_4239<class_3741> {
	private static final Identifier field_20849 = new Identifier("textures/entity/conduit/base.png");
	private static final Identifier field_20850 = new Identifier("textures/entity/conduit/cage.png");
	private static final Identifier field_20851 = new Identifier("textures/entity/conduit/wind.png");
	private static final Identifier field_20852 = new Identifier("textures/entity/conduit/wind_vertical.png");
	private static final Identifier field_20853 = new Identifier("textures/entity/conduit/open_eye.png");
	private static final Identifier field_20854 = new Identifier("textures/entity/conduit/closed_eye.png");
	private final EntityModel field_20855 = new class_4240.class_4243();
	private final EntityModel field_20856 = new class_4240.class_4241();
	private final class_4240.class_4244 field_20857 = new class_4240.class_4244();
	private final class_4240.class_4242 field_20858 = new class_4240.class_4242();

	public void method_1631(class_3741 arg, double d, double e, double f, float g, int i) {
		float h = (float)arg.field_18625 + g;
		if (!arg.method_16802()) {
			float j = arg.method_16796(0.0F);
			this.method_19327(field_20849);
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			GlStateManager.rotate(j, 0.0F, 1.0F, 0.0F);
			this.field_20855.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.popMatrix();
		} else if (arg.method_16802()) {
			float k = arg.method_16796(g) * (180.0F / (float)Math.PI);
			float l = MathHelper.sin(h * 0.1F) / 2.0F + 0.5F;
			l = l * l + l;
			this.method_19327(field_20850);
			GlStateManager.disableCull();
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)d + 0.5F, (float)e + 0.3F + l * 0.2F, (float)f + 0.5F);
			GlStateManager.rotate(k, 0.5F, 1.0F, 0.5F);
			this.field_20856.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.popMatrix();
			int m = 3;
			int n = arg.field_18625 / 3 % class_4240.class_4244.field_20862;
			this.field_20857.method_19333(n);
			int o = arg.field_18625 / (3 * class_4240.class_4244.field_20862) % 3;
			switch (o) {
				case 0:
					this.method_19327(field_20851);
					GlStateManager.pushMatrix();
					GlStateManager.translate((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					this.field_20857.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.translate((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.scale(0.875F, 0.875F, 0.875F);
					GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
					this.field_20857.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					break;
				case 1:
					this.method_19327(field_20852);
					GlStateManager.pushMatrix();
					GlStateManager.translate((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
					this.field_20857.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.translate((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.scale(0.875F, 0.875F, 0.875F);
					GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
					this.field_20857.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					break;
				case 2:
					this.method_19327(field_20851);
					GlStateManager.pushMatrix();
					GlStateManager.translate((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
					this.field_20857.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.translate((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.scale(0.875F, 0.875F, 0.875F);
					GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
					this.field_20857.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
			}

			Entity entity = MinecraftClient.getInstance().getCameraEntity();
			Vec2f vec2f = Vec2f.ZERO;
			if (entity != null) {
				vec2f = entity.getRotationClient();
			}

			if (arg.method_16803()) {
				this.method_19327(field_20853);
			} else {
				this.method_19327(field_20854);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate((float)d + 0.5F, (float)e + 0.3F + l * 0.2F, (float)f + 0.5F);
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.rotate(-vec2f.y, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(vec2f.x, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
			this.field_20858.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.083333336F);
			GlStateManager.popMatrix();
		}

		super.method_1631(arg, d, e, f, g, i);
	}

	static class class_4241 extends EntityModel {
		private final ModelPart field_20859;

		public class_4241() {
			this.textureWidth = 32;
			this.textureHeight = 16;
			this.field_20859 = new ModelPart(this, 0, 0);
			this.field_20859.addCuboid(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		}

		@Override
		public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
			this.field_20859.render(scale);
		}
	}

	static class class_4242 extends EntityModel {
		private final ModelPart field_20860;

		public class_4242() {
			this.textureWidth = 8;
			this.textureHeight = 8;
			this.field_20860 = new ModelPart(this, 0, 0);
			this.field_20860.addCuboid(-4.0F, -4.0F, 0.0F, 8, 8, 0, 0.01F);
		}

		@Override
		public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
			this.field_20860.render(scale);
		}
	}

	static class class_4243 extends EntityModel {
		private final ModelPart field_20861;

		public class_4243() {
			this.textureWidth = 32;
			this.textureHeight = 16;
			this.field_20861 = new ModelPart(this, 0, 0);
			this.field_20861.addCuboid(-3.0F, -3.0F, -3.0F, 6, 6, 6);
		}

		@Override
		public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
			this.field_20861.render(scale);
		}
	}

	static class class_4244 extends EntityModel {
		public static int field_20862 = 22;
		private final ModelPart[] field_20863 = new ModelPart[field_20862];
		private int field_20864;

		public class_4244() {
			this.textureWidth = 64;
			this.textureHeight = 1024;

			for (int i = 0; i < field_20862; i++) {
				this.field_20863[i] = new ModelPart(this, 0, 32 * i);
				this.field_20863[i].addCuboid(-8.0F, -8.0F, -8.0F, 16, 16, 16);
			}
		}

		@Override
		public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
			this.field_20863[this.field_20864].render(scale);
		}

		public void method_19333(int i) {
			this.field_20864 = i;
		}
	}
}
