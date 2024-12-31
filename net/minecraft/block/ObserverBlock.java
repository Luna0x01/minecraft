package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ObserverBlock extends FacingBlock {
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");

	public ObserverBlock() {
		super(Material.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.SOUTH).with(POWERED, false));
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, POWERED);
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
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if ((Boolean)state.get(POWERED)) {
			world.setBlockState(pos, state.with(POWERED, false), 2);
		} else {
			world.setBlockState(pos, state.with(POWERED, true), 2);
			world.createAndScheduleBlockTick(pos, this, 2);
		}

		this.method_13713(world, pos, state);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
	}

	public void method_13711(BlockState state, World world, BlockPos pos, Block block, BlockPos blockPos) {
		if (!world.isClient && pos.offset(state.get(FACING)).equals(blockPos)) {
			this.method_13712(state, world, pos);
		}
	}

	private void method_13712(BlockState state, World world, BlockPos pos) {
		if (!(Boolean)state.get(POWERED)) {
			if (!world.method_11489(pos, this)) {
				world.createAndScheduleBlockTick(pos, this, 2);
			}
		}
	}

	protected void method_13713(World world, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		world.updateNeighbor(blockPos, this, pos);
		world.updateNeighborsExcept(blockPos, this, direction);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getWeakRedstonePower(world, pos, direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) && state.get(FACING) == direction ? 15 : 0;
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		if (!world.isClient) {
			if ((Boolean)state.get(POWERED)) {
				this.onScheduledTick(world, pos, state, world.random);
			}

			this.method_13712(state, world, pos);
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		if ((Boolean)state.get(POWERED) && world.method_11489(pos, this)) {
			this.method_13713(world, pos, state.with(POWERED, false));
		}
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(FACING, Direction.getLookingDirection(pos, entity).getOpposite());
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getId();
		if ((Boolean)state.get(POWERED)) {
			i |= 8;
		}

		return i;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(FACING, Direction.getById(data & 7));
	}
}
