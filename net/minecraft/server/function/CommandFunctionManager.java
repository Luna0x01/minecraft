package net.minecraft.server.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class CommandFunctionManager {
	private static final Text NO_TRACE_IN_FUNCTION_TEXT = new TranslatableText("commands.debug.function.noRecursion");
	private static final Identifier TICK_TAG_ID = new Identifier("tick");
	private static final Identifier LOAD_TAG_ID = new Identifier("load");
	final MinecraftServer server;
	@Nullable
	private CommandFunctionManager.Execution execution;
	private List<CommandFunction> tickFunctions = ImmutableList.of();
	private boolean justLoaded;
	private FunctionLoader loader;

	public CommandFunctionManager(MinecraftServer server, FunctionLoader loader) {
		this.server = server;
		this.loader = loader;
		this.load(loader);
	}

	public int getMaxCommandChainLength() {
		return this.server.getGameRules().getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);
	}

	public CommandDispatcher<ServerCommandSource> getDispatcher() {
		return this.server.getCommandManager().getDispatcher();
	}

	public void tick() {
		this.executeAll(this.tickFunctions, TICK_TAG_ID);
		if (this.justLoaded) {
			this.justLoaded = false;
			Collection<CommandFunction> collection = this.loader.getTags().getTagOrEmpty(LOAD_TAG_ID).values();
			this.executeAll(collection, LOAD_TAG_ID);
		}
	}

	private void executeAll(Collection<CommandFunction> functions, Identifier label) {
		this.server.getProfiler().push(label::toString);

		for (CommandFunction commandFunction : functions) {
			this.execute(commandFunction, this.getScheduledCommandSource());
		}

		this.server.getProfiler().pop();
	}

	public int execute(CommandFunction function, ServerCommandSource source) {
		return this.execute(function, source, null);
	}

	public int execute(CommandFunction function, ServerCommandSource source, @Nullable CommandFunctionManager.Tracer tracer) {
		if (this.execution != null) {
			if (tracer != null) {
				this.execution.reportError(NO_TRACE_IN_FUNCTION_TEXT.getString());
				return 0;
			} else {
				this.execution.recursiveRun(function, source);
				return 0;
			}
		} else {
			int var4;
			try {
				this.execution = new CommandFunctionManager.Execution(tracer);
				var4 = this.execution.run(function, source);
			} finally {
				this.execution = null;
			}

			return var4;
		}
	}

	public void setFunctions(FunctionLoader loader) {
		this.loader = loader;
		this.load(loader);
	}

	private void load(FunctionLoader loader) {
		this.tickFunctions = ImmutableList.copyOf(loader.getTags().getTagOrEmpty(TICK_TAG_ID).values());
		this.justLoaded = true;
	}

	public ServerCommandSource getScheduledCommandSource() {
		return this.server.getCommandSource().withLevel(2).withSilent();
	}

	public Optional<CommandFunction> getFunction(Identifier id) {
		return this.loader.get(id);
	}

	public Tag<CommandFunction> getTag(Identifier id) {
		return this.loader.getTagOrEmpty(id);
	}

	public Iterable<Identifier> getAllFunctions() {
		return this.loader.getFunctions().keySet();
	}

	public Iterable<Identifier> getFunctionTags() {
		return this.loader.getTags().getTagIds();
	}

	public static class Entry {
		private final ServerCommandSource source;
		final int depth;
		private final CommandFunction.Element element;

		public Entry(ServerCommandSource source, int depth, CommandFunction.Element element) {
			this.source = source;
			this.depth = depth;
			this.element = element;
		}

		public void execute(
			CommandFunctionManager manager, Deque<CommandFunctionManager.Entry> entries, int maxChainLength, @Nullable CommandFunctionManager.Tracer tracer
		) {
			try {
				this.element.execute(manager, this.source, entries, maxChainLength, this.depth, tracer);
			} catch (CommandSyntaxException var6) {
				if (tracer != null) {
					tracer.traceError(this.depth, var6.getRawMessage().getString());
				}
			} catch (Exception var7) {
				if (tracer != null) {
					tracer.traceError(this.depth, var7.getMessage());
				}
			}
		}

		public String toString() {
			return this.element.toString();
		}
	}

	class Execution {
		private int depth;
		@Nullable
		private final CommandFunctionManager.Tracer tracer;
		private final Deque<CommandFunctionManager.Entry> queue = Queues.newArrayDeque();
		private final List<CommandFunctionManager.Entry> waitlist = Lists.newArrayList();

		Execution(@Nullable CommandFunctionManager.Tracer tracer) {
			this.tracer = tracer;
		}

		void recursiveRun(CommandFunction function, ServerCommandSource source) {
			int i = CommandFunctionManager.this.getMaxCommandChainLength();
			if (this.queue.size() + this.waitlist.size() < i) {
				this.waitlist.add(new CommandFunctionManager.Entry(source, this.depth, new CommandFunction.FunctionElement(function)));
			}
		}

		int run(CommandFunction function, ServerCommandSource source) {
			int i = CommandFunctionManager.this.getMaxCommandChainLength();
			int j = 0;
			CommandFunction.Element[] elements = function.getElements();

			for (int k = elements.length - 1; k >= 0; k--) {
				this.queue.push(new CommandFunctionManager.Entry(source, 0, elements[k]));
			}

			while (!this.queue.isEmpty()) {
				try {
					CommandFunctionManager.Entry entry = (CommandFunctionManager.Entry)this.queue.removeFirst();
					CommandFunctionManager.this.server.getProfiler().push(entry::toString);
					this.depth = entry.depth;
					entry.execute(CommandFunctionManager.this, this.queue, i, this.tracer);
					if (!this.waitlist.isEmpty()) {
						Lists.reverse(this.waitlist).forEach(this.queue::addFirst);
						this.waitlist.clear();
					}
				} finally {
					CommandFunctionManager.this.server.getProfiler().pop();
				}

				if (++j >= i) {
					return j;
				}
			}

			return j;
		}

		public void reportError(String message) {
			if (this.tracer != null) {
				this.tracer.traceError(this.depth, message);
			}
		}
	}

	public interface Tracer {
		void traceCommandStart(int depth, String command);

		void traceCommandEnd(int depth, String command, int result);

		void traceError(int depth, String message);

		void traceFunctionCall(int depth, Identifier function, int size);
	}
}
