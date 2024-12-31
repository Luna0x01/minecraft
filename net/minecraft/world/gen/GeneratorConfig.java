package net.minecraft.world.gen;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceManager;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public abstract class GeneratorConfig {
	protected LinkedList<StructurePiece> children = new LinkedList();
	protected BlockBox boundingBox;
	private int chunkX;
	private int chunkZ;

	public GeneratorConfig() {
	}

	public GeneratorConfig(int i, int j) {
		this.chunkX = i;
		this.chunkZ = j;
	}

	public BlockBox getBoundingBox() {
		return this.boundingBox;
	}

	public LinkedList<StructurePiece> getChildren() {
		return this.children;
	}

	public void generateStructure(World world, Random random, BlockBox boundingBox) {
		Iterator<StructurePiece> iterator = this.children.iterator();

		while (iterator.hasNext()) {
			StructurePiece structurePiece = (StructurePiece)iterator.next();
			if (structurePiece.getBoundingBox().intersects(boundingBox) && !structurePiece.generate(world, random, boundingBox)) {
				iterator.remove();
			}
		}
	}

	protected void setBoundingBoxFromChildren() {
		this.boundingBox = BlockBox.empty();

		for (StructurePiece structurePiece : this.children) {
			this.boundingBox.encompass(structurePiece.getBoundingBox());
		}
	}

	public NbtCompound toNbt(int chunkX, int chunkZ) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("id", StructurePieceManager.getId(this));
		nbtCompound.putInt("ChunkX", chunkX);
		nbtCompound.putInt("ChunkZ", chunkZ);
		nbtCompound.put("BB", this.boundingBox.toNbt());
		NbtList nbtList = new NbtList();

		for (StructurePiece structurePiece : this.children) {
			nbtList.add(structurePiece.toNbt());
		}

		nbtCompound.put("Children", nbtList);
		this.serialize(nbtCompound);
		return nbtCompound;
	}

	public void serialize(NbtCompound nbt) {
	}

	public void fromNbt(World random, NbtCompound nbt) {
		this.chunkX = nbt.getInt("ChunkX");
		this.chunkZ = nbt.getInt("ChunkZ");
		if (nbt.contains("BB")) {
			this.boundingBox = new BlockBox(nbt.getIntArray("BB"));
		}

		NbtList nbtList = nbt.getList("Children", 10);

		for (int i = 0; i < nbtList.size(); i++) {
			this.children.add(StructurePieceManager.getStructurePieceFromNbt(nbtList.getCompound(i), random));
		}

		this.deserialize(nbt);
	}

	public void deserialize(NbtCompound nbt) {
	}

	protected void method_80(World world, Random random, int i) {
		int j = world.getSeaLevel() - i;
		int k = this.boundingBox.getBlockCountY() + 1;
		if (k < j) {
			k += random.nextInt(j - k);
		}

		int l = k - this.boundingBox.maxY;
		this.boundingBox.move(0, l, 0);

		for (StructurePiece structurePiece : this.children) {
			structurePiece.translate(0, l, 0);
		}
	}

	protected void method_81(World world, Random random, int i, int j) {
		int k = j - i + 1 - this.boundingBox.getBlockCountY();
		int l = 1;
		if (k > 1) {
			l = i + random.nextInt(k);
		} else {
			l = i;
		}

		int m = l - this.boundingBox.minY;
		this.boundingBox.move(0, m, 0);

		for (StructurePiece structurePiece : this.children) {
			structurePiece.translate(0, m, 0);
		}
	}

	public boolean isValid() {
		return true;
	}

	public boolean method_9277(ChunkPos chunkPos) {
		return true;
	}

	public void method_9278(ChunkPos chunkPos) {
	}

	public int getChunkX() {
		return this.chunkX;
	}

	public int getChunkZ() {
		return this.chunkZ;
	}
}
