package net.minecraft.command;

import java.util.List;
import net.minecraft.util.math.BlockPos;

public interface Command extends Comparable<Command> {
	String getCommandName();

	String getUsageTranslationKey(CommandSource source);

	List<String> getAliases();

	void execute(CommandSource source, String[] args) throws CommandException;

	boolean isAccessible(CommandSource source);

	List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos);

	boolean isUsernameAtIndex(String[] args, int index);
}
