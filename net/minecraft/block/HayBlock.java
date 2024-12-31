package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HayBlock extends PillarBlock {
	public HayBlock() {
		super(Material.GRASS, MaterialColor.YELLOW);
		this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.Y));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public BlockState stateFromData(int data) {
		Direction.Axis axis = Direction.Axis.Y;
		int i = data & 12;
		if (i == 4) {
			axis = Direction.Axis.X;
		} else if (i == 8) {
			axis = Direction.Axis.Z;
		}

		return this.getDefaultState().with(AXIS, axis);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		Direction.Axis axis = state.get(AXIS);
		if (axis == Direction.Axis.X) {
			i |= 4;
		} else if (axis == Direction.Axis.Z) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, AXIS);
	}

	@Override
	protected ItemStack createStackFromBlock(BlockState state) {
		return new ItemStack(Item.fromBlock(this), 1, 0);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return super.getStateFromData(world, pos, dir, x, y, z, id, entity).with(AXIS, dir.getAxis());
	}
}
