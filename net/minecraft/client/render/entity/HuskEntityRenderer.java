package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

public class HuskEntityRenderer extends ZombieBaseEntityRenderer {
	private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/husk.png");

	public HuskEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	protected void scale(ZombieEntity zombieEntity, float f) {
		float g = 1.0625F;
		GlStateManager.scale(1.0625F, 1.0625F, 1.0625F);
		super.scale(zombieEntity, f);
	}

	@Override
	protected Identifier getTexture(ZombieEntity zombieEntity) {
		return TEXTURE;
	}
}
