package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.text.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class BlockWithEntity extends Block implements BlockEntityProvider {
	protected BlockWithEntity(Material material) {
		this(material, material.getColor());
	}

	protected BlockWithEntity(Material material, MaterialColor materialColor) {
		super(material, materialColor);
		this.blockEntity = true;
	}

	protected boolean isAdjacentToCactus(World world, BlockPos pos, Direction dir) {
		return world.getBlockState(pos.offset(dir)).getMaterial() == Material.CACTUS;
	}

	protected boolean isAdjacentToCactus(World world, BlockPos pos) {
		return this.isAdjacentToCactus(world, pos, Direction.NORTH)
			|| this.isAdjacentToCactus(world, pos, Direction.SOUTH)
			|| this.isAdjacentToCactus(world, pos, Direction.WEST)
			|| this.isAdjacentToCactus(world, pos, Direction.EAST);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		super.onBreaking(world, pos, state);
		world.removeBlockEntity(pos);
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		if (blockEntity instanceof Nameable && ((Nameable)blockEntity).hasCustomName()) {
			player.incrementStat(Stats.mined(this));
			player.addExhaustion(0.005F);
			if (world.isClient) {
				return;
			}

			int i = EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack);
			Item item = this.getDropItem(state, world.random, i);
			if (item == Items.AIR) {
				return;
			}

			ItemStack itemStack = new ItemStack(item, this.getDropCount(world.random));
			itemStack.setCustomName(((Nameable)blockEntity).getTranslationKey());
			onBlockBreak(world, pos, itemStack);
		} else {
			super.method_8651(world, player, pos, state, null, stack);
		}
	}

	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		super.onSyncedBlockEvent(state, world, pos, type, data);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity == null ? false : blockEntity.onBlockAction(type, data);
	}
}
