package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4414 {
	private static final Logger field_21721 = LogManager.getLogger();
	private static final SimpleCommandExceptionType field_21722 = new SimpleCommandExceptionType(new TranslatableText("commands.debug.notRunning"));
	private static final SimpleCommandExceptionType field_21723 = new SimpleCommandExceptionType(new TranslatableText("commands.debug.alreadyRunning"));

	public static void method_20648(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("debug").requires(arg -> arg.method_17575(3)))
					.then(CommandManager.method_17529("start").executes(commandContext -> method_20647((class_3915)commandContext.getSource()))))
				.then(CommandManager.method_17529("stop").executes(commandContext -> method_20650((class_3915)commandContext.getSource())))
		);
	}

	private static int method_20647(class_3915 arg) throws CommandSyntaxException {
		MinecraftServer minecraftServer = arg.method_17473();
		Profiler profiler = minecraftServer.profiler;
		if (profiler.method_21519()) {
			throw field_21723.create();
		} else {
			minecraftServer.enableProfiler();
			arg.method_17459(new TranslatableText("commands.debug.started", "Started the debug profiler. Type '/debug stop' to stop it."), true);
			return 0;
		}
	}

	private static int method_20650(class_3915 arg) throws CommandSyntaxException {
		MinecraftServer minecraftServer = arg.method_17473();
		Profiler profiler = minecraftServer.profiler;
		if (!profiler.method_21519()) {
			throw field_21722.create();
		} else {
			long l = Util.method_20230();
			int i = minecraftServer.getTicks();
			long m = l - profiler.method_21522();
			int j = i - profiler.method_21523();
			File file = new File(minecraftServer.getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
			file.getParentFile().mkdirs();
			Writer writer = null;

			try {
				writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
				writer.write(method_20646(m, j, profiler));
			} catch (Throwable var15) {
				field_21721.error("Could not save profiler results to {}", file, var15);
			} finally {
				IOUtils.closeQuietly(writer);
			}

			profiler.method_21521();
			float f = (float)m / 1.0E9F;
			float g = (float)j / f;
			arg.method_17459(new TranslatableText("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", f), j, String.format("%.2f", g)), true);
			return MathHelper.floor(g);
		}
	}

	private static String method_20646(long l, int i, Profiler profiler) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("---- Minecraft Profiler Results ----\n");
		stringBuilder.append("// ");
		stringBuilder.append(method_20644());
		stringBuilder.append("\n\n");
		stringBuilder.append("Time span: ").append(l).append(" ms\n");
		stringBuilder.append("Tick span: ").append(i).append(" ticks\n");
		stringBuilder.append("// This is approximately ")
			.append(String.format(Locale.ROOT, "%.2f", (float)i / ((float)l / 1.0E9F)))
			.append(" ticks per second. It should be ")
			.append(20)
			.append(" ticks per second\n\n");
		stringBuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
		method_20645(0, "root", stringBuilder, profiler);
		stringBuilder.append("--- END PROFILE DUMP ---\n\n");
		return stringBuilder.toString();
	}

	private static void method_20645(int i, String string, StringBuilder stringBuilder, Profiler profiler) {
		List<Profiler.Section> list = profiler.getData(string);
		if (list != null && list.size() >= 3) {
			for (int j = 1; j < list.size(); j++) {
				Profiler.Section section = (Profiler.Section)list.get(j);
				stringBuilder.append(String.format("[%02d] ", i));

				for (int k = 0; k < i; k++) {
					stringBuilder.append("|   ");
				}

				stringBuilder.append(section.name)
					.append(" - ")
					.append(String.format(Locale.ROOT, "%.2f", section.relativePercentage))
					.append("%/")
					.append(String.format(Locale.ROOT, "%.2f", section.absolutePercentage))
					.append("%\n");
				if (!"unspecified".equals(section.name)) {
					try {
						method_20645(i + 1, string + "." + section.name, stringBuilder, profiler);
					} catch (Exception var8) {
						stringBuilder.append("[[ EXCEPTION ").append(var8).append(" ]]");
					}
				}
			}
		}
	}

	private static String method_20644() {
		String[] strings = new String[]{
			"Shiny numbers!",
			"Am I not running fast enough? :(",
			"I'm working as hard as I can!",
			"Will I ever be good enough for you? :(",
			"Speedy. Zoooooom!",
			"Hello world",
			"40% better than a crash report.",
			"Now with extra numbers",
			"Now with less numbers",
			"Now with the same numbers",
			"You should add flames to things, it makes them go faster!",
			"Do you feel the need for... optimization?",
			"*cracks redstone whip*",
			"Maybe if you treated it better then it'll have more motivation to work faster! Poor server."
		};

		try {
			return strings[(int)(Util.method_20230() % (long)strings.length)];
		} catch (Throwable var2) {
			return "Witty comment unavailable :(";
		}
	}
}
