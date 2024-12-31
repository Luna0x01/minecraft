package net.minecraft.server.command;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;

public interface CommandRegistryProvider {
	int execute(CommandSource source, String name);

	List<String> getCompletions(CommandSource source, String name, @Nullable BlockPos pos);

	List<Command> method_3309(CommandSource source);

	Map<String, Command> getCommandMap();
}
