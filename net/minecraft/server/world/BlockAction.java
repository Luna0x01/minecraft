package net.minecraft.server.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BlockAction {
	private BlockPos field_9199;
	private Block field_7172;
	private int type;
	private int data;

	public BlockAction(BlockPos blockPos, Block block, int i, int j) {
		this.field_9199 = blockPos;
		this.type = i;
		this.data = j;
		this.field_7172 = block;
	}

	public BlockPos getPos() {
		return this.field_9199;
	}

	public int getType() {
		return this.type;
	}

	public int getData() {
		return this.data;
	}

	public Block getBlock() {
		return this.field_7172;
	}

	public boolean equals(Object object) {
		if (!(object instanceof BlockAction)) {
			return false;
		} else {
			BlockAction blockAction = (BlockAction)object;
			return this.field_9199.equals(blockAction.field_9199)
				&& this.type == blockAction.type
				&& this.data == blockAction.data
				&& this.field_7172 == blockAction.field_7172;
		}
	}

	public String toString() {
		return "TE(" + this.field_9199 + ")," + this.type + "," + this.data + "," + this.field_7172;
	}
}
