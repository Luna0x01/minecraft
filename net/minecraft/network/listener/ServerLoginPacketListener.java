package net.minecraft.network.listener;

import net.minecraft.class_4396;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;

public interface ServerLoginPacketListener extends PacketListener {
	void onHello(LoginHelloC2SPacket packet);

	void onKey(LoginKeyC2SPacket packet);

	void method_20392(class_4396 arg);
}
