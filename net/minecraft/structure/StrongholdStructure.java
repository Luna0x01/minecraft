package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GeneratorConfig;

public class StrongholdStructure extends StructureFeature {
	private final List<Biome> biomes;
	private boolean initialized;
	private ChunkPos[] count = new ChunkPos[128];
	private double distance = 32.0;
	private int spread = 3;

	public StrongholdStructure() {
		this.biomes = Lists.newArrayList();

		for (Biome biome : Biome.REGISTRY) {
			if (biome != null && biome.getDepth() > 0.0F) {
				this.biomes.add(biome);
			}
		}
	}

	public StrongholdStructure(Map<String, String> map) {
		this();

		for (Entry<String, String> entry : map.entrySet()) {
			if (((String)entry.getKey()).equals("distance")) {
				this.distance = MathHelper.parseDouble((String)entry.getValue(), this.distance, 1.0);
			} else if (((String)entry.getKey()).equals("count")) {
				this.count = new ChunkPos[MathHelper.parseInt((String)entry.getValue(), this.count.length, 1)];
			} else if (((String)entry.getKey()).equals("spread")) {
				this.spread = MathHelper.parseInt((String)entry.getValue(), this.spread, 1);
			}
		}
	}

	@Override
	public String getName() {
		return "Stronghold";
	}

	@Override
	public BlockPos method_9269(World world, BlockPos pos) {
		if (!this.initialized) {
			this.method_11850();
			this.initialized = true;
		}

		BlockPos blockPos = null;
		BlockPos.Mutable mutable = new BlockPos.Mutable(0, 0, 0);
		double d = Double.MAX_VALUE;

		for (ChunkPos chunkPos : this.count) {
			mutable.setPosition((chunkPos.x << 4) + 8, 32, (chunkPos.z << 4) + 8);
			double e = mutable.getSquaredDistance(pos);
			if (blockPos == null) {
				blockPos = new BlockPos(mutable);
				d = e;
			} else if (e < d) {
				blockPos = new BlockPos(mutable);
				d = e;
			}
		}

		return blockPos;
	}

	@Override
	protected boolean shouldStartAt(int chunkX, int chunkZ) {
		if (!this.initialized) {
			this.method_11850();
			this.initialized = true;
		}

		for (ChunkPos chunkPos : this.count) {
			if (chunkX == chunkPos.x && chunkZ == chunkPos.z) {
				return true;
			}
		}

		return false;
	}

	private void method_11850() {
		this.method_5515(this.world);
		int i = 0;

		for (GeneratorConfig generatorConfig : this.field_13012.values()) {
			if (i < this.count.length) {
				this.count[i++] = new ChunkPos(generatorConfig.getChunkX(), generatorConfig.getChunkZ());
			}
		}

		Random random = new Random();
		random.setSeed(this.world.getSeed());
		double d = random.nextDouble() * Math.PI * 2.0;
		int j = 0;
		int k = 0;
		int l = this.field_13012.size();
		if (l < this.count.length) {
			for (int m = 0; m < this.count.length; m++) {
				double e = 4.0 * this.distance + this.distance * (double)j * 6.0 + (random.nextDouble() - 0.5) * this.distance * 2.5;
				int n = (int)Math.round(Math.cos(d) * e);
				int o = (int)Math.round(Math.sin(d) * e);
				BlockPos blockPos = this.world.method_3726().method_11534((n << 4) + 8, (o << 4) + 8, 112, this.biomes, random);
				if (blockPos != null) {
					n = blockPos.getX() >> 4;
					o = blockPos.getZ() >> 4;
				}

				if (m >= l) {
					this.count[m] = new ChunkPos(n, o);
				}

				d += (Math.PI * 2) / (double)this.spread;
				if (++k == this.spread) {
					j++;
					k = 0;
					this.spread = this.spread + 2 * this.spread / (j + 1);
					this.spread = Math.min(this.spread, this.count.length - m);
					d += random.nextDouble() * Math.PI * 2.0;
				}
			}
		}
	}

	@Override
	protected List<BlockPos> method_50() {
		List<BlockPos> list = Lists.newArrayList();

		for (ChunkPos chunkPos : this.count) {
			if (chunkPos != null) {
				list.add(chunkPos.toBlockPos(64));
			}
		}

		return list;
	}

	@Override
	protected GeneratorConfig getGeneratorConfig(int chunkX, int chunkZ) {
		StrongholdStructure.StrongholdGeneratorConfig strongholdGeneratorConfig = new StrongholdStructure.StrongholdGeneratorConfig(
			this.world, this.random, chunkX, chunkZ
		);

		while (
			strongholdGeneratorConfig.method_11855().isEmpty() || ((StrongholdPieces.StartPiece)strongholdGeneratorConfig.method_11855().get(0)).portalRoom == null
		) {
			strongholdGeneratorConfig = new StrongholdStructure.StrongholdGeneratorConfig(this.world, this.random, chunkX, chunkZ);
		}

		return strongholdGeneratorConfig;
	}

	public static class StrongholdGeneratorConfig extends GeneratorConfig {
		public StrongholdGeneratorConfig() {
		}

		public StrongholdGeneratorConfig(World world, Random random, int i, int j) {
			super(i, j);
			StrongholdPieces.init();
			StrongholdPieces.StartPiece startPiece = new StrongholdPieces.StartPiece(0, random, (i << 4) + 2, (j << 4) + 2);
			this.field_13015.add(startPiece);
			startPiece.fillOpenings(startPiece, this.field_13015, random);
			List<StructurePiece> list = startPiece.pieces;

			while (!list.isEmpty()) {
				int k = random.nextInt(list.size());
				StructurePiece structurePiece = (StructurePiece)list.remove(k);
				structurePiece.fillOpenings(startPiece, this.field_13015, random);
			}

			this.setBoundingBoxFromChildren();
			this.method_80(world, random, 10);
		}
	}
}
