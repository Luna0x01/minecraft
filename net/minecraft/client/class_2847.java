package net.minecraft.client;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.class_4152;
import net.minecraft.class_4153;
import net.minecraft.class_4277;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.sound.Sounds;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorageAccess;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class class_2847 extends EntryListWidget.Entry<class_2847> implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat field_13360 = new SimpleDateFormat();
	private static final Identifier field_13361 = new Identifier("textures/misc/unknown_server.png");
	private static final Identifier field_13362 = new Identifier("textures/gui/world_selection.png");
	private final MinecraftClient field_13363;
	private final SelectWorldScreen field_13364;
	private final LevelSummary field_13365;
	private final Identifier iconIdentifier;
	private final class_2848 field_13367;
	private File iconFile;
	@Nullable
	private final NativeImageBackedTexture icon;
	private long field_13370;

	public class_2847(class_2848 arg, LevelSummary levelSummary, LevelStorageAccess levelStorageAccess) {
		this.field_13367 = arg;
		this.field_13364 = arg.method_12217();
		this.field_13365 = levelSummary;
		this.field_13363 = MinecraftClient.getInstance();
		this.iconIdentifier = new Identifier("worlds/" + Hashing.sha1().hashUnencodedChars(levelSummary.getFileName()) + "/icon");
		this.iconFile = levelStorageAccess.method_11957(levelSummary.getFileName(), "icon.png");
		if (!this.iconFile.isFile()) {
			this.iconFile = null;
		}

		this.icon = this.method_18893();
	}

	@Override
	public void method_6700(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.method_18403();
		int n = this.method_18404();
		String string = this.field_13365.getDisplayName();
		String string2 = this.field_13365.getFileName() + " (" + field_13360.format(new Date(this.field_13365.getLastPlayed())) + ")";
		String string3 = "";
		if (StringUtils.isEmpty(string)) {
			string = I18n.translate("selectWorld.world") + " " + (this.method_18402() + 1);
		}

		if (this.field_13365.requiresConversion()) {
			string3 = I18n.translate("selectWorld.conversion") + " " + string3;
		} else {
			string3 = I18n.translate("gameMode." + this.field_13365.method_261().getGameModeName());
			if (this.field_13365.isHardcore()) {
				string3 = Formatting.DARK_RED + I18n.translate("gameMode.hardcore") + Formatting.RESET;
			}

			if (this.field_13365.cheatsEnabled()) {
				string3 = string3 + ", " + I18n.translate("selectWorld.cheats");
			}

			String string4 = this.field_13365.method_17972().asFormattedString();
			if (this.field_13365.method_11959()) {
				if (this.field_13365.method_11960()) {
					string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + Formatting.RED + string4 + Formatting.RESET;
				} else {
					string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + Formatting.ITALIC + string4 + Formatting.RESET;
				}
			} else {
				string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + string4;
			}
		}

		this.field_13363.textRenderer.method_18355(string, (float)(n + 32 + 3), (float)(m + 1), 16777215);
		this.field_13363.textRenderer.method_18355(string2, (float)(n + 32 + 3), (float)(m + this.field_13363.textRenderer.fontHeight + 3), 8421504);
		this.field_13363
			.textRenderer
			.method_18355(string3, (float)(n + 32 + 3), (float)(m + this.field_13363.textRenderer.fontHeight + this.field_13363.textRenderer.fontHeight + 3), 8421504);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_13363.getTextureManager().bindTexture(this.icon != null ? this.iconIdentifier : field_13361);
		GlStateManager.enableBlend();
		DrawableHelper.drawTexture(n, m, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		GlStateManager.disableBlend();
		if (this.field_13363.options.touchscreen || bl) {
			this.field_13363.getTextureManager().bindTexture(field_13362);
			DrawableHelper.fill(n, m, n + 32, m + 32, -1601138544);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int o = k - n;
			int p = o < 32 ? 32 : 0;
			if (this.field_13365.method_11959()) {
				DrawableHelper.drawTexture(n, m, 32.0F, (float)p, 32, 32, 256.0F, 256.0F);
				if (this.field_13365.method_17973()) {
					DrawableHelper.drawTexture(n, m, 96.0F, (float)p, 32, 32, 256.0F, 256.0F);
					if (o < 32) {
						Text text = new TranslatableText("selectWorld.tooltip.unsupported", this.field_13365.method_17972()).formatted(Formatting.RED);
						this.field_13364.method_12201(this.field_13363.textRenderer.wrapStringToWidth(text.asFormattedString(), 175));
					}
				} else if (this.field_13365.method_11960()) {
					DrawableHelper.drawTexture(n, m, 96.0F, (float)p, 32, 32, 256.0F, 256.0F);
					if (o < 32) {
						this.field_13364
							.method_12201(
								Formatting.RED
									+ I18n.translate("selectWorld.tooltip.fromNewerVersion1")
									+ "\n"
									+ Formatting.RED
									+ I18n.translate("selectWorld.tooltip.fromNewerVersion2")
							);
					}
				}
			} else {
				DrawableHelper.drawTexture(n, m, 0.0F, (float)p, 32, 32, 256.0F, 256.0F);
			}
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.field_13367.method_12214(this.method_18402());
		if (d - (double)this.method_18404() <= 32.0) {
			this.method_12202();
			return true;
		} else if (Util.method_20227() - this.field_13370 < 250L) {
			this.method_12202();
			return true;
		} else {
			this.field_13370 = Util.method_20227();
			return false;
		}
	}

	public void method_12202() {
		if (this.field_13365.method_17974() || this.field_13365.method_17973()) {
			String string = I18n.translate("selectWorld.backupQuestion");
			String string2 = I18n.translate("selectWorld.backupWarning", this.field_13365.method_17972().asFormattedString(), "1.13.2");
			if (this.field_13365.method_17973()) {
				string = I18n.translate("selectWorld.backupQuestion.customized");
				string2 = I18n.translate("selectWorld.backupWarning.customized");
			}

			this.field_13363.setScreen(new class_4153(this.field_13364, bl -> {
				if (bl) {
					String stringx = this.field_13365.getFileName();
					EditWorldScreen.method_18857(this.field_13363.getCurrentSave(), stringx);
				}

				this.method_12210();
			}, string, string2));
		} else if (this.field_13365.method_11960()) {
			this.field_13363
				.setScreen(
					new ConfirmScreen(
						(bl, i) -> {
							if (bl) {
								try {
									this.method_12210();
								} catch (Exception var4) {
									LOGGER.error("Failure to open 'future world'", var4);
									this.field_13363
										.setScreen(
											new class_4152(
												() -> this.field_13363.setScreen(this.field_13364),
												new TranslatableText("selectWorld.futureworld.error.title"),
												new TranslatableText("selectWorld.futureworld.error.text")
											)
										);
								}
							} else {
								this.field_13363.setScreen(this.field_13364);
							}
						},
						I18n.translate("selectWorld.versionQuestion"),
						I18n.translate("selectWorld.versionWarning", this.field_13365.method_17972().asFormattedString()),
						I18n.translate("selectWorld.versionJoinButton"),
						I18n.translate("gui.cancel"),
						0
					)
				);
		} else {
			this.method_12210();
		}
	}

	public void method_12204() {
		this.field_13363
			.setScreen(
				new ConfirmScreen(
					(bl, i) -> {
						if (bl) {
							this.field_13363.setScreen(new ProgressScreen());
							LevelStorageAccess levelStorageAccess = this.field_13363.getCurrentSave();
							levelStorageAccess.clearAll();
							levelStorageAccess.deleteLevel(this.field_13365.getFileName());
							this.field_13367.method_18898(() -> this.field_13364.field_20497.getText(), true);
						}

						this.field_13363.setScreen(this.field_13364);
					},
					I18n.translate("selectWorld.deleteQuestion"),
					I18n.translate("selectWorld.deleteWarning", this.field_13365.getDisplayName()),
					I18n.translate("selectWorld.deleteButton"),
					I18n.translate("gui.cancel"),
					0
				)
			);
	}

	public void method_12206() {
		this.field_13363.setScreen(new EditWorldScreen((bl, i) -> {
			if (bl) {
				this.field_13367.method_18898(() -> this.field_13364.field_20497.getText(), true);
			}

			this.field_13363.setScreen(this.field_13364);
		}, this.field_13365.getFileName()));
	}

	public void method_12208() {
		try {
			this.field_13363.setScreen(new ProgressScreen());
			CreateWorldScreen createWorldScreen = new CreateWorldScreen(this.field_13364);
			SaveHandler saveHandler = this.field_13363.getCurrentSave().method_250(this.field_13365.getFileName(), null);
			LevelProperties levelProperties = saveHandler.getLevelProperties();
			saveHandler.clear();
			if (levelProperties != null) {
				createWorldScreen.copyWorld(levelProperties);
				if (this.field_13365.method_17973()) {
					this.field_13363
						.setScreen(
							new ConfirmScreen(
								(bl, i) -> {
									if (bl) {
										this.field_13363.setScreen(createWorldScreen);
									} else {
										this.field_13363.setScreen(this.field_13364);
									}
								},
								I18n.translate("selectWorld.recreate.customized.title"),
								I18n.translate("selectWorld.recreate.customized.text"),
								I18n.translate("gui.proceed"),
								I18n.translate("gui.cancel"),
								0
							)
						);
				} else {
					this.field_13363.setScreen(createWorldScreen);
				}
			}
		} catch (Exception var4) {
			LOGGER.error("Unable to recreate world", var4);
			this.field_13363
				.setScreen(
					new class_4152(
						() -> this.field_13363.setScreen(this.field_13364),
						new TranslatableText("selectWorld.recreate.error.title"),
						new TranslatableText("selectWorld.recreate.error.text")
					)
				);
		}
	}

	private void method_12210() {
		this.field_13363.getSoundManager().play(PositionedSoundInstance.method_12521(Sounds.UI_BUTTON_CLICK, 1.0F));
		if (this.field_13363.getCurrentSave().levelExists(this.field_13365.getFileName())) {
			this.field_13363.startIntegratedServer(this.field_13365.getFileName(), this.field_13365.getDisplayName(), null);
		}
	}

	@Nullable
	private NativeImageBackedTexture method_18893() {
		boolean bl = this.iconFile != null && this.iconFile.isFile();
		if (bl) {
			try {
				InputStream inputStream = new FileInputStream(this.iconFile);
				Throwable var3 = null;

				NativeImageBackedTexture var6;
				try {
					class_4277 lv = class_4277.method_19472(inputStream);
					Validate.validState(lv.method_19458() == 64, "Must be 64 pixels wide", new Object[0]);
					Validate.validState(lv.method_19478() == 64, "Must be 64 pixels high", new Object[0]);
					NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(lv);
					this.field_13363.getTextureManager().loadTexture(this.iconIdentifier, nativeImageBackedTexture);
					var6 = nativeImageBackedTexture;
				} catch (Throwable var16) {
					var3 = var16;
					throw var16;
				} finally {
					if (inputStream != null) {
						if (var3 != null) {
							try {
								inputStream.close();
							} catch (Throwable var15) {
								var3.addSuppressed(var15);
							}
						} else {
							inputStream.close();
						}
					}
				}

				return var6;
			} catch (Throwable var18) {
				LOGGER.error("Invalid icon for world {}", this.field_13365.getFileName(), var18);
				this.iconFile = null;
				return null;
			}
		} else {
			this.field_13363.getTextureManager().close(this.iconIdentifier);
			return null;
		}
	}

	public void close() {
		if (this.icon != null) {
			this.icon.close();
		}
	}

	@Override
	public void method_18401(float f) {
	}
}
