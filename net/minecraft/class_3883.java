package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

public abstract class class_3883<C extends class_3845> extends class_3902<C> {
	@Override
	protected ChunkPos method_17432(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = this.method_17399(chunkGenerator);
		int n = this.method_17400(chunkGenerator);
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((class_3812)random).method_17289(chunkGenerator.method_17024(), s, t, this.method_17401());
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
			Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos(i * 16 + 9, 0, j * 16 + 9), null);
			if (chunkGenerator.method_17015(biome, this)) {
				return true;
			}
		}

		return false;
	}

	protected int method_17399(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.method_17013().method_17221();
	}

	protected int method_17400(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.method_17013().method_17222();
	}

	@Override
	protected boolean method_17426(IWorld iWorld) {
		return iWorld.method_3588().hasStructures();
	}

	@Override
	protected abstract class_3992 method_17428(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j);

	protected abstract int method_17401();
}
