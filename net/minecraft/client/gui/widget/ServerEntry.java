package net.minecraft.client.gui.widget;

import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.class_4277;
import net.minecraft.class_4325;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;

public class ServerEntry extends MultiplayerServerListWidget.class_4169 {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ThreadPoolExecutor SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(
		5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new class_4325(LOGGER)).build()
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
		this.iconTextureId = new Identifier("servers/" + Hashing.sha1().hashUnencodedChars(serverInfo.address) + "/icon");
		this.icon = (NativeImageBackedTexture)this.client.getTextureManager().getTexture(this.iconTextureId);
	}

	@Override
	public void method_6700(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.method_18403();
		int n = this.method_18404();
		if (!this.serverInfo.online) {
			this.serverInfo.online = true;
			this.serverInfo.ping = -2L;
			this.serverInfo.label = "";
			this.serverInfo.playerCountLabel = "";
			SERVER_PINGER_THREAD_POOL.submit(() -> {
				try {
					this.parent.getServerListPinger().add(this.serverInfo);
				} catch (UnknownHostException var2) {
					this.serverInfo.ping = -1L;
					this.serverInfo.label = Formatting.DARK_RED + I18n.translate("multiplayer.status.cannot_resolve");
				} catch (Exception var3) {
					this.serverInfo.ping = -1L;
					this.serverInfo.label = Formatting.DARK_RED + I18n.translate("multiplayer.status.cannot_connect");
				}
			});
		}

		boolean bl2 = this.serverInfo.protocolVersion > 404;
		boolean bl3 = this.serverInfo.protocolVersion < 404;
		boolean bl4 = bl2 || bl3;
		this.client.textRenderer.method_18355(this.serverInfo.name, (float)(n + 32 + 3), (float)(m + 1), 16777215);
		List<String> list = this.client.textRenderer.wrapLines(this.serverInfo.label, i - 32 - 2);

		for (int o = 0; o < Math.min(list.size(), 2); o++) {
			this.client.textRenderer.method_18355((String)list.get(o), (float)(n + 32 + 3), (float)(m + 12 + this.client.textRenderer.fontHeight * o), 8421504);
		}

		String string = bl4 ? Formatting.DARK_RED + this.serverInfo.version : this.serverInfo.playerCountLabel;
		int p = this.client.textRenderer.getStringWidth(string);
		this.client.textRenderer.method_18355(string, (float)(n + i - p - 15 - 2), (float)(m + 1), 8421504);
		int q = 0;
		String string2 = null;
		int r;
		String string3;
		if (bl4) {
			r = 5;
			string3 = I18n.translate(bl2 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
			string2 = this.serverInfo.playerListSummary;
		} else if (this.serverInfo.online && this.serverInfo.ping != -2L) {
			if (this.serverInfo.ping < 0L) {
				r = 5;
			} else if (this.serverInfo.ping < 150L) {
				r = 0;
			} else if (this.serverInfo.ping < 300L) {
				r = 1;
			} else if (this.serverInfo.ping < 600L) {
				r = 2;
			} else if (this.serverInfo.ping < 1000L) {
				r = 3;
			} else {
				r = 4;
			}

			if (this.serverInfo.ping < 0L) {
				string3 = I18n.translate("multiplayer.status.no_connection");
			} else {
				string3 = this.serverInfo.ping + "ms";
				string2 = this.serverInfo.playerListSummary;
			}
		} else {
			q = 1;
			r = (int)(Util.method_20227() / 100L + (long)(this.method_18402() * 2) & 7L);
			if (r > 4) {
				r = 8 - r;
			}

			string3 = I18n.translate("multiplayer.status.pinging");
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
		DrawableHelper.drawTexture(n + i - 15, m, (float)(q * 10), (float)(176 + r * 8), 10, 8, 256.0F, 256.0F);
		if (this.serverInfo.getIcon() != null && !this.serverInfo.getIcon().equals(this.iconUri)) {
			this.iconUri = this.serverInfo.getIcon();
			this.checkServerIcon();
			this.parent.getServerList().saveFile();
		}

		if (this.icon != null) {
			this.renderIcon(n, m, this.iconTextureId);
		} else {
			this.renderIcon(n, m, UNKNOWN_TEXTURE);
		}

		int z = k - n;
		int aa = l - m;
		if (z >= i - 15 && z <= i - 5 && aa >= 0 && aa <= 8) {
			this.parent.setTooltip(string3);
		} else if (z >= i - p - 15 - 2 && z <= i - 15 - 2 && aa >= 0 && aa <= 8) {
			this.parent.setTooltip(string2);
		}

		if (this.client.options.touchscreen || bl) {
			this.client.getTextureManager().bindTexture(SORT_BUTTONS_TEXTURE);
			DrawableHelper.fill(n, m, n + 32, m + 32, -1601138544);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int ab = k - n;
			int ac = l - m;
			if (this.isVisible()) {
				if (ab < 32 && ab > 16) {
					DrawableHelper.drawTexture(n, m, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					DrawableHelper.drawTexture(n, m, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}

			if (this.parent.canSortUp(this, this.method_18402())) {
				if (ab < 16 && ac < 16) {
					DrawableHelper.drawTexture(n, m, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					DrawableHelper.drawTexture(n, m, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}

			if (this.parent.canSortDown(this, this.method_18402())) {
				if (ab < 16 && ac > 16) {
					DrawableHelper.drawTexture(n, m, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					DrawableHelper.drawTexture(n, m, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
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
			this.icon.method_19449().close();
			this.icon = null;
		} else {
			try {
				MemoryStack memoryStack = MemoryStack.stackPush();
				Throwable var2 = null;

				try {
					ByteBuffer byteBuffer = memoryStack.UTF8(this.serverInfo.getIcon(), false);
					ByteBuffer byteBuffer2 = Base64.getDecoder().decode(byteBuffer);
					ByteBuffer byteBuffer3 = memoryStack.malloc(byteBuffer2.remaining());
					byteBuffer3.put(byteBuffer2);
					byteBuffer3.rewind();
					class_4277 lv = class_4277.method_19473(byteBuffer3);
					Validate.validState(lv.method_19458() == 64, "Must be 64 pixels wide", new Object[0]);
					Validate.validState(lv.method_19478() == 64, "Must be 64 pixels high", new Object[0]);
					if (this.icon == null) {
						this.icon = new NativeImageBackedTexture(lv);
					} else {
						this.icon.method_19448(lv);
						this.icon.upload();
					}

					this.client.getTextureManager().loadTexture(this.iconTextureId, this.icon);
				} catch (Throwable var15) {
					var2 = var15;
					throw var15;
				} finally {
					if (memoryStack != null) {
						if (var2 != null) {
							try {
								memoryStack.close();
							} catch (Throwable var14) {
								var2.addSuppressed(var14);
							}
						} else {
							memoryStack.close();
						}
					}
				}
			} catch (Throwable var17) {
				LOGGER.error("Invalid icon for server {} ({})", this.serverInfo.name, this.serverInfo.address, var17);
				this.serverInfo.setIcon(null);
			}
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		double f = d - (double)this.method_18404();
		double g = e - (double)this.method_18403();
		if (f <= 32.0) {
			if (f < 32.0 && f > 16.0 && this.isVisible()) {
				this.parent.selectEntry(this.method_18402());
				this.parent.connect();
				return true;
			}

			if (f < 16.0 && g < 16.0 && this.parent.canSortUp(this, this.method_18402())) {
				this.parent.sortUp(this, this.method_18402(), Screen.hasShiftDown());
				return true;
			}

			if (f < 16.0 && g > 16.0 && this.parent.canSortDown(this, this.method_18402())) {
				this.parent.sortDown(this, this.method_18402(), Screen.hasShiftDown());
				return true;
			}
		}

		this.parent.selectEntry(this.method_18402());
		if (Util.method_20227() - this.time < 250L) {
			this.parent.connect();
		}

		this.time = Util.method_20227();
		return false;
	}

	public ServerInfo getServer() {
		return this.serverInfo;
	}
}
