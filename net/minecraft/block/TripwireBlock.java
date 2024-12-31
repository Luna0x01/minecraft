package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TripwireBlock extends Block {
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	public static final BooleanProperty SUSPENDED = BooleanProperty.of("suspended");
	public static final BooleanProperty ATTACHED = BooleanProperty.of("attached");
	public static final BooleanProperty DISARMED = BooleanProperty.of("disarmed");
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty WEST = BooleanProperty.of("west");

	public TripwireBlock() {
		super(Material.DECORATION);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(POWERED, false)
				.with(SUSPENDED, false)
				.with(ATTACHED, false)
				.with(DISARMED, false)
				.with(NORTH, false)
				.with(EAST, false)
				.with(SOUTH, false)
				.with(WEST, false)
		);
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
		this.setTickRandomly(true);
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		return state.with(NORTH, isConnectedTo(view, pos, state, Direction.NORTH))
			.with(EAST, isConnectedTo(view, pos, state, Direction.EAST))
			.with(SOUTH, isConnectedTo(view, pos, state, Direction.SOUTH))
			.with(WEST, isConnectedTo(view, pos, state, Direction.WEST));
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
	public RenderLayer getRenderLayerType() {
		return RenderLayer.TRANSLUCENT;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.STRING;
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Items.STRING;
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		boolean bl = (Boolean)state.get(SUSPENDED);
		boolean bl2 = !World.isOpaque(world, pos.down());
		if (bl != bl2) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
		}
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		BlockState blockState = view.getBlockState(pos);
		boolean bl = (Boolean)blockState.get(ATTACHED);
		boolean bl2 = (Boolean)blockState.get(SUSPENDED);
		if (!bl2) {
			this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
		} else if (!bl) {
			this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		} else {
			this.setBoundingBox(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
		}
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		state = state.with(SUSPENDED, !World.isOpaque(world, pos.down()));
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
		List<? extends Entity> list = world.getEntitiesIn(
			null,
			new Box(
				(double)pos.getX() + this.boundingBoxMinX,
				(double)pos.getY() + this.boundingBoxMinY,
				(double)pos.getZ() + this.boundingBoxMinZ,
				(double)pos.getX() + this.boundingBoxMaxX,
				(double)pos.getY() + this.boundingBoxMaxY,
				(double)pos.getZ() + this.boundingBoxMaxZ
			)
		);
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
			world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
		}
	}

	public static boolean isConnectedTo(BlockView view, BlockPos pos, BlockState state, Direction dir) {
		BlockPos blockPos = pos.offset(dir);
		BlockState blockState = view.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block == Blocks.TRIPWIRE_HOOK) {
			Direction direction = dir.getOpposite();
			return blockState.get(TripwireHookBlock.FACING) == direction;
		} else if (block == Blocks.TRIPWIRE) {
			boolean bl = (Boolean)state.get(SUSPENDED);
			boolean bl2 = (Boolean)blockState.get(SUSPENDED);
			return bl == bl2;
		} else {
			return false;
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(POWERED, (data & 1) > 0).with(SUSPENDED, (data & 2) > 0).with(ATTACHED, (data & 4) > 0).with(DISARMED, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		if ((Boolean)state.get(POWERED)) {
			i |= 1;
		}

		if ((Boolean)state.get(SUSPENDED)) {
			i |= 2;
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
	protected StateManager appendProperties() {
		return new StateManager(this, POWERED, SUSPENDED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
	}
}
