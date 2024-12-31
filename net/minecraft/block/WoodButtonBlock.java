package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WoodButtonBlock extends AbstractButtonBlock {
	protected WoodButtonBlock() {
		super(true);
	}

	@Override
	protected void method_11580(@Nullable PlayerEntity playerEntity, World world, BlockPos blockPos) {
		world.method_11486(playerEntity, blockPos, Sounds.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
	}

	@Override
	protected void method_11581(World world, BlockPos blockPos) {
		world.method_11486(null, blockPos, Sounds.BLOCK_WOOD_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
	}
}
