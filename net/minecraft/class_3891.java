package net.minecraft;

import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

public class class_3891 extends class_3883<class_3890> {
	@Override
	protected String method_17423() {
		return "Shipwreck";
	}

	@Override
	public int method_17433() {
		return 3;
	}

	@Override
	protected class_3992 method_17428(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j) {
		Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), null);
		return new class_3891.class_3892(iWorld, chunkGenerator, arg, i, j, biome);
	}

	@Override
	protected int method_17401() {
		return 165745295;
	}

	@Override
	protected int method_17399(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.method_17013().method_17223();
	}

	@Override
	protected int method_17400(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.method_17013().method_17224();
	}

	public static class class_3892 extends class_3992 {
		public class_3892() {
		}

		public class_3892(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j, Biome biome) {
			super(i, j, biome, arg, iWorld.method_3581());
			class_3890 lv = (class_3890)chunkGenerator.method_17021(biome, class_3844.field_19187);
			BlockRotation blockRotation = BlockRotation.values()[arg.nextInt(BlockRotation.values().length)];
			BlockPos blockPos = new BlockPos(i * 16, 90, j * 16);
			class_3988.method_17638(iWorld.method_3587().method_11956(), blockPos, blockRotation, this.field_19407, arg, lv);
			this.method_17660(iWorld);
		}
	}
}
