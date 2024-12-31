package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class AirBlock extends Block {
	protected AirBlock() {
		super(Material.AIR);
	}

	@Override
	public int getBlockType() {
		return -1;
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean canCollide(BlockState state, boolean bl) {
		return false;
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
	}

	@Override
	public boolean isReplaceable(World world, BlockPos pos) {
		return true;
	}
}
