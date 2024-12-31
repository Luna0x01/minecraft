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
	private List<Biome> biomes;
	private boolean initialized;
	private ChunkPos[] count = new ChunkPos[3];
	private double distance = 32.0;
	private int spread = 3;

	public StrongholdStructure() {
		this.biomes = Lists.newArrayList();

		for (Biome biome : Biome.getBiomes()) {
			if (biome != null && biome.depth > 0.0F) {
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
	protected boolean shouldStartAt(int chunkX, int chunkZ) {
		if (!this.initialized) {
			Random random = new Random();
			random.setSeed(this.world.getSeed());
			double d = random.nextDouble() * Math.PI * 2.0;
			int i = 1;

			for (int j = 0; j < this.count.length; j++) {
				double e = (1.25 * (double)i + random.nextDouble()) * this.distance * (double)i;
				int k = (int)Math.round(Math.cos(d) * e);
				int l = (int)Math.round(Math.sin(d) * e);
				BlockPos blockPos = this.world.getBiomeSource().method_3855((k << 4) + 8, (l << 4) + 8, 112, this.biomes, random);
				if (blockPos != null) {
					k = blockPos.getX() >> 4;
					l = blockPos.getZ() >> 4;
				}

				this.count[j] = new ChunkPos(k, l);
				d += (Math.PI * 2) * (double)i / (double)this.spread;
				if (j == this.spread) {
					i += 2 + random.nextInt(5);
					this.spread = this.spread + 1 + random.nextInt(2);
				}
			}

			this.initialized = true;
		}

		for (ChunkPos chunkPos : this.count) {
			if (chunkX == chunkPos.x && chunkZ == chunkPos.z) {
				return true;
			}
		}

		return false;
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

		while (strongholdGeneratorConfig.getChildren().isEmpty() || ((StrongholdPieces.StartPiece)strongholdGeneratorConfig.getChildren().get(0)).portalRoom == null) {
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
			this.children.add(startPiece);
			startPiece.fillOpenings(startPiece, this.children, random);
			List<StructurePiece> list = startPiece.pieces;

			while (!list.isEmpty()) {
				int k = random.nextInt(list.size());
				StructurePiece structurePiece = (StructurePiece)list.remove(k);
				structurePiece.fillOpenings(startPiece, this.children, random);
			}

			this.setBoundingBoxFromChildren();
			this.method_80(world, random, 10);
		}
	}
}
