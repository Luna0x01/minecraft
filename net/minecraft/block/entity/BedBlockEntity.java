package net.minecraft.block.entity;

import net.minecraft.block.BedBlock;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;

public class BedBlockEntity extends BlockEntity {
	private DyeColor color;

	public BedBlockEntity() {
		super(BlockEntityType.BED);
	}

	public BedBlockEntity(DyeColor dyeColor) {
		this();
		this.setColor(dyeColor);
	}

	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 11, this.getUpdatePacketContent());
	}

	public DyeColor getColor() {
		if (this.color == null) {
			this.color = ((BedBlock)this.method_16783().getBlock()).getColor();
		}

		return this.color;
	}

	public void setColor(DyeColor color) {
		this.color = color;
	}
}
