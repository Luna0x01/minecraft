package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.collection.DefaultedList;

public class InventoryS2CPacket implements Packet<ClientPlayPacketListener> {
	private int screenId;
	private List<ItemStack> field_15348;

	public InventoryS2CPacket() {
	}

	public InventoryS2CPacket(int i, DefaultedList<ItemStack> defaultedList) {
		this.screenId = i;
		this.field_15348 = DefaultedList.<ItemStack>ofSize(defaultedList.size(), ItemStack.EMPTY);

		for (int j = 0; j < this.field_15348.size(); j++) {
			this.field_15348.set(j, defaultedList.get(j).copy());
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.screenId = buf.readUnsignedByte();
		int i = buf.readShort();
		this.field_15348 = DefaultedList.<ItemStack>ofSize(i, ItemStack.EMPTY);

		for (int j = 0; j < i; j++) {
			this.field_15348.set(j, buf.readItemStack());
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.screenId);
		buf.writeShort(this.field_15348.size());

		for (ItemStack itemStack : this.field_15348) {
			buf.writeItemStack(itemStack);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onInventory(this);
	}

	public int getScreenId() {
		return this.screenId;
	}

	public List<ItemStack> method_13899() {
		return this.field_15348;
	}
}
