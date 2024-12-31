package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BarrierBlock extends Block {
	protected BarrierBlock() {
		super(Material.BARRIER);
		this.setUnbreakable();
		this.setResistance(6000001.0F);
		this.disableStats();
		this.translucent = true;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state) {
		return 1.0F;
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
	}
}
