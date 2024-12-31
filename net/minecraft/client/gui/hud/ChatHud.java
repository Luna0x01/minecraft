package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.Texts;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatHud extends DrawableHelper {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final List<String> messageHistory = Lists.newArrayList();
	private final List<ChatHudLine> messages = Lists.newArrayList();
	private final List<ChatHudLine> visibleMessages = Lists.newArrayList();
	private int scrolledLines;
	private boolean hasUnreadNewMessages;

	public ChatHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void render(int ticks) {
		if (this.client.options.chatVisibilityType != PlayerEntity.ChatVisibilityType.HIDDEN) {
			int i = this.getVisibleLineCount();
			int j = this.visibleMessages.size();
			double d = this.client.options.field_19989 * 0.9F + 0.1F;
			if (j > 0) {
				boolean bl = false;
				if (this.isChatFocused()) {
					bl = true;
				}

				double e = this.method_4938();
				int k = MathHelper.ceil((double)this.getWidth() / e);
				GlStateManager.pushMatrix();
				GlStateManager.translate(2.0F, 8.0F, 0.0F);
				GlStateManager.scale(e, e, 1.0);
				int l = 0;

				for (int m = 0; m + this.scrolledLines < this.visibleMessages.size() && m < i; m++) {
					ChatHudLine chatHudLine = (ChatHudLine)this.visibleMessages.get(m + this.scrolledLines);
					if (chatHudLine != null) {
						int n = ticks - chatHudLine.getCreationTick();
						if (n < 200 || bl) {
							double f = (double)n / 200.0;
							f = 1.0 - f;
							f *= 10.0;
							f = MathHelper.clamp(f, 0.0, 1.0);
							f *= f;
							int o = (int)(255.0 * f);
							if (bl) {
								o = 255;
							}

							o = (int)((double)o * d);
							l++;
							if (o > 3) {
								int p = 0;
								int q = -m * 9;
								fill(-2, q - 9, 0 + k + 4, q, o / 2 << 24);
								String string = chatHudLine.getText().asFormattedString();
								GlStateManager.enableBlend();
								this.client.textRenderer.drawWithShadow(string, 0.0F, (float)(q - 8), 16777215 + (o << 24));
								GlStateManager.disableAlphaTest();
								GlStateManager.disableBlend();
							}
						}
					}
				}

				if (bl) {
					int r = this.client.textRenderer.fontHeight;
					GlStateManager.translate(-3.0F, 0.0F, 0.0F);
					int s = j * r + j;
					int t = l * r + l;
					int u = this.scrolledLines * t / j;
					int v = t * t / s;
					if (s != t) {
						int w = u > 0 ? 170 : 96;
						int x = this.hasUnreadNewMessages ? 13382451 : 3355562;
						fill(0, -u, 2, -u - v, x + (w << 24));
						fill(2, -u, 1, -u - v, 13421772 + (w << 24));
					}
				}

				GlStateManager.popMatrix();
			}
		}
	}

	public void clear(boolean clearHistory) {
		this.visibleMessages.clear();
		this.messages.clear();
		if (clearHistory) {
			this.messageHistory.clear();
		}
	}

	public void addMessage(Text message) {
		this.addMessage(message, 0);
	}

	public void addMessage(Text message, int messageId) {
		this.addMessage(message, messageId, this.client.inGameHud.getTicks(), false);
		LOGGER.info("[CHAT] {}", message.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
	}

	private void addMessage(Text message, int messageId, int timestamp, boolean ignoreLimit) {
		if (messageId != 0) {
			this.removeMessage(messageId);
		}

		int i = MathHelper.floor((double)this.getWidth() / this.method_4938());
		List<Text> list = Texts.wrapLines(message, i, this.client.textRenderer, false, false);
		boolean bl = this.isChatFocused();

		for (Text text : list) {
			if (bl && this.scrolledLines > 0) {
				this.hasUnreadNewMessages = true;
				this.method_18378(1.0);
			}

			this.visibleMessages.add(0, new ChatHudLine(timestamp, text, messageId));
		}

		while (this.visibleMessages.size() > 100) {
			this.visibleMessages.remove(this.visibleMessages.size() - 1);
		}

		if (!ignoreLimit) {
			this.messages.add(0, new ChatHudLine(timestamp, message, messageId));

			while (this.messages.size() > 100) {
				this.messages.remove(this.messages.size() - 1);
			}
		}
	}

	public void reset() {
		this.visibleMessages.clear();
		this.resetScroll();

		for (int i = this.messages.size() - 1; i >= 0; i--) {
			ChatHudLine chatHudLine = (ChatHudLine)this.messages.get(i);
			this.addMessage(chatHudLine.getText(), chatHudLine.getId(), chatHudLine.getCreationTick(), true);
		}
	}

	public List<String> getMessageHistory() {
		return this.messageHistory;
	}

	public void addToMessageHistory(String message) {
		if (this.messageHistory.isEmpty() || !((String)this.messageHistory.get(this.messageHistory.size() - 1)).equals(message)) {
			this.messageHistory.add(message);
		}
	}

	public void resetScroll() {
		this.scrolledLines = 0;
		this.hasUnreadNewMessages = false;
	}

	public void method_18378(double d) {
		this.scrolledLines = (int)((double)this.scrolledLines + d);
		int i = this.visibleMessages.size();
		if (this.scrolledLines > i - this.getVisibleLineCount()) {
			this.scrolledLines = i - this.getVisibleLineCount();
		}

		if (this.scrolledLines <= 0) {
			this.scrolledLines = 0;
			this.hasUnreadNewMessages = false;
		}
	}

	@Nullable
	public Text method_18379(double d, double e) {
		if (!this.isChatFocused()) {
			return null;
		} else {
			double f = this.method_4938();
			double g = d - 2.0;
			double h = (double)this.client.field_19944.method_18322() - e - 40.0;
			g = (double)MathHelper.floor(g / f);
			h = (double)MathHelper.floor(h / f);
			if (!(g < 0.0) && !(h < 0.0)) {
				int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
				if (g <= (double)MathHelper.floor((double)this.getWidth() / this.method_4938()) && h < (double)(this.client.textRenderer.fontHeight * i + i)) {
					int j = (int)(h / (double)this.client.textRenderer.fontHeight + (double)this.scrolledLines);
					if (j >= 0 && j < this.visibleMessages.size()) {
						ChatHudLine chatHudLine = (ChatHudLine)this.visibleMessages.get(j);
						int k = 0;

						for (Text text : chatHudLine.getText()) {
							if (text instanceof LiteralText) {
								k += this.client.textRenderer.getStringWidth(Texts.getRenderChatMessage(((LiteralText)text).getRawString(), false));
								if ((double)k > g) {
									return text;
								}
							}
						}
					}

					return null;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	public boolean isChatFocused() {
		return this.client.currentScreen instanceof ChatScreen;
	}

	public void removeMessage(int messageId) {
		Iterator<ChatHudLine> iterator = this.visibleMessages.iterator();

		while (iterator.hasNext()) {
			ChatHudLine chatHudLine = (ChatHudLine)iterator.next();
			if (chatHudLine.getId() == messageId) {
				iterator.remove();
			}
		}

		iterator = this.messages.iterator();

		while (iterator.hasNext()) {
			ChatHudLine chatHudLine2 = (ChatHudLine)iterator.next();
			if (chatHudLine2.getId() == messageId) {
				iterator.remove();
				break;
			}
		}
	}

	public int getWidth() {
		return method_18380(this.client.options.field_19975);
	}

	public int getHeight() {
		return method_18381(this.isChatFocused() ? this.client.options.field_19977 : this.client.options.field_19976);
	}

	public double method_4938() {
		return this.client.options.field_19974;
	}

	public static int method_18380(double d) {
		int i = 320;
		int j = 40;
		return MathHelper.floor(d * 280.0 + 40.0);
	}

	public static int method_18381(double d) {
		int i = 180;
		int j = 20;
		return MathHelper.floor(d * 160.0 + 20.0);
	}

	public int getVisibleLineCount() {
		return this.getHeight() / 9;
	}
}
