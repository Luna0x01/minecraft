package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class InventoryS2CPacket implements Packet<ClientPlayPacketListener> {
	private int screenId;
	private ItemStack[] stacks;

	public InventoryS2CPacket() {
	}

	public InventoryS2CPacket(int i, List<ItemStack> list) {
		this.screenId = i;
		this.stacks = new ItemStack[list.size()];

		for (int j = 0; j < this.stacks.length; j++) {
			ItemStack itemStack = (ItemStack)list.get(j);
			this.stacks[j] = itemStack == null ? null : itemStack.copy();
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.screenId = buf.readUnsignedByte();
		int i = buf.readShort();
		this.stacks = new ItemStack[i];

		for (int j = 0; j < i; j++) {
			this.stacks[j] = buf.readItemStack();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.screenId);
		buf.writeShort(this.stacks.length);

		for (ItemStack itemStack : this.stacks) {
			buf.writeItemStack(itemStack);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onInventory(this);
	}

	public int getScreenId() {
		return this.screenId;
	}

	public ItemStack[] getSlotStacks() {
		return this.stacks;
	}
}
