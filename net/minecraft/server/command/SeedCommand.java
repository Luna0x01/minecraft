package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class SeedCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		dispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("seed").requires(source -> !dedicated || source.hasPermissionLevel(2)))
				.executes(
					context -> {
						long l = ((ServerCommandSource)context.getSource()).getWorld().getSeed();
						Text text = Texts.bracketed(
							new LiteralText(String.valueOf(l))
								.styled(
									style -> style.withColor(Formatting.GREEN)
											.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(l)))
											.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.copy.click")))
											.withInsertion(String.valueOf(l))
								)
						);
						((ServerCommandSource)context.getSource()).sendFeedback(new TranslatableText("commands.seed.success", text), false);
						return (int)l;
					}
				)
		);
	}
}
