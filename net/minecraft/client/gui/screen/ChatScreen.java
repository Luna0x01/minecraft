package net.minecraft.client.gui.screen;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.class_3965;
import net.minecraft.class_4122;
import net.minecraft.class_4228;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class ChatScreen extends Screen {
	private static final Pattern field_20219 = Pattern.compile("(\\s+)");
	private String originalChatText = "";
	private int messageHistorySize = -1;
	protected TextFieldWidget chatField;
	private String lastChatFieldText = "";
	protected final List<String> field_20216 = Lists.newArrayList();
	protected int field_20217;
	protected int field_20218;
	private ParseResults<class_3965> field_20220;
	private CompletableFuture<Suggestions> field_20221;
	private ChatScreen.class_4155 field_20222;
	private boolean field_20223;
	private boolean field_20224;

	public ChatScreen() {
	}

	public ChatScreen(String string) {
		this.lastChatFieldText = string;
	}

	@Nullable
	@Override
	public class_4122 getFocused() {
		return this.chatField;
	}

	@Override
	protected void init() {
		this.client.field_19946.method_18191(true);
		this.messageHistorySize = this.client.inGameHud.getChatHud().getMessageHistory().size();
		this.chatField = new TextFieldWidget(0, this.textRenderer, 4, this.height - 12, this.width - 4, 12);
		this.chatField.setMaxLength(256);
		this.chatField.setHasBorder(false);
		this.chatField.setFocused(true);
		this.chatField.setText(this.lastChatFieldText);
		this.chatField.setFocusUnlocked(false);
		this.chatField.method_18388(this::method_18538);
		this.chatField.method_18387(this::method_18531);
		this.field_20307.add(this.chatField);
		this.method_18543();
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.chatField.getText();
		this.init(client, width, height);
		this.method_18540(string);
		this.method_18543();
	}

	@Override
	public void removed() {
		this.client.field_19946.method_18191(false);
		this.client.inGameHud.getChatHud().resetScroll();
	}

	@Override
	public void tick() {
		this.chatField.tick();
	}

	private void method_18531(int i, String string) {
		String string2 = this.chatField.getText();
		this.field_20223 = !string2.equals(this.lastChatFieldText);
		this.method_18543();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.field_20222 != null && this.field_20222.method_18553(i, j, k)) {
			return true;
		} else if (i == 256) {
			this.client.setScreen(null);
			return true;
		} else if (i == 257 || i == 335) {
			String string = this.chatField.getText().trim();
			if (!string.isEmpty()) {
				this.sendMessage(string);
			}

			this.client.setScreen(null);
			return true;
		} else if (i == 265) {
			this.setChatFromHistory(-1);
			return true;
		} else if (i == 264) {
			this.setChatFromHistory(1);
			return true;
		} else if (i == 266) {
			this.client.inGameHud.getChatHud().method_18378((double)(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1));
			return true;
		} else if (i == 267) {
			this.client.inGameHud.getChatHud().method_18378((double)(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1));
			return true;
		} else {
			if (i == 258) {
				this.field_20223 = true;
				this.method_18542();
			}

			return this.chatField.keyPressed(i, j, k);
		}
	}

	public void method_18542() {
		if (this.field_20221 != null && this.field_20221.isDone()) {
			int i = 0;
			Suggestions suggestions = (Suggestions)this.field_20221.join();
			if (!suggestions.getList().isEmpty()) {
				for (Suggestion suggestion : suggestions.getList()) {
					i = Math.max(i, this.textRenderer.getStringWidth(suggestion.getText()));
				}

				int j = MathHelper.clamp(this.chatField.method_18392(suggestions.getRange().getStart()), 0, this.width - i);
				this.field_20222 = new ChatScreen.class_4155(j, this.height - 12, i, suggestions);
			}
		}
	}

	private static int method_18537(String string) {
		if (Strings.isNullOrEmpty(string)) {
			return 0;
		} else {
			int i = 0;
			Matcher matcher = field_20219.matcher(string);

			while (matcher.find()) {
				i = matcher.end();
			}

			return i;
		}
	}

	private void method_18543() {
		this.field_20220 = null;
		if (!this.field_20224) {
			this.chatField.method_18390(null);
			this.field_20222 = null;
		}

		this.field_20216.clear();
		String string = this.chatField.getText();
		StringReader stringReader = new StringReader(string);
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
			CommandDispatcher<class_3965> commandDispatcher = this.client.player.networkHandler.method_18963();
			this.field_20220 = commandDispatcher.parse(stringReader, this.client.player.networkHandler.method_18961());
			if (this.field_20222 == null || !this.field_20224) {
				StringReader stringReader2 = new StringReader(string.substring(0, Math.min(string.length(), this.chatField.getCursor())));
				if (stringReader2.canRead() && stringReader2.peek() == '/') {
					stringReader2.skip();
					ParseResults<class_3965> parseResults = commandDispatcher.parse(stringReader2, this.client.player.networkHandler.method_18961());
					this.field_20221 = commandDispatcher.getCompletionSuggestions(parseResults);
					this.field_20221.thenRun(() -> {
						if (this.field_20221.isDone()) {
							this.method_18544();
						}
					});
				}
			}
		} else {
			int i = method_18537(string);
			Collection<String> collection = this.client.player.networkHandler.method_18961().method_17576();
			this.field_20221 = class_3965.method_17571(collection, new SuggestionsBuilder(string, i));
		}
	}

	private void method_18544() {
		if (((Suggestions)this.field_20221.join()).isEmpty()
			&& !this.field_20220.getExceptions().isEmpty()
			&& this.chatField.getCursor() == this.chatField.getText().length()) {
			int i = 0;

			for (Entry<CommandNode<class_3965>, CommandSyntaxException> entry : this.field_20220.getExceptions().entrySet()) {
				CommandSyntaxException commandSyntaxException = (CommandSyntaxException)entry.getValue();
				if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
					i++;
				} else {
					this.field_20216.add(commandSyntaxException.getMessage());
				}
			}

			if (i > 0) {
				this.field_20216.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
			}
		}

		this.field_20217 = 0;
		this.field_20218 = this.width;
		if (this.field_20216.isEmpty()) {
			this.method_18532(Formatting.GRAY);
		}

		this.field_20222 = null;
		if (this.field_20223 && this.client.options.field_19978) {
			this.method_18542();
		}
	}

	private String method_18538(String string, int i) {
		return this.field_20220 != null ? method_18536(this.field_20220, string, i) : string;
	}

	public static String method_18536(ParseResults<class_3965> parseResults, String string, int i) {
		Formatting[] formattings = new Formatting[]{Formatting.AQUA, Formatting.YELLOW, Formatting.GREEN, Formatting.LIGHT_PURPLE, Formatting.GOLD};
		String string2 = Formatting.GRAY.toString();
		StringBuilder stringBuilder = new StringBuilder(string2);
		int j = 0;
		int k = -1;
		CommandContextBuilder<class_3965> commandContextBuilder = parseResults.getContext().getLastChild();

		for (ParsedArgument<class_3965, ?> parsedArgument : commandContextBuilder.getArguments().values()) {
			if (++k >= formattings.length) {
				k = 0;
			}

			int l = Math.max(parsedArgument.getRange().getStart() - i, 0);
			if (l >= string.length()) {
				break;
			}

			int m = Math.min(parsedArgument.getRange().getEnd() - i, string.length());
			if (m > 0) {
				stringBuilder.append(string, j, l);
				stringBuilder.append(formattings[k]);
				stringBuilder.append(string, l, m);
				stringBuilder.append(string2);
				j = m;
			}
		}

		if (parseResults.getReader().canRead()) {
			int n = Math.max(parseResults.getReader().getCursor() - i, 0);
			if (n < string.length()) {
				int o = Math.min(n + parseResults.getReader().getRemainingLength(), string.length());
				stringBuilder.append(string, j, n);
				stringBuilder.append(Formatting.RED);
				stringBuilder.append(string, n, o);
				j = o;
			}
		}

		stringBuilder.append(string, j, string.length());
		return stringBuilder.toString();
	}

	@Override
	public boolean mouseScrolled(double d) {
		if (d > 1.0) {
			d = 1.0;
		}

		if (d < -1.0) {
			d = -1.0;
		}

		if (this.field_20222 != null && this.field_20222.method_18547(d)) {
			return true;
		} else {
			if (!hasShiftDown()) {
				d *= 7.0;
			}

			this.client.inGameHud.getChatHud().method_18378(d);
			return true;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_20222 != null && this.field_20222.method_18550((int)d, (int)e, i)) {
			return true;
		} else {
			if (i == 0) {
				Text text = this.client.inGameHud.getChatHud().method_18379(d, e);
				if (text != null && this.handleTextClick(text)) {
					return true;
				}
			}

			return this.chatField.mouseClicked(d, e, i) ? true : super.mouseClicked(d, e, i);
		}
	}

	@Override
	protected void insertText(String text, boolean override) {
		if (override) {
			this.chatField.setText(text);
		} else {
			this.chatField.write(text);
		}
	}

	public void setChatFromHistory(int index) {
		int i = this.messageHistorySize + index;
		int j = this.client.inGameHud.getChatHud().getMessageHistory().size();
		i = MathHelper.clamp(i, 0, j);
		if (i != this.messageHistorySize) {
			if (i == j) {
				this.messageHistorySize = j;
				this.chatField.setText(this.originalChatText);
			} else {
				if (this.messageHistorySize == j) {
					this.originalChatText = this.chatField.getText();
				}

				this.chatField.setText((String)this.client.inGameHud.getChatHud().getMessageHistory().get(i));
				this.field_20222 = null;
				this.messageHistorySize = i;
				this.field_20223 = false;
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		fill(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
		this.chatField.method_18385(mouseX, mouseY, tickDelta);
		if (this.field_20222 != null) {
			this.field_20222.method_18549(mouseX, mouseY);
		} else {
			int i = 0;

			for (String string : this.field_20216) {
				fill(this.field_20217 - 1, this.height - 14 - 13 - 12 * i, this.field_20217 + this.field_20218 + 1, this.height - 2 - 13 - 12 * i, -16777216);
				this.textRenderer.drawWithShadow(string, (float)this.field_20217, (float)(this.height - 14 - 13 + 2 - 12 * i), -1);
				i++;
			}
		}

		Text text = this.client.inGameHud.getChatHud().method_18379((double)mouseX, (double)mouseY);
		if (text != null && text.getStyle().getHoverEvent() != null) {
			this.renderTextHoverEffect(text, mouseX, mouseY);
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}

	private void method_18532(Formatting formatting) {
		CommandContextBuilder<class_3965> commandContextBuilder = this.field_20220.getContext();
		CommandContextBuilder<class_3965> commandContextBuilder2 = commandContextBuilder.getLastChild();
		if (!commandContextBuilder2.getNodes().isEmpty()) {
			CommandNode<class_3965> commandNode;
			int i;
			if (this.field_20220.getReader().canRead()) {
				Entry<CommandNode<class_3965>, StringRange> entry = (Entry<CommandNode<class_3965>, StringRange>)Iterables.getLast(
					commandContextBuilder2.getNodes().entrySet()
				);
				commandNode = (CommandNode<class_3965>)entry.getKey();
				i = ((StringRange)entry.getValue()).getEnd() + 1;
			} else if (commandContextBuilder2.getNodes().size() > 1) {
				Entry<CommandNode<class_3965>, StringRange> entry2 = (Entry<CommandNode<class_3965>, StringRange>)Iterables.get(
					commandContextBuilder2.getNodes().entrySet(), commandContextBuilder2.getNodes().size() - 2
				);
				commandNode = (CommandNode<class_3965>)entry2.getKey();
				i = ((StringRange)entry2.getValue()).getEnd() + 1;
			} else {
				if (commandContextBuilder == commandContextBuilder2 || commandContextBuilder2.getNodes().isEmpty()) {
					return;
				}

				Entry<CommandNode<class_3965>, StringRange> entry3 = (Entry<CommandNode<class_3965>, StringRange>)Iterables.getLast(
					commandContextBuilder2.getNodes().entrySet()
				);
				commandNode = (CommandNode<class_3965>)entry3.getKey();
				i = ((StringRange)entry3.getValue()).getEnd() + 1;
			}

			Map<CommandNode<class_3965>, String> map = this.client
				.player
				.networkHandler
				.method_18963()
				.getSmartUsage(commandNode, this.client.player.networkHandler.method_18961());
			List<String> list = Lists.newArrayList();
			int m = 0;

			for (Entry<CommandNode<class_3965>, String> entry4 : map.entrySet()) {
				if (!(entry4.getKey() instanceof LiteralCommandNode)) {
					list.add(formatting + (String)entry4.getValue());
					m = Math.max(m, this.textRenderer.getStringWidth((String)entry4.getValue()));
				}
			}

			if (!list.isEmpty()) {
				this.field_20216.addAll(list);
				this.field_20217 = MathHelper.clamp(this.chatField.method_18392(i) + this.textRenderer.getStringWidth(" "), 0, this.width - m);
				this.field_20218 = m;
			}
		}
	}

	@Nullable
	private static String method_18541(String string, String string2) {
		return string2.startsWith(string) ? string2.substring(string.length()) : null;
	}

	private void method_18540(String string) {
		this.chatField.setText(string);
	}

	class class_4155 {
		private final class_4228 field_20226;
		private final Suggestions field_20227;
		private final String field_20228;
		private int field_20229;
		private int field_20230;
		private Vec2f field_20231 = Vec2f.ZERO;
		private boolean field_20232;

		private class_4155(int i, int j, int k, Suggestions suggestions) {
			this.field_20226 = new class_4228(i - 1, j - 3 - Math.min(suggestions.getList().size(), 10) * 12, k + 1, Math.min(suggestions.getList().size(), 10) * 12);
			this.field_20227 = suggestions;
			this.field_20228 = ChatScreen.this.chatField.getText();
			this.method_18552(0);
		}

		public void method_18549(int i, int j) {
			int k = Math.min(this.field_20227.getList().size(), 10);
			int l = -5592406;
			boolean bl = this.field_20229 > 0;
			boolean bl2 = this.field_20227.getList().size() > this.field_20229 + k;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.field_20231.x != (float)i || this.field_20231.y != (float)j;
			if (bl4) {
				this.field_20231 = new Vec2f((float)i, (float)j);
			}

			if (bl3) {
				DrawableHelper.fill(
					this.field_20226.method_19177(),
					this.field_20226.method_19178() - 1,
					this.field_20226.method_19177() + this.field_20226.method_19180(),
					this.field_20226.method_19178(),
					-805306368
				);
				DrawableHelper.fill(
					this.field_20226.method_19177(),
					this.field_20226.method_19178() + this.field_20226.method_19181(),
					this.field_20226.method_19177() + this.field_20226.method_19180(),
					this.field_20226.method_19178() + this.field_20226.method_19181() + 1,
					-805306368
				);
				if (bl) {
					for (int m = 0; m < this.field_20226.method_19180(); m++) {
						if (m % 2 == 0) {
							DrawableHelper.fill(
								this.field_20226.method_19177() + m, this.field_20226.method_19178() - 1, this.field_20226.method_19177() + m + 1, this.field_20226.method_19178(), -1
							);
						}
					}
				}

				if (bl2) {
					for (int n = 0; n < this.field_20226.method_19180(); n++) {
						if (n % 2 == 0) {
							DrawableHelper.fill(
								this.field_20226.method_19177() + n,
								this.field_20226.method_19178() + this.field_20226.method_19181(),
								this.field_20226.method_19177() + n + 1,
								this.field_20226.method_19178() + this.field_20226.method_19181() + 1,
								-1
							);
						}
					}
				}
			}

			boolean bl5 = false;

			for (int o = 0; o < k; o++) {
				Suggestion suggestion = (Suggestion)this.field_20227.getList().get(o + this.field_20229);
				DrawableHelper.fill(
					this.field_20226.method_19177(),
					this.field_20226.method_19178() + 12 * o,
					this.field_20226.method_19177() + this.field_20226.method_19180(),
					this.field_20226.method_19178() + 12 * o + 12,
					-805306368
				);
				if (i > this.field_20226.method_19177()
					&& i < this.field_20226.method_19177() + this.field_20226.method_19180()
					&& j > this.field_20226.method_19178() + 12 * o
					&& j < this.field_20226.method_19178() + 12 * o + 12) {
					if (bl4) {
						this.method_18552(o + this.field_20229);
					}

					bl5 = true;
				}

				ChatScreen.this.textRenderer
					.drawWithShadow(
						suggestion.getText(),
						(float)(this.field_20226.method_19177() + 1),
						(float)(this.field_20226.method_19178() + 2 + 12 * o),
						o + this.field_20229 == this.field_20230 ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.field_20227.getList().get(this.field_20230)).getTooltip();
				if (message != null) {
					ChatScreen.this.renderTooltip(ChatSerializer.method_20187(message).asFormattedString(), i, j);
				}
			}
		}

		public boolean method_18550(int i, int j, int k) {
			if (!this.field_20226.method_19179(i, j)) {
				return false;
			} else {
				int l = (j - this.field_20226.method_19178()) / 12 + this.field_20229;
				if (l >= 0 && l < this.field_20227.getList().size()) {
					this.method_18552(l);
					this.method_18546();
				}

				return true;
			}
		}

		public boolean method_18547(double d) {
			int i = (int)(
				ChatScreen.this.client.field_19945.method_18249()
					* (double)ChatScreen.this.client.field_19944.method_18321()
					/ (double)ChatScreen.this.client.field_19944.method_18319()
			);
			int j = (int)(
				ChatScreen.this.client.field_19945.method_18250()
					* (double)ChatScreen.this.client.field_19944.method_18322()
					/ (double)ChatScreen.this.client.field_19944.method_18320()
			);
			if (this.field_20226.method_19179(i, j)) {
				this.field_20229 = MathHelper.clamp((int)((double)this.field_20229 - d), 0, Math.max(this.field_20227.getList().size() - 10, 0));
				return true;
			} else {
				return false;
			}
		}

		public boolean method_18553(int i, int j, int k) {
			if (i == 265) {
				this.method_18548(-1);
				this.field_20232 = false;
				return true;
			} else if (i == 264) {
				this.method_18548(1);
				this.field_20232 = false;
				return true;
			} else if (i == 258) {
				if (this.field_20232) {
					this.method_18548(Screen.hasShiftDown() ? -1 : 1);
				}

				this.method_18546();
				return true;
			} else if (i == 256) {
				this.method_18551();
				return true;
			} else {
				return false;
			}
		}

		public void method_18548(int i) {
			this.method_18552(this.field_20230 + i);
			int j = this.field_20229;
			int k = this.field_20229 + 10 - 1;
			if (this.field_20230 < j) {
				this.field_20229 = MathHelper.clamp(this.field_20230, 0, Math.max(this.field_20227.getList().size() - 10, 0));
			} else if (this.field_20230 > k) {
				this.field_20229 = MathHelper.clamp(this.field_20230 + 1 - 10, 0, Math.max(this.field_20227.getList().size() - 10, 0));
			}
		}

		public void method_18552(int i) {
			this.field_20230 = i;
			if (this.field_20230 < 0) {
				this.field_20230 = this.field_20230 + this.field_20227.getList().size();
			}

			if (this.field_20230 >= this.field_20227.getList().size()) {
				this.field_20230 = this.field_20230 - this.field_20227.getList().size();
			}

			Suggestion suggestion = (Suggestion)this.field_20227.getList().get(this.field_20230);
			ChatScreen.this.chatField.method_18390(ChatScreen.method_18541(ChatScreen.this.chatField.getText(), suggestion.apply(this.field_20228)));
		}

		public void method_18546() {
			Suggestion suggestion = (Suggestion)this.field_20227.getList().get(this.field_20230);
			ChatScreen.this.field_20224 = true;
			ChatScreen.this.method_18540(suggestion.apply(this.field_20228));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			ChatScreen.this.chatField.method_18391(i);
			ChatScreen.this.chatField.setSelectionEnd(i);
			this.method_18552(this.field_20230);
			ChatScreen.this.field_20224 = false;
			this.field_20232 = true;
		}

		public void method_18551() {
			ChatScreen.this.field_20222 = null;
		}
	}
}
