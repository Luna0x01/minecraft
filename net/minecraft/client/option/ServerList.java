package net.minecraft.client.option;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerList {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final List<ServerInfo> servers = Lists.newArrayList();

	public ServerList(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.loadFile();
	}

	public void loadFile() {
		try {
			this.servers.clear();
			NbtCompound nbtCompound = NbtIo.read(new File(this.client.runDirectory, "servers.dat"));
			if (nbtCompound == null) {
				return;
			}

			NbtList nbtList = nbtCompound.getList("servers", 10);

			for (int i = 0; i < nbtList.size(); i++) {
				this.servers.add(ServerInfo.deserialize(nbtList.getCompound(i)));
			}
		} catch (Exception var4) {
			LOGGER.error("Couldn't load server list", var4);
		}
	}

	public void saveFile() {
		try {
			NbtList nbtList = new NbtList();

			for (ServerInfo serverInfo : this.servers) {
				nbtList.add((NbtElement)serverInfo.serialize());
			}

			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.put("servers", nbtList);
			NbtIo.safeWrite(nbtCompound, new File(this.client.runDirectory, "servers.dat"));
		} catch (Exception var4) {
			LOGGER.error("Couldn't save server list", var4);
		}
	}

	public ServerInfo get(int index) {
		return (ServerInfo)this.servers.get(index);
	}

	public void remove(int index) {
		this.servers.remove(index);
	}

	public void add(ServerInfo info) {
		this.servers.add(info);
	}

	public int size() {
		return this.servers.size();
	}

	public void swapEntries(int index1, int index2) {
		ServerInfo serverInfo = this.get(index1);
		this.servers.set(index1, this.get(index2));
		this.servers.set(index2, serverInfo);
		this.saveFile();
	}

	public void set(int index, ServerInfo info) {
		this.servers.set(index, info);
	}

	public static void updateServerListEntry(ServerInfo e) {
		ServerList serverList = new ServerList(MinecraftClient.getInstance());
		serverList.loadFile();

		for (int i = 0; i < serverList.size(); i++) {
			ServerInfo serverInfo = serverList.get(i);
			if (serverInfo.name.equals(e.name) && serverInfo.address.equals(e.address)) {
				serverList.set(i, e);
				break;
			}
		}

		serverList.saveFile();
	}
}
