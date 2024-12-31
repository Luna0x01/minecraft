package net.minecraft.block.pattern;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RenderBlockView;

public class CachedBlockPosition {
	private final RenderBlockView field_18695;
	private final BlockPos pos;
	private final boolean forceLoad;
	private BlockState state;
	private BlockEntity blockEntity;
	private boolean cachedEntity;

	public CachedBlockPosition(RenderBlockView renderBlockView, BlockPos blockPos, boolean bl) {
		this.field_18695 = renderBlockView;
		this.pos = blockPos;
		this.forceLoad = bl;
	}

	public BlockState getBlockState() {
		if (this.state == null && (this.forceLoad || this.field_18695.method_16359(this.pos))) {
			this.state = this.field_18695.getBlockState(this.pos);
		}

		return this.state;
	}

	@Nullable
	public BlockEntity getBlockEntity() {
		if (this.blockEntity == null && !this.cachedEntity) {
			this.blockEntity = this.field_18695.getBlockEntity(this.pos);
			this.cachedEntity = true;
		}

		return this.blockEntity;
	}

	public RenderBlockView method_16937() {
		return this.field_18695;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public static Predicate<CachedBlockPosition> method_16935(Predicate<BlockState> predicate) {
		return cachedBlockPosition -> cachedBlockPosition != null && predicate.test(cachedBlockPosition.getBlockState());
	}
}
