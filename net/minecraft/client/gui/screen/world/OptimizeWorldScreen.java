package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OptimizeWorldScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Object2IntMap<RegistryKey<World>> DIMENSION_COLORS = Util.make(new Object2IntOpenCustomHashMap(Util.identityHashStrategy()), colors -> {
		colors.put(World.OVERWORLD, -13408734);
		colors.put(World.NETHER, -10075085);
		colors.put(World.END, -8943531);
		colors.defaultReturnValue(-2236963);
	});
	private final BooleanConsumer callback;
	private final WorldUpdater updater;

	@Nullable
	public static OptimizeWorldScreen create(
		MinecraftClient client, BooleanConsumer callback, DataFixer dataFixer, LevelStorage.Session storageSession, boolean eraseCache
	) {
		DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();

		try {
			OptimizeWorldScreen var9;
			try (MinecraftClient.IntegratedResourceManager integratedResourceManager = client.createIntegratedResourceManager(
					impl, MinecraftClient::loadDataPackSettings, MinecraftClient::createSaveProperties, false, storageSession
				)) {
				SaveProperties saveProperties = integratedResourceManager.getSaveProperties();
				storageSession.backupLevelDataFile(impl, saveProperties);
				ImmutableSet<RegistryKey<World>> immutableSet = saveProperties.getGeneratorOptions().getWorlds();
				var9 = new OptimizeWorldScreen(callback, dataFixer, storageSession, saveProperties.getLevelInfo(), eraseCache, immutableSet);
			}

			return var9;
		} catch (Exception var12) {
			LOGGER.warn("Failed to load datapacks, can't optimize world", var12);
			return null;
		}
	}

	private OptimizeWorldScreen(
		BooleanConsumer callback,
		DataFixer dataFixer,
		LevelStorage.Session storageSession,
		LevelInfo levelInfo,
		boolean eraseCache,
		ImmutableSet<RegistryKey<World>> worlds
	) {
		super(new TranslatableText("optimizeWorld.title", levelInfo.getLevelName()));
		this.callback = callback;
		this.updater = new WorldUpdater(storageSession, dataFixer, worlds, eraseCache);
	}

	@Override
	protected void init() {
		super.init();
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 150, 200, 20, ScreenTexts.CANCEL, button -> {
			this.updater.cancel();
			this.callback.accept(false);
		}));
	}

	@Override
	public void tick() {
		if (this.updater.isDone()) {
			this.callback.accept(true);
		}
	}

	@Override
	public void onClose() {
		this.callback.accept(false);
	}

	@Override
	public void removed() {
		this.updater.cancel();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		int i = this.width / 2 - 150;
		int j = this.width / 2 + 150;
		int k = this.height / 4 + 100;
		int l = k + 10;
		drawCenteredText(matrices, this.textRenderer, this.updater.getStatus(), this.width / 2, k - 9 - 2, 10526880);
		if (this.updater.getTotalChunkCount() > 0) {
			fill(matrices, i - 1, k - 1, j + 1, l + 1, -16777216);
			drawTextWithShadow(matrices, this.textRenderer, new TranslatableText("optimizeWorld.info.converted", this.updater.getUpgradedChunkCount()), i, 40, 10526880);
			drawTextWithShadow(
				matrices, this.textRenderer, new TranslatableText("optimizeWorld.info.skipped", this.updater.getSkippedChunkCount()), i, 40 + 9 + 3, 10526880
			);
			drawTextWithShadow(
				matrices, this.textRenderer, new TranslatableText("optimizeWorld.info.total", this.updater.getTotalChunkCount()), i, 40 + (9 + 3) * 2, 10526880
			);
			int m = 0;
			UnmodifiableIterator o = this.updater.getWorlds().iterator();

			while (o.hasNext()) {
				RegistryKey<World> registryKey = (RegistryKey<World>)o.next();
				int n = MathHelper.floor(this.updater.getProgress(registryKey) * (float)(j - i));
				fill(matrices, i + m, k, i + m + n, l, DIMENSION_COLORS.getInt(registryKey));
				m += n;
			}

			int ox = this.updater.getUpgradedChunkCount() + this.updater.getSkippedChunkCount();
			drawCenteredText(matrices, this.textRenderer, ox + " / " + this.updater.getTotalChunkCount(), this.width / 2, k + 2 * 9 + 2, 10526880);
			drawCenteredText(matrices, this.textRenderer, MathHelper.floor(this.updater.getProgress() * 100.0F) + "%", this.width / 2, k + (l - k) / 2 - 9 / 2, 10526880);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}
}
