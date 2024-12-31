package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;

public class BlockParticle extends BlockDustParticle {
	protected BlockParticle(World world, double d, double e, double f, double g, double h, double i, BlockState blockState) {
		super(world, d, e, f, g, h, i, blockState);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
	}

	public static class Factory implements ParticleFactory {
		@Nullable
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			BlockState blockState = Block.getStateFromRawId(arr[0]);
			return blockState.getRenderType() == BlockRenderType.INVISIBLE
				? null
				: new BlockParticle(world, x, y, z, velocityX, velocityY, velocityZ, blockState).method_12262();
		}
	}
}
