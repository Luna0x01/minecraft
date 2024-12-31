package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FlowerPotBlock extends Block {
	private static final Map<Block, Block> field_18345 = Maps.newHashMap();
	protected static final VoxelShape field_18344 = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
	private final Block field_18346;

	public FlowerPotBlock(Block block, Block.Builder builder) {
		super(builder);
		this.field_18346 = block;
		field_18345.put(block, this);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18344;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		ItemStack itemStack = player.getStackInHand(hand);
		Item item = itemStack.getItem();
		Block block = item instanceof BlockItem ? (Block)field_18345.getOrDefault(((BlockItem)item).getBlock(), Blocks.AIR) : Blocks.AIR;
		boolean bl = block == Blocks.AIR;
		boolean bl2 = this.field_18346 == Blocks.AIR;
		if (bl != bl2) {
			if (bl2) {
				world.setBlockState(pos, block.getDefaultState(), 3);
				player.method_15928(Stats.POT_FLOWER);
				if (!player.abilities.creativeMode) {
					itemStack.decrement(1);
				}
			} else {
				ItemStack itemStack2 = new ItemStack(this.field_18346);
				if (itemStack.isEmpty()) {
					player.equipStack(hand, itemStack2);
				} else if (!player.method_13617(itemStack2)) {
					player.dropItem(itemStack2, false);
				}

				world.setBlockState(pos, Blocks.FLOWER_POT.getDefaultState(), 3);
			}
		}

		return true;
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return this.field_18346 == Blocks.AIR ? super.getPickBlock(world, pos, state) : new ItemStack(this.field_18346);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Blocks.FLOWER_POT;
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		super.method_410(blockState, world, blockPos, f, i);
		if (this.field_18346 != Blocks.AIR) {
			onBlockBreak(world, blockPos, new ItemStack(this.field_18346));
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction == Direction.DOWN && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
