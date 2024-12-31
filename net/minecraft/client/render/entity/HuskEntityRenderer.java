package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

public class HuskEntityRenderer extends ZombieEntityRenderer {
	private static final Identifier SKIN = new Identifier("textures/entity/zombie/husk.png");

	public HuskEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	protected void method_3985(ZombieEntity zombieEntity, float f) {
		float g = 1.0625F;
		GlStateManager.scalef(1.0625F, 1.0625F, 1.0625F);
		super.scale(zombieEntity, f);
	}

	@Override
	protected Identifier method_4163(ZombieEntity zombieEntity) {
		return SKIN;
	}
}
