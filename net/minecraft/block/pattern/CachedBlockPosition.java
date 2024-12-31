package net.minecraft.block.pattern;

import com.google.common.base.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CachedBlockPosition {
	private final World world;
	private final BlockPos pos;
	private final boolean forceLoad;
	private BlockState state;
	private BlockEntity blockEntity;
	private boolean cachedEntity;

	public CachedBlockPosition(World world, BlockPos blockPos, boolean bl) {
		this.world = world;
		this.pos = blockPos;
		this.forceLoad = bl;
	}

	public BlockState getBlockState() {
		if (this.state == null && (this.forceLoad || this.world.blockExists(this.pos))) {
			this.state = this.world.getBlockState(this.pos);
		}

		return this.state;
	}

	public BlockEntity getBlockEntity() {
		if (this.blockEntity == null && !this.cachedEntity) {
			this.blockEntity = this.world.getBlockEntity(this.pos);
			this.cachedEntity = true;
		}

		return this.blockEntity;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public static Predicate<CachedBlockPosition> matchesBlockState(Predicate<BlockState> state) {
		return new Predicate<CachedBlockPosition>() {
			public boolean apply(CachedBlockPosition cachedBlockPosition) {
				return cachedBlockPosition != null && state.apply(cachedBlockPosition.getBlockState());
			}
		};
	}
}
