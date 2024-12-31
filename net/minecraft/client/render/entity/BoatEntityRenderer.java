package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BoatEntityRenderer extends EntityRenderer<BoatEntity> {
	private static final Identifier[] SKIN = new Identifier[]{
		new Identifier("textures/entity/boat/oak.png"),
		new Identifier("textures/entity/boat/spruce.png"),
		new Identifier("textures/entity/boat/birch.png"),
		new Identifier("textures/entity/boat/jungle.png"),
		new Identifier("textures/entity/boat/acacia.png"),
		new Identifier("textures/entity/boat/dark_oak.png")
	};
	protected final BoatEntityModel model = new BoatEntityModel();

	public BoatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.8F;
	}

	public void method_3888(BoatEntity boatEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.translateToBoat(d, e, f);
		this.rotateToBoat(boatEntity, g, h);
		this.bindEntityTexture(boatEntity);
		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(boatEntity));
		}

		this.model.method_17071(boatEntity, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		if (this.renderOutlines) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.render(boatEntity, d, e, f, g, h);
	}

	public void rotateToBoat(BoatEntity boatEntity, float f, float g) {
		GlStateManager.rotatef(180.0F - f, 0.0F, 1.0F, 0.0F);
		float h = (float)boatEntity.getDamageWobbleTicks() - g;
		float i = boatEntity.getDamageWobbleStrength() - g;
		if (i < 0.0F) {
			i = 0.0F;
		}

		if (h > 0.0F) {
			GlStateManager.rotatef(MathHelper.sin(h) * h * i / 10.0F * (float)boatEntity.getDamageWobbleSide(), 1.0F, 0.0F, 0.0F);
		}

		float j = boatEntity.interpolateBubbleWobble(g);
		if (!MathHelper.equalsApproximate(j, 0.0F)) {
			GlStateManager.rotatef(boatEntity.interpolateBubbleWobble(g), 1.0F, 0.0F, 1.0F);
		}

		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
	}

	public void translateToBoat(double d, double e, double f) {
		GlStateManager.translatef((float)d, (float)e + 0.375F, (float)f);
	}

	protected Identifier method_3891(BoatEntity boatEntity) {
		return SKIN[boatEntity.getBoatType().ordinal()];
	}

	@Override
	public boolean hasSecondPass() {
		return true;
	}

	public void method_3887(BoatEntity boatEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.translateToBoat(d, e, f);
		this.rotateToBoat(boatEntity, g, h);
		this.bindEntityTexture(boatEntity);
		this.model.renderPass(boatEntity, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}
}
