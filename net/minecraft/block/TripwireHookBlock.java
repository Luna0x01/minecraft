package net.minecraft.block;

import com.google.common.base.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TripwireHookBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.DIRECTION;
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	public static final BooleanProperty ATTACHED = BooleanProperty.of("attached");
	protected static final Box field_12819 = new Box(0.3125, 0.0, 0.625, 0.6875, 0.625, 1.0);
	protected static final Box field_12820 = new Box(0.3125, 0.0, 0.0, 0.6875, 0.625, 0.375);
	protected static final Box field_12821 = new Box(0.625, 0.0, 0.3125, 1.0, 0.625, 0.6875);
	protected static final Box field_12822 = new Box(0.0, 0.0, 0.3125, 0.375, 0.625, 0.6875);

	public TripwireHookBlock() {
		super(Material.DECORATION);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false).with(ATTACHED, false));
		this.setItemGroup(ItemGroup.REDSTONE);
		this.setTickRandomly(true);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		switch ((Direction)state.get(FACING)) {
			case EAST:
			default:
				return field_12822;
			case WEST:
				return field_12821;
			case SOUTH:
				return field_12820;
			case NORTH:
				return field_12819;
		}
	}

	@Nullable
	@Override
	public Box getCollisionBox(BlockState state, World world, BlockPos pos) {
		return EMPTY_BOX;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canBePlacedAdjacent(World world, BlockPos pos, Direction direction) {
		return direction.getAxis().isHorizontal() && world.getBlockState(pos.offset(direction.getOpposite())).method_11734();
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			if (world.getBlockState(pos.offset(direction)).method_11734()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		BlockState blockState = this.getDefaultState().with(POWERED, false).with(ATTACHED, false);
		if (dir.getAxis().isHorizontal()) {
			blockState = blockState.with(FACING, dir);
		}

		return blockState;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		this.update(world, pos, state, false, false, -1, null);
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (block != this) {
			if (this.canBePlacedAtPos(world, blockPos, blockState)) {
				Direction direction = blockState.get(FACING);
				if (!world.getBlockState(blockPos.offset(direction.getOpposite())).method_11734()) {
					this.dropAsItem(world, blockPos, blockState, 0);
					world.setAir(blockPos);
				}
			}
		}
	}

	public void update(
		World world, BlockPos pos, BlockState hookState, boolean beingRemoved, boolean updateNeighbors, int tripwireLength, @Nullable BlockState otherState
	) {
		Direction direction = hookState.get(FACING);
		boolean bl = (Boolean)hookState.get(ATTACHED);
		boolean bl2 = (Boolean)hookState.get(POWERED);
		boolean bl3 = !beingRemoved;
		boolean bl4 = false;
		int i = 0;
		BlockState[] blockStates = new BlockState[42];

		for (int j = 1; j < 42; j++) {
			BlockPos blockPos = pos.offset(direction, j);
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.getBlock() == Blocks.TRIPWIRE_HOOK) {
				if (blockState.get(FACING) == direction.getOpposite()) {
					i = j;
				}
				break;
			}

			if (blockState.getBlock() != Blocks.TRIPWIRE && j != tripwireLength) {
				blockStates[j] = null;
				bl3 = false;
			} else {
				if (j == tripwireLength) {
					blockState = (BlockState)Objects.firstNonNull(otherState, blockState);
				}

				boolean bl5 = !(Boolean)blockState.get(TripwireBlock.DISARMED);
				boolean bl6 = (Boolean)blockState.get(TripwireBlock.POWERED);
				bl4 |= bl5 && bl6;
				blockStates[j] = blockState;
				if (j == tripwireLength) {
					world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
					bl3 &= bl5;
				}
			}
		}

		bl3 &= i > 1;
		bl4 &= bl3;
		BlockState blockState2 = this.getDefaultState().with(ATTACHED, bl3).with(POWERED, bl4);
		if (i > 0) {
			BlockPos blockPos2 = pos.offset(direction, i);
			Direction direction2 = direction.getOpposite();
			world.setBlockState(blockPos2, blockState2.with(FACING, direction2), 3);
			this.updateNeighborsOnAxis(world, blockPos2, direction2);
			this.method_11641(world, blockPos2, bl3, bl4, bl, bl2);
		}

		this.method_11641(world, pos, bl3, bl4, bl, bl2);
		if (!beingRemoved) {
			world.setBlockState(pos, blockState2.with(FACING, direction), 3);
			if (updateNeighbors) {
				this.updateNeighborsOnAxis(world, pos, direction);
			}
		}

		if (bl != bl3) {
			for (int k = 1; k < i; k++) {
				BlockPos blockPos3 = pos.offset(direction, k);
				BlockState blockState3 = blockStates[k];
				if (blockState3 != null && world.getBlockState(blockPos3).getBlock() != Blocks.AIR) {
					world.setBlockState(blockPos3, blockState3.with(ATTACHED, bl3), 3);
				}
			}
		}
	}

	@Override
	public void onRandomTick(World world, BlockPos pos, BlockState state, Random rand) {
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		this.update(world, pos, state, false, true, -1, null);
	}

	private void method_11641(World world, BlockPos blockPos, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		if (bl2 && !bl4) {
			world.method_11486(null, blockPos, Sounds.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4F, 0.6F);
		} else if (!bl2 && bl4) {
			world.method_11486(null, blockPos, Sounds.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4F, 0.5F);
		} else if (bl && !bl3) {
			world.method_11486(null, blockPos, Sounds.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4F, 0.7F);
		} else if (!bl && bl3) {
			world.method_11486(null, blockPos, Sounds.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
		}
	}

	private void updateNeighborsOnAxis(World world, BlockPos pos, Direction dir) {
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.offset(dir.getOpposite()), this);
	}

	private boolean canBePlacedAtPos(World world, BlockPos pos, BlockState state) {
		if (!this.canBePlacedAtPos(world, pos)) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		boolean bl = (Boolean)state.get(ATTACHED);
		boolean bl2 = (Boolean)state.get(POWERED);
		if (bl || bl2) {
			this.update(world, pos, state, true, false, -1, null);
		}

		if (bl2) {
			world.updateNeighborsAlways(pos, this);
			world.updateNeighborsAlways(pos.offset(((Direction)state.get(FACING)).getOpposite()), this);
		}

		super.onBreaking(world, pos, state);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!(Boolean)state.get(POWERED)) {
			return 0;
		} else {
			return state.get(FACING) == direction ? 15 : 0;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(FACING, Direction.fromHorizontal(data & 3)).with(POWERED, (data & 8) > 0).with(ATTACHED, (data & 4) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getHorizontal();
		if ((Boolean)state.get(POWERED)) {
			i |= 8;
		}

		if ((Boolean)state.get(ATTACHED)) {
			i |= 4;
		}

		return i;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, POWERED, ATTACHED);
	}
}
