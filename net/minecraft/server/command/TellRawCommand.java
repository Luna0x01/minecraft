package net.minecraft.server.command;

import com.google.gson.JsonParseException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.math.BlockPos;

public class TellRawCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "tellraw";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.tellraw.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.tellraw.usage");
		} else {
			PlayerEntity playerEntity = method_4639(minecraftServer, commandSource, args[0]);
			String string = method_10706(args, 1);

			try {
				Text text = Text.Serializer.deserializeText(string);
				playerEntity.sendMessage(ChatSerializer.process(commandSource, text, playerEntity));
			} catch (JsonParseException var7) {
				throw method_12701(var7);
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length == 1 ? method_2894(strings, server.getPlayerNames()) : Collections.emptyList();
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
