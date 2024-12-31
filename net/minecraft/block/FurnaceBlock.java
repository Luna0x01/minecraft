package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FurnaceBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);
	private final boolean isLit;
	private static boolean keepInventory;

	protected FurnaceBlock(boolean bl) {
		super(Material.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
		this.isLit = bl;
	}

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
			Block block = world.getBlockState(pos.north()).getBlock();
			Block block2 = world.getBlockState(pos.south()).getBlock();
			Block block3 = world.getBlockState(pos.west()).getBlock();
			Block block4 = world.getBlockState(pos.east()).getBlock();
			Direction direction = state.get(FACING);
			if (direction == Direction.NORTH && block.isFullBlock() && !block2.isFullBlock()) {
				direction = Direction.SOUTH;
			} else if (direction == Direction.SOUTH && block2.isFullBlock() && !block.isFullBlock()) {
				direction = Direction.NORTH;
			} else if (direction == Direction.WEST && block3.isFullBlock() && !block4.isFullBlock()) {
				direction = Direction.EAST;
			} else if (direction == Direction.EAST && block4.isFullBlock() && !block3.isFullBlock()) {
				direction = Direction.WEST;
			}

			world.setBlockState(pos, state.with(FACING, direction), 2);
		}
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (this.isLit) {
			Direction direction = state.get(FACING);
			double d = (double)pos.getX() + 0.5;
			double e = (double)pos.getY() + rand.nextDouble() * 6.0 / 16.0;
			double f = (double)pos.getZ() + 0.5;
			double g = 0.52;
			double h = rand.nextDouble() * 0.6 - 0.3;
			switch (direction) {
				case WEST:
					world.addParticle(ParticleType.SMOKE, d - g, e, f + h, 0.0, 0.0, 0.0);
					world.addParticle(ParticleType.FIRE, d - g, e, f + h, 0.0, 0.0, 0.0);
					break;
				case EAST:
					world.addParticle(ParticleType.SMOKE, d + g, e, f + h, 0.0, 0.0, 0.0);
					world.addParticle(ParticleType.FIRE, d + g, e, f + h, 0.0, 0.0, 0.0);
					break;
				case NORTH:
					world.addParticle(ParticleType.SMOKE, d + h, e, f - g, 0.0, 0.0, 0.0);
					world.addParticle(ParticleType.FIRE, d + h, e, f - g, 0.0, 0.0, 0.0);
					break;
				case SOUTH:
					world.addParticle(ParticleType.SMOKE, d + h, e, f + g, 0.0, 0.0, 0.0);
					world.addParticle(ParticleType.FIRE, d + h, e, f + g, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof FurnaceBlockEntity) {
				player.openInventory((FurnaceBlockEntity)blockEntity);
				player.incrementStat(Stats.FURNACE_INTERACTION);
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
	public boolean hasComparatorOutput() {
		return true;
	}

	@Override
	public int getComparatorOutput(World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Item.fromBlock(Blocks.FURNACE);
	}

	@Override
	public int getBlockType() {
		return 3;
	}

	@Override
	public BlockState getRenderState(BlockState state) {
		return this.getDefaultState().with(FACING, Direction.SOUTH);
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
	protected StateManager appendProperties() {
		return new StateManager(this, FACING);
	}
}
