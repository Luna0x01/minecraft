package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.CameraView;
import net.minecraft.client.render.entity.model.EnderCrystalEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EnderCrystalEntityRenderer extends EntityRenderer<EndCrystalEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/endercrystal/endercrystal.png");
	private final EntityModel model = new EnderCrystalEntityModel(0.0F, true);
	private final EntityModel field_13630 = new EnderCrystalEntityModel(0.0F, false);

	public EnderCrystalEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.shadowSize = 0.5F;
	}

	public void render(EndCrystalEntity endCrystalEntity, double d, double e, double f, float g, float h) {
		float i = (float)endCrystalEntity.endCrystalAge + h;
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)d, (float)e, (float)f);
		this.bindTexture(TEXTURE);
		float j = MathHelper.sin(i * 0.2F) / 2.0F + 0.5F;
		j = j * j + j;
		if (this.field_13631) {
			GlStateManager.enableColorMaterial();
			GlStateManager.method_12309(this.method_12454(endCrystalEntity));
		}

		if (endCrystalEntity.shouldShowBottom()) {
			this.model.render(endCrystalEntity, 0.0F, i * 3.0F, j * 0.2F, 0.0F, 0.0F, 0.0625F);
		} else {
			this.field_13630.render(endCrystalEntity, 0.0F, i * 3.0F, j * 0.2F, 0.0F, 0.0F, 0.0625F);
		}

		if (this.field_13631) {
			GlStateManager.method_12315();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		BlockPos blockPos = endCrystalEntity.getBeamTarget();
		if (blockPos != null) {
			this.bindTexture(EnderDragonEntityRenderer.CRYSTAL_BEAM_TEXTURE);
			float k = (float)blockPos.getX() + 0.5F;
			float l = (float)blockPos.getY() + 0.5F;
			float m = (float)blockPos.getZ() + 0.5F;
			EnderDragonEntityRenderer.method_12445(
				d,
				e - 1.3F + (double)(j * 0.4F),
				f,
				h,
				endCrystalEntity.x,
				endCrystalEntity.y,
				endCrystalEntity.z,
				endCrystalEntity.endCrystalAge,
				(double)k,
				(double)l,
				(double)m
			);
		}

		super.render(endCrystalEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(EndCrystalEntity endCrystalEntity) {
		return TEXTURE;
	}

	public boolean shouldRender(EndCrystalEntity endCrystalEntity, CameraView cameraView, double d, double e, double f) {
		return super.shouldRender(endCrystalEntity, cameraView, d, e, f) || endCrystalEntity.getBeamTarget() != null;
	}
}
