package net.minecraft.server.command;

import java.util.List;
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
	public void execute(CommandSource source, String[] args) throws CommandException {
		GameRuleManager gameRuleManager = this.method_4124();
		String string = args.length > 0 ? args[0] : "";
		String string2 = args.length > 1 ? method_10706(args, 1) : "";
		switch (args.length) {
			case 0:
				source.sendMessage(new LiteralText(concat(gameRuleManager.method_4670())));
				break;
			case 1:
				if (!gameRuleManager.contains(string)) {
					throw new CommandException("commands.gamerule.norule", string);
				}

				String string3 = gameRuleManager.getString(string);
				source.sendMessage(new LiteralText(string).append(" = ").append(string3));
				source.setStat(CommandStats.Type.QUERY_RESULT, gameRuleManager.getInt(string));
				break;
			default:
				if (gameRuleManager.method_8474(string, GameRuleManager.VariableType.BOOLEAN) && !"true".equals(string2) && !"false".equals(string2)) {
					throw new CommandException("commands.generic.boolean.invalid", string2);
				}

				gameRuleManager.setGameRule(string, string2);
				method_8831(gameRuleManager, string);
				run(source, this, "commands.gamerule.success", new Object[0]);
		}
	}

	public static void method_8831(GameRuleManager gameRuleManager, String string) {
		if ("reducedDebugInfo".equals(string)) {
			byte b = (byte)(gameRuleManager.getBoolean(string) ? 22 : 23);

			for (ServerPlayerEntity serverPlayerEntity : MinecraftServer.getServer().getPlayerManager().getPlayers()) {
				serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, this.method_4124().method_4670());
		} else {
			if (args.length == 2) {
				GameRuleManager gameRuleManager = this.method_4124();
				if (gameRuleManager.method_8474(args[0], GameRuleManager.VariableType.BOOLEAN)) {
					return method_2894(args, new String[]{"true", "false"});
				}
			}

			return null;
		}
	}

	private GameRuleManager method_4124() {
		return MinecraftServer.getServer().getWorld(0).getGameRules();
	}
}
