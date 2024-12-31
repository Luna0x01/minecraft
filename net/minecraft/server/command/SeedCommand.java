package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_3915;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.Formatting;

public class SeedCommand {
	public static void method_20989(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("seed")
					.requires(arg -> arg.method_17473().isSinglePlayer() || arg.method_17575(2)))
				.executes(
					commandContext -> {
						long l = ((class_3915)commandContext.getSource()).method_17468().method_3581();
						Text text = ChatSerializer.method_20188(
							new LiteralText(String.valueOf(l))
								.styled(
									style -> style.setFormatting(Formatting.GREEN)
											.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(l)))
											.setInsertion(String.valueOf(l))
								)
						);
						((class_3915)commandContext.getSource()).method_17459(new TranslatableText("commands.seed.success", text), false);
						return (int)l;
					}
				)
		);
	}
}
