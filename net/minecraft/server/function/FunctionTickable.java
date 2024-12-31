package net.minecraft.server.function;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandRegistryProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.world.World;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionTickable implements Tickable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final File rootDir;
	private final MinecraftServer server;
	private final Map<Identifier, Function> functions = Maps.newHashMap();
	private String gameLoopFunction = "-";
	private Function runningFunction;
	private final ArrayDeque<FunctionTickable.class_3350> field_16389 = new ArrayDeque();
	private boolean field_16390 = false;
	private final CommandSource commandSource = new CommandSource() {
		@Override
		public String getTranslationKey() {
			return FunctionTickable.this.gameLoopFunction;
		}

		@Override
		public boolean canUseCommand(int permissionLevel, String commandLiteral) {
			return permissionLevel <= 2;
		}

		@Override
		public World getWorld() {
			return FunctionTickable.this.server.worlds[0];
		}

		@Override
		public MinecraftServer getMinecraftServer() {
			return FunctionTickable.this.server;
		}
	};

	public FunctionTickable(@Nullable File file, MinecraftServer minecraftServer) {
		this.rootDir = file;
		this.server = minecraftServer;
		this.reset();
	}

	@Nullable
	public Function getFunction(Identifier id) {
		return (Function)this.functions.get(id);
	}

	public CommandRegistryProvider getCommandRegistry() {
		return this.server.getCommandManager();
	}

	public int getMaxCommandChainLength() {
		return this.server.worlds[0].getGameRules().getInt("maxCommandChainLength");
	}

	public Map<Identifier, Function> getFunctions() {
		return this.functions;
	}

	@Override
	public void tick() {
		String string = this.server.worlds[0].getGameRules().getString("gameLoopFunction");
		if (!string.equals(this.gameLoopFunction)) {
			this.gameLoopFunction = string;
			this.runningFunction = this.getFunction(new Identifier(string));
		}

		if (this.runningFunction != null) {
			this.execute(this.runningFunction, this.commandSource);
		}
	}

	public int execute(Function function, CommandSource source) {
		int i = this.getMaxCommandChainLength();
		if (this.field_16390) {
			if (this.field_16389.size() < i) {
				this.field_16389.addFirst(new FunctionTickable.class_3350(this, source, new Function.FunctionExecutable(function)));
			}

			return 0;
		} else {
			int var10;
			try {
				this.field_16390 = true;
				int j = 0;
				Function.Executable[] executables = function.getExecutables();

				for (int k = executables.length - 1; k >= 0; k--) {
					this.field_16389.push(new FunctionTickable.class_3350(this, source, executables[k]));
				}

				do {
					if (this.field_16389.isEmpty()) {
						return j;
					}

					((FunctionTickable.class_3350)this.field_16389.removeFirst()).method_14952(this.field_16389, i);
				} while (++j < i);

				var10 = j;
			} finally {
				this.field_16389.clear();
				this.field_16390 = false;
			}

			return var10;
		}
	}

	public void reset() {
		this.functions.clear();
		this.runningFunction = null;
		this.gameLoopFunction = "-";
		this.readFunctionsFromDir();
	}

	private void readFunctionsFromDir() {
		if (this.rootDir != null) {
			this.rootDir.mkdirs();

			for (File file : FileUtils.listFiles(this.rootDir, new String[]{"mcfunction"}, true)) {
				String string = FilenameUtils.removeExtension(this.rootDir.toURI().relativize(file.toURI()).toString());
				String[] strings = string.split("/", 2);
				if (strings.length == 2) {
					Identifier identifier = new Identifier(strings[0], strings[1]);

					try {
						this.functions.put(identifier, Function.fromLines(this, Files.readLines(file, StandardCharsets.UTF_8)));
					} catch (Throwable var7) {
						LOGGER.error("Couldn't read custom function " + identifier + " from " + file, var7);
					}
				}
			}

			if (!this.functions.isEmpty()) {
				LOGGER.info("Loaded " + this.functions.size() + " custom command functions");
			}
		}
	}

	public static class class_3350 {
		private final FunctionTickable tickable;
		private final CommandSource source;
		private final Function.Executable executable;

		public class_3350(FunctionTickable functionTickable, CommandSource commandSource, Function.Executable executable) {
			this.tickable = functionTickable;
			this.source = commandSource;
			this.executable = executable;
		}

		public void method_14952(ArrayDeque<FunctionTickable.class_3350> arrayDeque, int i) {
			this.executable.execute(this.tickable, this.source, arrayDeque, i);
		}

		public String toString() {
			return this.executable.toString();
		}
	}
}
