package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import net.minecraft.class_4216;
import net.minecraft.class_4227;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LanguageButton;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.resource.Resource;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DemoServerWorld;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorageAccess;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL;

public class TitleScreen extends Screen {
	private static final Random RANDOM = new Random();
	private final float minecraftRandomNumber;
	private String splashText;
	private ButtonWidget field_20321;
	private ButtonWidget resetDemoButton;
	private final Object mutex = new Object();
	public static final String MORE_INFO_MESSAGE = "Please click " + Formatting.UNDERLINE + "here" + Formatting.RESET + " for more information.";
	private int oldGl2Width;
	private int oldGl1Width;
	private int oldGlLeft;
	private int oldGlTop;
	private int oldGlRight;
	private int oldGlBottom;
	private String oldGl1;
	private String oldGl2 = MORE_INFO_MESSAGE;
	private String oldGlLink;
	private static final Identifier SPLASHES = new Identifier("texts/splashes.txt");
	private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
	private static final Identifier MINECRAFT_EDITION_TEXTURE = new Identifier("textures/gui/title/edition.png");
	private boolean realmsNotificationsInitialized;
	private Screen realmsNotificationScreen;
	private int copyrightTextWidth;
	private int field_15948;
	private final class_4227 field_20320 = new class_4227(new class_4216(new Identifier("textures/gui/title/background/panorama")));

	public TitleScreen() {
		this.splashText = "missingno";
		Resource resource = null;

		try {
			List<String> list = Lists.newArrayList();
			resource = MinecraftClient.getInstance().getResourceManager().getResource(SPLASHES);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

			String string;
			while ((string = bufferedReader.readLine()) != null) {
				string = string.trim();
				if (!string.isEmpty()) {
					list.add(string);
				}
			}

			if (!list.isEmpty()) {
				do {
					this.splashText = (String)list.get(RANDOM.nextInt(list.size()));
				} while (this.splashText.hashCode() == 125780783);
			}
		} catch (IOException var8) {
		} finally {
			IOUtils.closeQuietly(resource);
		}

		this.minecraftRandomNumber = RANDOM.nextFloat();
		this.oldGl1 = "";
		if (!GL.getCapabilities().OpenGL20 && !GLX.areShadersSupported()) {
			this.oldGl1 = I18n.translate("title.oldgl1");
			this.oldGl2 = I18n.translate("title.oldgl2");
			this.oldGlLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
		}
	}

	private boolean areRealmsNotificationsEnabled() {
		return MinecraftClient.getInstance().options.getIntVideoOptions(GameOptions.Option.REALMS_NOTIFICATIONS) && this.realmsNotificationScreen != null;
	}

