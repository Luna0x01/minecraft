package net.minecraft.structure;

import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MesaBiome;
import net.minecraft.world.gen.GeneratorConfig;
import net.minecraft.world.gen.MineshaftGeneratorConfig;

public class MineshaftStructure extends StructureFeature {
	private double chance = 0.004;

	public MineshaftStructure() {
	}

	@Override
	public String getName() {
		return "Mineshaft";
	}

	public MineshaftStructure(Map<String, String> map) {
		for (Entry<String, String> entry : map.entrySet()) {
			if (((String)entry.getKey()).equals("chance")) {
				this.chance = MathHelper.parseDouble((String)entry.getValue(), this.chance);
			}
		}
	}

	@Override
	protected boolean shouldStartAt(int chunkX, int chunkZ) {
		return this.random.nextDouble() < this.chance && this.random.nextInt(80) < Math.max(Math.abs(chunkX), Math.abs(chunkZ));
	}

	@Override
	public BlockPos method_9269(World world, BlockPos blockPos, boolean bl) {
		int i = 1000;
		int j = blockPos.getX() >> 4;
		int k = blockPos.getZ() >> 4;

		for (int l = 0; l <= 1000; l++) {
			for (int m = -l; m <= l; m++) {
				boolean bl2 = m == -l || m == l;

				for (int n = -l; n <= l; n++) {
					boolean bl3 = n == -l || n == l;
					if (bl2 || bl3) {
						int o = j + m;
						int p = k + n;
						this.random.setSeed((long)(o ^ p) ^ world.getSeed());
						this.random.nextInt();
						if (this.shouldStartAt(o, p) && (!bl || !world.method_13690(o, p))) {
							return new BlockPos((o << 4) + 8, 64, (p << 4) + 8);
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	protected GeneratorConfig getGeneratorConfig(int chunkX, int chunkZ) {
		Biome biome = this.world.getBiome(new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8));
		MineshaftStructure.class_3014 lv = biome instanceof MesaBiome ? MineshaftStructure.class_3014.MESA : MineshaftStructure.class_3014.NORMAL;
		return new MineshaftGeneratorConfig(this.world, this.random, chunkX, chunkZ, lv);
	}

	public static enum class_3014 {
		NORMAL,
		MESA;

		public static MineshaftStructure.class_3014 method_13368(int i) {
			return i >= 0 && i < values().length ? values()[i] : NORMAL;
		}
	}
}
