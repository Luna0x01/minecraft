package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.Identifier;

public class WitherSkullEntityRenderer extends EntityRenderer<WitherSkullEntity> {
	private static final Identifier field_6535 = new Identifier("textures/entity/wither/wither_invulnerable.png");
	private static final Identifier field_6536 = new Identifier("textures/entity/wither/wither.png");
	private final SkullEntityModel field_5214 = new SkullEntityModel();

	public WitherSkullEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	private float method_4353(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}

	public void render(WitherSkullEntity witherSkullEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		float i = this.method_4353(witherSkullEntity.prevYaw, witherSkullEntity.yaw, h);
		float j = witherSkullEntity.prevPitch + (witherSkullEntity.pitch - witherSkullEntity.prevPitch) * h;
		GlStateManager.translate((float)d, (float)e, (float)f);
		float k = 0.0625F;
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlphaTest();
		this.bindTexture(witherSkullEntity);
		if (this.field_13631) {
			GlStateManager.enableColorMaterial();
			GlStateManager.method_12309(this.method_12454(witherSkullEntity));
		}

		this.field_5214.render(witherSkullEntity, 0.0F, 0.0F, 0.0F, i, j, 0.0625F);
		if (this.field_13631) {
			GlStateManager.method_12315();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.render(witherSkullEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(WitherSkullEntity witherSkullEntity) {
		return witherSkullEntity.isCharged() ? field_6535 : field_6536;
	}
}
