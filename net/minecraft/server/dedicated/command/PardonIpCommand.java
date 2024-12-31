package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.regex.Matcher;
import net.minecraft.class_3915;
import net.minecraft.class_3965;
import net.minecraft.server.BannedIpList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;

public class PardonIpCommand {
	private static final SimpleCommandExceptionType field_21763 = new SimpleCommandExceptionType(new TranslatableText("commands.pardonip.invalid"));
	private static final SimpleCommandExceptionType field_21764 = new SimpleCommandExceptionType(new TranslatableText("commands.pardonip.failed"));

	public static void method_20882(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("pardon-ip")
					.requires(arg -> arg.method_17473().getPlayerManager().getIpBanList().isEnabled() && arg.method_17575(3)))
				.then(
					CommandManager.method_17530("target", StringArgumentType.word())
						.suggests(
							(commandContext, suggestionsBuilder) -> class_3965.method_17570(
									((class_3915)commandContext.getSource()).method_17473().getPlayerManager().getIpBanList().getNames(), suggestionsBuilder
								)
						)
						.executes(commandContext -> method_20881((class_3915)commandContext.getSource(), StringArgumentType.getString(commandContext, "target")))
				)
		);
	}

	private static int method_20881(class_3915 arg, String string) throws CommandSyntaxException {
		Matcher matcher = BanIpCommand.field_2725.matcher(string);
		if (!matcher.matches()) {
			throw field_21763.create();
		} else {
			BannedIpList bannedIpList = arg.method_17473().getPlayerManager().getIpBanList();
			if (!bannedIpList.method_21380(string)) {
				throw field_21764.create();
			} else {
				bannedIpList.remove(string);
				arg.method_17459(new TranslatableText("commands.pardonip.success", string), true);
				return 1;
			}
		}
	}
}
