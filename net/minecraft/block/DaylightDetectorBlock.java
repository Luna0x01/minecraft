package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class DaylightDetectorBlock extends BlockWithEntity {
	public static final IntProperty POWER = Properties.POWER;
	public static final BooleanProperty INVERTED = Properties.INVERTED;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

	public DaylightDetectorBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(POWER, Integer.valueOf(0)).withProperty(INVERTED, Boolean.valueOf(false)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return (Integer)state.getProperty(POWER);
	}

	public static void updateState(BlockState state, World world, BlockPos pos) {
		if (world.dimension.isOverworld()) {
			int i = world.method_16370(LightType.SKY, pos) - world.method_8520();
			float f = world.getSkyAngleRadians(1.0F);
			boolean bl = (Boolean)state.getProperty(INVERTED);
			if (bl) {
				i = 15 - i;
			} else if (i > 0) {
				float g = f < (float) Math.PI ? 0.0F : (float) (Math.PI * 2);
				f += (g - f) * 0.2F;
				i = Math.round((float)i * MathHelper.cos(f));
			}

			i = MathHelper.clamp(i, 0, 15);
			if ((Integer)state.getProperty(POWER) != i) {
				world.setBlockState(pos, state.withProperty(POWER, Integer.valueOf(i)), 3);
			}
		}
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (player.canModifyWorld()) {
			if (world.isClient) {
				return true;
			} else {
				BlockState blockState = state.method_16930(INVERTED);
				world.setBlockState(pos, blockState, 4);
				updateState(blockState, world, pos);
				return true;
			}
		} else {
			return super.onUse(state, world, pos, player, hand, direction, distanceX, distanceY, distanceZ);
		}
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new DaylightDetectorBlockEntity();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(POWER, INVERTED);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction == Direction.DOWN ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
	}
}
