package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_3086;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Identifier;

public class EvocationIllagerEntityRenderer extends MobEntityRenderer<HostileEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/evoker.png");

	public EvocationIllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_3086(0.0F), 0.5F);
	}

	protected Identifier getTexture(HostileEntity hostileEntity) {
		return TEXTURE;
	}

	protected void scale(HostileEntity hostileEntity, float f) {
		float g = 0.9375F;
		GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
	}
}
