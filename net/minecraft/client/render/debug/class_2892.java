package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

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
		BlockPos blockPos = this.client.player.getBlockPos();
		World world = this.client.player.world;
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.color(0.0F, 1.0F, 0.0F, 0.75F);
		GlStateManager.disableTexture();
		GlStateManager.method_12304(6.0F);

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			BlockState blockState = world.getBlockState(blockPos2);
			if (blockState.getBlock() == Blocks.WATER || blockState.getBlock() == Blocks.FLOWING_WATER) {
				double d = (double)AbstractFluidBlock.method_13710(blockState, world, blockPos2);
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
			BlockState blockState2 = world.getBlockState(blockPos3);
			if (blockState2.getBlock() == Blocks.WATER || blockState2.getBlock() == Blocks.FLOWING_WATER) {
				Integer integer = blockState2.get(AbstractFluidBlock.LEVEL);
				double e = integer > 7 ? 0.9 : 1.0 - 0.11 * (double)integer.intValue();
				String string = blockState2.getBlock() == Blocks.FLOWING_WATER ? "f" : "s";
				DebugRenderer.method_13450(
					string + " " + integer, (double)blockPos3.getX() + 0.5, (double)blockPos3.getY() + e, (double)blockPos3.getZ() + 0.5, tickDelta, -16777216
				);
			}
		}

		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
