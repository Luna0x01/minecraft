package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;

public interface BlockEntityProvider {
	@Nullable
	BlockEntity createBlockEntity(World world, int id);
}
