package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.ZipCompressor;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.RecordDumper;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PerfCommand {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final SimpleCommandExceptionType NOT_RUNNING_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.perf.notRunning"));
	private static final SimpleCommandExceptionType ALREADY_RUNNING_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.perf.alreadyRunning")
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("perf").requires(source -> source.hasPermissionLevel(4)))
					.then(CommandManager.literal("start").executes(context -> executeStart((ServerCommandSource)context.getSource()))))
				.then(CommandManager.literal("stop").executes(context -> executeStop((ServerCommandSource)context.getSource())))
		);
	}

	private static int executeStart(ServerCommandSource source) throws CommandSyntaxException {
		MinecraftServer minecraftServer = source.getServer();
		if (minecraftServer.isRecorderActive()) {
			throw ALREADY_RUNNING_EXCEPTION.create();
		} else {
			Consumer<ProfileResult> consumer = result -> sendProfilingStoppedMessage(source, result);
			Consumer<Path> consumer2 = dumpDirectory -> saveReport(source, dumpDirectory, minecraftServer);
			minecraftServer.setupRecorder(consumer, consumer2);
			source.sendFeedback(new TranslatableText("commands.perf.started"), false);
			return 0;
		}
	}

	private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
		MinecraftServer minecraftServer = source.getServer();
		if (!minecraftServer.isRecorderActive()) {
			throw NOT_RUNNING_EXCEPTION.create();
		} else {
			minecraftServer.stopRecorder();
			return 0;
		}
	}

	private static void saveReport(ServerCommandSource source, Path tempProfilingDirectory, MinecraftServer server) {
		String string = String.format(
			"%s-%s-%s",
			new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()),
			server.getSaveProperties().getLevelName(),
			SharedConstants.getGameVersion().getId()
		);

		String string2;
		try {
			string2 = FileNameUtil.getNextUniqueName(RecordDumper.DEBUG_PROFILING_DIRECTORY, string, ".zip");
		} catch (IOException var11) {
			source.sendError(new TranslatableText("commands.perf.reportFailed"));
			LOGGER.error(var11);
			return;
		}

		ZipCompressor zipCompressor = new ZipCompressor(RecordDumper.DEBUG_PROFILING_DIRECTORY.resolve(string2));

		try {
			zipCompressor.write(Paths.get("system.txt"), server.addSystemDetails(new SystemDetails()).collect());
			zipCompressor.copyAll(tempProfilingDirectory);
		} catch (Throwable var10) {
			try {
				zipCompressor.close();
			} catch (Throwable var8) {
				var10.addSuppressed(var8);
			}

			throw var10;
		}

		zipCompressor.close();

		try {
			FileUtils.forceDelete(tempProfilingDirectory.toFile());
		} catch (IOException var9) {
			LOGGER.warn("Failed to delete temporary profiling file {}", tempProfilingDirectory, var9);
		}

		source.sendFeedback(new TranslatableText("commands.perf.reportSaved", string2), false);
	}

	private static void sendProfilingStoppedMessage(ServerCommandSource source, ProfileResult result) {
		int i = result.getTickSpan();
		double d = (double)result.getTimeSpan() / (double)TimeHelper.SECOND_IN_MILLIS;
		source.sendFeedback(
			new TranslatableText("commands.perf.stopped", String.format(Locale.ROOT, "%.2f", d), i, String.format(Locale.ROOT, "%.2f", (double)i / d)), false
		);
	}
}
