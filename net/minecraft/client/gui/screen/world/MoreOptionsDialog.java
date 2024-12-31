package net.minecraft.client.gui.screen.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.DataResult.PartialResult;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class MoreOptionsDialog implements TickableElement, Drawable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Text CUSTOM_TEXT = new TranslatableText("generator.custom");
	private static final Text AMPLIFIED_INFO_TEXT = new TranslatableText("generator.amplified.info");
	private static final Text MAP_FEATURES_INFO_TEXT = new TranslatableText("selectWorld.mapFeatures.info");
	private MultilineText generatorInfoText = MultilineText.EMPTY;
	private TextRenderer textRenderer;
	private int parentWidth;
	private TextFieldWidget seedTextField;
	private ButtonWidget mapFeaturesButton;
	public ButtonWidget bonusItemsButton;
	private ButtonWidget mapTypeButton;
	private ButtonWidget customizeTypeButton;
	private ButtonWidget importSettingsButton;
	private DynamicRegistryManager.Impl registryManager;
	private GeneratorOptions generatorOptions;
	private Optional<GeneratorType> generatorType;
	private OptionalLong seedText;

	public MoreOptionsDialog(
		DynamicRegistryManager.Impl registryManager, GeneratorOptions generatorOptions, Optional<GeneratorType> generatorType, OptionalLong seedText
	) {
		this.registryManager = registryManager;
		this.generatorOptions = generatorOptions;
		this.generatorType = generatorType;
		this.seedText = seedText;
	}

	public void init(CreateWorldScreen parent, MinecraftClient client, TextRenderer textRenderer) {
		this.textRenderer = textRenderer;
		this.parentWidth = parent.width;
		this.seedTextField = new TextFieldWidget(this.textRenderer, this.parentWidth / 2 - 100, 60, 200, 20, new TranslatableText("selectWorld.enterSeed"));
		this.seedTextField.setText(seedToString(this.seedText));
		this.seedTextField.setChangedListener(string -> this.seedText = this.getSeed());
		parent.addChild(this.seedTextField);
		int i = this.parentWidth / 2 - 155;
		int j = this.parentWidth / 2 + 5;
		this.mapFeaturesButton = parent.addButton(new ButtonWidget(i, 100, 150, 20, new TranslatableText("selectWorld.mapFeatures"), buttonWidget -> {
			this.generatorOptions = this.generatorOptions.toggleGenerateStructures();
			buttonWidget.queueNarration(250);
		}) {
			@Override
			public Text getMessage() {
				return ScreenTexts.composeToggleText(super.getMessage(), MoreOptionsDialog.this.generatorOptions.shouldGenerateStructures());
			}

			@Override
			protected MutableText getNarrationMessage() {
				return super.getNarrationMessage().append(". ").append(new TranslatableText("selectWorld.mapFeatures.info"));
			}
		});
		this.mapFeaturesButton.visible = false;
		this.mapTypeButton = parent.addButton(
			new ButtonWidget(
				j,
				100,
				150,
				20,
				new TranslatableText("selectWorld.mapType"),
				buttonWidget -> {
					while (this.generatorType.isPresent()) {
						int ix = GeneratorType.VALUES.indexOf(this.generatorType.get()) + 1;
						if (ix >= GeneratorType.VALUES.size()) {
							ix = 0;
						}

						GeneratorType generatorType = (GeneratorType)GeneratorType.VALUES.get(ix);
						this.generatorType = Optional.of(generatorType);
						this.generatorOptions = generatorType.createDefaultOptions(
							this.registryManager, this.generatorOptions.getSeed(), this.generatorOptions.shouldGenerateStructures(), this.generatorOptions.hasBonusChest()
						);
						if (!this.generatorOptions.isDebugWorld() || Screen.hasShiftDown()) {
							break;
						}
					}

					parent.setMoreOptionsOpen();
					buttonWidget.queueNarration(250);
				}
			) {
				@Override
				public Text getMessage() {
					return super.getMessage()
						.shallowCopy()
						.append(" ")
						.append((Text)MoreOptionsDialog.this.generatorType.map(GeneratorType::getTranslationKey).orElse(MoreOptionsDialog.CUSTOM_TEXT));
				}

				@Override
				protected MutableText getNarrationMessage() {
					return Objects.equals(MoreOptionsDialog.this.generatorType, Optional.of(GeneratorType.AMPLIFIED))
						? super.getNarrationMessage().append(". ").append(MoreOptionsDialog.AMPLIFIED_INFO_TEXT)
						: super.getNarrationMessage();
				}
			}
		);
		this.mapTypeButton.visible = false;
		this.mapTypeButton.active = this.generatorType.isPresent();
		this.customizeTypeButton = parent.addButton(new ButtonWidget(j, 120, 150, 20, new TranslatableText("selectWorld.customizeType"), buttonWidget -> {
			GeneratorType.ScreenProvider screenProvider = (GeneratorType.ScreenProvider)GeneratorType.SCREEN_PROVIDERS.get(this.generatorType);
			if (screenProvider != null) {
				client.openScreen(screenProvider.createEditScreen(parent, this.generatorOptions));
			}
		}));
		this.customizeTypeButton.visible = false;
		this.bonusItemsButton = parent.addButton(new ButtonWidget(i, 151, 150, 20, new TranslatableText("selectWorld.bonusItems"), buttonWidget -> {
			this.generatorOptions = this.generatorOptions.toggleBonusChest();
			buttonWidget.queueNarration(250);
		}) {
			@Override
			public Text getMessage() {
				return ScreenTexts.composeToggleText(super.getMessage(), MoreOptionsDialog.this.generatorOptions.hasBonusChest() && !parent.hardcore);
			}
		});
		this.bonusItemsButton.visible = false;
		this.importSettingsButton = parent.addButton(
			new ButtonWidget(
				i,
				185,
				150,
				20,
				new TranslatableText("selectWorld.import_worldgen_settings"),
				buttonWidget -> {
					TranslatableText translatableText = new TranslatableText("selectWorld.import_worldgen_settings.select_file");
					String string = TinyFileDialogs.tinyfd_openFileDialog(translatableText.getString(), null, null, null, false);
					if (string != null) {
						DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
						ResourcePackManager resourcePackManager = new ResourcePackManager(
							new VanillaDataPackProvider(), new FileResourcePackProvider(parent.getDataPackTempDir().toFile(), ResourcePackSource.PACK_SOURCE_WORLD)
						);

						ServerResourceManager serverResourceManager;
						try {
							MinecraftServer.loadDataPacks(resourcePackManager, parent.dataPackSettings, false);
							CompletableFuture<ServerResourceManager> completableFuture = ServerResourceManager.reload(
								resourcePackManager.createResourcePacks(), CommandManager.RegistrationEnvironment.INTEGRATED, 2, Util.getMainWorkerExecutor(), client
							);
							client.runTasks(completableFuture::isDone);
							serverResourceManager = (ServerResourceManager)completableFuture.get();
						} catch (ExecutionException | InterruptedException var25) {
							LOGGER.error("Error loading data packs when importing world settings", var25);
							Text text = new TranslatableText("selectWorld.import_worldgen_settings.failure");
							Text text2 = new LiteralText(var25.getMessage());
							client.getToastManager().add(SystemToast.create(client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text, text2));
							resourcePackManager.close();
							return;
						}

						RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, serverResourceManager.getResourceManager(), impl);
						JsonParser jsonParser = new JsonParser();

						DataResult<GeneratorOptions> dataResult;
						try {
							BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(string));
							Throwable string2 = null;

							try {
								JsonElement jsonElement = jsonParser.parse(bufferedReader);
								dataResult = GeneratorOptions.CODEC.parse(registryOps, jsonElement);
							} catch (Throwable var24) {
								string2 = var24;
								throw var24;
							} finally {
								if (bufferedReader != null) {
									if (string2 != null) {
										try {
											bufferedReader.close();
										} catch (Throwable var23) {
											string2.addSuppressed(var23);
										}
									} else {
										bufferedReader.close();
									}
								}
							}
						} catch (JsonIOException | JsonSyntaxException | IOException var27) {
							dataResult = DataResult.error("Failed to parse file: " + var27.getMessage());
						}

						if (dataResult.error().isPresent()) {
							Text text3 = new TranslatableText("selectWorld.import_worldgen_settings.failure");
							String string2 = ((PartialResult)dataResult.error().get()).message();
							LOGGER.error("Error parsing world settings: {}", string2);
							Text text4 = new LiteralText(string2);
							client.getToastManager().add(SystemToast.create(client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text3, text4));
						}

						serverResourceManager.close();
						Lifecycle lifecycle = dataResult.lifecycle();
						dataResult.resultOrPartial(LOGGER::error)
							.ifPresent(
								generatorOptions -> {
									BooleanConsumer booleanConsumer = bl -> {
										client.openScreen(parent);
										if (bl) {
											this.importOptions(impl, generatorOptions);
										}
									};
									if (lifecycle == Lifecycle.stable()) {
										this.importOptions(impl, generatorOptions);
									} else if (lifecycle == Lifecycle.experimental()) {
										client.openScreen(
											new ConfirmScreen(
												booleanConsumer,
												new TranslatableText("selectWorld.import_worldgen_settings.experimental.title"),
												new TranslatableText("selectWorld.import_worldgen_settings.experimental.question")
											)
										);
									} else {
										client.openScreen(
											new ConfirmScreen(
												booleanConsumer,
												new TranslatableText("selectWorld.import_worldgen_settings.deprecated.title"),
												new TranslatableText("selectWorld.import_worldgen_settings.deprecated.question")
											)
										);
									}
								}
							);
					}
				}
			)
		);
		this.importSettingsButton.visible = false;
		this.generatorInfoText = MultilineText.create(textRenderer, AMPLIFIED_INFO_TEXT, this.mapTypeButton.getWidth());
	}

	private void importOptions(DynamicRegistryManager.Impl registryManager, GeneratorOptions generatorOptions) {
		this.registryManager = registryManager;
		this.generatorOptions = generatorOptions;
		this.generatorType = GeneratorType.method_29078(generatorOptions);
		this.seedText = OptionalLong.of(generatorOptions.getSeed());
		this.seedTextField.setText(seedToString(this.seedText));
		this.mapTypeButton.active = this.generatorType.isPresent();
	}

	@Override
	public void tick() {
		this.seedTextField.tick();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.mapFeaturesButton.visible) {
			this.textRenderer.drawWithShadow(matrices, MAP_FEATURES_INFO_TEXT, (float)(this.parentWidth / 2 - 150), 122.0F, -6250336);
		}

		this.seedTextField.render(matrices, mouseX, mouseY, delta);
		if (this.generatorType.equals(Optional.of(GeneratorType.AMPLIFIED))) {
			this.generatorInfoText.drawWithShadow(matrices, this.mapTypeButton.x + 2, this.mapTypeButton.y + 22, 9, 10526880);
		}
	}

	protected void setGeneratorOptions(GeneratorOptions generatorOptions) {
		this.generatorOptions = generatorOptions;
	}

	private static String seedToString(OptionalLong seed) {
		return seed.isPresent() ? Long.toString(seed.getAsLong()) : "";
	}

	private static OptionalLong tryParseLong(String string) {
		try {
			return OptionalLong.of(Long.parseLong(string));
		} catch (NumberFormatException var2) {
			return OptionalLong.empty();
		}
	}

	public GeneratorOptions getGeneratorOptions(boolean hardcore) {
		OptionalLong optionalLong = this.getSeed();
		return this.generatorOptions.withHardcore(hardcore, optionalLong);
	}

	private OptionalLong getSeed() {
		String string = this.seedTextField.getText();
		OptionalLong optionalLong;
		if (StringUtils.isEmpty(string)) {
			optionalLong = OptionalLong.empty();
		} else {
			OptionalLong optionalLong2 = tryParseLong(string);
			if (optionalLong2.isPresent() && optionalLong2.getAsLong() != 0L) {
				optionalLong = optionalLong2;
			} else {
				optionalLong = OptionalLong.of((long)string.hashCode());
			}
		}

		return optionalLong;
	}

	public boolean isDebugWorld() {
		return this.generatorOptions.isDebugWorld();
	}

	public void setVisible(boolean visible) {
		this.mapTypeButton.visible = visible;
		if (this.generatorOptions.isDebugWorld()) {
			this.mapFeaturesButton.visible = false;
			this.bonusItemsButton.visible = false;
			this.customizeTypeButton.visible = false;
			this.importSettingsButton.visible = false;
		} else {
			this.mapFeaturesButton.visible = visible;
			this.bonusItemsButton.visible = visible;
			this.customizeTypeButton.visible = visible && GeneratorType.SCREEN_PROVIDERS.containsKey(this.generatorType);
			this.importSettingsButton.visible = visible;
		}

		this.seedTextField.setVisible(visible);
	}

	public DynamicRegistryManager.Impl getRegistryManager() {
		return this.registryManager;
	}

	void loadDatapacks(ServerResourceManager serverResourceManager) {
		DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
		RegistryReadingOps<JsonElement> registryReadingOps = RegistryReadingOps.of(JsonOps.INSTANCE, this.registryManager);
		RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, serverResourceManager.getResourceManager(), impl);
		DataResult<GeneratorOptions> dataResult = GeneratorOptions.CODEC
			.encodeStart(registryReadingOps, this.generatorOptions)
			.flatMap(jsonElement -> GeneratorOptions.CODEC.parse(registryOps, jsonElement));
		dataResult.resultOrPartial(Util.method_29188("Error parsing worldgen settings after loading data packs: ", LOGGER::error)).ifPresent(generatorOptions -> {
			this.generatorOptions = generatorOptions;
			this.registryManager = impl;
		});
	}
}
