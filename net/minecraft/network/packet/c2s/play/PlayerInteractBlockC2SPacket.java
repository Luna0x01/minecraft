package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class PlayerInteractBlockC2SPacket implements Packet<ServerPlayPacketListener> {
	private static final BlockPos UNKNOWN_POS = new BlockPos(-1, -1, -1);
	private BlockPos pos;
	private int directionId;
	private ItemStack stack;
	private float distanceX;
	private float distanceY;
	private float distanceZ;

	public PlayerInteractBlockC2SPacket() {
	}

	public PlayerInteractBlockC2SPacket(ItemStack itemStack) {
		this(UNKNOWN_POS, 255, itemStack, 0.0F, 0.0F, 0.0F);
	}

	public PlayerInteractBlockC2SPacket(BlockPos blockPos, int i, ItemStack itemStack, float f, float g, float h) {
		this.pos = blockPos;
		this.directionId = i;
		this.stack = itemStack != null ? itemStack.copy() : null;
		this.distanceX = f;
		this.distanceY = g;
		this.distanceZ = h;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.directionId = buf.readUnsignedByte();
		this.stack = buf.readItemStack();
		this.distanceX = (float)buf.readUnsignedByte() / 16.0F;
		this.distanceY = (float)buf.readUnsignedByte() / 16.0F;
		this.distanceZ = (float)buf.readUnsignedByte() / 16.0F;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.pos);
		buf.writeByte(this.directionId);
		buf.writeItemStack(this.stack);
		buf.writeByte((int)(this.distanceX * 16.0F));
		buf.writeByte((int)(this.distanceY * 16.0F));
		buf.writeByte((int)(this.distanceZ * 16.0F));
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInteractBlock(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public int getDirectionId() {
		return this.directionId;
	}

	public ItemStack getStack() {
		return this.stack;
	}

	public float getDistanceX() {
		return this.distanceX;
	}

	public float getDistanceY() {
		return this.distanceY;
	}

	public float getDistanceZ() {
		return this.distanceZ;
	}
}
