package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
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
import net.minecraft.world.GameMode;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new IncorrectUsageException("commands.gamemode.usage");
		} else {
			GameMode gameMode = this.method_3540(commandSource, args[0]);
			PlayerEntity playerEntity = args.length >= 2 ? method_4639(minecraftServer, commandSource, args[1]) : getAsPlayer(commandSource);
			playerEntity.method_3170(gameMode);
			Text text = new TranslatableText("gameMode." + gameMode.getGameModeName());
			if (commandSource.getWorld().getGameRules().getBoolean("sendCommandFeedback")) {
				playerEntity.sendMessage(new TranslatableText("gameMode.changed", text));
			}

			if (playerEntity == commandSource) {
				run(commandSource, this, 1, "commands.gamemode.success.self", new Object[]{text});
			} else {
				run(commandSource, this, 1, "commands.gamemode.success.other", new Object[]{playerEntity.getTranslationKey(), text});
			}
		}
	}

	protected GameMode method_3540(CommandSource commandSource, String string) throws InvalidNumberException {
		GameMode gameMode = GameMode.method_11495(string, GameMode.NOT_SET);
		return gameMode == GameMode.NOT_SET ? LevelInfo.method_3754(parseClampedInt(string, 0, GameMode.gameModes().length - 2)) : gameMode;
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, new String[]{"survival", "creative", "adventure", "spectator"});
		} else {
			return strings.length == 2 ? method_2894(strings, server.getPlayerNames()) : Collections.emptyList();
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 1;
	}
}
