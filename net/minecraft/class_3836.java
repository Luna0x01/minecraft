package net.minecraft;

import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class class_3836 extends class_3883<class_3835> {
	@Override
	protected String method_17423() {
		return "Desert_Pyramid";
	}

	@Override
	public int method_17433() {
		return 3;
	}

	@Override
	protected class_3992 method_17428(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j) {
		Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), Biomes.PLAINS);
		return new class_3836.class_3837(iWorld, arg, i, j, biome);
	}

	@Override
	protected int method_17401() {
		return 14357617;
	}

	public static class class_3837 extends class_3992 {
		public class_3837() {
		}

		public class_3837(IWorld iWorld, class_3812 arg, int i, int j, Biome biome) {
			super(i, j, biome, arg, iWorld.method_3581());
			class_3974 lv = new class_3974(arg, i * 16, j * 16);
			this.field_19407.add(lv);
			this.method_17660(iWorld);
		}
	}
}
