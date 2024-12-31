package net.minecraft.structure;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;

public class StructurePlacementData {
	private BlockMirror mirror;
	private BlockRotation rotation;
	private boolean field_13026;
	private Block block;
	private ChunkPos chunkPos;
	private BlockBox field_13029;
	private boolean field_13030;

	public StructurePlacementData() {
		this(BlockMirror.NONE, BlockRotation.NONE, false, null, null);
	}

	public StructurePlacementData(BlockMirror blockMirror, BlockRotation blockRotation, boolean bl, @Nullable Block block, @Nullable BlockBox blockBox) {
		this.rotation = blockRotation;
		this.mirror = blockMirror;
		this.field_13026 = bl;
		this.block = block;
		this.chunkPos = null;
		this.field_13029 = blockBox;
		this.field_13030 = true;
	}

	public StructurePlacementData method_11864() {
		return new StructurePlacementData(this.mirror, this.rotation, this.field_13026, this.block, this.field_13029)
			.method_11865(this.chunkPos)
			.method_11873(this.field_13030);
	}

	public StructurePlacementData method_11867(BlockMirror blockMirror) {
		this.mirror = blockMirror;
		return this;
	}

	public StructurePlacementData method_11868(BlockRotation blockRotation) {
		this.rotation = blockRotation;
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

	public boolean method_11875() {
		return this.field_13026;
	}

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
		this.field_13029 = this.method_11872(this.chunkPos);
	}

	@Nullable
	private BlockBox method_11872(@Nullable ChunkPos chunkPos) {
		if (chunkPos == null) {
			return null;
		} else {
			int i = chunkPos.x * 16;
			int j = chunkPos.z * 16;
			return new BlockBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
		}
	}
}
