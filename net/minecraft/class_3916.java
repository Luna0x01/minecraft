package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.class_3072;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.ChunkBlockStateStorage;

public class class_3916 extends class_3902<class_3914> {
	@Override
	protected ChunkPos method_17432(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.method_17013().method_17229();
		int n = chunkGenerator.method_17013().method_17230();
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((class_3812)random).method_17289(chunkGenerator.method_17024(), s, t, 10387319);
		s *= m;
		t *= m;
		s += (random.nextInt(m - n) + random.nextInt(m - n)) / 2;
		t += (random.nextInt(m - n) + random.nextInt(m - n)) / 2;
		return new ChunkPos(s, t);
	}

	@Override
	protected boolean method_17431(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		ChunkPos chunkPos = this.method_17432(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			for (Biome biome : chunkGenerator.method_17020().method_16475(i * 16 + 9, j * 16 + 9, 32)) {
				if (!chunkGenerator.method_17015(biome, class_3844.field_19183)) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean method_17426(IWorld iWorld) {
		return iWorld.method_3588().hasStructures();
	}

	@Override
	protected class_3992 method_17428(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j) {
		Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), Biomes.DEFAULT);
		return new class_3916.class_3071(iWorld, chunkGenerator, arg, i, j, biome);
	}

	@Override
	protected String method_17423() {
		return "Mansion";
	}

	@Override
	public int method_17433() {
		return 8;
	}

	public static class class_3071 extends class_3992 {
		private boolean field_15196;

		public class_3071() {
		}

		public class_3071(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j, Biome biome) {
			super(i, j, biome, arg, iWorld.method_3581());
			BlockRotation blockRotation = BlockRotation.values()[arg.nextInt(BlockRotation.values().length)];
			int k = 5;
			int l = 5;
			if (blockRotation == BlockRotation.CLOCKWISE_90) {
				k = -5;
			} else if (blockRotation == BlockRotation.CLOCKWISE_180) {
				k = -5;
				l = -5;
			} else if (blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
				l = -5;
			}

			ChunkBlockStateStorage chunkBlockStateStorage = new ChunkBlockStateStorage(new ChunkPos(i, j), class_3790.field_18935);
			chunkGenerator.method_17016(chunkBlockStateStorage);
			int m = chunkBlockStateStorage.method_16992(class_3804.class_3805.MOTION_BLOCKING, 7, 7);
			int n = chunkBlockStateStorage.method_16992(class_3804.class_3805.MOTION_BLOCKING, 7, 7 + l);
			int o = chunkBlockStateStorage.method_16992(class_3804.class_3805.MOTION_BLOCKING, 7 + k, 7);
			int p = chunkBlockStateStorage.method_16992(class_3804.class_3805.MOTION_BLOCKING, 7 + k, 7 + l);
			int q = Math.min(Math.min(m, n), Math.min(o, p));
			if (q < 60) {
				this.field_15196 = false;
			} else {
				BlockPos blockPos = new BlockPos(i * 16 + 8, q + 1, j * 16 + 8);
				List<class_3072.class_3081> list = Lists.newLinkedList();
				class_3072.method_13778(iWorld.method_3587().method_11956(), blockPos, blockRotation, list, arg);
				this.field_19407.addAll(list);
				this.method_17660(iWorld);
				this.field_15196 = true;
			}
		}

		@Override
		public void method_82(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			super.method_82(iWorld, random, blockBox, chunkPos);
			int i = this.field_19408.minY;

			for (int j = blockBox.minX; j <= blockBox.maxX; j++) {
				for (int k = blockBox.minZ; k <= blockBox.maxZ; k++) {
					BlockPos blockPos = new BlockPos(j, i, k);
					if (!iWorld.method_8579(blockPos) && this.field_19408.contains(blockPos)) {
						boolean bl = false;

						for (StructurePiece structurePiece : this.field_19407) {
							if (structurePiece.getBoundingBox().contains(blockPos)) {
								bl = true;
								break;
							}
						}

						if (bl) {
							for (int l = i - 1; l > 1; l--) {
								BlockPos blockPos2 = new BlockPos(j, l, k);
								if (!iWorld.method_8579(blockPos2) && !iWorld.getBlockState(blockPos2).getMaterial().isFluid()) {
									break;
								}

								iWorld.setBlockState(blockPos2, Blocks.COBBLESTONE.getDefaultState(), 2);
							}
						}
					}
				}
			}
		}

		@Override
		public boolean method_85() {
			return this.field_15196;
		}
	}
}
