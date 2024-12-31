package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LanguageButton;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.resource.Resource;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DemoServerWorld;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorageAccess;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class TitleScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Random RANDOM = new Random();
	private final float minecraftRandomNumber;
	private String splashText;
	private ButtonWidget resetDemoButton;
	private float field_15949;
	private NativeImageBackedTexture backgroundTexture;
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
	private static final Identifier[] PANORAMA_CUBE_FACES = new Identifier[]{
		new Identifier("textures/gui/title/background/panorama_0.png"),
		new Identifier("textures/gui/title/background/panorama_1.png"),
		new Identifier("textures/gui/title/background/panorama_2.png"),
		new Identifier("textures/gui/title/background/panorama_3.png"),
		new Identifier("textures/gui/title/background/panorama_4.png"),
		new Identifier("textures/gui/title/background/panorama_5.png")
	};
	private Identifier backgroundTextureId;
	private ButtonWidget realmsButton;
	private boolean realmsNotificationsInitialized;
	private Screen realmsNotificationScreen;
	private int copyrightTextWidth;
	private int field_15948;

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
		if (!GLContext.getCapabilities().OpenGL20 && !GLX.areShadersSupported()) {
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
	protected void keyPressed(char id, int code) {
	}

	@Override
	public void init() {
		this.backgroundTexture = new NativeImageBackedTexture(256, 256);
		this.backgroundTextureId = this.client.getTextureManager().registerDynamicTexture("background", this.backgroundTexture);
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

		this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.translate("menu.options")));
		this.buttons.add(new ButtonWidget(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.translate("menu.quit")));
		this.buttons.add(new LanguageButton(5, this.width / 2 - 124, j + 72 + 12));
		synchronized (this.mutex) {
			this.oldGl1Width = this.textRenderer.getStringWidth(this.oldGl1);
			this.oldGl2Width = this.textRenderer.getStringWidth(this.oldGl2);
			int k = Math.max(this.oldGl1Width, this.oldGl2Width);
			this.oldGlLeft = (this.width - k) / 2;
			this.oldGlTop = ((ButtonWidget)this.buttons.get(0)).y - 24;
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
			this.realmsNotificationScreen.setScreenBounds(this.width, this.height);
			this.realmsNotificationScreen.init();
		}
	}

	private void initWidgetsNormal(int y, int spacingY) {
		this.buttons.add(new ButtonWidget(1, this.width / 2 - 100, y, I18n.translate("menu.singleplayer")));
		this.buttons.add(new ButtonWidget(2, this.width / 2 - 100, y + spacingY * 1, I18n.translate("menu.multiplayer")));
		this.realmsButton = this.addButton(new ButtonWidget(14, this.width / 2 - 100, y + spacingY * 2, I18n.translate("menu.online")));
	}

	private void initWidgetsDemo(int y, int spacingY) {
		this.buttons.add(new ButtonWidget(11, this.width / 2 - 100, y, I18n.translate("menu.playdemo")));
		this.resetDemoButton = this.addButton(new ButtonWidget(12, this.width / 2 - 100, y + spacingY * 1, I18n.translate("menu.resetdemo")));
		LevelStorageAccess levelStorageAccess = this.client.getCurrentSave();
		LevelProperties levelProperties = levelStorageAccess.getLevelProperties("Demo_World");
		if (levelProperties == null) {
			this.resetDemoButton.active = false;
		}
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == 0) {
			this.client.setScreen(new SettingsScreen(this, this.client.options));
		}

		if (button.id == 5) {
			this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()));
		}

		if (button.id == 1) {
			this.client.setScreen(new SelectWorldScreen(this));
		}

		if (button.id == 2) {
			this.client.setScreen(new MultiplayerScreen(this));
		}

		if (button.id == 14 && this.realmsButton.visible) {
			this.switchToRealms();
		}

		if (button.id == 4) {
			this.client.scheduleStop();
		}

		if (button.id == 11) {
			this.client.startIntegratedServer("Demo_World", "Demo_World", DemoServerWorld.INFO);
		}

		if (button.id == 12) {
			LevelStorageAccess levelStorageAccess = this.client.getCurrentSave();
			LevelProperties levelProperties = levelStorageAccess.getLevelProperties("Demo_World");
			if (levelProperties != null) {
				this.client
					.setScreen(
						new ConfirmScreen(
							this,
							I18n.translate("selectWorld.deleteQuestion"),
							"'" + levelProperties.getLevelName() + "' " + I18n.translate("selectWorld.deleteWarning"),
							I18n.translate("selectWorld.deleteButton"),
							I18n.translate("gui.cancel"),
							12
						)
					);
			}
		}
	}

	private void switchToRealms() {
		RealmsBridge realmsBridge = new RealmsBridge();
		realmsBridge.switchToRealms(this);
	}

	@Override
	public void confirmResult(boolean confirmed, int id) {
		if (confirmed && id == 12) {
			LevelStorageAccess levelStorageAccess = this.client.getCurrentSave();
			levelStorageAccess.clearAll();
			levelStorageAccess.deleteLevel("Demo_World");
			this.client.setScreen(this);
		} else if (id == 12) {
			this.client.setScreen(this);
		} else if (id == 13) {
			if (confirmed) {
				try {
					Class<?> class_ = Class.forName("java.awt.Desktop");
					Object object = class_.getMethod("getDesktop").invoke(null);
					class_.getMethod("browse", URI.class).invoke(object, new URI(this.oldGlLink));
				} catch (Throwable var5) {
					LOGGER.error("Couldn't open link", var5);
				}
			}

			this.client.setScreen(this);
		}
	}

	private void renderPanorama(int mouseX, int mouseY, float tickDelta) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		int i = 8;

		for (int j = 0; j < 64; j++) {
			GlStateManager.pushMatrix();
			float f = ((float)(j % 8) / 8.0F - 0.5F) / 64.0F;
			float g = ((float)(j / 8) / 8.0F - 0.5F) / 64.0F;
			float h = 0.0F;
			GlStateManager.translate(f, g, 0.0F);
			GlStateManager.rotate(MathHelper.sin(this.field_15949 / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-this.field_15949 * 0.1F, 0.0F, 1.0F, 0.0F);

			for (int k = 0; k < 6; k++) {
				GlStateManager.pushMatrix();
				if (k == 1) {
					GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (k == 2) {
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				if (k == 3) {
					GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (k == 4) {
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (k == 5) {
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				this.client.getTextureManager().bindTexture(PANORAMA_CUBE_FACES[k]);
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				int l = 255 / (j + 1);
				float m = 0.0F;
				bufferBuilder.vertex(-1.0, -1.0, 1.0).texture(0.0, 0.0).color(255, 255, 255, l).next();
				bufferBuilder.vertex(1.0, -1.0, 1.0).texture(1.0, 0.0).color(255, 255, 255, l).next();
				bufferBuilder.vertex(1.0, 1.0, 1.0).texture(1.0, 1.0).color(255, 255, 255, l).next();
				bufferBuilder.vertex(-1.0, 1.0, 1.0).texture(0.0, 1.0).color(255, 255, 255, l).next();
				tessellator.draw();
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
			GlStateManager.colorMask(true, true, true, false);
		}

		bufferBuilder.offset(0.0, 0.0, 0.0);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.matrixMode(5889);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableDepthTest();
	}

	private void method_14505() {
		this.client.getTextureManager().bindTexture(this.backgroundTextureId);
		GlStateManager.method_12294(3553, 10241, 9729);
		GlStateManager.method_12294(3553, 10240, 9729);
		GlStateManager.method_12275(3553, 0, 0, 0, 0, 0, 256, 256);
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.colorMask(true, true, true, false);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		GlStateManager.disableAlphaTest();
		int i = 3;

		for (int j = 0; j < 3; j++) {
			float f = 1.0F / (float)(j + 1);
			int k = this.width;
			int l = this.height;
			float g = (float)(j - 1) / 256.0F;
			bufferBuilder.vertex((double)k, (double)l, (double)this.zOffset).texture((double)(0.0F + g), 1.0).color(1.0F, 1.0F, 1.0F, f).next();
			bufferBuilder.vertex((double)k, 0.0, (double)this.zOffset).texture((double)(1.0F + g), 1.0).color(1.0F, 1.0F, 1.0F, f).next();
			bufferBuilder.vertex(0.0, 0.0, (double)this.zOffset).texture((double)(1.0F + g), 0.0).color(1.0F, 1.0F, 1.0F, f).next();
			bufferBuilder.vertex(0.0, (double)l, (double)this.zOffset).texture((double)(0.0F + g), 0.0).color(1.0F, 1.0F, 1.0F, f).next();
		}

		tessellator.draw();
		GlStateManager.enableAlphaTest();
		GlStateManager.colorMask(true, true, true, true);
	}

	private void renderBackground(int mouseX, int mouseY, float tickDelta) {
		this.client.getFramebuffer().unbind();
		GlStateManager.viewport(0, 0, 256, 256);
		this.renderPanorama(mouseX, mouseY, tickDelta);
		this.method_14505();
		this.method_14505();
		this.method_14505();
		this.method_14505();
		this.method_14505();
		this.method_14505();
		this.method_14505();
		this.client.getFramebuffer().bind(true);
		GlStateManager.viewport(0, 0, this.client.width, this.client.height);
		float f = 120.0F / (float)(this.width > this.height ? this.width : this.height);
		float g = (float)this.height * f / 256.0F;
		float h = (float)this.width * f / 256.0F;
		int i = this.width;
		int j = this.height;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)j, (double)this.zOffset).texture((double)(0.5F - g), (double)(0.5F + h)).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex((double)i, (double)j, (double)this.zOffset).texture((double)(0.5F - g), (double)(0.5F - h)).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex((double)i, 0.0, (double)this.zOffset).texture((double)(0.5F + g), (double)(0.5F - h)).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex(0.0, 0.0, (double)this.zOffset).texture((double)(0.5F + g), (double)(0.5F + h)).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		tessellator.draw();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.field_15949 += tickDelta;
		GlStateManager.disableAlphaTest();
		this.renderBackground(mouseX, mouseY, tickDelta);
		GlStateManager.enableAlphaTest();
		int i = 274;
		int j = this.width / 2 - 137;
		int k = 30;
		this.fillGradient(0, 0, this.width, this.height, -2130706433, 16777215);
		this.fillGradient(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
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
		float f = 1.8F - MathHelper.abs(MathHelper.sin((float)(MinecraftClient.getTime() % 1000L) / 1000.0F * (float) (Math.PI * 2)) * 0.1F);
		f = f * 100.0F / (float)(this.textRenderer.getStringWidth(this.splashText) + 32);
		GlStateManager.scale(f, f, f);
		this.drawCenteredString(this.textRenderer, this.splashText, 0, -8, -256);
		GlStateManager.popMatrix();
		String string = "Minecraft 1.12.2";
		if (this.client.isDemo()) {
			string = string + " Demo";
		} else {
			string = string + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
		}

		this.drawWithShadow(this.textRenderer, string, 2, this.height - 10, -1);
		this.drawWithShadow(this.textRenderer, "Copyright Mojang AB. Do not distribute!", this.field_15948, this.height - 10, -1);
		if (mouseX > this.field_15948
			&& mouseX < this.field_15948 + this.copyrightTextWidth
			&& mouseY > this.height - 10
			&& mouseY < this.height
			&& Mouse.isInsideWindow()) {
			fill(this.field_15948, this.height - 1, this.field_15948 + this.copyrightTextWidth, this.height, -1);
		}

		if (this.oldGl1 != null && !this.oldGl1.isEmpty()) {
			fill(this.oldGlLeft - 2, this.oldGlTop - 2, this.oldGlRight + 2, this.oldGlBottom - 1, 1428160512);
			this.drawWithShadow(this.textRenderer, this.oldGl1, this.oldGlLeft, this.oldGlTop, -1);
			this.drawWithShadow(this.textRenderer, this.oldGl2, (this.width - this.oldGl2Width) / 2, ((ButtonWidget)this.buttons.get(0)).y - 12, -1);
		}

		super.render(mouseX, mouseY, tickDelta);
		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationScreen.render(mouseX, mouseY, tickDelta);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		synchronized (this.mutex) {
			if (!this.oldGl1.isEmpty()
				&& !ChatUtil.isEmpty(this.oldGlLink)
				&& mouseX >= this.oldGlLeft
				&& mouseX <= this.oldGlRight
				&& mouseY >= this.oldGlTop
				&& mouseY <= this.oldGlBottom) {
				ConfirmChatLinkScreen confirmChatLinkScreen = new ConfirmChatLinkScreen(this, this.oldGlLink, 13, true);
				confirmChatLinkScreen.disableWarning();
				this.client.setScreen(confirmChatLinkScreen);
			}
		}

		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationScreen.mouseClicked(mouseX, mouseY, button);
		}

		if (mouseX > this.field_15948 && mouseX < this.field_15948 + this.copyrightTextWidth && mouseY > this.height - 10 && mouseY < this.height) {
			this.client.setScreen(new CreditsScreen(false, Runnables.doNothing()));
		}
	}

	@Override
	public void removed() {
		if (this.realmsNotificationScreen != null) {
			this.realmsNotificationScreen.removed();
		}
	}
}
