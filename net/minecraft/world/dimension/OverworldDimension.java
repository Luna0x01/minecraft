package net.minecraft.world.dimension;

public class OverworldDimension extends Dimension {
	@Override
	public DimensionType getDimensionType() {
		return DimensionType.OVERWORLD;
	}

	@Override
	public boolean canChunkBeUnloaded(int x, int z) {
		return !this.world.isChunkInsideSpawnChunks(x, z);
	}
}
