package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;

public interface BlockEntityProvider {
	BlockEntity createBlockEntity(World world, int id);
}
