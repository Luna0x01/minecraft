package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRuleManager;

public class GameRuleCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "gamerule";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.gamerule.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		GameRuleManager gameRuleManager = this.method_11544(minecraftServer);
		String string = args.length > 0 ? args[0] : "";
		String string2 = args.length > 1 ? method_10706(args, 1) : "";
		switch (args.length) {
			case 0:
				commandSource.sendMessage(new LiteralText(concat(gameRuleManager.method_4670())));
				break;
			case 1:
				if (!gameRuleManager.contains(string)) {
					throw new CommandException("commands.gamerule.norule", string);
				}

				String string3 = gameRuleManager.getString(string);
				commandSource.sendMessage(new LiteralText(string).append(" = ").append(string3));
				commandSource.setStat(CommandStats.Type.QUERY_RESULT, gameRuleManager.getInt(string));
				break;
			default:
				if (gameRuleManager.method_8474(string, GameRuleManager.VariableType.BOOLEAN) && !"true".equals(string2) && !"false".equals(string2)) {
					throw new CommandException("commands.generic.boolean.invalid", string2);
				}

				gameRuleManager.setGameRule(string, string2);
				method_8831(gameRuleManager, string, minecraftServer);
				run(commandSource, this, "commands.gamerule.success", new Object[]{string, string2});
		}
	}

	public static void method_8831(GameRuleManager gameRuleManager, String string, MinecraftServer minecraftServer) {
		if ("reducedDebugInfo".equals(string)) {
			byte b = (byte)(gameRuleManager.getBoolean(string) ? 22 : 23);

			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayers()) {
				serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, this.method_11544(server).method_4670());
		} else {
			if (strings.length == 2) {
				GameRuleManager gameRuleManager = this.method_11544(server);
				if (gameRuleManager.method_8474(strings[0], GameRuleManager.VariableType.BOOLEAN)) {
					return method_2894(strings, new String[]{"true", "false"});
				}
			}

			return Collections.emptyList();
		}
	}

	private GameRuleManager method_11544(MinecraftServer minecraftServer) {
		return minecraftServer.getWorld(0).getGameRules();
	}
}
