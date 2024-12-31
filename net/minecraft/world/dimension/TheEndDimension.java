package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.DragonRespawnAnimation;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.class_2711;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.EndChunkGenerator;

public class TheEndDimension extends Dimension {
	private DragonRespawnAnimation dragonFight = null;

	@Override
	public void init() {
		this.field_4787 = new class_2711(Biomes.SKY);
		this.hasNoSkylight = true;
		NbtCompound nbtCompound = this.world.getLevelProperties().method_11954(DimensionType.THE_END);
		this.dragonFight = this.world instanceof ServerWorld ? new DragonRespawnAnimation((ServerWorld)this.world, nbtCompound.getCompound("DragonFight")) : null;
	}

	@Override
	public ChunkGenerator getChunkGenerator() {
		return new EndChunkGenerator(this.world, this.world.getLevelProperties().hasStructures(), this.world.getSeed());
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
		float f = MathHelper.cos(skyAngle * (float) (Math.PI * 2)) * 2.0F + 0.5F;
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
		return this.world.method_8540(new BlockPos(x, 0, z)).getMaterial().blocksMovement();
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
		return false;
	}

	@Override
	public DimensionType getDimensionType() {
		return DimensionType.THE_END;
	}

	@Override
	public void method_11790() {
		NbtCompound nbtCompound = new NbtCompound();
		if (this.dragonFight != null) {
			nbtCompound.put("DragonFight", this.dragonFight.toTag());
		}

		this.world.getLevelProperties().method_11955(DimensionType.THE_END, nbtCompound);
	}

	@Override
	public void method_11791() {
		if (this.dragonFight != null) {
			this.dragonFight.method_11805();
		}
	}

	@Nullable
	public DragonRespawnAnimation method_11818() {
		return this.dragonFight;
	}
}
