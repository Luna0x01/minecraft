package net.minecraft.server.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand extends AbstractCommand {
	private static final Logger field_7445 = LogManager.getLogger();
	private long field_2727;
	private int field_2728;

	@Override
	public String getCommandName() {
		return "debug";
	}

	@Override
	public int getPermissionLevel() {
		return 3;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.debug.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.debug.usage");
		} else {
			if ("start".equals(args[0])) {
				if (args.length != 1) {
					throw new IncorrectUsageException("commands.debug.usage");
				}

				run(commandSource, this, "commands.debug.start", new Object[0]);
				minecraftServer.enableProfiler();
				this.field_2727 = MinecraftServer.getTimeMillis();
				this.field_2728 = minecraftServer.getTicks();
			} else {
				if (!"stop".equals(args[0])) {
					throw new IncorrectUsageException("commands.debug.usage");
				}

				if (args.length != 1) {
					throw new IncorrectUsageException("commands.debug.usage");
				}

				if (!minecraftServer.profiler.enabled) {
					throw new CommandException("commands.debug.notStarted");
				}

				long l = MinecraftServer.getTimeMillis();
				int i = minecraftServer.getTicks();
				long m = l - this.field_2727;
				int j = i - this.field_2728;
				this.method_2057(m, j, minecraftServer);
				minecraftServer.profiler.enabled = false;
				run(commandSource, this, "commands.debug.stop", new Object[]{(float)m / 1000.0F, j});
			}
		}
	}

	private void method_2057(long l, int i, MinecraftServer minecraftServer) {
		File file = new File(minecraftServer.getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
		file.getParentFile().mkdirs();
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(this.method_2058(l, i, minecraftServer));
		} catch (Throwable var8) {
			IOUtils.closeQuietly(fileWriter);
			field_7445.error("Could not save profiler results to {}", new Object[]{file, var8});
		}
	}

	private String method_2058(long l, int i, MinecraftServer minecraftServer) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("---- Minecraft Profiler Results ----\n");
		stringBuilder.append("// ");
		stringBuilder.append(method_2059());
		stringBuilder.append("\n\n");
		stringBuilder.append("Time span: ").append(l).append(" ms\n");
		stringBuilder.append("Tick span: ").append(i).append(" ticks\n");
		stringBuilder.append("// This is approximately ")
			.append(String.format("%.2f", (float)i / ((float)l / 1000.0F)))
			.append(" ticks per second. It should be ")
			.append(20)
			.append(" ticks per second\n\n");
		stringBuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
		this.method_2056(0, "root", stringBuilder, minecraftServer);
		stringBuilder.append("--- END PROFILE DUMP ---\n\n");
		return stringBuilder.toString();
	}

	private void method_2056(int i, String string, StringBuilder stringBuilder, MinecraftServer minecraftServer) {
		List<Profiler.Section> list = minecraftServer.profiler.getData(string);
		if (list != null && list.size() >= 3) {
			for (int j = 1; j < list.size(); j++) {
				Profiler.Section section = (Profiler.Section)list.get(j);
				stringBuilder.append(String.format("[%02d] ", i));

				for (int k = 0; k < i; k++) {
					stringBuilder.append("|   ");
				}

				stringBuilder.append(section.name)
					.append(" - ")
					.append(String.format("%.2f", section.relativePercentage))
					.append("%/")
					.append(String.format("%.2f", section.absolutePercentage))
					.append("%\n");
				if (!"unspecified".equals(section.name)) {
					try {
						this.method_2056(i + 1, string + "." + section.name, stringBuilder, minecraftServer);
					} catch (Exception var9) {
						stringBuilder.append("[[ EXCEPTION ").append(var9).append(" ]]");
					}
				}
			}
		}
	}

	private static String method_2059() {
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
			return strings[(int)(System.nanoTime() % (long)strings.length)];
		} catch (Throwable var2) {
			return "Witty comment unavailable :(";
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length == 1 ? method_2894(strings, new String[]{"start", "stop"}) : Collections.emptyList();
	}
}
