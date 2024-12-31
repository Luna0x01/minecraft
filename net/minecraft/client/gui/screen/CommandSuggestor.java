package net.minecraft.client.gui.screen;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Rect2i;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class CommandSuggestor {
	private static final Pattern BACKSLASH_S_PATTERN = Pattern.compile("(\\s+)");
	private final MinecraftClient client;
	private final Screen owner;
	private final TextFieldWidget textField;
	private final TextRenderer textRenderer;
	private final boolean slashRequired;
	private final boolean suggestingWhenEmpty;
	private final int inWindowIndexOffset;
	private final int maxSuggestionSize;
	private final boolean chatScreenSized;
	private final int color;
	private final List<String> messages = Lists.newArrayList();
	private int x;
	private int width;
	private ParseResults<CommandSource> parse;
	private CompletableFuture<Suggestions> pendingSuggestions;
	private CommandSuggestor.SuggestionWindow window;
	private boolean windowActive;
	private boolean completingSuggestions;

	public CommandSuggestor(
		MinecraftClient minecraftClient,
		Screen screen,
		TextFieldWidget textFieldWidget,
		TextRenderer textRenderer,
		boolean bl,
		boolean bl2,
		int i,
		int j,
		boolean bl3,
		int k
	) {
		this.client = minecraftClient;
		this.owner = screen;
		this.textField = textFieldWidget;
		this.textRenderer = textRenderer;
		this.slashRequired = bl;
		this.suggestingWhenEmpty = bl2;
		this.inWindowIndexOffset = i;
		this.maxSuggestionSize = j;
		this.chatScreenSized = bl3;
		this.color = k;
		textFieldWidget.setRenderTextProvider(this::provideRenderText);
	}

	public void setWindowActive(boolean bl) {
		this.windowActive = bl;
		if (!bl) {
			this.window = null;
		}
	}

	public boolean keyPressed(int i, int j, int k) {
		if (this.window != null && this.window.keyPressed(i, j, k)) {
			return true;
		} else if (this.owner.getFocused() == this.textField && i == 258) {
			this.showSuggestions(true);
			return true;
		} else {
			return false;
		}
	}

	public boolean mouseScrolled(double d) {
		return this.window != null && this.window.mouseScrolled(MathHelper.clamp(d, -1.0, 1.0));
	}

	public boolean mouseClicked(double d, double e, int i) {
		return this.window != null && this.window.mouseClicked((int)d, (int)e, i);
	}

	public void showSuggestions(boolean bl) {
		if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
			Suggestions suggestions = (Suggestions)this.pendingSuggestions.join();
			if (!suggestions.isEmpty()) {
				int i = 0;

				for (Suggestion suggestion : suggestions.getList()) {
					i = Math.max(i, this.textRenderer.getStringWidth(suggestion.getText()));
				}

				int j = MathHelper.clamp(
					this.textField.getCharacterX(suggestions.getRange().getStart()), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i
				);
				int k = this.chatScreenSized ? this.owner.height - 12 : 72;
				this.window = new CommandSuggestor.SuggestionWindow(j, k, i, suggestions, bl);
			}
		}
	}

	public void refresh() {
		String string = this.textField.getText();
		if (this.parse != null && !this.parse.getReader().getString().equals(string)) {
			this.parse = null;
		}

		if (!this.completingSuggestions) {
			this.textField.setSuggestion(null);
			this.window = null;
		}

		this.messages.clear();
		StringReader stringReader = new StringReader(string);
		boolean bl = stringReader.canRead() && stringReader.peek() == '/';
		if (bl) {
			stringReader.skip();
		}

		boolean bl2 = this.slashRequired || bl;
		int i = this.textField.getCursor();
		if (bl2) {
			CommandDispatcher<CommandSource> commandDispatcher = this.client.player.networkHandler.getCommandDispatcher();
			if (this.parse == null) {
				this.parse = commandDispatcher.parse(stringReader, this.client.player.networkHandler.getCommandSource());
			}

			int j = this.suggestingWhenEmpty ? stringReader.getCursor() : 1;
			if (i >= j && (this.window == null || !this.completingSuggestions)) {
				this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, i);
				this.pendingSuggestions.thenRun(() -> {
					if (this.pendingSuggestions.isDone()) {
						this.show();
					}
				});
			}
		} else {
			String string2 = string.substring(0, i);
			int k = getLastPlayerNameStart(string2);
			Collection<String> collection = this.client.player.networkHandler.getCommandSource().getPlayerNames();
			this.pendingSuggestions = CommandSource.suggestMatching(collection, new SuggestionsBuilder(string2, k));
		}
	}

	private static int getLastPlayerNameStart(String string) {
		if (Strings.isNullOrEmpty(string)) {
			return 0;
		} else {
			int i = 0;
			Matcher matcher = BACKSLASH_S_PATTERN.matcher(string);

			while (matcher.find()) {
				i = matcher.end();
			}

			return i;
		}
	}

	public void show() {
		if (this.textField.getCursor() == this.textField.getText().length()) {
			if (((Suggestions)this.pendingSuggestions.join()).isEmpty() && !this.parse.getExceptions().isEmpty()) {
				int i = 0;

				for (Entry<CommandNode<CommandSource>, CommandSyntaxException> entry : this.parse.getExceptions().entrySet()) {
					CommandSyntaxException commandSyntaxException = (CommandSyntaxException)entry.getValue();
					if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
						i++;
					} else {
						this.messages.add(commandSyntaxException.getMessage());
					}
				}

				if (i > 0) {
					this.messages.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
				}
			} else if (this.parse.getReader().canRead()) {
				this.messages.add(CommandManager.getException(this.parse).getMessage());
			}
		}

		this.x = 0;
		this.width = this.owner.width;
		if (this.messages.isEmpty()) {
			this.showUsages(Formatting.field_1080);
		}

		this.window = null;
		if (this.windowActive && this.client.options.autoSuggestions) {
			this.showSuggestions(false);
		}
	}

	private void showUsages(Formatting formatting) {
		CommandContextBuilder<CommandSource> commandContextBuilder = this.parse.getContext();
		SuggestionContext<CommandSource> suggestionContext = commandContextBuilder.findSuggestionContext(this.textField.getCursor());
		Map<CommandNode<CommandSource>, String> map = this.client
			.player
			.networkHandler
			.getCommandDispatcher()
			.getSmartUsage(suggestionContext.parent, this.client.player.networkHandler.getCommandSource());
		List<String> list = Lists.newArrayList();
		int i = 0;

		for (Entry<CommandNode<CommandSource>, String> entry : map.entrySet()) {
			if (!(entry.getKey() instanceof LiteralCommandNode)) {
				list.add(formatting + (String)entry.getValue());
				i = Math.max(i, this.textRenderer.getStringWidth((String)entry.getValue()));
			}
		}

		if (!list.isEmpty()) {
			this.messages.addAll(list);
			this.x = MathHelper.clamp(this.textField.getCharacterX(suggestionContext.startPos), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i);
			this.width = i;
		}
	}

	private String provideRenderText(String string, int i) {
		return this.parse != null ? highlight(this.parse, string, i) : string;
	}

	@Nullable
	private static String getSuggestionSuffix(String string, String string2) {
		return string2.startsWith(string) ? string2.substring(string.length()) : null;
	}

	public static String highlight(ParseResults<CommandSource> parseResults, String string, int i) {
		Formatting[] formattings = new Formatting[]{
			Formatting.field_1075, Formatting.field_1054, Formatting.field_1060, Formatting.field_1076, Formatting.field_1065
		};
		String string2 = Formatting.field_1080.toString();
		StringBuilder stringBuilder = new StringBuilder(string2);
		int j = 0;
		int k = -1;
		CommandContextBuilder<CommandSource> commandContextBuilder = parseResults.getContext().getLastChild();

		for (ParsedArgument<CommandSource, ?> parsedArgument : commandContextBuilder.getArguments().values()) {
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
				stringBuilder.append(Formatting.field_1061);
				stringBuilder.append(string, n, o);
				j = o;
			}
		}

		stringBuilder.append(string, j, string.length());
		return stringBuilder.toString();
	}

	public void render(int i, int j) {
		if (this.window != null) {
			this.window.render(i, j);
		} else {
			int k = 0;

			for (String string : this.messages) {
				int l = this.chatScreenSized ? this.owner.height - 14 - 13 - 12 * k : 72 + 12 * k;
				DrawableHelper.fill(this.x - 1, l, this.x + this.width + 1, l + 12, this.color);
				this.textRenderer.drawWithShadow(string, (float)this.x, (float)(l + 2), -1);
				k++;
			}
		}
	}

	public String method_23958() {
		return this.window != null ? "\n" + this.window.getNarration() : "";
	}

	public class SuggestionWindow {
		private final Rect2i area;
		private final Suggestions suggestions;
		private final String typedText;
		private int inWindowIndex;
		private int selection;
		private Vec2f mouse = Vec2f.ZERO;
		private boolean completed;
		private int lastNarrationIndex;

		private SuggestionWindow(int i, int j, int k, Suggestions suggestions, boolean bl) {
			int l = i - 1;
			int m = CommandSuggestor.this.chatScreenSized ? j - 3 - Math.min(suggestions.getList().size(), CommandSuggestor.this.maxSuggestionSize) * 12 : j;
			this.area = new Rect2i(l, m, k + 1, Math.min(suggestions.getList().size(), CommandSuggestor.this.maxSuggestionSize) * 12);
			this.suggestions = suggestions;
			this.typedText = CommandSuggestor.this.textField.getText();
			this.lastNarrationIndex = bl ? -1 : 0;
			this.select(0);
		}

		public void render(int i, int j) {
			int k = Math.min(this.suggestions.getList().size(), CommandSuggestor.this.maxSuggestionSize);
			int l = -5592406;
			boolean bl = this.inWindowIndex > 0;
			boolean bl2 = this.suggestions.getList().size() > this.inWindowIndex + k;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.mouse.x != (float)i || this.mouse.y != (float)j;
			if (bl4) {
				this.mouse = new Vec2f((float)i, (float)j);
			}

			if (bl3) {
				DrawableHelper.fill(this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), CommandSuggestor.this.color);
				DrawableHelper.fill(
					this.area.getX(),
					this.area.getY() + this.area.getHeight(),
					this.area.getX() + this.area.getWidth(),
					this.area.getY() + this.area.getHeight() + 1,
					CommandSuggestor.this.color
				);
				if (bl) {
					for (int m = 0; m < this.area.getWidth(); m++) {
						if (m % 2 == 0) {
							DrawableHelper.fill(this.area.getX() + m, this.area.getY() - 1, this.area.getX() + m + 1, this.area.getY(), -1);
						}
					}
				}

				if (bl2) {
					for (int n = 0; n < this.area.getWidth(); n++) {
						if (n % 2 == 0) {
							DrawableHelper.fill(
								this.area.getX() + n, this.area.getY() + this.area.getHeight(), this.area.getX() + n + 1, this.area.getY() + this.area.getHeight() + 1, -1
							);
						}
					}
				}
			}

			boolean bl5 = false;

			for (int o = 0; o < k; o++) {
				Suggestion suggestion = (Suggestion)this.suggestions.getList().get(o + this.inWindowIndex);
				DrawableHelper.fill(
					this.area.getX(), this.area.getY() + 12 * o, this.area.getX() + this.area.getWidth(), this.area.getY() + 12 * o + 12, CommandSuggestor.this.color
				);
				if (i > this.area.getX() && i < this.area.getX() + this.area.getWidth() && j > this.area.getY() + 12 * o && j < this.area.getY() + 12 * o + 12) {
					if (bl4) {
						this.select(o + this.inWindowIndex);
					}

					bl5 = true;
				}

				CommandSuggestor.this.textRenderer
					.drawWithShadow(
						suggestion.getText(), (float)(this.area.getX() + 1), (float)(this.area.getY() + 2 + 12 * o), o + this.inWindowIndex == this.selection ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.suggestions.getList().get(this.selection)).getTooltip();
				if (message != null) {
					CommandSuggestor.this.owner.renderTooltip(Texts.toText(message).asFormattedString(), i, j);
				}
			}
		}

		public boolean mouseClicked(int i, int j, int k) {
			if (!this.area.contains(i, j)) {
				return false;
			} else {
				int l = (j - this.area.getY()) / 12 + this.inWindowIndex;
				if (l >= 0 && l < this.suggestions.getList().size()) {
					this.select(l);
					this.complete();
				}

				return true;
			}
		}

		public boolean mouseScrolled(double d) {
			int i = (int)(
				CommandSuggestor.this.client.mouse.getX()
					* (double)CommandSuggestor.this.client.getWindow().getScaledWidth()
					/ (double)CommandSuggestor.this.client.getWindow().getWidth()
			);
			int j = (int)(
				CommandSuggestor.this.client.mouse.getY()
					* (double)CommandSuggestor.this.client.getWindow().getScaledHeight()
					/ (double)CommandSuggestor.this.client.getWindow().getHeight()
			);
			if (this.area.contains(i, j)) {
				this.inWindowIndex = MathHelper.clamp(
					(int)((double)this.inWindowIndex - d), 0, Math.max(this.suggestions.getList().size() - CommandSuggestor.this.maxSuggestionSize, 0)
				);
				return true;
			} else {
				return false;
			}
		}

		public boolean keyPressed(int i, int j, int k) {
			if (i == 265) {
				this.scroll(-1);
				this.completed = false;
				return true;
			} else if (i == 264) {
				this.scroll(1);
				this.completed = false;
				return true;
			} else if (i == 258) {
				if (this.completed) {
					this.scroll(Screen.hasShiftDown() ? -1 : 1);
				}

				this.complete();
				return true;
			} else if (i == 256) {
				this.discard();
				return true;
			} else {
				return false;
			}
		}

		public void scroll(int i) {
			this.select(this.selection + i);
			int j = this.inWindowIndex;
			int k = this.inWindowIndex + CommandSuggestor.this.maxSuggestionSize - 1;
			if (this.selection < j) {
				this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.suggestions.getList().size() - CommandSuggestor.this.maxSuggestionSize, 0));
			} else if (this.selection > k) {
				this.inWindowIndex = MathHelper.clamp(
					this.selection + CommandSuggestor.this.inWindowIndexOffset - CommandSuggestor.this.maxSuggestionSize,
					0,
					Math.max(this.suggestions.getList().size() - CommandSuggestor.this.maxSuggestionSize, 0)
				);
			}
		}

		public void select(int i) {
			this.selection = i;
			if (this.selection < 0) {
				this.selection = this.selection + this.suggestions.getList().size();
			}

			if (this.selection >= this.suggestions.getList().size()) {
				this.selection = this.selection - this.suggestions.getList().size();
			}

			Suggestion suggestion = (Suggestion)this.suggestions.getList().get(this.selection);
			CommandSuggestor.this.textField
				.setSuggestion(CommandSuggestor.getSuggestionSuffix(CommandSuggestor.this.textField.getText(), suggestion.apply(this.typedText)));
			if (NarratorManager.INSTANCE.isActive() && this.lastNarrationIndex != this.selection) {
				NarratorManager.INSTANCE.narrate(this.getNarration());
			}
		}

		public void complete() {
			Suggestion suggestion = (Suggestion)this.suggestions.getList().get(this.selection);
			CommandSuggestor.this.completingSuggestions = true;
			CommandSuggestor.this.textField.setText(suggestion.apply(this.typedText));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			CommandSuggestor.this.textField.setSelectionStart(i);
			CommandSuggestor.this.textField.setSelectionEnd(i);
			this.select(this.selection);
			CommandSuggestor.this.completingSuggestions = false;
			this.completed = true;
		}

		private String getNarration() {
			this.lastNarrationIndex = this.selection;
			List<Suggestion> list = this.suggestions.getList();
			Suggestion suggestion = (Suggestion)list.get(this.selection);
			Message message = suggestion.getTooltip();
			return message != null
				? I18n.translate("narration.suggestion.tooltip", this.selection + 1, list.size(), suggestion.getText(), message.getString())
				: I18n.translate("narration.suggestion", this.selection + 1, list.size(), suggestion.getText());
		}

		public void discard() {
			CommandSuggestor.this.window = null;
		}
	}
}
