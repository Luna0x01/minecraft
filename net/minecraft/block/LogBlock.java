package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class LogBlock extends PillarBlock {
	public static final EnumProperty<LogBlock.Axis> LOG_AXIS = EnumProperty.of("axis", LogBlock.Axis.class);

	public LogBlock() {
		super(Material.WOOD);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
		this.setStrength(2.0F);
		this.setBlockSoundGroup(BlockSoundGroup.field_12759);
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		int i = 4;
		int j = 5;
		if (world.isRegionLoaded(pos.add(-5, -5, -5), pos.add(5, 5, 5))) {
			for (BlockPos blockPos : BlockPos.iterate(pos.add(-4, -4, -4), pos.add(4, 4, 4))) {
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getMaterial() == Material.FOLIAGE && !(Boolean)blockState.get(LeavesBlock.CHECK_DECAY)) {
					world.setBlockState(blockPos, blockState.with(LeavesBlock.CHECK_DECAY, true), 4);
				}
			}
		}
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.stateFromData(id).with(LOG_AXIS, LogBlock.Axis.getByDirectionAxis(dir.getAxis()));
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch ((LogBlock.Axis)state.get(LOG_AXIS)) {
					case X:
						return state.with(LOG_AXIS, LogBlock.Axis.Z);
					case Z:
						return state.with(LOG_AXIS, LogBlock.Axis.X);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	public static enum Axis implements StringIdentifiable {
		X("x"),
		Y("y"),
		Z("z"),
		NONE("none");

		private final String name;

		private Axis(String string2) {
			this.name = string2;
		}

		public String toString() {
			return this.name;
		}

		public static LogBlock.Axis getByDirectionAxis(Direction.Axis axis) {
			switch (axis) {
				case X:
					return X;
				case Y:
					return Y;
				case Z:
					return Z;
				default:
					return NONE;
			}
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
