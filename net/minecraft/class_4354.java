package net.minecraft;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.UUID;
import net.minecraft.datafixer.DataFixerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.UserCache;

public class class_4354 implements class_4345 {
	private final class_4344 field_21424;

	public class_4354(class_4344 arg) {
		this.field_21424 = arg;
	}

	@Override
	public void method_19996(class_4346 arg) throws IOException {
		YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
		MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
		GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
		File file = new File(this.field_21424.method_19993().toFile(), "tmp");
		UserCache userCache = new UserCache(gameProfileRepository, new File(file, MinecraftServer.USER_CACHE_FILE.getName()));
		MinecraftServer minecraftServer = new MinecraftDedicatedServer(
			file, DataFixerFactory.method_21531(), yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache
		);
		minecraftServer.method_2971().method_17528(this.field_21424.method_19993().resolve("reports/commands.json").toFile());
	}

	@Override
	public String method_19995() {
		return "Command Syntax";
	}
}
