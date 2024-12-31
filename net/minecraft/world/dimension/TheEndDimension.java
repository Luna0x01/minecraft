package net.minecraft.world.dimension;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3811;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.DragonRespawnAnimation;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.BiomeSourceType;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class TheEndDimension extends Dimension {
	public static final BlockPos field_18968 = new BlockPos(100, 50, 0);
	private DragonRespawnAnimation dragonFight;

	@Override
	public void init() {
		NbtCompound nbtCompound = this.world.method_3588().method_11954(DimensionType.THE_END);
		this.dragonFight = this.world instanceof ServerWorld ? new DragonRespawnAnimation((ServerWorld)this.world, nbtCompound.getCompound("DragonFight")) : null;
		this.field_18953 = false;
	}

	@Override
	public ChunkGenerator<?> method_17193() {
		class_3811 lv = ChunkGeneratorType.FLOATING_ISLANDS.method_17040();
		lv.method_17212(Blocks.END_STONE.getDefaultState());
		lv.method_17213(Blocks.AIR.getDefaultState());
		lv.method_17279(this.getForcedSpawnPoint());
		return ChunkGeneratorType.FLOATING_ISLANDS
			.create(this.world, BiomeSourceType.THE_END.method_16484(BiomeSourceType.THE_END.method_16486().method_16544(this.world.method_3581())), lv);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 0.0F;
	}

	@Nullable
	@Override
	public float[] getBackgroundColor(float skyAngle, float tickDelta) {
		return null;
	}

	@Override
	public Vec3d getFogColor(float skyAngle, float tickDelta) {
		int i = 10518688;
		float f = MathHelper.cos(skyAngle * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		float g = 0.627451F;
		float h = 0.5019608F;
		float j = 0.627451F;
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

	@Nullable
	@Override
	public BlockPos method_17191(ChunkPos chunkPos, boolean bl) {
		Random random = new Random(this.world.method_3581());
		BlockPos blockPos = new BlockPos(chunkPos.getActualX() + random.nextInt(15), 0, chunkPos.getOppositeZ() + random.nextInt(15));
		return this.world.method_8540(blockPos).getMaterial().blocksMovement() ? blockPos : null;
	}

	@Override
	public BlockPos getForcedSpawnPoint() {
		return field_18968;
	}

	@Nullable
	@Override
	public BlockPos method_17190(int i, int j, boolean bl) {
		return this.method_17191(new ChunkPos(i >> 4, j >> 4), bl);
	}

	@Override
	public boolean isFogThick(int x, int z) {
		return false;
	}

	@Override
	public DimensionType method_11789() {
		return DimensionType.THE_END;
	}

	@Override
	public void method_11790() {
		NbtCompound nbtCompound = new NbtCompound();
		if (this.dragonFight != null) {
			nbtCompound.put("DragonFight", this.dragonFight.toTag());
		}

		this.world.method_3588().method_11955(DimensionType.THE_END, nbtCompound);
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
