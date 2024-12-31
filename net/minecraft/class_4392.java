package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class class_4392 implements Packet<ServerPlayPacketListener> {
	private int field_21624;
	private String field_21625;
	private boolean field_21626;

	public class_4392() {
	}

	public class_4392(int i, String string, boolean bl) {
		this.field_21624 = i;
		this.field_21625 = string;
		this.field_21626 = bl;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21624 = buf.readVarInt();
		this.field_21625 = buf.readString(32767);
		this.field_21626 = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.field_21624);
		buf.writeString(this.field_21625);
		buf.writeBoolean(this.field_21626);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_20284(this);
	}

	@Nullable
	public CommandBlockExecutor method_20364(World world) {
		Entity entity = world.getEntityById(this.field_21624);
		return entity instanceof CommandBlockMinecartEntity ? ((CommandBlockMinecartEntity)entity).getCommandExecutor() : null;
	}

	public String method_20366() {
		return this.field_21625;
	}

	public boolean method_20367() {
		return this.field_21626;
	}
}
