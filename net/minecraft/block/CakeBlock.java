package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CakeBlock extends Block {
	public static final IntProperty BITES = IntProperty.of("bites", 0, 6);
	protected static final Box[] field_12609 = new Box[]{
		new Box(0.0625, 0.0, 0.0625, 0.9375, 0.5, 0.9375),
		new Box(0.1875, 0.0, 0.0625, 0.9375, 0.5, 0.9375),
		new Box(0.3125, 0.0, 0.0625, 0.9375, 0.5, 0.9375),
		new Box(0.4375, 0.0, 0.0625, 0.9375, 0.5, 0.9375),
		new Box(0.5625, 0.0, 0.0625, 0.9375, 0.5, 0.9375),
		new Box(0.6875, 0.0, 0.0625, 0.9375, 0.5, 0.9375),
		new Box(0.8125, 0.0, 0.0625, 0.9375, 0.5, 0.9375)
	};

	protected CakeBlock() {
		super(Material.CAKE);
		this.setDefaultState(this.stateManager.getDefaultState().with(BITES, 0));
		this.setTickRandomly(true);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12609[state.get(BITES)];
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction direction, float f, float g, float h) {
		if (!world.isClient) {
			return this.method_8698(world, pos, state, player);
		} else {
			ItemStack itemStack = player.getStackInHand(hand);
			return this.method_8698(world, pos, state, player) || itemStack.isEmpty();
		}
	}

	private boolean method_8698(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!playerEntity.canConsume(false)) {
			return false;
		} else {
			playerEntity.incrementStat(Stats.CAKE_SLICES_EATEN);
			playerEntity.getHungerManager().add(2, 0.1F);
			int i = (Integer)blockState.get(BITES);
			if (i < 6) {
				world.setBlockState(blockPos, blockState.with(BITES, i + 1), 3);
			} else {
				world.setAir(blockPos);
			}

			return true;
		}
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return super.canBePlacedAtPos(world, pos) ? this.isOnSolidBlock(world, pos) : false;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!this.isOnSolidBlock(world, pos)) {
			world.setAir(pos);
		}
	}

	private boolean isOnSolidBlock(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).getMaterial().isSolid();
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.AIR;
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.CAKE);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(BITES, data);
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(BITES);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, BITES);
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return (7 - (Integer)state.get(BITES)) * 2;
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}
}
