package net.minecraft.structure;

import java.util.Random;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.chunk.EndChunkGenerator;
import net.minecraft.world.gen.GeneratorConfig;

public class EndCityStructure extends StructureFeature {
	private final int field_12991 = 20;
	private final int field_12992 = 11;
	private final EndChunkGenerator field_12993;

	public EndCityStructure(EndChunkGenerator endChunkGenerator) {
		this.field_12993 = endChunkGenerator;
	}

	@Override
	public String getName() {
		return "EndCity";
	}

	@Override
	protected boolean shouldStartAt(int chunkX, int chunkZ) {
		int i = chunkX;
		int j = chunkZ;
		if (chunkX < 0) {
			chunkX -= 19;
		}

		if (chunkZ < 0) {
			chunkZ -= 19;
		}

		int k = chunkX / 20;
		int l = chunkZ / 20;
		Random random = this.world.getStructureRandom(k, l, 10387313);
		k *= 20;
		l *= 20;
		k += (random.nextInt(9) + random.nextInt(9)) / 2;
		l += (random.nextInt(9) + random.nextInt(9)) / 2;
		if (i == k && j == l && this.field_12993.method_11822(i, j)) {
			int m = method_13772(i, j, this.field_12993);
			return m >= 60;
		} else {
			return false;
		}
	}

	@Override
	protected GeneratorConfig getGeneratorConfig(int chunkX, int chunkZ) {
		return new EndCityStructure.EndCityGeneratorConfig(this.world, this.field_12993, this.random, chunkX, chunkZ);
	}

	@Override
	public BlockPos method_9269(World world, BlockPos blockPos, boolean bl) {
		this.world = world;
		return method_13774(world, this, blockPos, 20, 11, 10387313, true, 100, bl);
	}

	private static int method_13772(int i, int j, EndChunkGenerator endChunkGenerator) {
		Random random = new Random((long)(i + j * 10387313));
		BlockRotation blockRotation = BlockRotation.values()[random.nextInt(BlockRotation.values().length)];
		ChunkBlockStateStorage chunkBlockStateStorage = new ChunkBlockStateStorage();
		endChunkGenerator.method_9195(i, j, chunkBlockStateStorage);
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

		int m = chunkBlockStateStorage.method_11819(7, 7);
		int n = chunkBlockStateStorage.method_11819(7, 7 + l);
		int o = chunkBlockStateStorage.method_11819(7 + k, 7);
		int p = chunkBlockStateStorage.method_11819(7 + k, 7 + l);
		return Math.min(Math.min(m, n), Math.min(o, p));
	}

	public static class EndCityGeneratorConfig extends GeneratorConfig {
		private boolean field_12994;

		public EndCityGeneratorConfig() {
		}

		public EndCityGeneratorConfig(World world, EndChunkGenerator endChunkGenerator, Random random, int i, int j) {
			super(i, j);
			this.method_11833(world, endChunkGenerator, random, i, j);
		}

		private void method_11833(World world, EndChunkGenerator endChunkGenerator, Random random, int i, int j) {
			Random random2 = new Random((long)(i + j * 10387313));
			BlockRotation blockRotation = BlockRotation.values()[random2.nextInt(BlockRotation.values().length)];
			int k = EndCityStructure.method_13772(i, j, endChunkGenerator);
			if (k < 60) {
				this.field_12994 = false;
			} else {
				BlockPos blockPos = new BlockPos(i * 16 + 8, k, j * 16 + 8);
				class_2759.method_11837(world.getSaveHandler().method_11956(), blockPos, blockRotation, this.field_13015, random);
				this.setBoundingBoxFromChildren();
				this.field_12994 = true;
			}
		}

		@Override
		public boolean isValid() {
			return this.field_12994;
		}
	}
}
