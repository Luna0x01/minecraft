package net.minecraft.network.packet.s2c.query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.PacketByteBuf;

public class QueryResponseS2CPacket implements Packet<ClientQueryPacketListener> {
	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(ServerMetadata.Version.class, new ServerMetadata.Version.Serializer())
		.registerTypeAdapter(ServerMetadata.Players.class, new ServerMetadata.Players.Deserializer())
		.registerTypeAdapter(ServerMetadata.class, new ServerMetadata.Deserializer())
		.registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
		.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
		.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
		.create();
	private ServerMetadata metadata;

	public QueryResponseS2CPacket() {
	}

	public QueryResponseS2CPacket(ServerMetadata serverMetadata) {
		this.metadata = serverMetadata;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.metadata = (ServerMetadata)GSON.fromJson(buf.readString(32767), ServerMetadata.class);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(GSON.toJson(this.metadata));
	}

	public void apply(ClientQueryPacketListener clientQueryPacketListener) {
		clientQueryPacketListener.onResponse(this);
	}

	public ServerMetadata getServerMetadata() {
		return this.metadata;
	}
}
