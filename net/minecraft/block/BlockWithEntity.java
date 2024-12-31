package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class BlockWithEntity extends Block implements BlockEntityProvider {
	protected BlockWithEntity(Material material) {
		this(material, material.getColor());
	}

	protected BlockWithEntity(Material material, MaterialColor materialColor) {
		super(material, materialColor);
		this.blockEntity = true;
	}

	protected boolean isAdjacentToCactus(World world, BlockPos pos, Direction dir) {
		return world.getBlockState(pos.offset(dir)).getBlock().getMaterial() == Material.CACTUS;
	}

	protected boolean isAdjacentToCactus(World world, BlockPos pos) {
		return this.isAdjacentToCactus(world, pos, Direction.NORTH)
			|| this.isAdjacentToCactus(world, pos, Direction.SOUTH)
			|| this.isAdjacentToCactus(world, pos, Direction.WEST)
			|| this.isAdjacentToCactus(world, pos, Direction.EAST);
	}

	@Override
	public int getBlockType() {
		return -1;
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		super.onBreaking(world, pos, state);
		world.removeBlockEntity(pos);
	}

	@Override
	public boolean onEvent(World world, BlockPos pos, BlockState state, int id, int data) {
		super.onEvent(world, pos, state, id, data);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity == null ? false : blockEntity.onBlockAction(id, data);
	}
}
