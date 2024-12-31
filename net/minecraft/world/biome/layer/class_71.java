package net.minecraft.world.biome.layer;

public class class_71 extends class_84 {
	public class_71(long l, Layer layer) {
		super(l, layer);
	}

	@Override
	protected int method_6598(int i, int j, int k, int l) {
		return this.getRandomBiome(new int[]{i, j, k, l});
	}
}
