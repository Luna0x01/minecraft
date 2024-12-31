package net.minecraft.server.command;

import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.InvalidNumberException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;

public class DifficultyCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "difficulty";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.difficulty.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new IncorrectUsageException("commands.difficulty.usage");
		} else {
			Difficulty difficulty = this.method_6540(args[0]);
			MinecraftServer.getServer().setDifficulty(difficulty);
			run(source, this, "commands.difficulty.success", new Object[]{new TranslatableText(difficulty.getName())});
		}
	}

	protected Difficulty method_6540(String string) throws InvalidNumberException {
		if (string.equalsIgnoreCase("peaceful") || string.equalsIgnoreCase("p")) {
			return Difficulty.PEACEFUL;
		} else if (string.equalsIgnoreCase("easy") || string.equalsIgnoreCase("e")) {
			return Difficulty.EASY;
		} else if (string.equalsIgnoreCase("normal") || string.equalsIgnoreCase("n")) {
			return Difficulty.NORMAL;
		} else {
			return !string.equalsIgnoreCase("hard") && !string.equalsIgnoreCase("h") ? Difficulty.byOrdinal(parseClampedInt(string, 0, 3)) : Difficulty.HARD;
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length == 1 ? method_2894(args, new String[]{"peaceful", "easy", "normal", "hard"}) : null;
	}
}
