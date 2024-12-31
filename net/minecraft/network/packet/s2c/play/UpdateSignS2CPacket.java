package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UpdateSignS2CPacket implements Packet<ClientPlayPacketListener> {
	private World world;
	private BlockPos blockPos;
	private Text[] signText;

	public UpdateSignS2CPacket() {
	}

	public UpdateSignS2CPacket(World world, BlockPos blockPos, Text[] texts) {
		this.world = world;
		this.blockPos = blockPos;
		this.signText = new Text[]{texts[0], texts[1], texts[2], texts[3]};
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.blockPos = buf.readBlockPos();
		this.signText = new Text[4];

		for (int i = 0; i < 4; i++) {
			this.signText[i] = buf.readText();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.blockPos);

		for (int i = 0; i < 4; i++) {
			buf.writeText(this.signText[i]);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onUpdateSign(this);
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public Text[] getSignText() {
		return this.signText;
	}
}
