package net.minecraft.server.function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.class_3915;
import net.minecraft.class_4469;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionTickable implements Tickable, ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier field_21667 = new Identifier("tick");
	private static final Identifier field_21668 = new Identifier("load");
	public static final int field_21665 = "functions/".length();
	public static final int field_21666 = ".mcfunction".length();
	private final MinecraftServer server;
	private final Map<Identifier, Function> functions = Maps.newHashMap();
	private final ArrayDeque<FunctionTickable.class_3350> field_16389 = new ArrayDeque();
	private boolean field_16390;
	private final TagContainer<Function> field_21669 = new TagContainer<>(
		identifier -> this.getFunction(identifier) != null, this::getFunction, "tags/functions", true, "function"
	);
	private final List<Function> field_21670 = Lists.newArrayList();
	private boolean field_21671;

	public FunctionTickable(MinecraftServer minecraftServer) {
		this.server = minecraftServer;
	}

	@Nullable
	public Function getFunction(Identifier id) {
		return (Function)this.functions.get(id);
	}

	public MinecraftServer method_20453() {
		return this.server;
	}

	public int getMaxCommandChainLength() {
		return this.server.method_20335().getInt("maxCommandChainLength");
	}

	public Map<Identifier, Function> getFunctions() {
		return this.functions;
	}

	public CommandDispatcher<class_3915> method_20461() {
		return this.server.method_2971().method_17518();
	}

	@Override
	public void tick() {
		this.server.profiler.push(field_21667::toString);

		for (Function function : this.field_21670) {
			this.method_14944(function, this.method_20462());
		}

		this.server.profiler.pop();
		if (this.field_21671) {
			this.field_21671 = false;
			Collection<Function> collection = this.method_20463().getOrCreate(field_21668).values();
			this.server.profiler.push(field_21668::toString);

			for (Function function2 : collection) {
				this.method_14944(function2, this.method_20462());
			}

			this.server.profiler.pop();
		}
	}

	public int method_14944(Function function, class_3915 arg) {
		int i = this.getMaxCommandChainLength();
		if (this.field_16390) {
			if (this.field_16389.size() < i) {
				this.field_16389.addFirst(new FunctionTickable.class_3350(this, arg, new Function.FunctionExecutable(function)));
			}

			return 0;
		} else {
			int var16;
			try {
				this.field_16390 = true;
				int j = 0;
				Function.Executable[] executables = function.getExecutables();

				for (int k = executables.length - 1; k >= 0; k--) {
					this.field_16389.push(new FunctionTickable.class_3350(this, arg, executables[k]));
				}

				do {
					if (this.field_16389.isEmpty()) {
						return j;
					}

					try {
						FunctionTickable.class_3350 lv = (FunctionTickable.class_3350)this.field_16389.removeFirst();
						this.server.profiler.push(lv::toString);
						lv.method_14952(this.field_16389, i);
					} finally {
						this.server.profiler.pop();
					}
				} while (++j < i);

				var16 = j;
			} finally {
				this.field_16389.clear();
				this.field_16390 = false;
			}

			return var16;
		}
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		this.functions.clear();
		this.field_21670.clear();
		this.field_21669.method_21489();
		Collection<Identifier> collection = resourceManager.method_21372("functions", stringx -> stringx.endsWith(".mcfunction"));
		List<CompletableFuture<Function>> list = Lists.newArrayList();

		for (Identifier identifier : collection) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(field_21665, string.length() - field_21666));
			list.add(
				CompletableFuture.supplyAsync(() -> method_20458(resourceManager, identifier), class_4469.field_21928)
					.thenApplyAsync(listx -> Function.method_17357(identifier2, this, listx))
					.handle((function, throwable) -> this.method_20454(function, throwable, identifier))
			);
		}

		CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
		if (!this.functions.isEmpty()) {
			LOGGER.info("Loaded {} custom command functions", this.functions.size());
		}

		this.field_21669.method_21487(resourceManager);
		this.field_21670.addAll(this.field_21669.getOrCreate(field_21667).values());
		this.field_21671 = true;
	}

	@Nullable
	private Function method_20454(Function function, @Nullable Throwable throwable, Identifier identifier) {
		if (throwable != null) {
			LOGGER.error("Couldn't load function at {}", identifier, throwable);
			return null;
		} else {
			synchronized (this.functions) {
				this.functions.put(function.method_17355(), function);
				return function;
			}
		}
	}

	private static List<String> method_20458(ResourceManager resourceManager, Identifier identifier) {
		try {
			Resource resource = resourceManager.getResource(identifier);
			Throwable var3 = null;

			List var4;
			try {
				var4 = IOUtils.readLines(resource.getInputStream(), StandardCharsets.UTF_8);
			} catch (Throwable var14) {
				var3 = var14;
				throw var14;
			} finally {
				if (resource != null) {
					if (var3 != null) {
						try {
							resource.close();
						} catch (Throwable var13) {
							var3.addSuppressed(var13);
						}
					} else {
						resource.close();
					}
				}
			}

			return var4;
		} catch (IOException var16) {
			throw new CompletionException(var16);
		}
	}

	public class_3915 method_20462() {
		return this.server.method_20330().method_17449(2).method_17448();
	}

	public TagContainer<Function> method_20463() {
		return this.field_21669;
	}

	public static class class_3350 {
		private final FunctionTickable tickable;
		private final class_3915 field_16394;
		private final Function.Executable executable;

		public class_3350(FunctionTickable functionTickable, class_3915 arg, Function.Executable executable) {
			this.tickable = functionTickable;
			this.field_16394 = arg;
			this.executable = executable;
		}

		public void method_14952(ArrayDeque<FunctionTickable.class_3350> arrayDeque, int i) {
			try {
				this.executable.method_14541(this.tickable, this.field_16394, arrayDeque, i);
			} catch (Throwable var4) {
			}
		}

		public String toString() {
			return this.executable.toString();
		}
	}
}
