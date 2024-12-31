package net.minecraft.server.command;

import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.InvalidNumberException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.LevelInfo;

public class GameModeCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "gamemode";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.gamemode.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new IncorrectUsageException("commands.gamemode.usage");
		} else {
			LevelInfo.GameMode gameMode = this.method_3540(source, args[0]);
			PlayerEntity playerEntity = args.length >= 2 ? getPlayer(source, args[1]) : getAsPlayer(source);
			playerEntity.setGameMode(gameMode);
			playerEntity.fallDistance = 0.0F;
			if (source.getWorld().getGameRules().getBoolean("sendCommandFeedback")) {
				playerEntity.sendMessage(new TranslatableText("gameMode.changed"));
			}

			Text text = new TranslatableText("gameMode." + gameMode.getName());
			if (playerEntity != source) {
				run(source, this, 1, "commands.gamemode.success.other", new Object[]{playerEntity.getTranslationKey(), text});
			} else {
				run(source, this, 1, "commands.gamemode.success.self", new Object[]{text});
			}
		}
	}

	protected LevelInfo.GameMode method_3540(CommandSource commandSource, String string) throws InvalidNumberException {
		if (string.equalsIgnoreCase(LevelInfo.GameMode.SURVIVAL.getName()) || string.equalsIgnoreCase("s")) {
			return LevelInfo.GameMode.SURVIVAL;
		} else if (string.equalsIgnoreCase(LevelInfo.GameMode.CREATIVE.getName()) || string.equalsIgnoreCase("c")) {
			return LevelInfo.GameMode.CREATIVE;
		} else if (string.equalsIgnoreCase(LevelInfo.GameMode.ADVENTURE.getName()) || string.equalsIgnoreCase("a")) {
			return LevelInfo.GameMode.ADVENTURE;
		} else {
			return !string.equalsIgnoreCase(LevelInfo.GameMode.SPECTATOR.getName()) && !string.equalsIgnoreCase("sp")
				? LevelInfo.getGameModeById(parseClampedInt(string, 0, LevelInfo.GameMode.values().length - 2))
				: LevelInfo.GameMode.SPECTATOR;
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, new String[]{"survival", "creative", "adventure", "spectator"});
		} else {
			return args.length == 2 ? method_2894(args, this.method_3541()) : null;
		}
	}

	protected String[] method_3541() {
		return MinecraftServer.getServer().getPlayerNames();
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 1;
	}
}
