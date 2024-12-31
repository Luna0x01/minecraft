package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FarmlandBlock extends Block {
	public static final IntProperty MOISTURE = IntProperty.of("moisture", 0, 7);
	protected static final Box field_12663 = new Box(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);

	protected FarmlandBlock() {
		super(Material.DIRT);
		this.setDefaultState(this.stateManager.getDefaultState().with(MOISTURE, 0));
		this.setTickRandomly(true);
		this.setOpacity(255);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12663;
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
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		int i = (Integer)state.get(MOISTURE);
		if (!this.isWatered(world, pos) && !world.hasRain(pos.up())) {
			if (i > 0) {
				world.setBlockState(pos, state.with(MOISTURE, i - 1), 2);
			} else if (!this.hasCrop(world, pos)) {
				world.setBlockState(pos, Blocks.DIRT.getDefaultState());
			}
		} else if (i < 7) {
			world.setBlockState(pos, state.with(MOISTURE, 7), 2);
		}
	}

	@Override
	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		if (!world.isClient
			&& world.random.nextFloat() < distance - 0.5F
			&& entity instanceof LivingEntity
			&& (entity instanceof PlayerEntity || world.getGameRules().getBoolean("mobGriefing"))
			&& entity.width * entity.width * entity.height > 0.512F) {
			world.setBlockState(pos, Blocks.DIRT.getDefaultState());
		}

		super.onLandedUpon(world, pos, entity, distance);
	}

	private boolean hasCrop(World world, BlockPos pos) {
		Block block = world.getBlockState(pos.up()).getBlock();
		return block instanceof CropBlock || block instanceof StemBlock;
	}

	private boolean isWatered(World world, BlockPos pos) {
		for (BlockPos.Mutable mutable : BlockPos.mutableIterate(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
			if (world.getBlockState(mutable).getMaterial() == Material.WATER) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		super.method_8641(blockState, world, blockPos, block);
		if (world.getBlockState(blockPos.up()).getMaterial().isSolid()) {
			world.setBlockState(blockPos, Blocks.DIRT.getDefaultState());
		}
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		switch (direction) {
			case UP:
				return true;
			case NORTH:
			case SOUTH:
			case WEST:
			case EAST:
				BlockState blockState = view.getBlockState(pos.offset(direction));
				Block block = blockState.getBlock();
				return !blockState.isFullBoundsCubeForCulling() && block != Blocks.FARMLAND && block != Blocks.GRASS_PATH;
			default:
				return super.method_8654(state, view, pos, direction);
		}
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Blocks.DIRT.getDropItem(Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.DIRT), random, id);
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Blocks.DIRT);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(MOISTURE, data & 7);
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(MOISTURE);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, MOISTURE);
	}
}
