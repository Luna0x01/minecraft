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
	public int getBlockType() {
		return -1;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public float getAmbientOcclusionLightLevel() {
		return 1.0F;
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
	}
}
