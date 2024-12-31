package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TwitchStreamProvider;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.ChatUserMode;
import tv.twitch.chat.ChatUserSubscription;

public class StreamUtilitiesScreen extends Screen {
	private static final Formatting DARK_GREEN = Formatting.DARK_GREEN;
	private static final Formatting RED = Formatting.RED;
	private static final Formatting DARK_PURPLE = Formatting.DARK_PURPLE;
	private final ChatUserInfo chatUserInfo;
	private final Text chatUserName;
	private final List<Text> displayTexts = Lists.newArrayList();
	private final TwitchStreamProvider twitchStreamProvider;
	private int textX;

	public StreamUtilitiesScreen(TwitchStreamProvider twitchStreamProvider, ChatUserInfo chatUserInfo) {
		this.twitchStreamProvider = twitchStreamProvider;
		this.chatUserInfo = chatUserInfo;
		this.chatUserName = new LiteralText(chatUserInfo.displayName);
		this.displayTexts.addAll(getDisplayTexts(chatUserInfo.modes, chatUserInfo.subscriptions, twitchStreamProvider));
	}

	public static List<Text> getDisplayTexts(Set<ChatUserMode> modes, Set<ChatUserSubscription> subscriptions, TwitchStreamProvider provider) {
		String string = provider == null ? null : provider.getCurrentChannelName();
		boolean bl = provider != null && provider.isChannelNameSet();
		List<Text> list = Lists.newArrayList();

		for (ChatUserMode chatUserMode : modes) {
			Text text = getUserModeText(chatUserMode, string, bl);
			if (text != null) {
				Text text2 = new LiteralText("- ");
				text2.append(text);
				list.add(text2);
			}
		}

		for (ChatUserSubscription chatUserSubscription : subscriptions) {
			Text text3 = getSubscriberText(chatUserSubscription, string, bl);
			if (text3 != null) {
				Text text4 = new LiteralText("- ");
				text4.append(text3);
				list.add(text4);
			}
		}

		return list;
	}

	public static Text getSubscriberText(ChatUserSubscription subscription, String channel, boolean nameSet) {
		Text text = null;
		if (subscription == ChatUserSubscription.TTV_CHAT_USERSUB_SUBSCRIBER) {
			if (channel == null) {
				text = new TranslatableText("stream.user.subscription.subscriber");
			} else if (nameSet) {
				text = new TranslatableText("stream.user.subscription.subscriber.self");
			} else {
				text = new TranslatableText("stream.user.subscription.subscriber.other", channel);
			}

			text.getStyle().setFormatting(DARK_GREEN);
		} else if (subscription == ChatUserSubscription.TTV_CHAT_USERSUB_TURBO) {
			text = new TranslatableText("stream.user.subscription.turbo");
			text.getStyle().setFormatting(DARK_PURPLE);
		}

		return text;
	}

	public static Text getUserModeText(ChatUserMode mode, String channel, boolean nameSet) {
		Text text = null;
		if (mode == ChatUserMode.TTV_CHAT_USERMODE_ADMINSTRATOR) {
			text = new TranslatableText("stream.user.mode.administrator");
			text.getStyle().setFormatting(DARK_PURPLE);
		} else if (mode == ChatUserMode.TTV_CHAT_USERMODE_BANNED) {
			if (channel == null) {
				text = new TranslatableText("stream.user.mode.banned");
			} else if (nameSet) {
				text = new TranslatableText("stream.user.mode.banned.self");
			} else {
				text = new TranslatableText("stream.user.mode.banned.other", channel);
			}

			text.getStyle().setFormatting(RED);
		} else if (mode == ChatUserMode.TTV_CHAT_USERMODE_BROADCASTER) {
			if (channel == null) {
				text = new TranslatableText("stream.user.mode.broadcaster");
			} else if (nameSet) {
				text = new TranslatableText("stream.user.mode.broadcaster.self");
			} else {
				text = new TranslatableText("stream.user.mode.broadcaster.other");
			}

			text.getStyle().setFormatting(DARK_GREEN);
		} else if (mode == ChatUserMode.TTV_CHAT_USERMODE_MODERATOR) {
			if (channel == null) {
				text = new TranslatableText("stream.user.mode.moderator");
			} else if (nameSet) {
				text = new TranslatableText("stream.user.mode.moderator.self");
			} else {
				text = new TranslatableText("stream.user.mode.moderator.other", channel);
			}

			text.getStyle().setFormatting(DARK_GREEN);
		} else if (mode == ChatUserMode.TTV_CHAT_USERMODE_STAFF) {
			text = new TranslatableText("stream.user.mode.staff");
			text.getStyle().setFormatting(DARK_PURPLE);
		}

		return text;
	}

	@Override
	public void init() {
		int i = this.width / 3;
		int j = i - 130;
		this.buttons.add(new ButtonWidget(1, i * 0 + j / 2, this.height - 70, 130, 20, I18n.translate("stream.userinfo.timeout")));
		this.buttons.add(new ButtonWidget(0, i * 1 + j / 2, this.height - 70, 130, 20, I18n.translate("stream.userinfo.ban")));
		this.buttons.add(new ButtonWidget(2, i * 2 + j / 2, this.height - 70, 130, 20, I18n.translate("stream.userinfo.mod")));
		this.buttons.add(new ButtonWidget(5, i * 0 + j / 2, this.height - 45, 130, 20, I18n.translate("gui.cancel")));
		this.buttons.add(new ButtonWidget(3, i * 1 + j / 2, this.height - 45, 130, 20, I18n.translate("stream.userinfo.unban")));
		this.buttons.add(new ButtonWidget(4, i * 2 + j / 2, this.height - 45, 130, 20, I18n.translate("stream.userinfo.unmod")));
		int k = 0;

		for (Text text : this.displayTexts) {
			k = Math.max(k, this.textRenderer.getStringWidth(text.asFormattedString()));
		}

		this.textX = this.width / 2 - k / 2;
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 0) {
				this.twitchStreamProvider.sendChatMessage("/ban " + this.chatUserInfo.displayName);
			} else if (button.id == 3) {
				this.twitchStreamProvider.sendChatMessage("/unban " + this.chatUserInfo.displayName);
			} else if (button.id == 2) {
				this.twitchStreamProvider.sendChatMessage("/mod " + this.chatUserInfo.displayName);
			} else if (button.id == 4) {
				this.twitchStreamProvider.sendChatMessage("/unmod " + this.chatUserInfo.displayName);
			} else if (button.id == 1) {
				this.twitchStreamProvider.sendChatMessage("/timeout " + this.chatUserInfo.displayName);
			}

			this.client.setScreen(null);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.chatUserName.asUnformattedString(), this.width / 2, 70, 16777215);
		int i = 80;

		for (Text text : this.displayTexts) {
			this.drawWithShadow(this.textRenderer, text.asFormattedString(), this.textX, i, 16777215);
			i += this.textRenderer.fontHeight;
		}

		super.render(mouseX, mouseY, tickDelta);
	}
}
