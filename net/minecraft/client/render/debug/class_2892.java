package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.RenderBlockView;

public class class_2892 implements DebugRenderer.DebugRenderable {
	private final MinecraftClient client;
	private PlayerEntity player;
	private double field_14982;
	private double field_14983;
	private double field_14984;

	public class_2892(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(float tickDelta, long limitTime) {
		this.player = this.client.player;
		this.field_14982 = this.player.prevTickX + (this.player.x - this.player.prevTickX) * (double)tickDelta;
		this.field_14983 = this.player.prevTickY + (this.player.y - this.player.prevTickY) * (double)tickDelta;
		this.field_14984 = this.player.prevTickZ + (this.player.z - this.player.prevTickZ) * (double)tickDelta;
		BlockPos blockPos = this.client.player.method_4086();
		RenderBlockView renderBlockView = this.client.player.world;
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.color(0.0F, 1.0F, 0.0F, 0.75F);
		GlStateManager.disableTexture();
		GlStateManager.method_12304(6.0F);

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			FluidState fluidState = renderBlockView.getFluidState(blockPos2);
			if (fluidState.matches(FluidTags.WATER)) {
				double d = (double)((float)blockPos2.getY() + fluidState.method_17810());
				WorldRenderer.method_13433(
					new Box(
							(double)((float)blockPos2.getX() + 0.01F),
							(double)((float)blockPos2.getY() + 0.01F),
							(double)((float)blockPos2.getZ() + 0.01F),
							(double)((float)blockPos2.getX() + 0.99F),
							d,
							(double)((float)blockPos2.getZ() + 0.99F)
						)
						.offset(-this.field_14982, -this.field_14983, -this.field_14984),
					1.0F,
					1.0F,
					1.0F,
					0.2F
				);
			}
		}

		for (BlockPos blockPos3 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			FluidState fluidState2 = renderBlockView.getFluidState(blockPos3);
			if (fluidState2.matches(FluidTags.WATER)) {
				DebugRenderer.method_13450(
					String.valueOf(fluidState2.method_17811()),
					(double)blockPos3.getX() + 0.5,
					(double)((float)blockPos3.getY() + fluidState2.method_17810()),
					(double)blockPos3.getZ() + 0.5,
					tickDelta,
					-16777216
				);
			}
		}

		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
