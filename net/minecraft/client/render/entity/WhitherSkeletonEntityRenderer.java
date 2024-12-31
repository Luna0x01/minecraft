package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_3146;
import net.minecraft.util.Identifier;

public class WhitherSkeletonEntityRenderer extends SkeletonEntityRenderer {
	private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/wither_skeleton.png");

	public WhitherSkeletonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	@Override
	protected Identifier getTexture(class_3146 arg) {
		return TEXTURE;
	}

	protected void scale(class_3146 arg, float f) {
		GlStateManager.scale(1.2F, 1.2F, 1.2F);
	}
}
