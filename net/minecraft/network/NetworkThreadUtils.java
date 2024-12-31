package net.minecraft.network;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.util.ThreadExecutor;

public class NetworkThreadUtils {
	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T listener, ThreadExecutor executor) throws OffThreadException {
		if (!executor.isOnThread()) {
			executor.submit(() -> packet.apply(listener));
			throw OffThreadException.INSTANCE;
		}
	}
}
