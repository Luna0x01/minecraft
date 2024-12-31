package net.minecraft.client;

import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorageAccess;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2847 implements EntryListWidget.Entry {
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
	private NativeImageBackedTexture icon;
	private long field_13370;

	public class_2847(class_2848 arg, LevelSummary levelSummary, LevelStorageAccess levelStorageAccess) {
		this.field_13367 = arg;
		this.field_13364 = arg.method_12217();
		this.field_13365 = levelSummary;
		this.field_13363 = MinecraftClient.getInstance();
		this.iconIdentifier = new Identifier("worlds/" + levelSummary.getFileName() + "/icon");
		this.iconFile = levelStorageAccess.method_11957(levelSummary.getFileName(), "icon.png");
		if (!this.iconFile.isFile()) {
			this.iconFile = null;
		}

		this.loadIcon();
	}

	@Override
	public void method_6700(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
		String string = this.field_13365.getDisplayName();
		String string2 = this.field_13365.getFileName() + " (" + field_13360.format(new Date(this.field_13365.getLastPlayed())) + ")";
		String string3 = "";
		if (StringUtils.isEmpty(string)) {
			string = I18n.translate("selectWorld.world") + " " + (i + 1);
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

			String string4 = this.field_13365.method_11958();
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

		this.field_13363.textRenderer.draw(string, j + 32 + 3, k + 1, 16777215);
		this.field_13363.textRenderer.draw(string2, j + 32 + 3, k + this.field_13363.textRenderer.fontHeight + 3, 8421504);
		this.field_13363.textRenderer.draw(string3, j + 32 + 3, k + this.field_13363.textRenderer.fontHeight + this.field_13363.textRenderer.fontHeight + 3, 8421504);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_13363.getTextureManager().bindTexture(this.icon != null ? this.iconIdentifier : field_13361);
		GlStateManager.enableBlend();
		DrawableHelper.drawTexture(j, k, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		GlStateManager.disableBlend();
		if (this.field_13363.options.touchscreen || bl) {
			this.field_13363.getTextureManager().bindTexture(field_13362);
			DrawableHelper.fill(j, k, j + 32, k + 32, -1601138544);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int p = n - j;
			int q = p < 32 ? 32 : 0;
			if (this.field_13365.method_11959()) {
				DrawableHelper.drawTexture(j, k, 32.0F, (float)q, 32, 32, 256.0F, 256.0F);
				if (this.field_13365.method_11960()) {
					DrawableHelper.drawTexture(j, k, 96.0F, (float)q, 32, 32, 256.0F, 256.0F);
					if (p < 32) {
						this.field_13364
							.method_12201(
								Formatting.RED
									+ I18n.translate("selectWorld.tooltip.fromNewerVersion1")
									+ "\n"
									+ Formatting.RED
									+ I18n.translate("selectWorld.tooltip.fromNewerVersion2")
							);
					}
				} else {
					DrawableHelper.drawTexture(j, k, 64.0F, (float)q, 32, 32, 256.0F, 256.0F);
					if (p < 32) {
						this.field_13364
							.method_12201(
								Formatting.GOLD + I18n.translate("selectWorld.tooltip.snapshot1") + "\n" + Formatting.GOLD + I18n.translate("selectWorld.tooltip.snapshot2")
							);
					}
				}
			} else {
				DrawableHelper.drawTexture(j, k, 0.0F, (float)q, 32, 32, 256.0F, 256.0F);
			}
		}
	}

	@Override
	public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
		this.field_13367.method_12214(index);
		if (x <= 32 && x < 32) {
			this.method_12202();
			return true;
		} else if (MinecraftClient.getTime() - this.field_13370 < 250L) {
			this.method_12202();
			return true;
		} else {
			this.field_13370 = MinecraftClient.getTime();
			return false;
		}
	}

	public void method_12202() {
		if (this.field_13365.method_11960()) {
			this.field_13363
				.setScreen(
					new ConfirmScreen(
						new IdentifiableBooleanConsumer() {
							@Override
							public void confirmResult(boolean confirmed, int id) {
								if (confirmed) {
									class_2847.this.method_12210();
								} else {
									class_2847.this.field_13363.setScreen(class_2847.this.field_13364);
								}
							}
						},
						I18n.translate("selectWorld.versionQuestion"),
						I18n.translate("selectWorld.versionWarning", this.field_13365.method_11958()),
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
					new IdentifiableBooleanConsumer() {
						@Override
						public void confirmResult(boolean confirmed, int id) {
							if (confirmed) {
								class_2847.this.field_13363.setScreen(new ProgressScreen());
								LevelStorageAccess levelStorageAccess = class_2847.this.field_13363.getCurrentSave();
								levelStorageAccess.clearAll();
								levelStorageAccess.deleteLevel(class_2847.this.field_13365.getFileName());
								class_2847.this.field_13367.method_12215();
							}

							class_2847.this.field_13363.setScreen(class_2847.this.field_13364);
						}
					},
					I18n.translate("selectWorld.deleteQuestion"),
					"'" + this.field_13365.getDisplayName() + "' " + I18n.translate("selectWorld.deleteWarning"),
					I18n.translate("selectWorld.deleteButton"),
					I18n.translate("gui.cancel"),
					0
				)
			);
	}

	public void method_12206() {
		this.field_13363.setScreen(new EditWorldScreen(this.field_13364, this.field_13365.getFileName()));
	}

	public void method_12208() {
		this.field_13363.setScreen(new ProgressScreen());
		CreateWorldScreen createWorldScreen = new CreateWorldScreen(this.field_13364);
		SaveHandler saveHandler = this.field_13363.getCurrentSave().createSaveHandler(this.field_13365.getFileName(), false);
		LevelProperties levelProperties = saveHandler.getLevelProperties();
		saveHandler.clear();
		if (levelProperties != null) {
			createWorldScreen.copyWorld(levelProperties);
			this.field_13363.setScreen(createWorldScreen);
		}
	}

	private void method_12210() {
		this.field_13363.getSoundManager().play(PositionedSoundInstance.method_12521(Sounds.UI_BUTTON_CLICK, 1.0F));
		if (this.field_13363.getCurrentSave().levelExists(this.field_13365.getFileName())) {
			this.field_13363.startIntegratedServer(this.field_13365.getFileName(), this.field_13365.getDisplayName(), null);
		}
	}

	private void loadIcon() {
		boolean bl = this.iconFile != null && this.iconFile.isFile();
		if (bl) {
			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageIO.read(this.iconFile);
				Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
				Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
			} catch (Throwable var4) {
				LOGGER.error("Invalid icon for world {}", this.field_13365.getFileName(), var4);
				this.iconFile = null;
				return;
			}

			if (this.icon == null) {
				this.icon = new NativeImageBackedTexture(bufferedImage.getWidth(), bufferedImage.getHeight());
				this.field_13363.getTextureManager().loadTexture(this.iconIdentifier, this.icon);
			}

			bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.icon.getPixels(), 0, bufferedImage.getWidth());
			this.icon.upload();
		} else if (!bl) {
			this.field_13363.getTextureManager().close(this.iconIdentifier);
			this.icon = null;
		}
	}

	@Override
	public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
	}

	@Override
	public void method_9473(int i, int j, int k, float f) {
	}
}
