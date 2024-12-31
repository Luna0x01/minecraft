package net.minecraft.structure;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3812;
import net.minecraft.block.Block;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class StructurePlacementData {
	private BlockMirror mirror = BlockMirror.NONE;
	private BlockRotation rotation = BlockRotation.NONE;
	private BlockPos field_19433 = new BlockPos(0, 0, 0);
	private boolean field_13026;
	@Nullable
	private Block block;
	@Nullable
	private ChunkPos chunkPos;
	@Nullable
	private BlockBox field_13029;
	private boolean field_13030 = true;
	private boolean field_19434 = true;
	private float field_14875 = 1.0F;
	@Nullable
	private Random field_14876;
	@Nullable
	private Long field_14877;
	@Nullable
	private Integer field_19435;
	private int field_19436;

	public StructurePlacementData method_11864() {
		StructurePlacementData structurePlacementData = new StructurePlacementData();
		structurePlacementData.mirror = this.mirror;
		structurePlacementData.rotation = this.rotation;
		structurePlacementData.field_19433 = this.field_19433;
		structurePlacementData.field_13026 = this.field_13026;
		structurePlacementData.block = this.block;
		structurePlacementData.chunkPos = this.chunkPos;
		structurePlacementData.field_13029 = this.field_13029;
		structurePlacementData.field_13030 = this.field_13030;
		structurePlacementData.field_19434 = this.field_19434;
		structurePlacementData.field_14875 = this.field_14875;
		structurePlacementData.field_14876 = this.field_14876;
		structurePlacementData.field_14877 = this.field_14877;
		structurePlacementData.field_19435 = this.field_19435;
		structurePlacementData.field_19436 = this.field_19436;
		return structurePlacementData;
	}

	public StructurePlacementData method_11867(BlockMirror blockMirror) {
		this.mirror = blockMirror;
		return this;
	}

	public StructurePlacementData method_11868(BlockRotation blockRotation) {
		this.rotation = blockRotation;
		return this;
	}

	public StructurePlacementData method_17691(BlockPos blockPos) {
		this.field_19433 = blockPos;
		return this;
	}

	public StructurePlacementData method_11870(boolean bl) {
		this.field_13026 = bl;
		return this;
	}

	public StructurePlacementData method_11866(Block block) {
		this.block = block;
		return this;
	}

	public StructurePlacementData method_11865(ChunkPos chunkPos) {
		this.chunkPos = chunkPos;
		return this;
	}

	public StructurePlacementData method_11869(BlockBox blockBox) {
		this.field_13029 = blockBox;
		return this;
	}

	public StructurePlacementData method_13387(@Nullable Long long_) {
		this.field_14877 = long_;
		return this;
	}

	public StructurePlacementData method_13388(@Nullable Random random) {
		this.field_14876 = random;
		return this;
	}

	public StructurePlacementData method_13385(float f) {
		this.field_14875 = f;
		return this;
	}

	public BlockMirror method_11871() {
		return this.mirror;
	}

	public StructurePlacementData method_11873(boolean bl) {
		this.field_13030 = bl;
		return this;
	}

	public BlockRotation method_11874() {
		return this.rotation;
	}

	public BlockPos method_17693() {
		return this.field_19433;
	}

	public Random method_13386(@Nullable BlockPos blockPos) {
		if (this.field_14876 != null) {
			return this.field_14876;
		} else if (this.field_14877 != null) {
			return this.field_14877 == 0L ? new Random(Util.method_20227()) : new Random(this.field_14877);
		} else {
			return blockPos == null ? new Random(Util.method_20227()) : class_3812.method_17287(blockPos.getX(), blockPos.getZ(), 0L, 987234911L);
		}
	}

	public float method_13389() {
		return this.field_14875;
	}

	public boolean method_11875() {
		return this.field_13026;
	}

	@Nullable
	public Block method_11876() {
		return this.block;
	}

	@Nullable
	public BlockBox method_11877() {
		if (this.field_13029 == null && this.chunkPos != null) {
			this.method_11879();
		}

		return this.field_13029;
	}

	public boolean method_11878() {
		return this.field_13030;
	}

	void method_11879() {
		if (this.chunkPos != null) {
			this.field_13029 = this.method_11872(this.chunkPos);
		}
	}

	public boolean method_17694() {
		return this.field_19434;
	}

	public List<Structure.StructureBlockInfo> method_17692(List<List<Structure.StructureBlockInfo>> list, @Nullable BlockPos blockPos) {
		this.field_19435 = 8;
		if (this.field_19435 != null && this.field_19435 >= 0 && this.field_19435 < list.size()) {
			return (List<Structure.StructureBlockInfo>)list.get(this.field_19435);
		} else {
			this.field_19435 = this.method_13386(blockPos).nextInt(list.size());
			return (List<Structure.StructureBlockInfo>)list.get(this.field_19435);
		}
	}

	@Nullable
	private BlockBox method_11872(@Nullable ChunkPos chunkPos) {
		if (chunkPos == null) {
			return this.field_13029;
		} else {
			int i = chunkPos.x * 16;
			int j = chunkPos.z * 16;
			return new BlockBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
		}
	}
}
