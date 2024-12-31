package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

public class RequestCommandCompletionsC2SPacket implements Packet<ServerPlayPacketListener> {
	private String partialCommand;
	private boolean field_13796;
	@Nullable
	private BlockPos lookingAt;

	public RequestCommandCompletionsC2SPacket() {
	}

	public RequestCommandCompletionsC2SPacket(String string, @Nullable BlockPos blockPos, boolean bl) {
		this.partialCommand = string;
		this.lookingAt = blockPos;
		this.field_13796 = bl;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.partialCommand = buf.readString(32767);
		this.field_13796 = buf.readBoolean();
		boolean bl = buf.readBoolean();
		if (bl) {
			this.lookingAt = buf.readBlockPos();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(StringUtils.substring(this.partialCommand, 0, 32767));
		buf.writeBoolean(this.field_13796);
		boolean bl = this.lookingAt != null;
		buf.writeBoolean(bl);
		if (bl) {
			buf.writeBlockPos(this.lookingAt);
		}
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onRequestCommandCompletions(this);
	}

	public String getPartialCommand() {
		return this.partialCommand;
	}

	@Nullable
	public BlockPos getLookingAt() {
		return this.lookingAt;
	}

	public boolean method_12684() {
		return this.field_13796;
	}
}
