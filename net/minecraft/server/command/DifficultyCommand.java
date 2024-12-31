package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new IncorrectUsageException("commands.difficulty.usage");
		} else {
			Difficulty difficulty = this.method_6540(args[0]);
			minecraftServer.setDifficulty(difficulty);
			run(commandSource, this, "commands.difficulty.success", new Object[]{new TranslatableText(difficulty.getName())});
		}
	}

	protected Difficulty method_6540(String string) throws InvalidNumberException {
		if ("peaceful".equalsIgnoreCase(string) || "p".equalsIgnoreCase(string)) {
			return Difficulty.PEACEFUL;
		} else if ("easy".equalsIgnoreCase(string) || "e".equalsIgnoreCase(string)) {
			return Difficulty.EASY;
		} else if ("normal".equalsIgnoreCase(string) || "n".equalsIgnoreCase(string)) {
			return Difficulty.NORMAL;
		} else {
			return !"hard".equalsIgnoreCase(string) && !"h".equalsIgnoreCase(string) ? Difficulty.byOrdinal(parseClampedInt(string, 0, 3)) : Difficulty.HARD;
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length == 1 ? method_2894(strings, new String[]{"peaceful", "easy", "normal", "hard"}) : Collections.emptyList();
	}
}
