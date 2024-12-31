package net.minecraft;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class class_4385 implements Packet<ServerPlayPacketListener> {
	private ItemStack field_21581;
	private boolean field_21582;
	private Hand field_21583;

	public class_4385() {
	}

	public class_4385(ItemStack itemStack, boolean bl, Hand hand) {
		this.field_21581 = itemStack.copy();
		this.field_21582 = bl;
		this.field_21583 = hand;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21581 = buf.readItemStack();
		this.field_21582 = buf.readBoolean();
		this.field_21583 = buf.readEnumConstant(Hand.class);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeItemStack(this.field_21581);
		buf.writeBoolean(this.field_21582);
		buf.writeEnumConstant(this.field_21583);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_20277(this);
	}

	public ItemStack method_20291() {
		return this.field_21581;
	}

	public boolean method_20292() {
		return this.field_21582;
	}

	public Hand method_20293() {
		return this.field_21583;
	}
}
