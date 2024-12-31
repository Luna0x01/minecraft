package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class class_3906 extends class_3883<class_3905> {
	private static final List<Biome.SpawnEntry> field_19264 = Lists.newArrayList(new Biome.SpawnEntry[]{new Biome.SpawnEntry(EntityType.WITCH, 1, 1, 1)});

	@Override
	protected String method_17423() {
		return "Swamp_Hut";
	}

	@Override
	public int method_17433() {
		return 3;
	}

	@Override
	protected class_3992 method_17428(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j) {
		Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), Biomes.PLAINS);
		return new class_3906.class_3907(iWorld, arg, i, j, biome);
	}

	@Override
	protected int method_17401() {
		return 14357620;
	}

	@Override
	public List<Biome.SpawnEntry> method_17347() {
		return field_19264;
	}

	public boolean method_17437(IWorld iWorld, BlockPos blockPos) {
		class_3992 lv = this.method_17430(iWorld, blockPos);
		if (lv != field_19260 && lv instanceof class_3906.class_3907 && !lv.method_17665().isEmpty()) {
			StructurePiece structurePiece = (StructurePiece)lv.method_17665().get(0);
			return structurePiece instanceof class_3993;
		} else {
			return false;
		}
	}

	public static class class_3907 extends class_3992 {
		public class_3907() {
		}

		public class_3907(IWorld iWorld, class_3812 arg, int i, int j, Biome biome) {
			super(i, j, biome, arg, iWorld.method_3581());
			class_3993 lv = new class_3993(arg, i * 16, j * 16);
			this.field_19407.add(lv);
			this.method_17660(iWorld);
		}
	}
}
