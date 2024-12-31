package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TripwireBlock extends Block {
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	public static final BooleanProperty ATTACHED = BooleanProperty.of("attached");
	public static final BooleanProperty DISARMED = BooleanProperty.of("disarmed");
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	protected static final Box field_12815 = new Box(0.0, 0.0625, 0.0, 1.0, 0.15625, 1.0);
	protected static final Box field_12816 = new Box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);

	public TripwireBlock() {
		super(Material.DECORATION);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(POWERED, false)
				.with(ATTACHED, false)
				.with(DISARMED, false)
				.with(NORTH, false)
				.with(EAST, false)
				.with(SOUTH, false)
				.with(WEST, false)
		);
		this.setTickRandomly(true);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return !state.get(ATTACHED) ? field_12816 : field_12815;
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		return state.with(NORTH, isConnectedTo(view, pos, state, Direction.NORTH))
			.with(EAST, isConnectedTo(view, pos, state, Direction.EAST))
			.with(SOUTH, isConnectedTo(view, pos, state, Direction.SOUTH))
			.with(WEST, isConnectedTo(view, pos, state, Direction.WEST));
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
	public RenderLayer getRenderLayerType() {
		return RenderLayer.TRANSLUCENT;
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.STRING;
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.STRING);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state, 3);
		this.update(world, pos, state);
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		this.update(world, pos, state.with(POWERED, true));
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient) {
			if (player.getMainHandStack() != null && player.getMainHandStack().getItem() == Items.SHEARS) {
				world.setBlockState(pos, state.with(DISARMED, true), 4);
			}
		}
	}

	private void update(World world, BlockPos pos, BlockState state) {
		for (Direction direction : new Direction[]{Direction.SOUTH, Direction.WEST}) {
			for (int k = 1; k < 42; k++) {
				BlockPos blockPos = pos.offset(direction, k);
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() == Blocks.TRIPWIRE_HOOK) {
					if (blockState.get(TripwireHookBlock.FACING) == direction.getOpposite()) {
						Blocks.TRIPWIRE_HOOK.update(world, blockPos, blockState, false, true, k, state);
					}
					break;
				}

				if (blockState.getBlock() != Blocks.TRIPWIRE) {
					break;
				}
			}
		}
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!world.isClient) {
			if (!(Boolean)state.get(POWERED)) {
				this.updatePowered(world, pos);
			}
		}
	}

	@Override
	public void onRandomTick(World world, BlockPos pos, BlockState state, Random rand) {
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			if ((Boolean)world.getBlockState(pos).get(POWERED)) {
				this.updatePowered(world, pos);
			}
		}
	}

	private void updatePowered(World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		boolean bl = (Boolean)blockState.get(POWERED);
		boolean bl2 = false;
		List<? extends Entity> list = world.getEntitiesIn(null, blockState.getCollisionBox((BlockView)world, pos).offset(pos));
		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (!entity.canAvoidTraps()) {
					bl2 = true;
					break;
				}
			}
		}

		if (bl2 != bl) {
			blockState = blockState.with(POWERED, bl2);
			world.setBlockState(pos, blockState, 3);
			this.update(world, pos, blockState);
		}

		if (bl2) {
			world.createAndScheduleBlockTick(new BlockPos(pos), this, this.getTickRate(world));
		}
	}

	public static boolean isConnectedTo(BlockView view, BlockPos pos, BlockState state, Direction dir) {
		BlockPos blockPos = pos.offset(dir);
		BlockState blockState = view.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block == Blocks.TRIPWIRE_HOOK) {
			Direction direction = dir.getOpposite();
			return blockState.get(TripwireHookBlock.FACING) == direction;
		} else {
			return block == Blocks.TRIPWIRE;
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(POWERED, (data & 1) > 0).with(ATTACHED, (data & 4) > 0).with(DISARMED, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		if ((Boolean)state.get(POWERED)) {
			i |= 1;
		}

		if ((Boolean)state.get(ATTACHED)) {
			i |= 4;
		}

		if ((Boolean)state.get(DISARMED)) {
			i |= 8;
		}

		return i;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
			case CLOCKWISE_90:
				return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
			default:
				return state;
		}
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
			case FRONT_BACK:
				return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
			default:
				return super.withMirror(state, mirror);
		}
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
	}
}
