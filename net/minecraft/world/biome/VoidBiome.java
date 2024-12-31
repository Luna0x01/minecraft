package net.minecraft.world.biome;

public class VoidBiome extends Biome {
	public VoidBiome(Biome.Settings settings) {
		super(settings);
		this.monsterEntries.clear();
		this.passiveEntries.clear();
		this.waterEntries.clear();
		this.flyingEntries.clear();
		this.biomeDecorator = new VoidBiomeDecorator();
	}

	@Override
	public boolean method_11504() {
		return true;
	}
}
