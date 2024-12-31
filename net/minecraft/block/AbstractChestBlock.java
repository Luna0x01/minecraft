package net.minecraft.block;

import java.util.function.Supplier;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractChestBlock<E extends BlockEntity> extends BlockWithEntity {
	protected final Supplier<BlockEntityType<? extends E>> entityTypeRetriever;

	protected AbstractChestBlock(Block.Settings settings, Supplier<BlockEntityType<? extends E>> supplier) {
		super(settings);
		this.entityTypeRetriever = supplier;
	}

	public abstract DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(
		BlockState blockState, World world, BlockPos blockPos, boolean bl
	);
}
