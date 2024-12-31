package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CakeBlock extends Block {
	public static final IntProperty BITES = IntProperty.of("bites", 0, 6);

	protected CakeBlock() {
		super(Material.CAKE);
		this.setDefaultState(this.stateManager.getDefaultState().with(BITES, 0));
		this.setTickRandomly(true);
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		float f = 0.0625F;
		float g = (float)(1 + (Integer)view.getBlockState(pos).get(BITES) * 2) / 16.0F;
		float h = 0.5F;
		this.setBoundingBox(g, 0.0F, f, 1.0F - f, h, 1.0F - f);
	}

	@Override
	public void setBlockItemBounds() {
		float f = 0.0625F;
		float g = 0.5F;
		this.setBoundingBox(f, 0.0F, f, 1.0F - f, g, 1.0F - f);
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		float f = 0.0625F;
		float g = (float)(1 + (Integer)state.get(BITES) * 2) / 16.0F;
		float h = 0.5F;
		return new Box(
			(double)((float)pos.getX() + g),
			(double)pos.getY(),
			(double)((float)pos.getZ() + f),
			(double)((float)(pos.getX() + 1) - f),
			(double)((float)pos.getY() + h),
			(double)((float)(pos.getZ() + 1) - f)
		);
	}

	@Override
	public Box getSelectionBox(World world, BlockPos pos) {
		return this.getCollisionBox(world, pos, world.getBlockState(pos));
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
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		this.onCakeConsuming(world, pos, state, player);
		return true;
	}

	@Override
	public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
		this.onCakeConsuming(world, pos, world.getBlockState(pos), player);
	}

	private void onCakeConsuming(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (player.canConsume(false)) {
			player.incrementStat(Stats.CAKE_SLICES_EATEN);
			player.getHungerManager().add(2, 0.1F);
			int i = (Integer)state.get(BITES);
			if (i < 6) {
				world.setBlockState(pos, state.with(BITES, i + 1), 3);
			} else {
				world.setAir(pos);
			}
		}
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return super.canBePlacedAtPos(world, pos) ? this.isOnSolidBlock(world, pos) : false;
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!this.isOnSolidBlock(world, pos)) {
			world.setAir(pos);
		}
	}

	private boolean isOnSolidBlock(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).getBlock().getMaterial().isSolid();
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return null;
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Items.CAKE;
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
	public int getComparatorOutput(World world, BlockPos pos) {
		return (7 - (Integer)world.getBlockState(pos).get(BITES)) * 2;
	}

	@Override
	public boolean hasComparatorOutput() {
		return true;
	}
}
