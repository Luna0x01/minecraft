package net.minecraft;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;

public class class_4213 implements Predicate<CachedBlockPosition> {
	private final BlockState field_20633;
	private final Set<Property<?>> field_20634;
	@Nullable
	private final NbtCompound field_20635;

	public class_4213(BlockState blockState, Set<Property<?>> set, @Nullable NbtCompound nbtCompound) {
		this.field_20633 = blockState;
		this.field_20634 = set;
		this.field_20635 = nbtCompound;
	}

	public BlockState method_19037() {
		return this.field_20633;
	}

	public boolean test(CachedBlockPosition cachedBlockPosition) {
		BlockState blockState = cachedBlockPosition.getBlockState();
		if (blockState.getBlock() != this.field_20633.getBlock()) {
			return false;
		} else {
			for (Property<?> property : this.field_20634) {
				if (blockState.getProperty(property) != this.field_20633.getProperty(property)) {
					return false;
				}
			}

			if (this.field_20635 == null) {
				return true;
			} else {
				BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
				return blockEntity != null && NbtHelper.areEqual(this.field_20635, blockEntity.toNbt(new NbtCompound()), true);
			}
		}
	}

	public boolean method_19039(ServerWorld serverWorld, BlockPos blockPos, int i) {
		if (!serverWorld.setBlockState(blockPos, this.field_20633, i)) {
			return false;
		} else {
			if (this.field_20635 != null) {
				BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
				if (blockEntity != null) {
					NbtCompound nbtCompound = this.field_20635.copy();
					nbtCompound.putInt("x", blockPos.getX());
					nbtCompound.putInt("y", blockPos.getY());
					nbtCompound.putInt("z", blockPos.getZ());
					blockEntity.fromNbt(nbtCompound);
				}
			}

			return true;
		}
	}
}
