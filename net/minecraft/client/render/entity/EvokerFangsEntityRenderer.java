package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;

public class EvokerFangsEntityRenderer extends EntityRenderer<EvokerFangsEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/fangs.png");
	private final EvokerFangsEntityModel model = new EvokerFangsEntityModel();

	public EvokerFangsEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void render(EvokerFangsEntity evokerFangsEntity, double d, double e, double f, float g, float h) {
		float i = evokerFangsEntity.getAnimationProgress(h);
		if (i != 0.0F) {
			float j = 2.0F;
			if (i > 0.9F) {
				j = (float)((double)j * ((1.0 - (double)i) / 0.1F));
			}

			GlStateManager.pushMatrix();
			GlStateManager.disableCull();
			GlStateManager.enableAlphaTest();
			this.bindTexture(evokerFangsEntity);
			GlStateManager.translate((float)d, (float)e, (float)f);
			GlStateManager.rotate(90.0F - evokerFangsEntity.yaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.scale(-j, -j, j);
			float k = 0.03125F;
			GlStateManager.translate(0.0F, -0.626F, 0.0F);
			this.model.render(evokerFangsEntity, i, 0.0F, 0.0F, evokerFangsEntity.yaw, evokerFangsEntity.pitch, 0.03125F);
			GlStateManager.popMatrix();
			GlStateManager.enableCull();
			super.render(evokerFangsEntity, d, e, f, g, h);
		}
	}

	protected Identifier getTexture(EvokerFangsEntity evokerFangsEntity) {
		return TEXTURE;
	}
}
