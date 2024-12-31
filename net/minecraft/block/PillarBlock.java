package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PillarBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.of("axis", Direction.Axis.class);

	protected PillarBlock(Material material) {
		super(material, material.getColor());
	}

	protected PillarBlock(Material material, MaterialColor materialColor) {
		super(material, materialColor);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch ((Direction.Axis)state.get(AXIS)) {
					case X:
						return state.with(AXIS, Direction.Axis.Z);
					case Z:
						return state.with(AXIS, Direction.Axis.X);
					default:
						return state;
				}
			default:
				return state;
		}
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
		return new ItemStack(Item.fromBlock(this));
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return super.getStateFromData(world, pos, dir, x, y, z, id, entity).with(AXIS, dir.getAxis());
	}
}
