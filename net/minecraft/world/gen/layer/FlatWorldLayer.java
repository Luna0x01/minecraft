package net.minecraft.world.gen.layer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;

public class FlatWorldLayer {
	private final int field_10179;
	private BlockState blockState;
	private int thickness = 1;
	private int field_4956;

	public FlatWorldLayer(int i, Block block) {
		this(3, i, block);
	}

	public FlatWorldLayer(int i, int j, Block block) {
		this.field_10179 = i;
		this.thickness = j;
		this.blockState = block.getDefaultState();
	}

	public FlatWorldLayer(int i, int j, Block block, int k) {
		this(i, j, block);
		this.blockState = block.stateFromData(k);
	}

	public int getThickness() {
		return this.thickness;
	}

	public BlockState getBlockState() {
		return this.blockState;
	}

	private Block getBlock() {
		return this.blockState.getBlock();
	}

	private int getBlockData() {
		return this.blockState.getBlock().getData(this.blockState);
	}

	public int method_4111() {
		return this.field_4956;
	}

	public void setLayerLevel(int i) {
		this.field_4956 = i;
	}

	public String toString() {
		String string;
		if (this.field_10179 >= 3) {
			Identifier identifier = Block.REGISTRY.getIdentifier(this.getBlock());
			string = identifier == null ? "null" : identifier.toString();
			if (this.thickness > 1) {
				string = this.thickness + "*" + string;
			}
		} else {
			string = Integer.toString(Block.getIdByBlock(this.getBlock()));
			if (this.thickness > 1) {
				string = this.thickness + "x" + string;
			}
		}

		int i = this.getBlockData();
		if (i > 0) {
			string = string + ":" + i;
		}

		return string;
	}
}
