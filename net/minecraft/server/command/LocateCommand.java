package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.class_3915;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LocateCommand {
	private static final SimpleCommandExceptionType field_21760 = new SimpleCommandExceptionType(new TranslatableText("commands.locate.failed"));

	public static void method_20849(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529(
																			"locate"
																		)
																		.requires(arg -> arg.method_17575(2)))
																	.then(CommandManager.method_17529("Village").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "Village"))))
																.then(CommandManager.method_17529("Mineshaft").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "Mineshaft"))))
															.then(CommandManager.method_17529("Mansion").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "Mansion"))))
														.then(CommandManager.method_17529("Igloo").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "Igloo"))))
													.then(
														CommandManager.method_17529("Desert_Pyramid").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "Desert_Pyramid"))
													))
												.then(
													CommandManager.method_17529("Jungle_Pyramid").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "Jungle_Pyramid"))
												))
											.then(CommandManager.method_17529("Swamp_Hut").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "Swamp_Hut"))))
										.then(CommandManager.method_17529("Stronghold").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "Stronghold"))))
									.then(CommandManager.method_17529("Monument").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "Monument"))))
								.then(CommandManager.method_17529("Fortress").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "Fortress"))))
							.then(CommandManager.method_17529("EndCity").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "EndCity"))))
						.then(CommandManager.method_17529("Ocean_Ruin").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "Ocean_Ruin"))))
					.then(CommandManager.method_17529("Buried_Treasure").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "Buried_Treasure"))))
				.then(CommandManager.method_17529("Shipwreck").executes(commandContext -> method_20848((class_3915)commandContext.getSource(), "Shipwreck")))
		);
	}

	private static int method_20848(class_3915 arg, String string) throws CommandSyntaxException {
		BlockPos blockPos = new BlockPos(arg.method_17467());
		BlockPos blockPos2 = arg.method_17468().method_13688(string, blockPos, 100, false);
		if (blockPos2 == null) {
			throw field_21760.create();
		} else {
			int i = MathHelper.floor(method_20846(blockPos.getX(), blockPos.getZ(), blockPos2.getX(), blockPos2.getZ()));
			Text text = ChatSerializer.method_20188(new TranslatableText("chat.coordinates", blockPos2.getX(), "~", blockPos2.getZ()))
				.styled(
					style -> style.setFormatting(Formatting.GREEN)
							.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockPos2.getX() + " ~ " + blockPos2.getZ()))
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.coordinates.tooltip")))
				);
			arg.method_17459(new TranslatableText("commands.locate.success", string, text, i), false);
			return i;
		}
	}

	private static float method_20846(int i, int j, int k, int l) {
		int m = k - i;
		int n = l - j;
		return MathHelper.sqrt((float)(m * m + n * n));
	}
}
