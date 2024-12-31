package net.minecraft.server.command;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.class_3915;
import net.minecraft.class_3965;
import net.minecraft.class_4323;
import net.minecraft.class_4327;
import net.minecraft.class_4376;
import net.minecraft.class_4404;
import net.minecraft.class_4406;
import net.minecraft.class_4407;
import net.minecraft.class_4408;
import net.minecraft.class_4409;
import net.minecraft.class_4410;
import net.minecraft.class_4412;
import net.minecraft.class_4414;
import net.minecraft.class_4415;
import net.minecraft.class_4416;
import net.minecraft.class_4417;
import net.minecraft.class_4418;
import net.minecraft.class_4419;
import net.minecraft.class_4421;
import net.minecraft.class_4424;
import net.minecraft.class_4425;
import net.minecraft.class_4426;
import net.minecraft.class_4429;
import net.minecraft.class_4430;
import net.minecraft.class_4431;
import net.minecraft.class_4432;
import net.minecraft.class_4433;
import net.minecraft.class_4437;
import net.minecraft.command.CommandException;
import net.minecraft.command.StopSoundCommand;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.dedicated.command.BanIpCommand;
import net.minecraft.server.dedicated.command.DeOpCommand;
import net.minecraft.server.dedicated.command.OpCommand;
import net.minecraft.server.dedicated.command.PardonCommand;
import net.minecraft.server.dedicated.command.PardonIpCommand;
import net.minecraft.server.dedicated.command.SaveAllCommand;
import net.minecraft.server.dedicated.command.SaveOffCommand;
import net.minecraft.server.dedicated.command.SaveOnCommand;
import net.minecraft.server.dedicated.command.StopCommand;
import net.minecraft.server.dedicated.command.WhitelistCommand;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandManager {
	private static final Logger field_19322 = LogManager.getLogger();
	private final CommandDispatcher<class_3915> field_19323 = new CommandDispatcher();

	public CommandManager(boolean bl) {
		class_4404.method_20489(this.field_19323);
		class_4419.method_20689(this.field_19323);
		class_4408.method_20537(this.field_19323);
		class_4409.method_20576(this.field_19323);
		class_4410.method_20587(this.field_19323);
		class_4437.method_21217(this.field_19323);
		class_4412.method_20612(this.field_19323);
		class_4414.method_20648(this.field_19323);
		class_4415.method_20656(this.field_19323);
		DifficultyCommand.method_20660(this.field_19323);
		class_4416.method_20667(this.field_19323);
		class_4417.method_20674(this.field_19323);
		class_4418.method_20678(this.field_19323);
		class_4421.method_20758(this.field_19323);
		FillCommand.method_20778(this.field_19323);
		FunctionCommand.method_20809(this.field_19323);
		GameModeCommand.method_20815(this.field_19323);
		GameRuleCommand.method_20821(this.field_19323);
		GiveCommand.method_20826(this.field_19323);
		HelpCommand.method_20829(this.field_19323);
		KickCommand.method_20833(this.field_19323);
		KillCommand.method_20838(this.field_19323);
		class_4425.method_20842(this.field_19323);
		LocateCommand.method_20849(this.field_19323);
		MessageCommand.method_20866(this.field_19323);
		ParticleCommand.method_20887(this.field_19323);
		PlaySoundCommand.method_20897(this.field_19323);
		PublishCommand.method_20906(this.field_19323);
		ReloadCommand.method_20919(this.field_19323);
		RecipeCommand.method_20912(this.field_19323);
		ReplaceItemCommand.method_20924(this.field_19323);
		class_4426.method_20944(this.field_19323);
		ScoreboardCommand.method_20962(this.field_19323);
		SeedCommand.method_20989(this.field_19323);
		SetBlockCommand.method_20994(this.field_19323);
		SpawnPointCommand.method_21005(this.field_19323);
		class_4430.method_21011(this.field_19323);
		class_4431.method_21017(this.field_19323);
		StopSoundCommand.method_21035(this.field_19323);
		SummonCommand.method_21042(this.field_19323);
		class_4432.method_21049(this.field_19323);
		class_4433.method_21068(this.field_19323);
		TeleportCommand.method_21107(this.field_19323);
		TellRawCommand.method_21119(this.field_19323);
		class_4424.method_20794(this.field_19323);
		TimeCommand.method_21123(this.field_19323);
		TitleCommand.method_21140(this.field_19323);
		TriggerCommand.method_21151(this.field_19323);
		WeatherCommand.method_21160(this.field_19323);
		WorldBorderCommand.method_21192(this.field_19323);
		if (bl) {
			BanIpCommand.method_20512(this.field_19323);
			class_4406.method_20518(this.field_19323);
			class_4407.method_20525(this.field_19323);
			DeOpCommand.method_20641(this.field_19323);
			OpCommand.method_20870(this.field_19323);
			PardonCommand.method_20877(this.field_19323);
			PardonIpCommand.method_20882(this.field_19323);
			SaveAllCommand.method_20933(this.field_19323);
			SaveOffCommand.method_20938(this.field_19323);
			SaveOnCommand.method_20941(this.field_19323);
			class_4429.method_21001(this.field_19323);
			StopCommand.method_21031(this.field_19323);
			WhitelistCommand.method_21171(this.field_19323);
		}

		this.field_19323
			.findAmbiguities(
				(commandNode, commandNode2, commandNode3, collection) -> field_19322.warn(
						"Ambiguity between arguments {} and {} with inputs: {}", this.field_19323.getPath(commandNode2), this.field_19323.getPath(commandNode3), collection
					)
			);
		this.field_19323.setConsumer((commandContext, blx, i) -> ((class_3915)commandContext.getSource()).method_17457(commandContext, blx, i));
	}

	public void method_17528(File file) {
		try {
			Files.write(
				new GsonBuilder().setPrettyPrinting().create().toJson(class_4323.method_19894(this.field_19323, this.field_19323.getRoot())), file, StandardCharsets.UTF_8
			);
		} catch (IOException var3) {
			field_19322.error("Couldn't write out command tree!", var3);
		}
	}

	public int method_17519(class_3915 arg, String string) {
		StringReader stringReader = new StringReader(string);
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		arg.method_17473().profiler.push(string);

		byte var20;
		try {
			return this.field_19323.execute(stringReader, arg);
		} catch (CommandException var13) {
			arg.method_17458(var13.method_17390());
			return 0;
		} catch (CommandSyntaxException var14) {
			arg.method_17458(ChatSerializer.method_20187(var14.getRawMessage()));
			if (var14.getInput() != null && var14.getCursor() >= 0) {
				int i = Math.min(var14.getInput().length(), var14.getCursor());
				Text text = new LiteralText("").formatted(Formatting.GRAY).styled(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, string)));
				if (i > 10) {
					text.append("...");
				}

				text.append(var14.getInput().substring(Math.max(0, i - 10), i));
				if (i < var14.getInput().length()) {
					Text text2 = new LiteralText(var14.getInput().substring(i)).formatted(new Formatting[]{Formatting.RED, Formatting.UNDERLINE});
					text.append(text2);
				}

				text.append(new TranslatableText("command.context.here").formatted(new Formatting[]{Formatting.RED, Formatting.ITALIC}));
				arg.method_17458(text);
			}

			return 0;
		} catch (Exception var15) {
			Text text3 = new LiteralText(var15.getMessage() == null ? var15.getClass().getName() : var15.getMessage());
			if (field_19322.isDebugEnabled()) {
				StackTraceElement[] stackTraceElements = var15.getStackTrace();

				for (int j = 0; j < Math.min(stackTraceElements.length, 3); j++) {
					text3.append("\n\n")
						.append(stackTraceElements[j].getMethodName())
						.append("\n ")
						.append(stackTraceElements[j].getFileName())
						.append(":")
						.append(String.valueOf(stackTraceElements[j].getLineNumber()));
				}
			}

			arg.method_17458(new TranslatableText("command.failed").styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text3))));
			var20 = 0;
		} finally {
			arg.method_17473().profiler.pop();
		}

		return var20;
	}

	public void method_17532(ServerPlayerEntity serverPlayerEntity) {
		Map<CommandNode<class_3915>, CommandNode<class_3965>> map = Maps.newHashMap();
		RootCommandNode<class_3965> rootCommandNode = new RootCommandNode();
		map.put(this.field_19323.getRoot(), rootCommandNode);
		this.method_17525(this.field_19323.getRoot(), rootCommandNode, serverPlayerEntity.method_15582(), map);
		serverPlayerEntity.networkHandler.sendPacket(new class_4376(rootCommandNode));
	}

	private void method_17525(
		CommandNode<class_3915> commandNode, CommandNode<class_3965> commandNode2, class_3915 arg, Map<CommandNode<class_3915>, CommandNode<class_3965>> map
	) {
		for (CommandNode<class_3915> commandNode3 : commandNode.getChildren()) {
			if (commandNode3.canUse(arg)) {
				ArgumentBuilder<class_3965, ?> argumentBuilder = commandNode3.createBuilder();
				argumentBuilder.requires(argx -> true);
				if (argumentBuilder.getCommand() != null) {
					argumentBuilder.executes(commandContext -> 0);
				}

				if (argumentBuilder instanceof RequiredArgumentBuilder) {
					RequiredArgumentBuilder<class_3965, ?> requiredArgumentBuilder = (RequiredArgumentBuilder<class_3965, ?>)argumentBuilder;
					if (requiredArgumentBuilder.getSuggestionsProvider() != null) {
						requiredArgumentBuilder.suggests(class_4327.method_19906(requiredArgumentBuilder.getSuggestionsProvider()));
					}
				}

				if (argumentBuilder.getRedirect() != null) {
					argumentBuilder.redirect((CommandNode)map.get(argumentBuilder.getRedirect()));
				}

				CommandNode<class_3965> commandNode4 = argumentBuilder.build();
				map.put(commandNode3, commandNode4);
				commandNode2.addChild(commandNode4);
				if (!commandNode3.getChildren().isEmpty()) {
					this.method_17525(commandNode3, commandNode4, arg, map);
				}
			}
		}
	}

	public static LiteralArgumentBuilder<class_3915> method_17529(String string) {
		return LiteralArgumentBuilder.literal(string);
	}

	public static <T> RequiredArgumentBuilder<class_3915, T> method_17530(String string, ArgumentType<T> argumentType) {
		return RequiredArgumentBuilder.argument(string, argumentType);
	}

	public static Predicate<String> method_17520(CommandManager.class_3938 arg) {
		return string -> {
			try {
				arg.parse(new StringReader(string));
				return true;
			} catch (CommandSyntaxException var3) {
				return false;
			}
		};
	}

	public CommandDispatcher<class_3915> method_17518() {
		return this.field_19323;
	}

	@FunctionalInterface
	public interface class_3938 {
		void parse(StringReader stringReader) throws CommandSyntaxException;
	}
}
