package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.class_3559;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SignItem extends class_3559 {
	public SignItem(Item.Settings settings) {
		super(Blocks.SIGN, Blocks.WALL_SIGN, settings);
	}

	@Override
	protected boolean method_16014(BlockPos blockPos, World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockState blockState) {
		boolean bl = super.method_16014(blockPos, world, playerEntity, itemStack, blockState);
		if (!world.isClient && !bl && playerEntity != null) {
			playerEntity.openEditSignScreen((SignBlockEntity)world.getBlockEntity(blockPos));
		}

		return bl;
	}
}
