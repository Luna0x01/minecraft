package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public abstract class class_3992 {
	protected final List<StructurePiece> field_19407 = Lists.newArrayList();
	protected BlockBox field_19408;
	protected int field_19409;
	protected int field_19410;
	private Biome field_19411;
	private int field_19412;

	public class_3992() {
	}

	public class_3992(int i, int j, Biome biome, class_3812 arg, long l) {
		this.field_19409 = i;
		this.field_19410 = j;
		this.field_19411 = biome;
		arg.method_17291(l, this.field_19409, this.field_19410);
	}

	public BlockBox method_17664() {
		return this.field_19408;
	}

	public List<StructurePiece> method_17665() {
		return this.field_19407;
	}

	public void method_82(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
		synchronized (this.field_19407) {
			Iterator<StructurePiece> iterator = this.field_19407.iterator();

			while (iterator.hasNext()) {
				StructurePiece structurePiece = (StructurePiece)iterator.next();
				if (structurePiece.getBoundingBox().intersects(blockBox) && !structurePiece.method_58(iWorld, random, blockBox, chunkPos)) {
					iterator.remove();
				}
			}

			this.method_17660(iWorld);
		}
	}

	protected void method_17660(BlockView blockView) {
		this.field_19408 = BlockBox.empty();

		for (StructurePiece structurePiece : this.field_19407) {
			this.field_19408.encompass(structurePiece.getBoundingBox());
		}
	}

	public NbtCompound method_17659(int i, int j) {
		NbtCompound nbtCompound = new NbtCompound();
		if (this.method_85()) {
			nbtCompound.putString("id", StructurePieceManager.method_5519(this));
			nbtCompound.putString("biome", Registry.BIOME.getId(this.field_19411).toString());
			nbtCompound.putInt("ChunkX", i);
			nbtCompound.putInt("ChunkZ", j);
			nbtCompound.putInt("references", this.field_19412);
			nbtCompound.put("BB", this.field_19408.toNbt());
			NbtList nbtList = new NbtList();
			synchronized (this.field_19407) {
				for (StructurePiece structurePiece : this.field_19407) {
					nbtList.add((NbtElement)structurePiece.toNbt());
				}
			}

			nbtCompound.put("Children", nbtList);
			this.method_5533(nbtCompound);
			return nbtCompound;
		} else {
			nbtCompound.putString("id", "INVALID");
			return nbtCompound;
		}
	}

	public void method_5533(NbtCompound nbtCompound) {
	}

	public void method_17662(IWorld iWorld, NbtCompound nbtCompound) {
		this.field_19409 = nbtCompound.getInt("ChunkX");
		this.field_19410 = nbtCompound.getInt("ChunkZ");
		this.field_19412 = nbtCompound.getInt("references");
		this.field_19411 = nbtCompound.contains("biome")
			? Registry.BIOME.getByIdentifier(new Identifier(nbtCompound.getString("biome")))
			: iWorld.method_3586().method_17046().method_17020().method_16480(new BlockPos((this.field_19409 << 4) + 9, 0, (this.field_19410 << 4) + 9), Biomes.PLAINS);
		if (nbtCompound.contains("BB")) {
			this.field_19408 = new BlockBox(nbtCompound.getIntArray("BB"));
		}

		NbtList nbtList = nbtCompound.getList("Children", 10);

		for (int i = 0; i < nbtList.size(); i++) {
			this.field_19407.add(StructurePieceManager.method_5522(nbtList.getCompound(i), iWorld));
		}

		this.method_5534(nbtCompound);
	}

	public void method_5534(NbtCompound nbtCompound) {
	}

	protected void method_17663(RenderBlockView renderBlockView, Random random, int i) {
		int j = renderBlockView.method_8483() - i;
		int k = this.field_19408.getBlockCountY() + 1;
		if (k < j) {
			k += random.nextInt(j - k);
		}

		int l = k - this.field_19408.maxY;
		this.field_19408.move(0, l, 0);

		for (StructurePiece structurePiece : this.field_19407) {
			structurePiece.translate(0, l, 0);
		}
	}

	protected void method_17661(BlockView blockView, Random random, int i, int j) {
		int k = j - i + 1 - this.field_19408.getBlockCountY();
		int l;
		if (k > 1) {
			l = i + random.nextInt(k);
		} else {
			l = i;
		}

		int n = l - this.field_19408.minY;
		this.field_19408.move(0, n, 0);

		for (StructurePiece structurePiece : this.field_19407) {
			structurePiece.translate(0, n, 0);
		}
	}

	public boolean method_85() {
		return true;
	}

	public void method_9278(ChunkPos chunkPos) {
	}

	public int method_17666() {
		return this.field_19409;
	}

	public int method_17667() {
		return this.field_19410;
	}

	public BlockPos method_17658() {
		return new BlockPos(this.field_19409 << 4, 0, this.field_19410 << 4);
	}

	public boolean method_17668() {
		return this.field_19412 < this.method_17670();
	}

	public void method_17669() {
		this.field_19412++;
	}

	protected int method_17670() {
		return 1;
	}
}
