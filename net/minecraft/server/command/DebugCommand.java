package net.minecraft.server.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
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
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.debug.usage");
		} else {
			if (args[0].equals("start")) {
				if (args.length != 1) {
					throw new IncorrectUsageException("commands.debug.usage");
				}

				run(source, this, "commands.debug.start", new Object[0]);
				MinecraftServer.getServer().enableProfiler();
				this.field_2727 = MinecraftServer.getTimeMillis();
				this.field_2728 = MinecraftServer.getServer().getTicks();
			} else {
				if (!args[0].equals("stop")) {
					throw new IncorrectUsageException("commands.debug.usage");
				}

				if (args.length != 1) {
					throw new IncorrectUsageException("commands.debug.usage");
				}

				if (!MinecraftServer.getServer().profiler.enabled) {
					throw new CommandException("commands.debug.notStarted");
				}

				long l = MinecraftServer.getTimeMillis();
				int i = MinecraftServer.getServer().getTicks();
				long m = l - this.field_2727;
				int j = i - this.field_2728;
				this.method_2057(m, j);
				MinecraftServer.getServer().profiler.enabled = false;
				run(source, this, "commands.debug.stop", new Object[]{(float)m / 1000.0F, j});
			}
		}
	}

	private void method_2057(long l, int i) {
		File file = new File(
			MinecraftServer.getServer().getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt"
		);
		file.getParentFile().mkdirs();

		try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(this.method_2058(l, i));
			fileWriter.close();
		} catch (Throwable var6) {
			field_7445.error("Could not save profiler results to " + file, var6);
		}
	}

	private String method_2058(long l, int i) {
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
		this.method_2056(0, "root", stringBuilder);
		stringBuilder.append("--- END PROFILE DUMP ---\n\n");
		return stringBuilder.toString();
	}

	private void method_2056(int i, String string, StringBuilder stringBuilder) {
		List<Profiler.Section> list = MinecraftServer.getServer().profiler.getData(string);
		if (list != null && list.size() >= 3) {
			for (int j = 1; j < list.size(); j++) {
				Profiler.Section section = (Profiler.Section)list.get(j);
				stringBuilder.append(String.format("[%02d] ", i));

				for (int k = 0; k < i; k++) {
					stringBuilder.append(" ");
				}

				stringBuilder.append(section.name)
					.append(" - ")
					.append(String.format("%.2f", section.relativePercentage))
					.append("%/")
					.append(String.format("%.2f", section.absolutePercentage))
					.append("%\n");
				if (!section.name.equals("unspecified")) {
					try {
						this.method_2056(i + 1, string + "." + section.name, stringBuilder);
					} catch (Exception var8) {
						stringBuilder.append("[[ EXCEPTION ").append(var8).append(" ]]");
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
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length == 1 ? method_2894(args, new String[]{"start", "stop"}) : null;
	}
}
