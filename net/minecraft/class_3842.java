package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.class_2759;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.ChunkBlockStateStorage;

public class class_3842 extends class_3902<class_3841> {
	@Override
	protected ChunkPos method_17432(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.method_17013().method_17227();
		int n = chunkGenerator.method_17013().method_17228();
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((class_3812)random).method_17289(chunkGenerator.method_17024(), s, t, 10387313);
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
			Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), Biomes.DEFAULT);
			if (!chunkGenerator.method_17015(biome, class_3844.field_19193)) {
				return false;
			} else {
				int k = method_17338(i, j, chunkGenerator);
				return k >= 60;
			}
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
		return new class_3842.class_2758(iWorld, chunkGenerator, arg, i, j, biome);
	}

	@Override
	protected String method_17423() {
		return "EndCity";
	}

	@Override
	public int method_17433() {
		return 9;
	}

	private static int method_17338(int i, int j, ChunkGenerator<?> chunkGenerator) {
		Random random = new Random((long)(i + j * 10387313));
		BlockRotation blockRotation = BlockRotation.values()[random.nextInt(BlockRotation.values().length)];
		ChunkBlockStateStorage chunkBlockStateStorage = new ChunkBlockStateStorage(new ChunkPos(i, j), class_3790.field_18935);
		chunkGenerator.method_17016(chunkBlockStateStorage);
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

		int m = chunkBlockStateStorage.method_16992(class_3804.class_3805.MOTION_BLOCKING, 7, 7);
		int n = chunkBlockStateStorage.method_16992(class_3804.class_3805.MOTION_BLOCKING, 7, 7 + l);
		int o = chunkBlockStateStorage.method_16992(class_3804.class_3805.MOTION_BLOCKING, 7 + k, 7);
		int p = chunkBlockStateStorage.method_16992(class_3804.class_3805.MOTION_BLOCKING, 7 + k, 7 + l);
		return Math.min(Math.min(m, n), Math.min(o, p));
	}

	public static class class_2758 extends class_3992 {
		private boolean field_12994;

		public class_2758() {
		}

		public class_2758(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j, Biome biome) {
			super(i, j, biome, arg, iWorld.method_3581());
			BlockRotation blockRotation = BlockRotation.values()[arg.nextInt(BlockRotation.values().length)];
			int k = class_3842.method_17338(i, j, chunkGenerator);
			if (k < 60) {
				this.field_12994 = false;
			} else {
				BlockPos blockPos = new BlockPos(i * 16 + 8, k, j * 16 + 8);
				class_2759.method_11837(iWorld.method_3587().method_11956(), blockPos, blockRotation, this.field_19407, arg);
				this.method_17660(iWorld);
				this.field_12994 = true;
			}
		}

		@Override
		public boolean method_85() {
			return this.field_12994;
		}
	}
}
