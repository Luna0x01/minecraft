package net.minecraft;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_4017 implements class_4019 {
	private static final Logger field_19476 = LogManager.getLogger();
	private static final Direction[] field_19477 = Direction.values();
	private final IntPriorityQueue field_19478 = new IntArrayFIFOQueue(786);

	public int method_17733(RenderBlockView renderBlockView, BlockPos blockPos) {
		return renderBlockView.method_16370(this.method_17742(), blockPos);
	}

	public void method_17734(class_3602 arg, BlockPos blockPos, int i) {
		arg.method_16403(this.method_17742(), blockPos, i);
	}

	protected int method_17729(BlockView blockView, BlockPos blockPos) {
		return blockView.getBlockState(blockPos).method_16885(blockView, blockPos);
	}

	protected int method_17737(BlockView blockView, BlockPos blockPos) {
		return blockView.getBlockState(blockPos).getLuminance();
	}

	private int method_17735(@Nullable Direction direction, int i, int j, int k, int l) {
		int m = 7;
		if (direction != null) {
			m = direction.ordinal();
		}

		return m << 24 | i << 18 | j << 10 | k << 4 | l << 0;
	}

	private int method_17728(int i) {
		return i >> 18 & 63;
	}

	private int method_17736(int i) {
		return i >> 10 & 0xFF;
	}

	private int method_17738(int i) {
		return i >> 4 & 63;
	}

	private int method_17739(int i) {
		return i >> 0 & 15;
	}

	@Nullable
	private Direction method_17740(int i) {
		int j = i >> 24 & 7;
		return j == 7 ? null : Direction.values()[i >> 24 & 7];
	}

	protected void method_17732(IWorld iWorld, ChunkPos chunkPos) {
		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			while (!this.field_19478.isEmpty()) {
				int i = this.field_19478.dequeueInt();
				int j = this.method_17739(i);
				int k = this.method_17728(i) - 16;
				int l = this.method_17736(i);
				int m = this.method_17738(i) - 16;
				Direction direction = this.method_17740(i);

				for (Direction direction2 : field_19477) {
					if (direction2 != direction) {
						int n = k + direction2.getOffsetX();
						int o = l + direction2.getOffsetY();
						int p = m + direction2.getOffsetZ();
						if (o <= 255 && o >= 0) {
							pooled.setPosition(n + chunkPos.getActualX(), o, p + chunkPos.getActualZ());
							int q = this.method_17729(iWorld, pooled);
							int r = j - Math.max(q, 1);
							if (r > 0 && r > this.method_17733(iWorld, pooled)) {
								this.method_17734(iWorld, pooled, r);
								this.method_17731(chunkPos, pooled, r);
							}
						}
					}
				}
			}
		}
	}

	protected void method_17730(ChunkPos chunkPos, int i, int j, int k, int l) {
		int m = i - chunkPos.getActualX() + 16;
		int n = k - chunkPos.getActualZ() + 16;
		this.field_19478.enqueue(this.method_17735(null, m, j, n, l));
	}

	protected void method_17731(ChunkPos chunkPos, BlockPos blockPos, int i) {
		this.method_17730(chunkPos, blockPos.getX(), blockPos.getY(), blockPos.getZ(), i);
	}
}
