package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class GlowSquidEntityRenderer extends SquidEntityRenderer<GlowSquidEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/squid/glow_squid.png");

	public GlowSquidEntityRenderer(EntityRendererFactory.Context context, SquidEntityModel<GlowSquidEntity> squidEntityModel) {
		super(context, squidEntityModel);
	}

	public Identifier getTexture(GlowSquidEntity glowSquidEntity) {
		return TEXTURE;
	}

	protected int getBlockLight(GlowSquidEntity glowSquidEntity, BlockPos blockPos) {
		int i = (int)MathHelper.method_37166(0.0F, 15.0F, 1.0F - (float)glowSquidEntity.getDarkTicksRemaining() / 10.0F);
		return i == 15 ? 15 : Math.max(i, super.getBlockLight(glowSquidEntity, blockPos));
	}
}
