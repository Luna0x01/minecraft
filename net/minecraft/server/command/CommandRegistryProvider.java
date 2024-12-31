package net.minecraft.server.command;

import java.util.List;
import java.util.Map;
import net.minecraft.command.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;

public interface CommandRegistryProvider {
	int execute(CommandSource source, String name);

	List<String> getCompletions(CommandSource source, String name, BlockPos pos);

	List<Command> method_3309(CommandSource source);

	Map<String, Command> getCommandMap();
}
