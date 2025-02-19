package net.minecraft.block.pattern;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class CachedBlockPosition {
	private final WorldView world;
	private final BlockPos pos;
	private final boolean forceLoad;
	private BlockState state;
	private BlockEntity blockEntity;
	private boolean cachedEntity;

	public CachedBlockPosition(WorldView world, BlockPos pos, boolean forceLoad) {
		this.world = world;
		this.pos = pos.toImmutable();
		this.forceLoad = forceLoad;
	}

	public BlockState getBlockState() {
		if (this.state == null && (this.forceLoad || this.world.isChunkLoaded(this.pos))) {
			this.state = this.world.getBlockState(this.pos);
		}

		return this.state;
	}

	@Nullable
	public BlockEntity getBlockEntity() {
		if (this.blockEntity == null && !this.cachedEntity) {
			this.blockEntity = this.world.getBlockEntity(this.pos);
			this.cachedEntity = true;
		}

		return this.blockEntity;
	}

	public WorldView getWorld() {
		return this.world;
	}

	public BlockPos getBlockPos() {
		return this.pos;
	}

	public static Predicate<CachedBlockPosition> matchesBlockState(Predicate<BlockState> state) {
		return cachedBlockPosition -> cachedBlockPosition != null && state.test(cachedBlockPosition.getBlockState());
	}
}
