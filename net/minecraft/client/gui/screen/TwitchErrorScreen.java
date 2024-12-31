package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NullTwitchStream;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.TwitchStreamProvider;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import tv.twitch.ErrorCode;

public class TwitchErrorScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Text unavailableText = new TranslatableText("stream.unavailable.title");
	private final Screen parent;
	private final TwitchErrorScreen.ErrorCause errorCause;
	private final List<TranslatableText> errorDetails;
	private final List<String> errorMessages = Lists.newArrayList();

	public TwitchErrorScreen(Screen screen, TwitchErrorScreen.ErrorCause errorCause) {
		this(screen, errorCause, null);
	}

	public TwitchErrorScreen(Screen screen, TwitchErrorScreen.ErrorCause errorCause, List<TranslatableText> list) {
		this.parent = screen;
		this.errorCause = errorCause;
		this.errorDetails = list;
	}

	@Override
	public void init() {
		if (this.errorMessages.isEmpty()) {
			this.errorMessages.addAll(this.textRenderer.wrapLines(this.errorCause.getTitle().asFormattedString(), (int)((float)this.width * 0.75F)));
			if (this.errorDetails != null) {
				this.errorMessages.add("");

				for (TranslatableText translatableText : this.errorDetails) {
					this.errorMessages.add(translatableText.computeValue());
				}
			}
		}

		if (this.errorCause.getUnavailableMessage() != null) {
			this.buttons.add(new ButtonWidget(0, this.width / 2 - 155, this.height - 50, 150, 20, I18n.translate("gui.cancel")));
			this.buttons
				.add(
					new ButtonWidget(1, this.width / 2 - 155 + 160, this.height - 50, 150, 20, I18n.translate(this.errorCause.getUnavailableMessage().asFormattedString()))
				);
		} else {
			this.buttons.add(new ButtonWidget(0, this.width / 2 - 75, this.height - 50, 150, 20, I18n.translate("gui.cancel")));
		}
	}

	@Override
	public void removed() {
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		int i = Math.max((int)((double)this.height * 0.85 / 2.0 - (double)((float)(this.errorMessages.size() * this.textRenderer.fontHeight) / 2.0F)), 50);
		this.drawCenteredString(this.textRenderer, this.unavailableText.asFormattedString(), this.width / 2, i - this.textRenderer.fontHeight * 2, 16777215);

		for (String string : this.errorMessages) {
			this.drawCenteredString(this.textRenderer, string, this.width / 2, i, 10526880);
			i += this.textRenderer.fontHeight;
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 1) {
				switch (this.errorCause) {
					case ACCOUNT_NOT_BOUND:
					case FAILED_TWITCH_AUTH:
						this.setUrl("https://account.mojang.com/me/settings");
						break;
					case ACCOUNT_NOT_MIGRATED:
						this.setUrl("https://account.mojang.com/migrate");
						break;
					case UNSUPPORTED_OS_MAC:
						this.setUrl("http://www.apple.com/osx/");
						break;
					case UNKNOWN:
					case LIBRARY_FAILURE:
					case INITIALIZATION_FAILURE:
						this.setUrl("http://bugs.mojang.com/browse/MC");
				}
			}

			this.client.setScreen(this.parent);
		}
	}

	private void setUrl(String url) {
		try {
			Class<?> class_ = Class.forName("java.awt.Desktop");
			Object object = class_.getMethod("getDesktop").invoke(null);
			class_.getMethod("browse", URI.class).invoke(object, new URI(url));
		} catch (Throwable var4) {
			LOGGER.error("Couldn't open link", var4);
		}
	}

	public static void openNew(Screen parent) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TwitchStreamProvider twitchStreamProvider = minecraftClient.getTwitchStreamProvider();
		if (!GLX.advanced) {
			List<TranslatableText> list = Lists.newArrayList();
			list.add(new TranslatableText("stream.unavailable.no_fbo.version", GL11.glGetString(7938)));
			list.add(new TranslatableText("stream.unavailable.no_fbo.blend", GLContext.getCapabilities().GL_EXT_blend_func_separate));
			list.add(new TranslatableText("stream.unavailable.no_fbo.arb", GLContext.getCapabilities().GL_ARB_framebuffer_object));
			list.add(new TranslatableText("stream.unavailable.no_fbo.ext", GLContext.getCapabilities().GL_EXT_framebuffer_object));
			minecraftClient.setScreen(new TwitchErrorScreen(parent, TwitchErrorScreen.ErrorCause.NO_FBO, list));
		} else if (twitchStreamProvider instanceof NullTwitchStream) {
			if (((NullTwitchStream)twitchStreamProvider).getThrowable().getMessage().contains("Can't load AMD 64-bit .dll on a IA 32-bit platform")) {
				minecraftClient.setScreen(new TwitchErrorScreen(parent, TwitchErrorScreen.ErrorCause.LIBRARY_ARCH_MISMATCH));
			} else {
				minecraftClient.setScreen(new TwitchErrorScreen(parent, TwitchErrorScreen.ErrorCause.LIBRARY_FAILURE));
			}
		} else if (!twitchStreamProvider.isRunning() && twitchStreamProvider.getErrorCode() == ErrorCode.TTV_EC_OS_TOO_OLD) {
			switch (Util.getOperatingSystem()) {
				case WINDOWS:
					minecraftClient.setScreen(new TwitchErrorScreen(parent, TwitchErrorScreen.ErrorCause.UNSUPPORTED_OS_WINDOWS));
					break;
				case MACOS:
					minecraftClient.setScreen(new TwitchErrorScreen(parent, TwitchErrorScreen.ErrorCause.UNSUPPORTED_OS_MAC));
					break;
				default:
					minecraftClient.setScreen(new TwitchErrorScreen(parent, TwitchErrorScreen.ErrorCause.UNSUPPORTED_OS_OTHER));
			}
		} else if (!minecraftClient.getTwitchPropertyMap().containsKey("twitch_access_token")) {
			if (minecraftClient.getSession().getAccountType() == Session.AccountType.LEGACY) {
				minecraftClient.setScreen(new TwitchErrorScreen(parent, TwitchErrorScreen.ErrorCause.ACCOUNT_NOT_MIGRATED));
			} else {
				minecraftClient.setScreen(new TwitchErrorScreen(parent, TwitchErrorScreen.ErrorCause.ACCOUNT_NOT_BOUND));
			}
		} else if (!twitchStreamProvider.isLoggedIn()) {
			switch (twitchStreamProvider.getReason()) {
				case INVALID_TOKEN:
					minecraftClient.setScreen(new TwitchErrorScreen(parent, TwitchErrorScreen.ErrorCause.FAILED_TWITCH_AUTH));
					break;
				case ERROR:
				default:
					minecraftClient.setScreen(new TwitchErrorScreen(parent, TwitchErrorScreen.ErrorCause.FAILED_TWITCH_AUTH_ERROR));
			}
		} else if (twitchStreamProvider.getErrorCode() != null) {
			List<TranslatableText> list2 = Arrays.asList(
				new TranslatableText("stream.unavailable.initialization_failure.extra", ErrorCode.getString(twitchStreamProvider.getErrorCode()))
			);
			minecraftClient.setScreen(new TwitchErrorScreen(parent, TwitchErrorScreen.ErrorCause.INITIALIZATION_FAILURE, list2));
		} else {
			minecraftClient.setScreen(new TwitchErrorScreen(parent, TwitchErrorScreen.ErrorCause.UNKNOWN));
		}
	}

	public static enum ErrorCause {
		NO_FBO(new TranslatableText("stream.unavailable.no_fbo")),
		LIBRARY_ARCH_MISMATCH(new TranslatableText("stream.unavailable.library_arch_mismatch")),
		LIBRARY_FAILURE(new TranslatableText("stream.unavailable.library_failure"), new TranslatableText("stream.unavailable.report_to_mojang")),
		UNSUPPORTED_OS_WINDOWS(new TranslatableText("stream.unavailable.not_supported.windows")),
		UNSUPPORTED_OS_MAC(new TranslatableText("stream.unavailable.not_supported.mac"), new TranslatableText("stream.unavailable.not_supported.mac.okay")),
		UNSUPPORTED_OS_OTHER(new TranslatableText("stream.unavailable.not_supported.other")),
		ACCOUNT_NOT_MIGRATED(new TranslatableText("stream.unavailable.account_not_migrated"), new TranslatableText("stream.unavailable.account_not_migrated.okay")),
		ACCOUNT_NOT_BOUND(new TranslatableText("stream.unavailable.account_not_bound"), new TranslatableText("stream.unavailable.account_not_bound.okay")),
		FAILED_TWITCH_AUTH(new TranslatableText("stream.unavailable.failed_auth"), new TranslatableText("stream.unavailable.failed_auth.okay")),
		FAILED_TWITCH_AUTH_ERROR(new TranslatableText("stream.unavailable.failed_auth_error")),
		INITIALIZATION_FAILURE(new TranslatableText("stream.unavailable.initialization_failure"), new TranslatableText("stream.unavailable.report_to_mojang")),
		UNKNOWN(new TranslatableText("stream.unavailable.unknown"), new TranslatableText("stream.unavailable.report_to_mojang"));

		private final Text title;
		private final Text unavailable;

		private ErrorCause(Text text) {
			this(text, null);
		}

		private ErrorCause(Text text, Text text2) {
			this.title = text;
			this.unavailable = text2;
		}

		public Text getTitle() {
			return this.title;
		}

		public Text getUnavailableMessage() {
			return this.unavailable;
		}
	}
}
