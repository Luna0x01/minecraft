package net.minecraft.block;

import com.google.common.base.Objects;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TripwireHookBlock extends Block {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	public static final BooleanProperty ATTACHED = BooleanProperty.of("attached");
	public static final BooleanProperty SUSPENDED = BooleanProperty.of("suspended");

	public TripwireHookBlock() {
		super(Material.DECORATION);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false).with(ATTACHED, false).with(SUSPENDED, false));
		this.setItemGroup(ItemGroup.REDSTONE);
		this.setTickRandomly(true);
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		return state.with(SUSPENDED, !World.isOpaque(view, pos.down()));
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
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean canBePlacedAdjacent(World world, BlockPos pos, Direction direction) {
		return direction.getAxis().isHorizontal() && world.getBlockState(pos.offset(direction.getOpposite())).getBlock().isFullCube();
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			if (world.getBlockState(pos.offset(direction)).getBlock().isFullCube()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		BlockState blockState = this.getDefaultState().with(POWERED, false).with(ATTACHED, false).with(SUSPENDED, false);
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
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (block != this) {
			if (this.canBePlacedAtPos(world, pos, state)) {
				Direction direction = state.get(FACING);
				if (!world.getBlockState(pos.offset(direction.getOpposite())).getBlock().isFullCube()) {
					this.dropAsItem(world, pos, state, 0);
					world.setAir(pos);
				}
			}
		}
	}

	public void update(World world, BlockPos pos, BlockState hookState, boolean beingRemoved, boolean updateNeighbors, int tripwireLength, BlockState otherState) {
		Direction direction = hookState.get(FACING);
		boolean bl = (Boolean)hookState.get(ATTACHED);
		boolean bl2 = (Boolean)hookState.get(POWERED);
		boolean bl3 = !World.isOpaque(world, pos.down());
		boolean bl4 = !beingRemoved;
		boolean bl5 = false;
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
				bl4 = false;
			} else {
				if (j == tripwireLength) {
					blockState = (BlockState)Objects.firstNonNull(otherState, blockState);
				}

				boolean bl6 = !(Boolean)blockState.get(TripwireBlock.DISARMED);
				boolean bl7 = (Boolean)blockState.get(TripwireBlock.POWERED);
				boolean bl8 = (Boolean)blockState.get(TripwireBlock.SUSPENDED);
				bl4 &= bl8 == bl3;
				bl5 |= bl6 && bl7;
				blockStates[j] = blockState;
				if (j == tripwireLength) {
					world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
					bl4 &= bl6;
				}
			}
		}

		bl4 &= i > 1;
		bl5 &= bl4;
		BlockState blockState2 = this.getDefaultState().with(ATTACHED, bl4).with(POWERED, bl5);
		if (i > 0) {
			BlockPos blockPos2 = pos.offset(direction, i);
			Direction direction2 = direction.getOpposite();
			world.setBlockState(blockPos2, blockState2.with(FACING, direction2), 3);
			this.updateNeighborsOnAxis(world, blockPos2, direction2);
			this.playSound(world, blockPos2, bl4, bl5, bl, bl2);
		}

		this.playSound(world, pos, bl4, bl5, bl, bl2);
		if (!beingRemoved) {
			world.setBlockState(pos, blockState2.with(FACING, direction), 3);
			if (updateNeighbors) {
				this.updateNeighborsOnAxis(world, pos, direction);
			}
		}

		if (bl != bl4) {
			for (int k = 1; k < i; k++) {
				BlockPos blockPos3 = pos.offset(direction, k);
				BlockState blockState3 = blockStates[k];
				if (blockState3 != null && world.getBlockState(blockPos3).getBlock() != Blocks.AIR) {
					world.setBlockState(blockPos3, blockState3.with(ATTACHED, bl4), 3);
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

	private void playSound(World world, BlockPos pos, boolean attached, boolean on, boolean detached, boolean off) {
		if (on && !off) {
			world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5, "random.click", 0.4F, 0.6F);
		} else if (!on && off) {
			world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5, "random.click", 0.4F, 0.5F);
		} else if (attached && !detached) {
			world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5, "random.click", 0.4F, 0.7F);
		} else if (!attached && detached) {
			world.playSound(
				(double)pos.getX() + 0.5, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5, "random.bowhit", 0.4F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F)
			);
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
	public void setBoundingBox(BlockView view, BlockPos pos) {
		float f = 0.1875F;
		switch ((Direction)view.getBlockState(pos).get(FACING)) {
			case EAST:
				this.setBoundingBox(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
				break;
			case WEST:
				this.setBoundingBox(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
				break;
			case SOUTH:
				this.setBoundingBox(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
				break;
			case NORTH:
				this.setBoundingBox(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
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
	public int getWeakRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		if (!(Boolean)state.get(POWERED)) {
			return 0;
		} else {
			return state.get(FACING) == facing ? 15 : 0;
		}
	}

	@Override
	public boolean emitsRedstonePower() {
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
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, POWERED, ATTACHED, SUSPENDED);
	}
}
