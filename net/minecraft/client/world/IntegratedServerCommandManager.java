package net.minecraft.client.world;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.integrated.IntegratedServer;

public class IntegratedServerCommandManager extends CommandManager {
	public IntegratedServerCommandManager(IntegratedServer integratedServer) {
		super(integratedServer);
	}
}
