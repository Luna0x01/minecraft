package net.minecraft.command;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public interface Command extends Comparable<Command> {
	String getCommandName();

	String getUsageTranslationKey(CommandSource source);

	List<String> getAliases();

	void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException;

	boolean method_3278(MinecraftServer server, CommandSource source);

	List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos);

	boolean isUsernameAtIndex(String[] args, int index);
}
