package net.minecraft;

import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.VillagePieces;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class class_3912 extends class_3902<class_3911> {
	@Override
	public String method_17423() {
		return "Village";
	}

	@Override
	public int method_17433() {
		return 8;
	}

	@Override
	protected boolean method_17426(IWorld iWorld) {
		return iWorld.method_3588().hasStructures();
	}

	@Override
	protected ChunkPos method_17432(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.method_17013().method_17214();
		int n = chunkGenerator.method_17013().method_17215();
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((class_3812)random).method_17289(chunkGenerator.method_17024(), s, t, 10387312);
		s *= m;
		t *= m;
		s += random.nextInt(m - n);
		t += random.nextInt(m - n);
		return new ChunkPos(s, t);
	}

	@Override
	protected boolean method_17431(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		ChunkPos chunkPos = this.method_17432(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), Biomes.DEFAULT);
			return chunkGenerator.method_17015(biome, class_3844.field_19181);
		} else {
			return false;
		}
	}

	@Override
	protected class_3992 method_17428(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j) {
		Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), Biomes.DEFAULT);
		return new class_3912.class_38(iWorld, chunkGenerator, arg, i, j, biome);
	}

	public static class class_38 extends class_3992 {
		private boolean field_73;

		public class_38() {
		}

		public class_38(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j, Biome biome) {
			super(i, j, biome, arg, iWorld.method_3581());
			class_3911 lv = (class_3911)chunkGenerator.method_17021(biome, class_3844.field_19181);
			List<VillagePieces.PieceData> list = VillagePieces.getPieceData(arg, lv.field_19272);
			VillagePieces.StartPiece startPiece = new VillagePieces.StartPiece(0, arg, (i << 4) + 2, (j << 4) + 2, list, lv);
			this.field_19407.add(startPiece);
			startPiece.fillOpenings(startPiece, this.field_19407, arg);
			List<StructurePiece> list2 = startPiece.field_6247;
			List<StructurePiece> list3 = startPiece.field_6246;

			while (!list2.isEmpty() || !list3.isEmpty()) {
				if (list2.isEmpty()) {
					int k = arg.nextInt(list3.size());
					StructurePiece structurePiece = (StructurePiece)list3.remove(k);
					structurePiece.fillOpenings(startPiece, this.field_19407, arg);
				} else {
					int l = arg.nextInt(list2.size());
					StructurePiece structurePiece2 = (StructurePiece)list2.remove(l);
					structurePiece2.fillOpenings(startPiece, this.field_19407, arg);
				}
			}

			this.method_17660(iWorld);
			int m = 0;

			for (StructurePiece structurePiece3 : this.field_19407) {
				if (!(structurePiece3 instanceof VillagePieces.DelegatingPiece)) {
					m++;
				}
			}

			this.field_73 = m > 2;
		}

		@Override
		public boolean method_85() {
			return this.field_73;
		}

		@Override
		public void method_5533(NbtCompound nbtCompound) {
			super.method_5533(nbtCompound);
			nbtCompound.putBoolean("Valid", this.field_73);
		}

		@Override
		public void method_5534(NbtCompound nbtCompound) {
			super.method_5534(nbtCompound);
			this.field_73 = nbtCompound.getBoolean("Valid");
		}
	}
}
