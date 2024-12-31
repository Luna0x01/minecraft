package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;

public class ElderGuardianEntityRenderer extends GuardianEntityRenderer {
	private static final Identifier TEXTURE = new Identifier("textures/entity/guardian_elder.png");

	public ElderGuardianEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	protected void scale(GuardianEntity guardianEntity, float f) {
		GlStateManager.scale(2.35F, 2.35F, 2.35F);
	}

	@Override
	protected Identifier getTexture(GuardianEntity guardianEntity) {
		return TEXTURE;
	}
}
