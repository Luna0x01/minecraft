package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.MineshaftPieces;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class class_3867 extends class_3902<class_3866> {
	@Override
	protected boolean method_17431(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		((class_3812)random).method_17291(chunkGenerator.method_17024(), i, j);
		Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), Biomes.DEFAULT);
		if (chunkGenerator.method_17015(biome, class_3844.field_19182)) {
			class_3866 lv = (class_3866)chunkGenerator.method_17021(biome, class_3844.field_19182);
			double d = lv.field_19215;
			return random.nextDouble() < d;
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
		return new class_3867.class_1258(iWorld, chunkGenerator, arg, i, j, biome);
	}

	@Override
	protected String method_17423() {
		return "Mineshaft";
	}

	@Override
	public int method_17433() {
		return 8;
	}

	public static class class_1258 extends class_3992 {
		private class_3867.class_3014 field_19217;

		public class_1258() {
		}

		public class_1258(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j, Biome biome) {
			super(i, j, biome, arg, iWorld.method_3581());
			class_3866 lv = (class_3866)chunkGenerator.method_17021(biome, class_3844.field_19182);
			this.field_19217 = lv.field_19216;
			MineshaftPieces.MineshaftRoom mineshaftRoom = new MineshaftPieces.MineshaftRoom(0, arg, (i << 4) + 2, (j << 4) + 2, this.field_19217);
			this.field_19407.add(mineshaftRoom);
			mineshaftRoom.fillOpenings(mineshaftRoom, this.field_19407, arg);
			this.method_17660(iWorld);
			if (lv.field_19216 == class_3867.class_3014.MESA) {
				int k = -5;
				int l = iWorld.method_8483() - this.field_19408.maxY + this.field_19408.getBlockCountY() / 2 - -5;
				this.field_19408.move(0, l, 0);

				for (StructurePiece structurePiece : this.field_19407) {
					structurePiece.translate(0, l, 0);
				}
			} else {
				this.method_17663(iWorld, arg, 10);
			}
		}
	}

	public static enum class_3014 {
		NORMAL,
		MESA;

		public static class_3867.class_3014 method_13368(int i) {
			return i >= 0 && i < values().length ? values()[i] : NORMAL;
		}
	}
}
