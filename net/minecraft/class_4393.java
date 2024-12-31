package net.minecraft;

import java.io.IOException;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class class_4393 implements Packet<ServerPlayPacketListener> {
	private BlockPos field_21627;
	private StructureBlockEntity.class_3745 field_21628;
	private StructureBlockMode field_21629;
	private String field_21630;
	private BlockPos field_21631;
	private BlockPos field_21632;
	private BlockMirror field_21633;
	private BlockRotation field_21634;
	private String field_21635;
	private boolean field_21636;
	private boolean field_21637;
	private boolean field_21638;
	private float field_21639;
	private long field_21640;

	public class_4393() {
	}

	public class_4393(
		BlockPos blockPos,
		StructureBlockEntity.class_3745 arg,
		StructureBlockMode structureBlockMode,
		String string,
		BlockPos blockPos2,
		BlockPos blockPos3,
		BlockMirror blockMirror,
		BlockRotation blockRotation,
		String string2,
		boolean bl,
		boolean bl2,
		boolean bl3,
		float f,
		long l
	) {
		this.field_21627 = blockPos;
		this.field_21628 = arg;
		this.field_21629 = structureBlockMode;
		this.field_21630 = string;
		this.field_21631 = blockPos2;
		this.field_21632 = blockPos3;
		this.field_21633 = blockMirror;
		this.field_21634 = blockRotation;
		this.field_21635 = string2;
		this.field_21636 = bl;
		this.field_21637 = bl2;
		this.field_21638 = bl3;
		this.field_21639 = f;
		this.field_21640 = l;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21627 = buf.readBlockPos();
		this.field_21628 = buf.readEnumConstant(StructureBlockEntity.class_3745.class);
		this.field_21629 = buf.readEnumConstant(StructureBlockMode.class);
		this.field_21630 = buf.readString(32767);
		this.field_21631 = new BlockPos(
			MathHelper.clamp(buf.readByte(), -32, 32), MathHelper.clamp(buf.readByte(), -32, 32), MathHelper.clamp(buf.readByte(), -32, 32)
		);
		this.field_21632 = new BlockPos(MathHelper.clamp(buf.readByte(), 0, 32), MathHelper.clamp(buf.readByte(), 0, 32), MathHelper.clamp(buf.readByte(), 0, 32));
		this.field_21633 = buf.readEnumConstant(BlockMirror.class);
		this.field_21634 = buf.readEnumConstant(BlockRotation.class);
		this.field_21635 = buf.readString(12);
		this.field_21639 = MathHelper.clamp(buf.readFloat(), 0.0F, 1.0F);
		this.field_21640 = buf.readVarLong();
		int i = buf.readByte();
		this.field_21636 = (i & 1) != 0;
		this.field_21637 = (i & 2) != 0;
		this.field_21638 = (i & 4) != 0;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.field_21627);
		buf.writeEnumConstant(this.field_21628);
		buf.writeEnumConstant(this.field_21629);
		buf.writeString(this.field_21630);
		buf.writeByte(this.field_21631.getX());
		buf.writeByte(this.field_21631.getY());
		buf.writeByte(this.field_21631.getZ());
		buf.writeByte(this.field_21632.getX());
		buf.writeByte(this.field_21632.getY());
		buf.writeByte(this.field_21632.getZ());
		buf.writeEnumConstant(this.field_21633);
		buf.writeEnumConstant(this.field_21634);
		buf.writeString(this.field_21635);
		buf.writeFloat(this.field_21639);
		buf.method_10608(this.field_21640);
		int i = 0;
		if (this.field_21636) {
			i |= 1;
		}

		if (this.field_21637) {
			i |= 2;
		}

		if (this.field_21638) {
			i |= 4;
		}

		buf.writeByte(i);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_20285(this);
	}

	public BlockPos method_20369() {
		return this.field_21627;
	}

	public StructureBlockEntity.class_3745 method_20370() {
		return this.field_21628;
	}

	public StructureBlockMode method_20371() {
		return this.field_21629;
	}

	public String method_20372() {
		return this.field_21630;
	}

	public BlockPos method_20373() {
		return this.field_21631;
	}

	public BlockPos method_20374() {
		return this.field_21632;
	}

	public BlockMirror method_20375() {
		return this.field_21633;
	}

	public BlockRotation method_20376() {
		return this.field_21634;
	}

	public String method_20377() {
		return this.field_21635;
	}

	public boolean method_20378() {
		return this.field_21636;
	}

	public boolean method_20379() {
		return this.field_21637;
	}

	public boolean method_20380() {
		return this.field_21638;
	}

	public float method_20381() {
		return this.field_21639;
	}

	public long method_20382() {
		return this.field_21640;
	}
}
