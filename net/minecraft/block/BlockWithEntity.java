package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.text.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockWithEntity extends Block implements BlockEntityProvider {
	private static final Logger field_17734 = LogManager.getLogger();

	protected BlockWithEntity(Block.Builder builder) {
		super(builder);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			super.onStateReplaced(state, world, pos, newState, moved);
			world.removeBlockEntity(pos);
		}
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		if (blockEntity instanceof Nameable && ((Nameable)blockEntity).hasCustomName()) {
			player.method_15932(Stats.MINED.method_21429(this));
			player.addExhaustion(0.005F);
			if (world.isClient) {
				field_17734.debug("Never going to hit this!");
				return;
			}

			int i = EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack);
			Item item = this.getDroppedItem(state, world, pos, i).getItem();
			if (item == Items.AIR) {
				return;
			}

			ItemStack itemStack = new ItemStack(item, this.getDropCount(state, world.random));
			itemStack.setCustomName(((Nameable)blockEntity).method_15541());
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
