package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.SaveProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadCommand {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void tryReloadDataPacks(Collection<String> dataPacks, ServerCommandSource source) {
		source.getServer().reloadResources(dataPacks).exceptionally(throwable -> {
			LOGGER.warn("Failed to execute reload", throwable);
			source.sendError(new TranslatableText("commands.reload.failure"));
			return null;
		});
	}

	private static Collection<String> findNewDataPacks(ResourcePackManager dataPackManager, SaveProperties saveProperties, Collection<String> enabledDataPacks) {
		dataPackManager.scanPacks();
		Collection<String> collection = Lists.newArrayList(enabledDataPacks);
		Collection<String> collection2 = saveProperties.getDataPackSettings().getDisabled();

		for (String string : dataPackManager.getNames()) {
			if (!collection2.contains(string) && !collection.contains(string)) {
				collection.add(string);
			}
		}

		return collection;
	}

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("reload").requires(source -> source.hasPermissionLevel(2))).executes(context -> {
				ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
				MinecraftServer minecraftServer = serverCommandSource.getServer();
				ResourcePackManager resourcePackManager = minecraftServer.getDataPackManager();
				SaveProperties saveProperties = minecraftServer.getSaveProperties();
				Collection<String> collection = resourcePackManager.getEnabledNames();
				Collection<String> collection2 = findNewDataPacks(resourcePackManager, saveProperties, collection);
				serverCommandSource.sendFeedback(new TranslatableText("commands.reload.success"), true);
				tryReloadDataPacks(collection2, serverCommandSource);
				return 0;
			})
		);
	}
}
