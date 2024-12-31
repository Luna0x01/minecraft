package net.minecraft;

import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class class_3856 extends class_3883<class_3855> {
	@Override
	protected String method_17423() {
		return "Igloo";
	}

	@Override
	public int method_17433() {
		return 3;
	}

	@Override
	protected class_3992 method_17428(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j) {
		Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), Biomes.PLAINS);
		return new class_3856.class_3857(iWorld, chunkGenerator, arg, i, j, biome);
	}

	@Override
	protected int method_17401() {
		return 14357618;
	}

	public static class class_3857 extends class_3992 {
		public class_3857() {
		}

		public class_3857(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j, Biome biome) {
			super(i, j, biome, arg, iWorld.method_3581());
			class_3855 lv = (class_3855)chunkGenerator.method_17021(biome, class_3844.field_19186);
			int k = i * 16;
			int l = j * 16;
			BlockPos blockPos = new BlockPos(k, 90, l);
			BlockRotation blockRotation = BlockRotation.values()[arg.nextInt(BlockRotation.values().length)];
			class_3998 lv2 = iWorld.method_3587().method_11956();
			class_3976.method_17602(lv2, blockPos, blockRotation, this.field_19407, arg, lv);
			this.method_17660(iWorld);
		}
	}
}
