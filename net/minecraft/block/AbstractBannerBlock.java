package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractBannerBlock extends BlockWithEntity {
	private final DyeColor color;

	protected AbstractBannerBlock(DyeColor dyeColor, Block.Builder builder) {
		super(builder);
		this.color = dyeColor;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canMobSpawnInside() {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new BannerBlockEntity(this.color);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.WHITE_BANNER;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof BannerBlockEntity ? ((BannerBlockEntity)blockEntity).method_16775(state) : super.getPickBlock(world, pos, state);
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		onBlockBreak(world, blockPos, this.getPickBlock(world, blockPos, blockState));
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		if (blockEntity instanceof BannerBlockEntity) {
			onBlockBreak(world, pos, ((BannerBlockEntity)blockEntity).method_16775(state));
			player.method_15932(Stats.MINED.method_21429(this));
		} else {
			super.method_8651(world, player, pos, state, null, stack);
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BannerBlockEntity) {
			((BannerBlockEntity)blockEntity).method_16774(itemStack, this.color);
		}
	}

	public DyeColor getColor() {
		return this.color;
	}
}
