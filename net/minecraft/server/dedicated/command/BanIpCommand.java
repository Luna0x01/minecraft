package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.class_3915;
import net.minecraft.class_4102;
import net.minecraft.class_4317;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.BannedIpList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BanIpCommand {
	public static final Pattern field_2725 = Pattern.compile(
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
	);
	private static final SimpleCommandExceptionType field_21690 = new SimpleCommandExceptionType(new TranslatableText("commands.banip.invalid"));
	private static final SimpleCommandExceptionType field_21691 = new SimpleCommandExceptionType(new TranslatableText("commands.banip.failed"));

	public static void method_20512(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("ban-ip")
					.requires(arg -> arg.method_17473().getPlayerManager().getIpBanList().isEnabled() && arg.method_17575(3)))
				.then(
					((RequiredArgumentBuilder)CommandManager.method_17530("target", StringArgumentType.word())
							.executes(commandContext -> method_20511((class_3915)commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), null)))
						.then(
							CommandManager.method_17530("reason", class_4102.method_18091())
								.executes(
									commandContext -> method_20511(
											(class_3915)commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), class_4102.method_18093(commandContext, "reason")
										)
								)
						)
				)
		);
	}

	private static int method_20511(class_3915 arg, String string, @Nullable Text text) throws CommandSyntaxException {
		Matcher matcher = field_2725.matcher(string);
		if (matcher.matches()) {
			return method_20514(arg, string, text);
		} else {
			ServerPlayerEntity serverPlayerEntity = arg.method_17473().getPlayerManager().getPlayer(string);
			if (serverPlayerEntity != null) {
				return method_20514(arg, serverPlayerEntity.getIp(), text);
			} else {
				throw field_21690.create();
			}
		}
	}

	private static int method_20514(class_3915 arg, String string, @Nullable Text text) throws CommandSyntaxException {
		BannedIpList bannedIpList = arg.method_17473().getPlayerManager().getIpBanList();
		if (bannedIpList.method_21380(string)) {
			throw field_21691.create();
		} else {
			List<ServerPlayerEntity> list = arg.method_17473().getPlayerManager().getPlayersByIp(string);
			BannedIpEntry bannedIpEntry = new BannedIpEntry(string, null, arg.method_17466(), null, text == null ? null : text.getString());
			bannedIpList.add(bannedIpEntry);
			arg.method_17459(new TranslatableText("commands.banip.success", string, bannedIpEntry.getReason()), true);
			if (!list.isEmpty()) {
				arg.method_17459(new TranslatableText("commands.banip.info", list.size(), class_4317.method_19732(list)), true);
			}

			for (ServerPlayerEntity serverPlayerEntity : list) {
				serverPlayerEntity.networkHandler.method_14977(new TranslatableText("multiplayer.disconnect.ip_banned"));
			}

			return list.size();
		}
	}
}
