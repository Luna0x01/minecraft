package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeSourceType;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.DebugChunkGenerator;
import net.minecraft.world.chunk.EndChunkGenerator;
import net.minecraft.world.chunk.FlatChunkGenerator;
import net.minecraft.world.chunk.SurfaceChunkGenerator;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.level.LevelGeneratorType;

public class class_3794 extends Dimension {
	@Override
	public DimensionType method_11789() {
		return DimensionType.OVERWORLD;
	}

	@Override
	public boolean method_17189(int i, int j) {
		return !this.world.isChunkInsideSpawnChunks(i, j) && super.method_17189(i, j);
	}

	@Override
	protected void init() {
		this.field_18953 = true;
	}

	@Override
	public ChunkGenerator<? extends class_3798> method_17193() {
		LevelGeneratorType levelGeneratorType = this.world.method_3588().getGeneratorType();
		ChunkGeneratorType<class_3917, FlatChunkGenerator> chunkGeneratorType = ChunkGeneratorType.FLAT;
		ChunkGeneratorType<class_3799, DebugChunkGenerator> chunkGeneratorType2 = ChunkGeneratorType.DEBUG;
		ChunkGeneratorType<class_3807, class_3808> chunkGeneratorType3 = ChunkGeneratorType.CAVES;
		ChunkGeneratorType<class_3811, EndChunkGenerator> chunkGeneratorType4 = ChunkGeneratorType.FLOATING_ISLANDS;
		ChunkGeneratorType<class_3809, SurfaceChunkGenerator> chunkGeneratorType5 = ChunkGeneratorType.SURFACE;
		BiomeSourceType<class_3633, class_3632> biomeSourceType = BiomeSourceType.FIXED;
		BiomeSourceType<class_3660, class_3659> biomeSourceType2 = BiomeSourceType.VANILLA_LAYERED;
		BiomeSourceType<class_3617, class_3616> biomeSourceType3 = BiomeSourceType.CHECKERBOARD;
		if (levelGeneratorType == LevelGeneratorType.FLAT) {
			class_3917 lv = class_3917.method_17480(new Dynamic(class_4372.field_21487, this.world.method_3588().method_17950()));
			class_3633 lv2 = biomeSourceType.method_16486().method_16498(lv.method_17497());
			return chunkGeneratorType.create(this.world, biomeSourceType.method_16484(lv2), lv);
		} else if (levelGeneratorType == LevelGeneratorType.DEBUG) {
			class_3633 lv3 = biomeSourceType.method_16486().method_16498(Biomes.PLAINS);
			return chunkGeneratorType2.create(this.world, biomeSourceType.method_16484(lv3), chunkGeneratorType2.method_17040());
		} else if (levelGeneratorType != LevelGeneratorType.field_17505) {
			class_3809 lv10 = chunkGeneratorType5.method_17040();
			class_3660 lv11 = biomeSourceType2.method_16486().method_16535(this.world.method_3588()).method_16534(lv10);
			return chunkGeneratorType5.create(this.world, biomeSourceType2.method_16484(lv11), lv10);
		} else {
			SingletonBiomeSource singletonBiomeSource = null;
			JsonElement jsonElement = (JsonElement)Dynamic.convert(class_4372.field_21487, JsonOps.INSTANCE, this.world.method_3588().method_17950());
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if (jsonObject.has("biome_source") && jsonObject.getAsJsonObject("biome_source").has("type") && jsonObject.getAsJsonObject("biome_source").has("options")) {
				Identifier identifier = new Identifier(jsonObject.getAsJsonObject("biome_source").getAsJsonPrimitive("type").getAsString());
				JsonObject jsonObject2 = jsonObject.getAsJsonObject("biome_source").getAsJsonObject("options");
				Biome[] biomes = new Biome[]{Biomes.OCEAN};
				if (jsonObject2.has("biomes")) {
					JsonArray jsonArray = jsonObject2.getAsJsonArray("biomes");
					biomes = jsonArray.size() > 0 ? new Biome[jsonArray.size()] : new Biome[]{Biomes.OCEAN};

					for (int i = 0; i < jsonArray.size(); i++) {
						Biome biome = Registry.BIOME.getByIdentifier(new Identifier(jsonArray.get(i).getAsString()));
						biomes[i] = biome != null ? biome : Biomes.OCEAN;
					}
				}

				if (BiomeSourceType.FIXED.method_16487().equals(identifier)) {
					class_3633 lv4 = biomeSourceType.method_16486().method_16498(biomes[0]);
					singletonBiomeSource = biomeSourceType.method_16484(lv4);
				}

				if (BiomeSourceType.CHECKERBOARD.method_16487().equals(identifier)) {
					int j = jsonObject2.has("size") ? jsonObject2.getAsJsonPrimitive("size").getAsInt() : 2;
					class_3617 lv5 = biomeSourceType3.method_16486().method_16495(biomes).method_16494(j);
					singletonBiomeSource = biomeSourceType3.method_16484(lv5);
				}

				if (BiomeSourceType.VANILLA_LAYERED.method_16487().equals(identifier)) {
					class_3660 lv6 = biomeSourceType2.method_16486().method_16534(new class_3809()).method_16535(this.world.method_3588());
					singletonBiomeSource = biomeSourceType2.method_16484(lv6);
				}
			}

			if (singletonBiomeSource == null) {
				singletonBiomeSource = biomeSourceType.method_16484(biomeSourceType.method_16486().method_16498(Biomes.OCEAN));
			}

			BlockState blockState = Blocks.STONE.getDefaultState();
			BlockState blockState2 = Blocks.WATER.getDefaultState();
			if (jsonObject.has("chunk_generator") && jsonObject.getAsJsonObject("chunk_generator").has("options")) {
				if (jsonObject.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_block")) {
					String string = jsonObject.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_block").getAsString();
					Block block = Registry.BLOCK.get(new Identifier(string));
					if (block != null) {
						blockState = block.getDefaultState();
					}
				}

				if (jsonObject.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_fluid")) {
					String string2 = jsonObject.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_fluid").getAsString();
					Block block2 = Registry.BLOCK.get(new Identifier(string2));
					if (block2 != null) {
						blockState2 = block2.getDefaultState();
					}
				}
			}

			if (jsonObject.has("chunk_generator") && jsonObject.getAsJsonObject("chunk_generator").has("type")) {
				Identifier identifier2 = new Identifier(jsonObject.getAsJsonObject("chunk_generator").getAsJsonPrimitive("type").getAsString());
				if (ChunkGeneratorType.CAVES.method_17042().equals(identifier2)) {
					class_3807 lv7 = chunkGeneratorType3.method_17040();
					lv7.method_17212(blockState);
					lv7.method_17213(blockState2);
					return chunkGeneratorType3.create(this.world, singletonBiomeSource, lv7);
				}

				if (ChunkGeneratorType.FLOATING_ISLANDS.method_17042().equals(identifier2)) {
					class_3811 lv8 = chunkGeneratorType4.method_17040();
					lv8.method_17279(new BlockPos(0, 64, 0));
					lv8.method_17212(blockState);
					lv8.method_17213(blockState2);
					return chunkGeneratorType4.create(this.world, singletonBiomeSource, lv8);
				}
			}

			class_3809 lv9 = chunkGeneratorType5.method_17040();
			lv9.method_17212(blockState);
			lv9.method_17213(blockState2);
			return chunkGeneratorType5.create(this.world, singletonBiomeSource, lv9);
		}
	}