	@Override
	public void tick() {
		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationScreen.tick();
		}
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}

	@Override
	public boolean method_18607() {
		return false;
	}

	@Override
	protected void init() {
		this.copyrightTextWidth = this.textRenderer.getStringWidth("Copyright Mojang AB. Do not distribute!");
		this.field_15948 = this.width - this.copyrightTextWidth - 2;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			this.splashText = "Merry X-mas!";
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			this.splashText = "Happy new year!";
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			this.splashText = "OOoooOOOoooo! Spooky!";
		}

		int i = 24;
		int j = this.height / 4 + 48;
		if (this.client.isDemo()) {
			this.initWidgetsDemo(j, 24);
		} else {
			this.initWidgetsNormal(j, 24);
		}

		this.field_20321 = this.addButton(new ButtonWidget(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.translate("menu.options")) {
			@Override
			public void method_18374(double d, double e) {
				TitleScreen.this.client.setScreen(new SettingsScreen(TitleScreen.this, TitleScreen.this.client.options));
			}
		});
		this.addButton(new ButtonWidget(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.translate("menu.quit")) {
			@Override
			public void method_18374(double d, double e) {
				TitleScreen.this.client.scheduleStop();
			}
		});
		this.addButton(
			new LanguageButton(5, this.width / 2 - 124, j + 72 + 12) {
				@Override
				public void method_18374(double d, double e) {
					TitleScreen.this.client
						.setScreen(new LanguageOptionsScreen(TitleScreen.this, TitleScreen.this.client.options, TitleScreen.this.client.getLanguageManager()));
				}
			}
		);
		synchronized (this.mutex) {
			this.oldGl1Width = this.textRenderer.getStringWidth(this.oldGl1);
			this.oldGl2Width = this.textRenderer.getStringWidth(this.oldGl2);
			int k = Math.max(this.oldGl1Width, this.oldGl2Width);
			this.oldGlLeft = (this.width - k) / 2;
			this.oldGlTop = j - 24;
			this.oldGlRight = this.oldGlLeft + k;
			this.oldGlBottom = this.oldGlTop + 24;
		}

		this.client.setConnectedToRealms(false);
		if (MinecraftClient.getInstance().options.getIntVideoOptions(GameOptions.Option.REALMS_NOTIFICATIONS) && !this.realmsNotificationsInitialized) {
			RealmsBridge realmsBridge = new RealmsBridge();
			this.realmsNotificationScreen = realmsBridge.getNotificationScreen(this);
			this.realmsNotificationsInitialized = true;
		}

		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationScreen.init(this.client, this.width, this.height);
		}
	}

	private void initWidgetsNormal(int y, int spacingY) {
		this.addButton(new ButtonWidget(1, this.width / 2 - 100, y, I18n.translate("menu.singleplayer")) {
			@Override
			public void method_18374(double d, double e) {
				TitleScreen.this.client.setScreen(new SelectWorldScreen(TitleScreen.this));
			}
		});
		this.addButton(new ButtonWidget(2, this.width / 2 - 100, y + spacingY * 1, I18n.translate("menu.multiplayer")) {
			@Override
			public void method_18374(double d, double e) {
				TitleScreen.this.client.setScreen(new MultiplayerScreen(TitleScreen.this));
			}
		});
		this.addButton(new ButtonWidget(14, this.width / 2 - 100, y + spacingY * 2, I18n.translate("menu.online")) {
			@Override
			public void method_18374(double d, double e) {
				TitleScreen.this.switchToRealms();
			}
		});
	}

	private void initWidgetsDemo(int y, int spacingY) {
		this.addButton(new ButtonWidget(11, this.width / 2 - 100, y, I18n.translate("menu.playdemo")) {
			@Override
			public void method_18374(double d, double e) {
				TitleScreen.this.client.startIntegratedServer("Demo_World", "Demo_World", DemoServerWorld.INFO);
			}
		});
		this.resetDemoButton = this.addButton(
			new ButtonWidget(12, this.width / 2 - 100, y + spacingY * 1, I18n.translate("menu.resetdemo")) {
				@Override
				public void method_18374(double d, double e) {
					LevelStorageAccess levelStorageAccess = TitleScreen.this.client.getCurrentSave();
					LevelProperties levelProperties = levelStorageAccess.getLevelProperties("Demo_World");
					if (levelProperties != null) {
						TitleScreen.this.client
							.setScreen(
								new ConfirmScreen(
									TitleScreen.this,
									I18n.translate("selectWorld.deleteQuestion"),
									I18n.translate("selectWorld.deleteWarning", levelProperties.getLevelName()),
									I18n.translate("selectWorld.deleteButton"),
									I18n.translate("gui.cancel"),
									12
								)
							);
					}
				}
			}
		);
		LevelStorageAccess levelStorageAccess = this.client.getCurrentSave();
		LevelProperties levelProperties = levelStorageAccess.getLevelProperties("Demo_World");
		if (levelProperties == null) {
			this.resetDemoButton.active = false;
		}
	}

	private void switchToRealms() {
		RealmsBridge realmsBridge = new RealmsBridge();
		realmsBridge.switchToRealms(this);
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (bl && i == 12) {
			LevelStorageAccess levelStorageAccess = this.client.getCurrentSave();
			levelStorageAccess.clearAll();
			levelStorageAccess.deleteLevel("Demo_World");
			this.client.setScreen(this);
		} else if (i == 12) {
			this.client.setScreen(this);
		} else if (i == 13) {
			if (bl) {
				Util.getOperatingSystem().method_20236(this.oldGlLink);
			}

			this.client.setScreen(this);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.field_20320.method_19176(tickDelta);
		int i = 274;
		int j = this.width / 2 - 137;
		int k = 30;
		this.client.getTextureManager().bindTexture(new Identifier("textures/gui/title/background/panorama_overlay.png"));
		drawTexture(0, 0, 0.0F, 0.0F, 16, 128, this.width, this.height, 16.0F, 128.0F);
		this.client.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if ((double)this.minecraftRandomNumber < 1.0E-4) {
			this.drawTexture(j + 0, 30, 0, 0, 99, 44);
			this.drawTexture(j + 99, 30, 129, 0, 27, 44);
			this.drawTexture(j + 99 + 26, 30, 126, 0, 3, 44);
			this.drawTexture(j + 99 + 26 + 3, 30, 99, 0, 26, 44);
			this.drawTexture(j + 155, 30, 0, 45, 155, 44);
		} else {
			this.drawTexture(j + 0, 30, 0, 0, 155, 44);
			this.drawTexture(j + 155, 30, 0, 45, 155, 44);
		}

		this.client.getTextureManager().bindTexture(MINECRAFT_EDITION_TEXTURE);
		drawTexture(j + 88, 67, 0.0F, 0.0F, 98, 14, 128.0F, 16.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)(this.width / 2 + 90), 70.0F, 0.0F);
		GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
		float f = 1.8F - MathHelper.abs(MathHelper.sin((float)(Util.method_20227() % 1000L) / 1000.0F * (float) (Math.PI * 2)) * 0.1F);
		f = f * 100.0F / (float)(this.textRenderer.getStringWidth(this.splashText) + 32);
		GlStateManager.scale(f, f, f);
		this.drawCenteredString(this.textRenderer, this.splashText, 0, -8, -256);
		GlStateManager.popMatrix();
		String string = "Minecraft 1.13.2";
		if (this.client.isDemo()) {
			string = string + " Demo";
		} else {
			string = string + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
		}

		this.drawWithShadow(this.textRenderer, string, 2, this.height - 10, -1);
		this.drawWithShadow(this.textRenderer, "Copyright Mojang AB. Do not distribute!", this.field_15948, this.height - 10, -1);
		if (mouseX > this.field_15948 && mouseX < this.field_15948 + this.copyrightTextWidth && mouseY > this.height - 10 && mouseY < this.height) {
			fill(this.field_15948, this.height - 1, this.field_15948 + this.copyrightTextWidth, this.height, -1);
		}

		if (this.oldGl1 != null && !this.oldGl1.isEmpty()) {
			fill(this.oldGlLeft - 2, this.oldGlTop - 2, this.oldGlRight + 2, this.oldGlBottom - 1, 1428160512);
			this.drawWithShadow(this.textRenderer, this.oldGl1, this.oldGlLeft, this.oldGlTop, -1);
			this.drawWithShadow(this.textRenderer, this.oldGl2, (this.width - this.oldGl2Width) / 2, this.oldGlTop + 12, -1);
		}

		super.render(mouseX, mouseY, tickDelta);
		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationScreen.render(mouseX, mouseY, tickDelta);
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (super.mouseClicked(d, e, i)) {
			return true;
		} else {
			synchronized (this.mutex) {
				if (!this.oldGl1.isEmpty()
					&& !ChatUtil.isEmpty(this.oldGlLink)
					&& d >= (double)this.oldGlLeft
					&& d <= (double)this.oldGlRight
					&& e >= (double)this.oldGlTop
					&& e <= (double)this.oldGlBottom) {
					ConfirmChatLinkScreen confirmChatLinkScreen = new ConfirmChatLinkScreen(this, this.oldGlLink, 13, true);
					confirmChatLinkScreen.disableWarning();
					this.client.setScreen(confirmChatLinkScreen);
					return true;
				}
			}

			if (this.areRealmsNotificationsEnabled() && this.realmsNotificationScreen.mouseClicked(d, e, i)) {
				return true;
			} else {
				if (d > (double)this.field_15948 && d < (double)(this.field_15948 + this.copyrightTextWidth) && e > (double)(this.height - 10) && e < (double)this.height) {
					this.client.setScreen(new CreditsScreen(false, Runnables.doNothing()));
				}

				return false;
			}
		}
	}

	@Override
	public void removed() {
		if (this.realmsNotificationScreen != null) {
			this.realmsNotificationScreen.removed();
		}
	}
}
