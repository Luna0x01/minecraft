package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FurnaceBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = HorizontalFacingBlock.DIRECTION;
	private final boolean isLit;
	private static boolean keepInventory;

	protected FurnaceBlock(boolean bl) {
		super(Material.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
		this.isLit = bl;
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(Blocks.FURNACE);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		this.setDirection(world, pos, state);
	}

	private void setDirection(World world, BlockPos pos, BlockState state) {
		if (!world.isClient) {
			BlockState blockState = world.getBlockState(pos.north());
			BlockState blockState2 = world.getBlockState(pos.south());
			BlockState blockState3 = world.getBlockState(pos.west());
			BlockState blockState4 = world.getBlockState(pos.east());
			Direction direction = state.get(FACING);
			if (direction == Direction.NORTH && blockState.isFullBlock() && !blockState2.isFullBlock()) {
				direction = Direction.SOUTH;
			} else if (direction == Direction.SOUTH && blockState2.isFullBlock() && !blockState.isFullBlock()) {
				direction = Direction.NORTH;
			} else if (direction == Direction.WEST && blockState3.isFullBlock() && !blockState4.isFullBlock()) {
				direction = Direction.EAST;
			} else if (direction == Direction.EAST && blockState4.isFullBlock() && !blockState3.isFullBlock()) {
				direction = Direction.WEST;
			}

			world.setBlockState(pos, state.with(FACING, direction), 2);
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (this.isLit) {
			Direction direction = state.get(FACING);
			double d = (double)pos.getX() + 0.5;
			double e = (double)pos.getY() + random.nextDouble() * 6.0 / 16.0;
			double f = (double)pos.getZ() + 0.5;
			double g = 0.52;
			double h = random.nextDouble() * 0.6 - 0.3;
			if (random.nextDouble() < 0.1) {
				world.playSound(
					(double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, Sounds.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false
				);
			}

			switch (direction) {
				case WEST:
					world.addParticle(ParticleType.SMOKE, d - 0.52, e, f + h, 0.0, 0.0, 0.0);
					world.addParticle(ParticleType.FIRE, d - 0.52, e, f + h, 0.0, 0.0, 0.0);
					break;
				case EAST:
					world.addParticle(ParticleType.SMOKE, d + 0.52, e, f + h, 0.0, 0.0, 0.0);
					world.addParticle(ParticleType.FIRE, d + 0.52, e, f + h, 0.0, 0.0, 0.0);
					break;
				case NORTH:
					world.addParticle(ParticleType.SMOKE, d + h, e, f - 0.52, 0.0, 0.0, 0.0);
					world.addParticle(ParticleType.FIRE, d + h, e, f - 0.52, 0.0, 0.0, 0.0);
					break;
				case SOUTH:
					world.addParticle(ParticleType.SMOKE, d + h, e, f + 0.52, 0.0, 0.0, 0.0);
					world.addParticle(ParticleType.FIRE, d + h, e, f + 0.52, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof FurnaceBlockEntity) {
				playerEntity.openInventory((FurnaceBlockEntity)blockEntity);
				playerEntity.incrementStat(Stats.FURNACE_INTERACTION);
			}

			return true;
		}
	}

	public static void setBlockState(boolean lit, World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		keepInventory = true;
		if (lit) {
			world.setBlockState(pos, Blocks.LIT_FURNACE.getDefaultState().with(FACING, blockState.get(FACING)), 3);
			world.setBlockState(pos, Blocks.LIT_FURNACE.getDefaultState().with(FACING, blockState.get(FACING)), 3);
		} else {
			world.setBlockState(pos, Blocks.FURNACE.getDefaultState().with(FACING, blockState.get(FACING)), 3);
			world.setBlockState(pos, Blocks.FURNACE.getDefaultState().with(FACING, blockState.get(FACING)), 3);
		}

		keepInventory = false;
		if (blockEntity != null) {
			blockEntity.cancelRemoval();
			world.setBlockEntity(pos, blockEntity);
		}
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new FurnaceBlockEntity();
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(FACING, entity.getHorizontalDirection().getOpposite());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlockState(pos, state.with(FACING, placer.getHorizontalDirection().getOpposite()), 2);
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof FurnaceBlockEntity) {
				((FurnaceBlockEntity)blockEntity).setCustomName(itemStack.getCustomName());
			}
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		if (!keepInventory) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof FurnaceBlockEntity) {
				ItemScatterer.spawn(world, pos, (FurnaceBlockEntity)blockEntity);
				world.updateHorizontalAdjacent(pos, this);
			}
		}

		super.onBreaking(world, pos, state);
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Blocks.FURNACE);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState stateFromData(int data) {
		Direction direction = Direction.getById(data);
		if (direction.getAxis() == Direction.Axis.Y) {
			direction = Direction.NORTH;
		}

		return this.getDefaultState().with(FACING, direction);
	}

	@Override
	public int getData(BlockState state) {
		return ((Direction)state.get(FACING)).getId();
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
		return new StateManager(this, FACING);
	}
}
