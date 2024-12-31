package net.minecraft.client.gui.widget;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerEntry implements EntryListWidget.Entry {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ThreadPoolExecutor SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(
		5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build()
	);
	private static final Identifier UNKNOWN_TEXTURE = new Identifier("textures/misc/unknown_server.png");
	private static final Identifier SORT_BUTTONS_TEXTURE = new Identifier("textures/gui/server_selection.png");
	private final MultiplayerScreen parent;
	private final MinecraftClient client;
	private final ServerInfo serverInfo;
	private final Identifier iconTextureId;
	private String iconUri;
	private NativeImageBackedTexture icon;
	private long time;

	protected ServerEntry(MultiplayerScreen multiplayerScreen, ServerInfo serverInfo) {
		this.parent = multiplayerScreen;
		this.serverInfo = serverInfo;
		this.client = MinecraftClient.getInstance();
		this.iconTextureId = new Identifier("servers/" + serverInfo.address + "/icon");
		this.icon = (NativeImageBackedTexture)this.client.getTextureManager().getTexture(this.iconTextureId);
	}

	@Override
	public void method_6700(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
		if (!this.serverInfo.online) {
			this.serverInfo.online = true;
			this.serverInfo.ping = -2L;
			this.serverInfo.label = "";
			this.serverInfo.playerCountLabel = "";
			SERVER_PINGER_THREAD_POOL.submit(new Runnable() {
				public void run() {
					try {
						ServerEntry.this.parent.getServerListPinger().add(ServerEntry.this.serverInfo);
					} catch (UnknownHostException var2) {
						ServerEntry.this.serverInfo.ping = -1L;
						ServerEntry.this.serverInfo.label = Formatting.DARK_RED + I18n.translate("multiplayer.status.cannot_resolve");
					} catch (Exception var3) {
						ServerEntry.this.serverInfo.ping = -1L;
						ServerEntry.this.serverInfo.label = Formatting.DARK_RED + I18n.translate("multiplayer.status.cannot_connect");
					}
				}
			});
		}

		boolean bl2 = this.serverInfo.protocolVersion > 340;
		boolean bl3 = this.serverInfo.protocolVersion < 340;
		boolean bl4 = bl2 || bl3;
		this.client.textRenderer.draw(this.serverInfo.name, j + 32 + 3, k + 1, 16777215);
		List<String> list = this.client.textRenderer.wrapLines(this.serverInfo.label, l - 32 - 2);

		for (int p = 0; p < Math.min(list.size(), 2); p++) {
			this.client.textRenderer.draw((String)list.get(p), j + 32 + 3, k + 12 + this.client.textRenderer.fontHeight * p, 8421504);
		}

		String string = bl4 ? Formatting.DARK_RED + this.serverInfo.version : this.serverInfo.playerCountLabel;
		int q = this.client.textRenderer.getStringWidth(string);
		this.client.textRenderer.draw(string, j + l - q - 15 - 2, k + 1, 8421504);
		int r = 0;
		String string2 = null;
		int s;
		String string3;
		if (bl4) {
			s = 5;
			string3 = I18n.translate(bl2 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
			string2 = this.serverInfo.playerListSummary;
		} else if (this.serverInfo.online && this.serverInfo.ping != -2L) {
			if (this.serverInfo.ping < 0L) {
				s = 5;
			} else if (this.serverInfo.ping < 150L) {
				s = 0;
			} else if (this.serverInfo.ping < 300L) {
				s = 1;
			} else if (this.serverInfo.ping < 600L) {
				s = 2;
			} else if (this.serverInfo.ping < 1000L) {
				s = 3;
			} else {
				s = 4;
			}

			if (this.serverInfo.ping < 0L) {
				string3 = I18n.translate("multiplayer.status.no_connection");
			} else {
				string3 = this.serverInfo.ping + "ms";
				string2 = this.serverInfo.playerListSummary;
			}
		} else {
			r = 1;
			s = (int)(MinecraftClient.getTime() / 100L + (long)(i * 2) & 7L);
			if (s > 4) {
				s = 8 - s;
			}

			string3 = I18n.translate("multiplayer.status.pinging");
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
		DrawableHelper.drawTexture(j + l - 15, k, (float)(r * 10), (float)(176 + s * 8), 10, 8, 256.0F, 256.0F);
		if (this.serverInfo.getIcon() != null && !this.serverInfo.getIcon().equals(this.iconUri)) {
			this.iconUri = this.serverInfo.getIcon();
			this.checkServerIcon();
			this.parent.getServerList().saveFile();
		}

		if (this.icon != null) {
			this.renderIcon(j, k, this.iconTextureId);
		} else {
			this.renderIcon(j, k, UNKNOWN_TEXTURE);
		}

		int aa = n - j;
		int ab = o - k;
		if (aa >= l - 15 && aa <= l - 5 && ab >= 0 && ab <= 8) {
			this.parent.setTooltip(string3);
		} else if (aa >= l - q - 15 - 2 && aa <= l - 15 - 2 && ab >= 0 && ab <= 8) {
			this.parent.setTooltip(string2);
		}

		if (this.client.options.touchscreen || bl) {
			this.client.getTextureManager().bindTexture(SORT_BUTTONS_TEXTURE);
			DrawableHelper.fill(j, k, j + 32, k + 32, -1601138544);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int ac = n - j;
			int ad = o - k;
			if (this.isVisible()) {
				if (ac < 32 && ac > 16) {
					DrawableHelper.drawTexture(j, k, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					DrawableHelper.drawTexture(j, k, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}

			if (this.parent.canSortUp(this, i)) {
				if (ac < 16 && ad < 16) {
					DrawableHelper.drawTexture(j, k, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					DrawableHelper.drawTexture(j, k, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}

			if (this.parent.canSortDown(this, i)) {
				if (ac < 16 && ad > 16) {
					DrawableHelper.drawTexture(j, k, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					DrawableHelper.drawTexture(j, k, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}
		}
	}

	protected void renderIcon(int x, int y, Identifier textureId) {
		this.client.getTextureManager().bindTexture(textureId);
		GlStateManager.enableBlend();
		DrawableHelper.drawTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		GlStateManager.disableBlend();
	}

	private boolean isVisible() {
		return true;
	}

	private void checkServerIcon() {
		if (this.serverInfo.getIcon() == null) {
			this.client.getTextureManager().close(this.iconTextureId);
			this.icon = null;
		} else {
			ByteBuf byteBuf = Unpooled.copiedBuffer(this.serverInfo.getIcon(), StandardCharsets.UTF_8);
			ByteBuf byteBuf2 = null;

			BufferedImage bufferedImage;
			label80: {
				try {
					byteBuf2 = Base64.decode(byteBuf);
					bufferedImage = TextureUtil.create(new ByteBufInputStream(byteBuf2));
					Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
					Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
					break label80;
				} catch (Throwable var8) {
					LOGGER.error("Invalid icon for server {} ({})", this.serverInfo.name, this.serverInfo.address, var8);
					this.serverInfo.setIcon(null);
				} finally {
					byteBuf.release();
					if (byteBuf2 != null) {
						byteBuf2.release();
					}
				}

				return;
			}

			if (this.icon == null) {
				this.icon = new NativeImageBackedTexture(bufferedImage.getWidth(), bufferedImage.getHeight());
				this.client.getTextureManager().loadTexture(this.iconTextureId, this.icon);
			}

			bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.icon.getPixels(), 0, bufferedImage.getWidth());
			this.icon.upload();
		}
	}

	@Override
	public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
		if (x <= 32) {
			if (x < 32 && x > 16 && this.isVisible()) {
				this.parent.selectEntry(index);
				this.parent.connect();
				return true;
			}

			if (x < 16 && y < 16 && this.parent.canSortUp(this, index)) {
				this.parent.sortUp(this, index, Screen.hasShiftDown());
				return true;
			}

			if (x < 16 && y > 16 && this.parent.canSortDown(this, index)) {
				this.parent.sortDown(this, index, Screen.hasShiftDown());
				return true;
			}
		}

		this.parent.selectEntry(index);
		if (MinecraftClient.getTime() - this.time < 250L) {
			this.parent.connect();
		}

		this.time = MinecraftClient.getTime();
		return false;
	}

	@Override
	public void method_9473(int i, int j, int k, float f) {
	}

	@Override
	public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
	}

	public ServerInfo getServer() {
		return this.serverInfo;
	}
}
