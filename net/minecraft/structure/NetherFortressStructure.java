package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GeneratorConfig;

public class NetherFortressStructure extends StructureFeature {
	private List<Biome.SpawnEntry> monsterSpawns = Lists.newArrayList();

	public NetherFortressStructure() {
		this.monsterSpawns.add(new Biome.SpawnEntry(BlazeEntity.class, 10, 2, 3));
		this.monsterSpawns.add(new Biome.SpawnEntry(ZombiePigmanEntity.class, 5, 4, 4));
		this.monsterSpawns.add(new Biome.SpawnEntry(SkeletonEntity.class, 10, 4, 4));
		this.monsterSpawns.add(new Biome.SpawnEntry(MagmaCubeEntity.class, 3, 4, 4));
	}

	@Override
	public String getName() {
		return "Fortress";
	}

	public List<Biome.SpawnEntry> getMonsterSpawns() {
		return this.monsterSpawns;
	}

	@Override
	protected boolean shouldStartAt(int chunkX, int chunkZ) {
		int i = chunkX >> 4;
		int j = chunkZ >> 4;
		this.random.setSeed((long)(i ^ j << 4) ^ this.world.getSeed());
		this.random.nextInt();
		if (this.random.nextInt(3) != 0) {
			return false;
		} else {
			return chunkX != (i << 4) + 4 + this.random.nextInt(8) ? false : chunkZ == (j << 4) + 4 + this.random.nextInt(8);
		}
	}

	@Override
	protected GeneratorConfig getGeneratorConfig(int chunkX, int chunkZ) {
		return new NetherFortressStructure.FortressGeneratorConfig(this.world, this.random, chunkX, chunkZ);
	}

	public static class FortressGeneratorConfig extends GeneratorConfig {
		public FortressGeneratorConfig() {
		}

		public FortressGeneratorConfig(World world, Random random, int i, int j) {
			super(i, j);
			NetherFortressPieces.StartPiece startPiece = new NetherFortressPieces.StartPiece(random, (i << 4) + 2, (j << 4) + 2);
			this.children.add(startPiece);
			startPiece.fillOpenings(startPiece, this.children, random);
			List<StructurePiece> list = startPiece.pieces;

			while (!list.isEmpty()) {
				int k = random.nextInt(list.size());
				StructurePiece structurePiece = (StructurePiece)list.remove(k);
				structurePiece.fillOpenings(startPiece, this.children, random);
			}

			this.setBoundingBoxFromChildren();
			this.method_81(world, random, 48, 70);
		}
	}
}
