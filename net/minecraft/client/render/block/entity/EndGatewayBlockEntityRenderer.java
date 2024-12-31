package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EndGatewayBlockEntityRenderer extends EndPortalBlockEntityRenderer {
	private static final Identifier TEXTURE = new Identifier("textures/entity/end_gateway_beam.png");

	@Override
	public void method_1631(EndPortalBlockEntity endPortalBlockEntity, double d, double e, double f, float g, int i) {
		GlStateManager.disableFog();
		EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)endPortalBlockEntity;
		if (endGatewayBlockEntity.method_11692() || endGatewayBlockEntity.hasCooldown()) {
			GlStateManager.alphaFunc(516, 0.1F);
			this.method_19327(TEXTURE);
			float h = endGatewayBlockEntity.method_11692() ? endGatewayBlockEntity.method_13746(g) : endGatewayBlockEntity.method_13747(g);
			double j = endGatewayBlockEntity.method_11692() ? 256.0 - e : 50.0;
			h = MathHelper.sin(h * (float) Math.PI);
			int k = MathHelper.floor((double)h * j);
			float[] fs = endGatewayBlockEntity.method_11692() ? DyeColor.MAGENTA.getColorComponents() : DyeColor.PURPLE.getColorComponents();
			BeaconBlockEntityRenderer.method_12407(d, e, f, (double)g, (double)h, endGatewayBlockEntity.getEntityWorld().getLastUpdateTime(), 0, k, fs, 0.15, 0.175);
			BeaconBlockEntityRenderer.method_12407(d, e, f, (double)g, (double)h, endGatewayBlockEntity.getEntityWorld().getLastUpdateTime(), 0, -k, fs, 0.15, 0.175);
		}

		super.method_1631(endPortalBlockEntity, d, e, f, g, i);
		GlStateManager.enableFog();
	}

	@Override
	protected int method_13854(double d) {
		return super.method_13854(d) + 1;
	}

	@Override
	protected float method_13855() {
		return 1.0F;
	}
}
