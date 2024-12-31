package net.minecraft.world.gen.feature;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3871;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class FillerBlockFeature extends class_3844<class_3871> {
	private boolean field_12982;
	private FillerBlockFeature.class_2756 field_12983;
	private BlockPos field_12984;

	public void method_11823(FillerBlockFeature.class_2756 arg) {
		this.field_12983 = arg;
	}

	public void method_11825(boolean bl) {
		this.field_12982 = bl;
	}

	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		if (this.field_12983 == null) {
			throw new IllegalStateException("Decoration requires priming with a spike");
		} else {
			int i = this.field_12983.method_11829();

			for (BlockPos.Mutable mutable : BlockPos.mutableIterate(
				new BlockPos(blockPos.getX() - i, 0, blockPos.getZ() - i), new BlockPos(blockPos.getX() + i, this.field_12983.method_11830() + 10, blockPos.getZ() + i)
			)) {
				if (mutable.squaredDistanceTo((double)blockPos.getX(), (double)mutable.getY(), (double)blockPos.getZ()) <= (double)(i * i + 1)
					&& mutable.getY() < this.field_12983.method_11830()) {
					this.method_17344(iWorld, mutable, Blocks.OBSIDIAN.getDefaultState());
				} else if (mutable.getY() > 65) {
					this.method_17344(iWorld, mutable, Blocks.AIR.getDefaultState());
				}
			}

			if (this.field_12983.method_11831()) {
				int j = -2;
				int k = 2;
				int l = 3;
				BlockPos.Mutable mutable2 = new BlockPos.Mutable();

				for (int m = -2; m <= 2; m++) {
					for (int n = -2; n <= 2; n++) {
						for (int o = 0; o <= 3; o++) {
							boolean bl = MathHelper.abs(m) == 2;
							boolean bl2 = MathHelper.abs(n) == 2;
							boolean bl3 = o == 3;
							if (bl || bl2 || bl3) {
								boolean bl4 = m == -2 || m == 2 || bl3;
								boolean bl5 = n == -2 || n == 2 || bl3;
								BlockState blockState = Blocks.IRON_BARS
									.getDefaultState()
									.withProperty(PaneBlock.field_18265, Boolean.valueOf(bl4 && n != -2))
									.withProperty(PaneBlock.field_18267, Boolean.valueOf(bl4 && n != 2))
									.withProperty(PaneBlock.field_18268, Boolean.valueOf(bl5 && m != -2))
									.withProperty(PaneBlock.field_18266, Boolean.valueOf(bl5 && m != 2));
								this.method_17344(iWorld, mutable2.setPosition(blockPos.getX() + m, this.field_12983.method_11830() + o, blockPos.getZ() + n), blockState);
							}
						}
					}
				}
			}

			EndCrystalEntity endCrystalEntity = new EndCrystalEntity(iWorld.method_16348());
			endCrystalEntity.setBeamTarget(this.field_12984);
			endCrystalEntity.setInvulnerable(this.field_12982);
			endCrystalEntity.refreshPositionAndAngles(
				(double)((float)blockPos.getX() + 0.5F),
				(double)(this.field_12983.method_11830() + 1),
				(double)((float)blockPos.getZ() + 0.5F),
				random.nextFloat() * 360.0F,
				0.0F
			);
			iWorld.method_3686(endCrystalEntity);
			this.method_17344(iWorld, new BlockPos(blockPos.getX(), this.field_12983.method_11830(), blockPos.getZ()), Blocks.BEDROCK.getDefaultState());
			return true;
		}
	}

	public void method_11824(@Nullable BlockPos blockPos) {
		this.field_12984 = blockPos;
	}

	public static class class_2756 {
		private final int field_12985;
		private final int field_12986;
		private final int field_12987;
		private final int field_12988;
		private final boolean field_12989;
		private final Box field_12990;

		public class_2756(int i, int j, int k, int l, boolean bl) {
			this.field_12985 = i;
			this.field_12986 = j;
			this.field_12987 = k;
			this.field_12988 = l;
			this.field_12989 = bl;
			this.field_12990 = new Box((double)(i - k), 0.0, (double)(j - k), (double)(i + k), 256.0, (double)(j + k));
		}

		public boolean method_11827(BlockPos blockPos) {
			int i = this.field_12985 - this.field_12987;
			int j = this.field_12986 - this.field_12987;
			return blockPos.getX() == (i & -16) && blockPos.getZ() == (j & -16);
		}

		public int method_11826() {
			return this.field_12985;
		}

		public int method_11828() {
			return this.field_12986;
		}

		public int method_11829() {
			return this.field_12987;
		}

		public int method_11830() {
			return this.field_12988;
		}

		public boolean method_11831() {
			return this.field_12989;
		}

		public Box method_11832() {
			return this.field_12990;
		}
	}
}
