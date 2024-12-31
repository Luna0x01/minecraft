package net.minecraft.server.command;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.SyntaxException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TitleCommand extends AbstractCommand {
	private static final Logger field_11422 = LogManager.getLogger();

	@Override
	public String getCommandName() {
		return "title";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.title.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.title.usage");
		} else {
			if (args.length < 3) {
				if ("title".equals(args[1]) || "subtitle".equals(args[1])) {
					throw new IncorrectUsageException("commands.title.usage.title");
				}

				if ("times".equals(args[1])) {
					throw new IncorrectUsageException("commands.title.usage.times");
				}
			}

			ServerPlayerEntity serverPlayerEntity = getPlayer(source, args[0]);
			TitleS2CPacket.Action action = TitleS2CPacket.Action.fromName(args[1]);
			if (action != TitleS2CPacket.Action.CLEAR && action != TitleS2CPacket.Action.RESET) {
				if (action == TitleS2CPacket.Action.TIMES) {
					if (args.length != 5) {
						throw new IncorrectUsageException("commands.title.usage");
					} else {
						int i = parseInt(args[2]);
						int j = parseInt(args[3]);
						int k = parseInt(args[4]);
						TitleS2CPacket titleS2CPacket2 = new TitleS2CPacket(i, j, k);
						serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket2);
						run(source, this, "commands.title.success", new Object[0]);
					}
				} else if (args.length < 3) {
					throw new IncorrectUsageException("commands.title.usage");
				} else {
					String string = method_10706(args, 2);

					Text text;
					try {
						text = Text.Serializer.deserialize(string);
					} catch (JsonParseException var9) {
						Throwable throwable = ExceptionUtils.getRootCause(var9);
						throw new SyntaxException("commands.tellraw.jsonException", throwable == null ? "" : throwable.getMessage());
					}

					TitleS2CPacket titleS2CPacket3 = new TitleS2CPacket(action, ChatSerializer.process(source, text, serverPlayerEntity));
					serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket3);
					run(source, this, "commands.title.success", new Object[0]);
				}
			} else if (args.length != 2) {
				throw new IncorrectUsageException("commands.title.usage");
			} else {
				TitleS2CPacket titleS2CPacket = new TitleS2CPacket(action, null);
				serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
				run(source, this, "commands.title.success", new Object[0]);
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, MinecraftServer.getServer().getPlayerNames());
		} else {
			return args.length == 2 ? method_2894(args, TitleS2CPacket.Action.getNames()) : null;
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
