package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LilyPadBlock extends PlantBlock {
	protected LilyPadBlock() {
		float f = 0.5F;
		float g = 0.015625F;
		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, g, 0.5F + f);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public void appendCollisionBoxes(World world, BlockPos pos, BlockState state, Box box, List<Box> list, Entity entity) {
		if (entity == null || !(entity instanceof BoatEntity)) {
			super.appendCollisionBoxes(world, pos, state, box, list, entity);
		}
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return new Box(
			(double)pos.getX() + this.boundingBoxMinX,
			(double)pos.getY() + this.boundingBoxMinY,
			(double)pos.getZ() + this.boundingBoxMinZ,
			(double)pos.getX() + this.boundingBoxMaxX,
			(double)pos.getY() + this.boundingBoxMaxY,
			(double)pos.getZ() + this.boundingBoxMaxZ
		);
	}

	@Override
	public int getColor() {
		return 7455580;
	}

	@Override
	public int getColor(BlockState state) {
		return 7455580;
	}

	@Override
	public int getBlockColor(BlockView view, BlockPos pos, int id) {
		return 2129968;
	}

	@Override
	protected boolean canPlantOnTop(Block block) {
		return block == Blocks.WATER;
	}

	@Override
	public boolean canPlantAt(World world, BlockPos pos, BlockState state) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			BlockState blockState = world.getBlockState(pos.down());
			return blockState.getBlock().getMaterial() == Material.WATER && (Integer)blockState.get(AbstractFluidBlock.LEVEL) == 0;
		} else {
			return false;
		}
	}

	@Override
	public int getData(BlockState state) {
		return 0;
	}
}
