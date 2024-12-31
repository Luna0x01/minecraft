package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.LeashEntityModel;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.util.Identifier;

public class LeashKnotEntityRenderer extends EntityRenderer<LeashKnotEntity> {
	private static final Identifier field_6500 = new Identifier("textures/entity/lead_knot.png");
	private LeashEntityModel field_6501 = new LeashEntityModel();

	public LeashKnotEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void render(LeashKnotEntity leashKnotEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.translate((float)d, (float)e, (float)f);
		float i = 0.0625F;
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlphaTest();
		this.bindTexture(leashKnotEntity);
		if (this.field_13631) {
			GlStateManager.enableColorMaterial();
			GlStateManager.method_12309(this.method_12454(leashKnotEntity));
		}

		this.field_6501.render(leashKnotEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, i);
		if (this.field_13631) {
			GlStateManager.method_12315();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.render(leashKnotEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(LeashKnotEntity leashKnotEntity) {
		return field_6500;
	}
}
