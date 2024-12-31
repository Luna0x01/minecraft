package net.minecraft.structure;

import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.math.MathHelper;
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
	protected GeneratorConfig getGeneratorConfig(int chunkX, int chunkZ) {
		return new MineshaftGeneratorConfig(this.world, this.random, chunkX, chunkZ);
	}
}