	@Nullable
	@Override
	public BlockPos method_17191(ChunkPos chunkPos, boolean bl) {
		for (int i = chunkPos.getActualX(); i <= chunkPos.getOppositeX(); i++) {
			for (int j = chunkPos.getActualZ(); j <= chunkPos.getOppositeZ(); j++) {
				BlockPos blockPos = this.method_17190(i, j, bl);
				if (blockPos != null) {
					return blockPos;
				}
			}
		}

		return null;
	}

	@Nullable
	@Override
	public BlockPos method_17190(int i, int j, boolean bl) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(i, 0, j);
		Biome biome = this.world.method_8577(mutable);
		BlockState blockState = biome.method_16450().method_17720();
		if (bl && !blockState.getBlock().isIn(BlockTags.VALID_SPAWN)) {
			return null;
		} else {
			Chunk chunk = this.world.method_16347(i >> 4, j >> 4);
			int k = chunk.method_16992(class_3804.class_3805.MOTION_BLOCKING, i & 15, j & 15);
			if (k < 0) {
				return null;
			} else if (chunk.method_16992(class_3804.class_3805.WORLD_SURFACE, i & 15, j & 15) > chunk.method_16992(class_3804.class_3805.OCEAN_FLOOR, i & 15, j & 15)) {
				return null;
			} else {
				for (int l = k + 1; l >= 0; l--) {
					mutable.setPosition(i, l, j);
					BlockState blockState2 = this.world.getBlockState(mutable);
					if (!blockState2.getFluidState().isEmpty()) {
						break;
					}

					if (blockState2.equals(blockState)) {
						return mutable.up().toImmutable();
					}
				}

				return null;
			}
		}
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		int i = (int)(timeOfDay % 24000L);
		float f = ((float)i + tickDelta) / 24000.0F - 0.25F;
		if (f < 0.0F) {
			f++;
		}

		if (f > 1.0F) {
			f--;
		}

		float var7 = 1.0F - (float)((Math.cos((double)f * Math.PI) + 1.0) / 2.0);
		return f + (var7 - f) / 3.0F;
	}

	@Override
	public boolean canPlayersSleep() {
		return true;
	}

	@Override
	public Vec3d getFogColor(float skyAngle, float tickDelta) {
		float f = MathHelper.cos(skyAngle * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		float g = 0.7529412F;
		float h = 0.84705883F;
		float i = 1.0F;
		g *= f * 0.94F + 0.06F;
		h *= f * 0.94F + 0.06F;
		i *= f * 0.91F + 0.09F;
		return new Vec3d((double)g, (double)h, (double)i);
	}

	@Override
	public boolean containsWorldSpawn() {
		return true;
	}

	@Override
	public boolean isFogThick(int x, int z) {
		return false;
	}
}
