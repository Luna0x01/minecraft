package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.LlamaSpitModel;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.Identifier;

public class LlamaSpitEntityRenderer extends EntityRenderer<LlamaSpitEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/llama/spit.png");
	private final LlamaSpitModel model = new LlamaSpitModel();

	public LlamaSpitEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void render(LlamaSpitEntity llamaSpitEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)d, (float)e + 0.15F, (float)f);
		GlStateManager.rotate(llamaSpitEntity.prevYaw + (llamaSpitEntity.yaw - llamaSpitEntity.prevYaw) * h - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(llamaSpitEntity.prevPitch + (llamaSpitEntity.pitch - llamaSpitEntity.prevPitch) * h, 0.0F, 0.0F, 1.0F);
		this.bindTexture(llamaSpitEntity);
		if (this.field_13631) {
			GlStateManager.enableColorMaterial();
			GlStateManager.method_12309(this.method_12454(llamaSpitEntity));
		}

		this.model.render(llamaSpitEntity, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		if (this.field_13631) {
			GlStateManager.method_12315();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.render(llamaSpitEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(LlamaSpitEntity llamaSpitEntity) {
		return TEXTURE;
	}
}
