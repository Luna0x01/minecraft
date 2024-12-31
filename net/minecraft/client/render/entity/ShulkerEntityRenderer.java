package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.CameraView;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.ShulkerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ShulkerEntityRenderer extends MobEntityRenderer<ShulkerEntity> {
	private static final Identifier field_13635 = new Identifier("textures/entity/shulker/endergolem.png");
	private int field_13636;

	public ShulkerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ShulkerEntityModel shulkerEntityModel) {
		super(entityRenderDispatcher, shulkerEntityModel, 0.0F);
		this.addFeature(new ShulkerEntityRenderer.class_2897());
		this.field_13636 = shulkerEntityModel.method_12225();
		this.shadowSize = 0.0F;
	}

	public void render(ShulkerEntity shulkerEntity, double d, double e, double f, float g, float h) {
		if (this.field_13636 != ((ShulkerEntityModel)this.model).method_12225()) {
			this.model = new ShulkerEntityModel();
			this.field_13636 = ((ShulkerEntityModel)this.model).method_12225();
		}

		int i = shulkerEntity.method_13229();
		if (i > 0 && shulkerEntity.method_13231()) {
			BlockPos blockPos = shulkerEntity.method_13227();
			BlockPos blockPos2 = shulkerEntity.method_13230();
			double j = (double)((float)i - h) / 6.0;
			j *= j;
			double k = (double)(blockPos.getX() - blockPos2.getX()) * j;
			double l = (double)(blockPos.getY() - blockPos2.getY()) * j;
			double m = (double)(blockPos.getZ() - blockPos2.getZ()) * j;
			super.render(shulkerEntity, d - k, e - l, f - m, g, h);
		} else {
			super.render(shulkerEntity, d, e, f, g, h);
		}
	}

	public boolean shouldRender(ShulkerEntity shulkerEntity, CameraView cameraView, double d, double e, double f) {
		if (super.shouldRender(shulkerEntity, cameraView, d, e, f)) {
			return true;
		} else {
			if (shulkerEntity.method_13229() > 0 && shulkerEntity.method_13231()) {
				BlockPos blockPos = shulkerEntity.method_13230();
				BlockPos blockPos2 = shulkerEntity.method_13227();
				Vec3d vec3d = new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
				Vec3d vec3d2 = new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
				if (cameraView.isBoxInFrustum(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z))) {
					return true;
				}
			}

			return false;
		}
	}

	protected Identifier getTexture(ShulkerEntity shulkerEntity) {
		return field_13635;
	}

	protected void method_5777(ShulkerEntity shulkerEntity, float f, float g, float h) {
		super.method_5777(shulkerEntity, f, g, h);
		switch (shulkerEntity.method_13226()) {
			case DOWN:
			default:
				break;
			case EAST:
				GlStateManager.translate(0.5F, 0.5F, 0.0F);
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
				break;
			case WEST:
				GlStateManager.translate(-0.5F, 0.5F, 0.0F);
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
				break;
			case NORTH:
				GlStateManager.translate(0.0F, 0.5F, -0.5F);
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				break;
			case SOUTH:
				GlStateManager.translate(0.0F, 0.5F, 0.5F);
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				break;
			case UP:
				GlStateManager.translate(0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		}
	}

	protected void scale(ShulkerEntity shulkerEntity, float f) {
		float g = 0.999F;
		GlStateManager.scale(0.999F, 0.999F, 0.999F);
	}

	class class_2897 implements FeatureRenderer<ShulkerEntity> {
		private class_2897() {
		}

		public void render(ShulkerEntity shulkerEntity, float f, float g, float h, float i, float j, float k, float l) {
			GlStateManager.pushMatrix();
			switch (shulkerEntity.method_13226()) {
				case DOWN:
				default:
					break;
				case EAST:
					GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.translate(1.0F, -1.0F, 0.0F);
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
					break;
				case WEST:
					GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.translate(-1.0F, -1.0F, 0.0F);
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
					break;
				case NORTH:
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.translate(0.0F, -1.0F, -1.0F);
					break;
				case SOUTH:
					GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.translate(0.0F, -1.0F, 1.0F);
					break;
				case UP:
					GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.translate(0.0F, -2.0F, 0.0F);
			}

			ModelPart modelPart = ((ShulkerEntityModel)ShulkerEntityRenderer.this.getModel()).field_13395;
			modelPart.posY = j * (float) (Math.PI / 180.0);
			modelPart.posX = k * (float) (Math.PI / 180.0);
			ShulkerEntityRenderer.this.bindTexture(ShulkerEntityRenderer.field_13635);
			modelPart.render(l);
			GlStateManager.popMatrix();
		}

		@Override
		public boolean combineTextures() {
			return false;
		}
	}
}
