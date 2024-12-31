package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;

public class ConnectingBlock extends Block {
	private static final Direction[] DIRECTIONS = Direction.values();
	public static final BooleanProperty NORTH = Properties.NORTH;
	public static final BooleanProperty EAST = Properties.EAST;
	public static final BooleanProperty SOUTH = Properties.SOUTH;
	public static final BooleanProperty WEST = Properties.WEST;
	public static final BooleanProperty UP = Properties.UP;
	public static final BooleanProperty DOWN = Properties.DOWN;
	public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY = Util.make(Maps.newEnumMap(Direction.class), directions -> {
		directions.put(Direction.NORTH, NORTH);
		directions.put(Direction.EAST, EAST);
		directions.put(Direction.SOUTH, SOUTH);
		directions.put(Direction.WEST, WEST);
		directions.put(Direction.UP, UP);
		directions.put(Direction.DOWN, DOWN);
	});
	protected final VoxelShape[] connectionsToShape;

	protected ConnectingBlock(float f, Block.Builder builder) {
		super(builder);
		this.connectionsToShape = this.generateFacingsToShapeMap(f);
	}

	private VoxelShape[] generateFacingsToShapeMap(float radius) {
		float f = 0.5F - radius;
		float g = 0.5F + radius;
		VoxelShape voxelShape = Block.createCuboidShape(
			(double)(f * 16.0F), (double)(f * 16.0F), (double)(f * 16.0F), (double)(g * 16.0F), (double)(g * 16.0F), (double)(g * 16.0F)
		);
		VoxelShape[] voxelShapes = new VoxelShape[DIRECTIONS.length];

		for (int i = 0; i < DIRECTIONS.length; i++) {
			Direction direction = DIRECTIONS[i];
			voxelShapes[i] = VoxelShapes.cuboid(
				0.5 + Math.min((double)(-radius), (double)direction.getOffsetX() * 0.5),
				0.5 + Math.min((double)(-radius), (double)direction.getOffsetY() * 0.5),
				0.5 + Math.min((double)(-radius), (double)direction.getOffsetZ() * 0.5),
				0.5 + Math.max((double)radius, (double)direction.getOffsetX() * 0.5),
				0.5 + Math.max((double)radius, (double)direction.getOffsetY() * 0.5),
				0.5 + Math.max((double)radius, (double)direction.getOffsetZ() * 0.5)
			);
		}

		VoxelShape[] voxelShapes2 = new VoxelShape[64];

		for (int j = 0; j < 64; j++) {
			VoxelShape voxelShape2 = voxelShape;

			for (int k = 0; k < DIRECTIONS.length; k++) {
				if ((j & 1 << k) != 0) {
					voxelShape2 = VoxelShapes.union(voxelShape2, voxelShapes[k]);
				}
			}

			voxelShapes2[j] = voxelShape2;
		}

		return voxelShapes2;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return this.connectionsToShape[this.getConnectionMask(state)];
	}

	protected int getConnectionMask(BlockState state) {
		int i = 0;

		for (int j = 0; j < DIRECTIONS.length; j++) {
			if ((Boolean)state.getProperty((Property)FACING_TO_PROPERTY.get(DIRECTIONS[j]))) {
				i |= 1 << j;
			}
		}

		return i;
	}
}
