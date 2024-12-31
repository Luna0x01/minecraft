package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.NetherFortressPieces;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class class_3869 extends class_3902<class_3868> {
	private static final List<Biome.SpawnEntry> field_19220 = Lists.newArrayList(
		new Biome.SpawnEntry[]{
			new Biome.SpawnEntry(EntityType.BLAZE, 10, 2, 3),
			new Biome.SpawnEntry(EntityType.ZOMBIE_PIGMAN, 5, 4, 4),
			new Biome.SpawnEntry(EntityType.WITHER_SKELETON, 8, 5, 5),
			new Biome.SpawnEntry(EntityType.SKELETON, 2, 5, 5),
			new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 3, 4, 4)
		}
	);

	@Override
	protected boolean method_17431(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		int k = i >> 4;
		int l = j >> 4;
		random.setSeed((long)(k ^ l << 4) ^ chunkGenerator.method_17024());
		random.nextInt();
		if (random.nextInt(3) != 0) {
			return false;
		} else if (i != (k << 4) + 4 + random.nextInt(8)) {
			return false;
		} else if (j != (l << 4) + 4 + random.nextInt(8)) {
			return false;
		} else {
			Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), Biomes.DEFAULT);
			return chunkGenerator.method_17015(biome, class_3844.field_19192);
		}
	}

	@Override
	protected boolean method_17426(IWorld iWorld) {
		return iWorld.method_3588().hasStructures();
	}

	@Override
	protected class_3992 method_17428(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j) {
		Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), Biomes.DEFAULT);
		return new class_3869.class_1260(iWorld, arg, i, j, biome);
	}

	@Override
	protected String method_17423() {
		return "Fortress";
	}

	@Override
	public int method_17433() {
		return 8;
	}

	@Override
	public List<Biome.SpawnEntry> method_17347() {
		return field_19220;
	}

	public static class class_1260 extends class_3992 {
		public class_1260() {
		}

		public class_1260(IWorld iWorld, class_3812 arg, int i, int j, Biome biome) {
			super(i, j, biome, arg, iWorld.method_3581());
			NetherFortressPieces.StartPiece startPiece = new NetherFortressPieces.StartPiece(arg, (i << 4) + 2, (j << 4) + 2);
			this.field_19407.add(startPiece);
			startPiece.fillOpenings(startPiece, this.field_19407, arg);
			List<StructurePiece> list = startPiece.pieces;

			while (!list.isEmpty()) {
				int k = arg.nextInt(list.size());
				StructurePiece structurePiece = (StructurePiece)list.remove(k);
				structurePiece.fillOpenings(startPiece, this.field_19407, arg);
			}

			this.method_17660(iWorld);
			this.method_17661(iWorld, arg, 48, 70);
		}
	}
}
