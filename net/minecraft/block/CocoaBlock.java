package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CocoaBlock extends FacingBlock implements Growable {
	public static final IntProperty AGE = IntProperty.of("age", 0, 2);

	public CocoaBlock() {
		super(Material.PLANT);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(AGE, 0));
		this.setTickRandomly(true);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!this.isOnJungleWood(world, pos, state)) {
			this.replaceAndDrop(world, pos, state);
		} else if (world.random.nextInt(5) == 0) {
			int i = (Integer)state.get(AGE);
			if (i < 2) {
				world.setBlockState(pos, state.with(AGE, i + 1), 2);
			}
		}
	}

	public boolean isOnJungleWood(World world, BlockPos pos, BlockState state) {
		pos = pos.offset(state.get(FACING));
		BlockState blockState = world.getBlockState(pos);
		return blockState.getBlock() == Blocks.LOG && blockState.get(PlanksBlock.VARIANT) == PlanksBlock.WoodType.JUNGLE;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		this.setBoundingBox(world, pos);
		return super.getCollisionBox(world, pos, state);
	}

	@Override
	public Box getSelectionBox(World world, BlockPos pos) {
		this.setBoundingBox(world, pos);
		return super.getSelectionBox(world, pos);
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		BlockState blockState = view.getBlockState(pos);
		Direction direction = blockState.get(FACING);
		int i = (Integer)blockState.get(AGE);
		int j = 4 + i * 2;
		int k = 5 + i * 2;
		float f = (float)j / 2.0F;
		switch (direction) {
			case SOUTH:
				this.setBoundingBox((8.0F - f) / 16.0F, (12.0F - (float)k) / 16.0F, (15.0F - (float)j) / 16.0F, (8.0F + f) / 16.0F, 0.75F, 0.9375F);
				break;
			case NORTH:
				this.setBoundingBox((8.0F - f) / 16.0F, (12.0F - (float)k) / 16.0F, 0.0625F, (8.0F + f) / 16.0F, 0.75F, (1.0F + (float)j) / 16.0F);
				break;
			case WEST:
				this.setBoundingBox(0.0625F, (12.0F - (float)k) / 16.0F, (8.0F - f) / 16.0F, (1.0F + (float)j) / 16.0F, 0.75F, (8.0F + f) / 16.0F);
				break;
			case EAST:
				this.setBoundingBox((15.0F - (float)j) / 16.0F, (12.0F - (float)k) / 16.0F, (8.0F - f) / 16.0F, 0.9375F, 0.75F, (8.0F + f) / 16.0F);
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		Direction direction = Direction.fromRotation((double)placer.yaw);
		world.setBlockState(pos, state.with(FACING, direction), 2);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		if (!dir.getAxis().isHorizontal()) {
			dir = Direction.NORTH;
		}

		return this.getDefaultState().with(FACING, dir.getOpposite()).with(AGE, 0);
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!this.isOnJungleWood(world, pos, state)) {
			this.replaceAndDrop(world, pos, state);
		}
	}

	private void replaceAndDrop(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		this.dropAsItem(world, pos, state, 0);
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		int i = (Integer)state.get(AGE);
		int j = 1;
		if (i >= 2) {
			j = 3;
		}

		for (int k = 0; k < j; k++) {
			onBlockBreak(world, pos, new ItemStack(Items.DYE, 1, DyeColor.BROWN.getSwappedId()));
		}
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Items.DYE;
	}

	@Override
	public int getMeta(World world, BlockPos pos) {
		return DyeColor.BROWN.getSwappedId();
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
		return (Integer)state.get(AGE) < 2;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(AGE, (Integer)state.get(AGE) + 1), 2);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(FACING, Direction.fromHorizontal(data)).with(AGE, (data & 15) >> 2);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getHorizontal();
		return i | (Integer)state.get(AGE) << 2;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, AGE);
	}
}
