package net.minecraft.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GeneratorConfig;

public class VillageStructure extends StructureFeature {
	public static final List<Biome> BIOMES = Arrays.asList(Biome.PLAINS, Biome.DESERT, Biome.SAVANNA);
	private int size;
	private int distance = 32;
	private int field_4973 = 8;

	public VillageStructure() {
	}

	public VillageStructure(Map<String, String> map) {
		this();

		for (Entry<String, String> entry : map.entrySet()) {
			if (((String)entry.getKey()).equals("size")) {
				this.size = MathHelper.parseInt((String)entry.getValue(), this.size, 0);
			} else if (((String)entry.getKey()).equals("distance")) {
				this.distance = MathHelper.parseInt((String)entry.getValue(), this.distance, this.field_4973 + 1);
			}
		}
	}

	@Override
	public String getName() {
		return "Village";
	}

	@Override
	protected boolean shouldStartAt(int chunkX, int chunkZ) {
		int i = chunkX;
		int j = chunkZ;
		if (chunkX < 0) {
			chunkX -= this.distance - 1;
		}

		if (chunkZ < 0) {
			chunkZ -= this.distance - 1;
		}

		int k = chunkX / this.distance;
		int l = chunkZ / this.distance;
		Random random = this.world.getStructureRandom(k, l, 10387312);
		k *= this.distance;
		l *= this.distance;
		k += random.nextInt(this.distance - this.field_4973);
		l += random.nextInt(this.distance - this.field_4973);
		if (i == k && j == l) {
			boolean bl = this.world.getBiomeSource().isValid(i * 16 + 8, j * 16 + 8, 0, BIOMES);
			if (bl) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected GeneratorConfig getGeneratorConfig(int chunkX, int chunkZ) {
		return new VillageStructure.VillageGeneratorConfig(this.world, this.random, chunkX, chunkZ, this.size);
	}

	public static class VillageGeneratorConfig extends GeneratorConfig {
		private boolean valid;

		public VillageGeneratorConfig() {
		}

		public VillageGeneratorConfig(World world, Random random, int i, int j, int k) {
			super(i, j);
			List<VillagePieces.PieceData> list = VillagePieces.getPieceData(random, k);
			VillagePieces.StartPiece startPiece = new VillagePieces.StartPiece(world.getBiomeSource(), 0, random, (i << 4) + 2, (j << 4) + 2, list, k);
			this.children.add(startPiece);
			startPiece.fillOpenings(startPiece, this.children, random);
			List<StructurePiece> list2 = startPiece.field_6247;
			List<StructurePiece> list3 = startPiece.field_6246;

			while (!list2.isEmpty() || !list3.isEmpty()) {
				if (list2.isEmpty()) {
					int l = random.nextInt(list3.size());
					StructurePiece structurePiece = (StructurePiece)list3.remove(l);
					structurePiece.fillOpenings(startPiece, this.children, random);
				} else {
					int m = random.nextInt(list2.size());
					StructurePiece structurePiece2 = (StructurePiece)list2.remove(m);
					structurePiece2.fillOpenings(startPiece, this.children, random);
				}
			}

			this.setBoundingBoxFromChildren();
			int n = 0;

			for (StructurePiece structurePiece3 : this.children) {
				if (!(structurePiece3 instanceof VillagePieces.DelegatingPiece)) {
					n++;
				}
			}

			this.valid = n > 2;
		}

		@Override
		public boolean isValid() {
			return this.valid;
		}

		@Override
		public void serialize(NbtCompound nbt) {
			super.serialize(nbt);
			nbt.putBoolean("Valid", this.valid);
		}

		@Override
		public void deserialize(NbtCompound nbt) {
			super.deserialize(nbt);
			this.valid = nbt.getBoolean("Valid");
		}
	}
}
