package net.minecraft.server.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BlockAction {
	private final BlockPos field_9199;
	private final Block field_7172;
	private final int type;
	private final int data;

	public BlockAction(BlockPos blockPos, Block block, int i, int j) {
		this.field_9199 = blockPos;
		this.field_7172 = block;
		this.type = i;
		this.data = j;
	}

	public BlockPos getPos() {
		return this.field_9199;
	}

	public Block getBlock() {
		return this.field_7172;
	}

	public int getType() {
		return this.type;
	}

	public int getData() {
		return this.data;
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
