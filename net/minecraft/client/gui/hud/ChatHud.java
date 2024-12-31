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
import net.minecraft.client.util.Window;
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
			float f = this.client.options.chatOpacity * 0.9F + 0.1F;
			if (j > 0) {
				boolean bl = false;
				if (this.isChatFocused()) {
					bl = true;
				}

				float g = this.getChatScale();
				int k = MathHelper.ceil((float)this.getWidth() / g);
				GlStateManager.pushMatrix();
				GlStateManager.translate(2.0F, 8.0F, 0.0F);
				GlStateManager.scale(g, g, 1.0F);
				int l = 0;

				for (int m = 0; m + this.scrolledLines < this.visibleMessages.size() && m < i; m++) {
					ChatHudLine chatHudLine = (ChatHudLine)this.visibleMessages.get(m + this.scrolledLines);
					if (chatHudLine != null) {
						int n = ticks - chatHudLine.getCreationTick();
						if (n < 200 || bl) {
							double d = (double)n / 200.0;
							d = 1.0 - d;
							d *= 10.0;
							d = MathHelper.clamp(d, 0.0, 1.0);
							d *= d;
							int o = (int)(255.0 * d);
							if (bl) {
								o = 255;
							}

							o = (int)((float)o * f);
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
		LOGGER.info("[CHAT] {}", new Object[]{message.asUnformattedString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n")});
	}

	private void addMessage(Text message, int messageId, int timestamp, boolean ignoreLimit) {
		if (messageId != 0) {
			this.removeMessage(messageId);
		}

		int i = MathHelper.floor((float)this.getWidth() / this.getChatScale());
		List<Text> list = Texts.wrapLines(message, i, this.client.textRenderer, false, false);
		boolean bl = this.isChatFocused();

		for (Text text : list) {
			if (bl && this.scrolledLines > 0) {
				this.hasUnreadNewMessages = true;
				this.scroll(1);
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

	public void scroll(int lines) {
		this.scrolledLines += lines;
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
	public Text getTextAt(int x, int y) {
		if (!this.isChatFocused()) {
			return null;
		} else {
			Window window = new Window(this.client);
			int i = window.getScaleFactor();
			float f = this.getChatScale();
			int j = x / i - 2;
			int k = y / i - 40;
			j = MathHelper.floor((float)j / f);
			k = MathHelper.floor((float)k / f);
			if (j >= 0 && k >= 0) {
				int l = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
				if (j <= MathHelper.floor((float)this.getWidth() / this.getChatScale()) && k < this.client.textRenderer.fontHeight * l + l) {
					int m = k / this.client.textRenderer.fontHeight + this.scrolledLines;
					if (m >= 0 && m < this.visibleMessages.size()) {
						ChatHudLine chatHudLine = (ChatHudLine)this.visibleMessages.get(m);
						int n = 0;

						for (Text text : chatHudLine.getText()) {
							if (text instanceof LiteralText) {
								n += this.client.textRenderer.getStringWidth(Texts.getRenderChatMessage(((LiteralText)text).getRawString(), false));
								if (n > j) {
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
		return getWidth(this.client.options.chatWidth);
	}

	public int getHeight() {
		return getHeight(this.isChatFocused() ? this.client.options.chatHeightFocused : this.client.options.chatHeightUnfocused);
	}

	public float getChatScale() {
		return this.client.options.chatScale;
	}

	public static int getWidth(float chatWidth) {
		int i = 320;
		int j = 40;
		return MathHelper.floor(chatWidth * 280.0F + 40.0F);
	}

	public static int getHeight(float chatHeight) {
		int i = 180;
		int j = 20;
		return MathHelper.floor(chatHeight * 160.0F + 20.0F);
	}

	public int getVisibleLineCount() {
		return this.getHeight() / 9;
	}
}
