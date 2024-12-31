package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FarmlandBlock extends Block {
	public static final IntProperty MOISTURE = IntProperty.of("moisture", 0, 7);

	protected FarmlandBlock() {
		super(Material.DIRT);
		this.setDefaultState(this.stateManager.getDefaultState().with(MOISTURE, 0));
		this.setTickRandomly(true);
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
		this.setOpacity(255);
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1));
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
		if (entity instanceof LivingEntity) {
			if (!world.isClient && world.random.nextFloat() < distance - 0.5F) {
				if (!(entity instanceof PlayerEntity) && !world.getGameRules().getBoolean("mobGriefing")) {
					return;
				}

				world.setBlockState(pos, Blocks.DIRT.getDefaultState());
			}

			super.onLandedUpon(world, pos, entity, distance);
		}
	}

	private boolean hasCrop(World world, BlockPos pos) {
		Block block = world.getBlockState(pos.up()).getBlock();
		return block instanceof CropBlock || block instanceof StemBlock;
	}

	private boolean isWatered(World world, BlockPos pos) {
		for (BlockPos.Mutable mutable : BlockPos.mutableIterate(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
			if (world.getBlockState(mutable).getBlock().getMaterial() == Material.WATER) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		super.neighborUpdate(world, pos, state, block);
		if (world.getBlockState(pos.up()).getBlock().getMaterial().isSolid()) {
			world.setBlockState(pos, Blocks.DIRT.getDefaultState());
		}
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		switch (facing) {
			case UP:
				return true;
			case NORTH:
			case SOUTH:
			case WEST:
			case EAST:
				Block block = view.getBlockState(pos).getBlock();
				return !block.hasTransparency() && block != Blocks.FARMLAND;
			default:
				return super.isSideInvisible(view, pos, facing);
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Blocks.DIRT.getDropItem(Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.DIRT), random, id);
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Item.fromBlock(Blocks.DIRT);
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
