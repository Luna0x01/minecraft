package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.TemplePieces;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

public class class_3817 extends class_3902<class_3816> {
	@Override
	protected boolean method_17431(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), null);
		if (chunkGenerator.method_17015(biome, class_3844.field_19194)) {
			((class_3812)random).method_17289(chunkGenerator.method_17024(), i, j, 10387320);
			class_3816 lv = (class_3816)chunkGenerator.method_17021(biome, class_3844.field_19194);
			return random.nextFloat() < lv.field_19072;
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
		Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), null);
		return new class_3817.class_3818(iWorld, chunkGenerator, arg, i, j, biome);
	}

	@Override
	protected String method_17423() {
		return "Buried_Treasure";
	}

	@Override
	public int method_17433() {
		return 1;
	}

	public static class class_3818 extends class_3992 {
		public class_3818() {
		}

		public class_3818(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j, Biome biome) {
			super(i, j, biome, arg, iWorld.method_3581());
			int k = i * 16;
			int l = j * 16;
			BlockPos blockPos = new BlockPos(k + 9, 90, l + 9);
			this.field_19407.add(new TemplePieces.class_3970(blockPos));
			this.method_17660(iWorld);
		}

		@Override
		public BlockPos method_17658() {
			return new BlockPos((this.field_19409 << 4) + 9, 0, (this.field_19410 << 4) + 9);
		}
	}
}
