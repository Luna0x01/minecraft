package net.minecraft.server.command;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.SyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.exception.ExceptionUtils;

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
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.tellraw.usage");
		} else {
			PlayerEntity playerEntity = getPlayer(source, args[0]);
			String string = method_10706(args, 1);

			try {
				Text text = Text.Serializer.deserialize(string);
				playerEntity.sendMessage(ChatSerializer.process(source, text, playerEntity));
			} catch (JsonParseException var7) {
				Throwable throwable = ExceptionUtils.getRootCause(var7);
				throw new SyntaxException("commands.tellraw.jsonException", throwable == null ? "" : throwable.getMessage());
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length == 1 ? method_2894(args, MinecraftServer.getServer().getPlayerNames()) : null;
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
