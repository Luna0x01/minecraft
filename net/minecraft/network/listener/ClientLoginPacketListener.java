package net.minecraft.network.listener;

import net.minecraft.class_4394;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;

public interface ClientLoginPacketListener extends PacketListener {
	void onHello(LoginHelloS2CPacket packet);

	void onLoginSuccess(LoginSuccessS2CPacket packet);

	void onDisconnect(LoginDisconnectS2CPacket packet);

	void onCompression(LoginCompressionS2CPacket packet);

	void method_20383(class_4394 arg);
}
