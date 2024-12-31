package net.minecraft.world.dimension;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.EndChunkGenerator;

public class TheEndDimension extends Dimension {
	@Override
	public void init() {
		this.biomeSource = new SingletonBiomeSource(Biome.THE_END, 0.0F);
		this.dimensionType = 1;
		this.hasNoSkylight = true;
	}

	@Override
	public ChunkProvider createChunkGenerator() {
		return new EndChunkGenerator(this.world, this.world.getSeed());
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 0.0F;
	}

	@Override
	public float[] getBackgroundColor(float skyAngle, float tickDelta) {
		return null;
	}

	@Override
	public Vec3d getFogColor(float skyAngle, float tickDelta) {
		int i = 10518688;
		float f = MathHelper.cos(skyAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		float g = (float)(i >> 16 & 0xFF) / 255.0F;
		float h = (float)(i >> 8 & 0xFF) / 255.0F;
		float j = (float)(i & 0xFF) / 255.0F;
		g *= f * 0.0F + 0.15F;
		h *= f * 0.0F + 0.15F;
		j *= f * 0.0F + 0.15F;
		return new Vec3d((double)g, (double)h, (double)j);
	}

	@Override
	public boolean hasGround() {
		return false;
	}

	@Override
	public boolean containsWorldSpawn() {
		return false;
	}

	@Override
	public boolean canPlayersSleep() {
		return false;
	}

	@Override
	public float getCloudHeight() {
		return 8.0F;
	}

	@Override
	public boolean isSpawnableBlock(int x, int z) {
		return this.world.getBlockAt(new BlockPos(x, 0, z)).getMaterial().blocksMovement();
	}

	@Override
	public BlockPos getForcedSpawnPoint() {
		return new BlockPos(100, 50, 0);
	}

	@Override
	public int getAverageYLevel() {
		return 50;
	}

	@Override
	public boolean isFogThick(int x, int z) {
		return true;
	}

	@Override
	public String getName() {
		return "The End";
	}

	@Override
	public String getPersistentStateSuffix() {
		return "_end";
	}
}
