package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SnowLayerBlock extends Block {
	public static final IntProperty LAYERS = IntProperty.of("layers", 1, 8);

	protected SnowLayerBlock() {
		super(Material.SNOW_LAYER);
		this.setDefaultState(this.stateManager.getDefaultState().with(LAYERS, 1));
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.DECORATIONS);
		this.setBlockItemBounds();
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return (Integer)view.getBlockState(pos).get(LAYERS) < 5;
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		int i = (Integer)state.get(LAYERS) - 1;
		float f = 0.125F;
		return new Box(
			(double)pos.getX() + this.boundingBoxMinX,
			(double)pos.getY() + this.boundingBoxMinY,
			(double)pos.getZ() + this.boundingBoxMinZ,
			(double)pos.getX() + this.boundingBoxMaxX,
			(double)((float)pos.getY() + (float)i * f),
			(double)pos.getZ() + this.boundingBoxMaxZ
		);
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
	public void setBlockItemBounds() {
		this.updateBoundingBox(0);
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		BlockState blockState = view.getBlockState(pos);
		this.updateBoundingBox((Integer)blockState.get(LAYERS));
	}

	protected void updateBoundingBox(int layers) {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, (float)layers / 8.0F, 1.0F);
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		Block block = blockState.getBlock();
		if (block != Blocks.ICE && block != Blocks.PACKED_ICE) {
			if (block.getMaterial() == Material.FOLIAGE) {
				return true;
			} else {
				return block == this && blockState.get(LAYERS) >= 7 ? true : block.hasTransparency() && block.material.blocksMovement();
			}
		} else {
			return false;
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		this.isPlacementValid(world, pos, state);
	}

	private boolean isPlacementValid(World world, BlockPos pos, BlockState state) {
		if (!this.canBePlacedAtPos(world, pos)) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void harvest(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity be) {
		onBlockBreak(world, pos, new ItemStack(Items.SNOWBALL, (Integer)state.get(LAYERS) + 1, 0));
		world.setAir(pos);
		player.incrementStat(Stats.BLOCK_STATS[Block.getIdByBlock(this)]);
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.SNOWBALL;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (world.getLightAtPos(LightType.BLOCK, pos) > 11) {
			this.dropAsItem(world, pos, world.getBlockState(pos), 0);
			world.setAir(pos);
		}
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		return facing == Direction.UP ? true : super.isSideInvisible(view, pos, facing);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(LAYERS, (data & 7) + 1);
	}

	@Override
	public boolean isReplaceable(World world, BlockPos pos) {
		return (Integer)world.getBlockState(pos).get(LAYERS) == 1;
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(LAYERS) - 1;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, LAYERS);
	}
}
