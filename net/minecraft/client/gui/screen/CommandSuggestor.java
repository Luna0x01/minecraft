package net.minecraft.client.gui.screen;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
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
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class CommandSuggestor {
	private static final Pattern BACKSLASH_S_PATTERN = Pattern.compile("(\\s+)");
	private static final Style ERROR_FORMATTING = Style.EMPTY.withColor(Formatting.RED);
	private static final Style INFO_FORMATTING = Style.EMPTY.withColor(Formatting.GRAY);
	private static final List<Style> HIGHLIGHT_FORMATTINGS = (List<Style>)Stream.of(
			Formatting.AQUA, Formatting.YELLOW, Formatting.GREEN, Formatting.LIGHT_PURPLE, Formatting.GOLD
		)
		.map(Style.EMPTY::withColor)
		.collect(ImmutableList.toImmutableList());
	final MinecraftClient client;
	final Screen owner;
	final TextFieldWidget textField;
	final TextRenderer textRenderer;
	private final boolean slashOptional;
	private final boolean suggestingWhenEmpty;
	final int inWindowIndexOffset;
	final int maxSuggestionSize;
	final boolean chatScreenSized;
	final int color;
	private final List<OrderedText> messages = Lists.newArrayList();
	private int x;
	private int width;
	@Nullable
	private ParseResults<CommandSource> parse;
	@Nullable
	private CompletableFuture<Suggestions> pendingSuggestions;
	@Nullable
	CommandSuggestor.SuggestionWindow window;
	private boolean windowActive;
	boolean completingSuggestions;

	public CommandSuggestor(
		MinecraftClient client,
		Screen owner,
		TextFieldWidget textField,
		TextRenderer textRenderer,
		boolean slashOptional,
		boolean suggestingWhenEmpty,
		int inWindowIndexOffset,
		int maxSuggestionSize,
		boolean chatScreenSized,
		int color
	) {
		this.client = client;
		this.owner = owner;
		this.textField = textField;
		this.textRenderer = textRenderer;
		this.slashOptional = slashOptional;
		this.suggestingWhenEmpty = suggestingWhenEmpty;
		this.inWindowIndexOffset = inWindowIndexOffset;
		this.maxSuggestionSize = maxSuggestionSize;
		this.chatScreenSized = chatScreenSized;
		this.color = color;
		textField.setRenderTextProvider(this::provideRenderText);
	}

	public void setWindowActive(boolean windowActive) {
		this.windowActive = windowActive;
		if (!windowActive) {
			this.window = null;
		}
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.window != null && this.window.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (this.owner.getFocused() == this.textField && keyCode == 258) {
			this.showSuggestions(true);
			return true;
		} else {
			return false;
		}
	}

	public boolean mouseScrolled(double amount) {
		return this.window != null && this.window.mouseScrolled(MathHelper.clamp(amount, -1.0, 1.0));
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.window != null && this.window.mouseClicked((int)mouseX, (int)mouseY, button);
	}

	public void showSuggestions(boolean narrateFirstSuggestion) {
		if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
			Suggestions suggestions = (Suggestions)this.pendingSuggestions.join();
			if (!suggestions.isEmpty()) {
				int i = 0;

				for (Suggestion suggestion : suggestions.getList()) {
					i = Math.max(i, this.textRenderer.getWidth(suggestion.getText()));
				}

				int j = MathHelper.clamp(
					this.textField.getCharacterX(suggestions.getRange().getStart()), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i
				);
				int k = this.chatScreenSized ? this.owner.height - 12 : 72;
				this.window = new CommandSuggestor.SuggestionWindow(j, k, i, this.sortSuggestions(suggestions), narrateFirstSuggestion);
			}
		}
	}

	private List<Suggestion> sortSuggestions(Suggestions suggestions) {
		String string = this.textField.getText().substring(0, this.textField.getCursor());
		int i = getLastPlayerNameStart(string);
		String string2 = string.substring(i).toLowerCase(Locale.ROOT);
		List<Suggestion> list = Lists.newArrayList();
		List<Suggestion> list2 = Lists.newArrayList();

		for (Suggestion suggestion : suggestions.getList()) {
			if (!suggestion.getText().startsWith(string2) && !suggestion.getText().startsWith("minecraft:" + string2)) {
				list2.add(suggestion);
			} else {
				list.add(suggestion);
			}
		}

		list.addAll(list2);
		return list;
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

		boolean bl2 = this.slashOptional || bl;
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

	private static int getLastPlayerNameStart(String input) {
		if (Strings.isNullOrEmpty(input)) {
			return 0;
		} else {
			int i = 0;
			Matcher matcher = BACKSLASH_S_PATTERN.matcher(input);

			while (matcher.find()) {
				i = matcher.end();
			}

			return i;
		}
	}

	private static OrderedText formatException(CommandSyntaxException exception) {
		Text text = Texts.toText(exception.getRawMessage());
		String string = exception.getContext();
		return string == null ? text.asOrderedText() : new TranslatableText("command.context.parse_error", text, exception.getCursor(), string).asOrderedText();
	}

	private void show() {
		if (this.textField.getCursor() == this.textField.getText().length()) {
			if (((Suggestions)this.pendingSuggestions.join()).isEmpty() && !this.parse.getExceptions().isEmpty()) {
				int i = 0;

				for (Entry<CommandNode<CommandSource>, CommandSyntaxException> entry : this.parse.getExceptions().entrySet()) {
					CommandSyntaxException commandSyntaxException = (CommandSyntaxException)entry.getValue();
					if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
						i++;
					} else {
						this.messages.add(formatException(commandSyntaxException));
					}
				}

				if (i > 0) {
					this.messages.add(formatException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
				}
			} else if (this.parse.getReader().canRead()) {
				this.messages.add(formatException(CommandManager.getException(this.parse)));
			}
		}

		this.x = 0;
		this.width = this.owner.width;
		if (this.messages.isEmpty()) {
			this.showUsages(Formatting.GRAY);
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
		List<OrderedText> list = Lists.newArrayList();
		int i = 0;
		Style style = Style.EMPTY.withColor(formatting);

		for (Entry<CommandNode<CommandSource>, String> entry : map.entrySet()) {
			if (!(entry.getKey() instanceof LiteralCommandNode)) {
				list.add(OrderedText.styledForwardsVisitedString((String)entry.getValue(), style));
				i = Math.max(i, this.textRenderer.getWidth((String)entry.getValue()));
			}
		}

		if (!list.isEmpty()) {
			this.messages.addAll(list);
			this.x = MathHelper.clamp(this.textField.getCharacterX(suggestionContext.startPos), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i);
			this.width = i;
		}
	}

	private OrderedText provideRenderText(String original, int firstCharacterIndex) {
		return this.parse != null ? highlight(this.parse, original, firstCharacterIndex) : OrderedText.styledForwardsVisitedString(original, Style.EMPTY);
	}

	@Nullable
	static String getSuggestionSuffix(String original, String suggestion) {
		return suggestion.startsWith(original) ? suggestion.substring(original.length()) : null;
	}

	private static OrderedText highlight(ParseResults<CommandSource> parse, String original, int firstCharacterIndex) {
		List<OrderedText> list = Lists.newArrayList();
		int i = 0;
		int j = -1;
		CommandContextBuilder<CommandSource> commandContextBuilder = parse.getContext().getLastChild();

		for (ParsedArgument<CommandSource, ?> parsedArgument : commandContextBuilder.getArguments().values()) {
			if (++j >= HIGHLIGHT_FORMATTINGS.size()) {
				j = 0;
			}

			int k = Math.max(parsedArgument.getRange().getStart() - firstCharacterIndex, 0);
			if (k >= original.length()) {
				break;
			}

			int l = Math.min(parsedArgument.getRange().getEnd() - firstCharacterIndex, original.length());
			if (l > 0) {
				list.add(OrderedText.styledForwardsVisitedString(original.substring(i, k), INFO_FORMATTING));
				list.add(OrderedText.styledForwardsVisitedString(original.substring(k, l), (Style)HIGHLIGHT_FORMATTINGS.get(j)));
				i = l;
			}
		}

		if (parse.getReader().canRead()) {
			int m = Math.max(parse.getReader().getCursor() - firstCharacterIndex, 0);
			if (m < original.length()) {
				int n = Math.min(m + parse.getReader().getRemainingLength(), original.length());
				list.add(OrderedText.styledForwardsVisitedString(original.substring(i, m), INFO_FORMATTING));
				list.add(OrderedText.styledForwardsVisitedString(original.substring(m, n), ERROR_FORMATTING));
				i = n;
			}
		}

		list.add(OrderedText.styledForwardsVisitedString(original.substring(i), INFO_FORMATTING));
		return OrderedText.concat(list);
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY) {
		if (this.window != null) {
			this.window.render(matrices, mouseX, mouseY);
		} else {
			int i = 0;

			for (OrderedText orderedText : this.messages) {
				int j = this.chatScreenSized ? this.owner.height - 14 - 13 - 12 * i : 72 + 12 * i;
				DrawableHelper.fill(matrices, this.x - 1, j, this.x + this.width + 1, j + 12, this.color);
				this.textRenderer.drawWithShadow(matrices, orderedText, (float)this.x, (float)(j + 2), -1);
				i++;
			}
		}
	}

	public String getNarration() {
		return this.window != null ? "\n" + this.window.getNarration() : "";
	}

	public class SuggestionWindow {
		private final Rect2i area;
		private final String typedText;
		private final List<Suggestion> suggestions;
		private int inWindowIndex;
		private int selection;
		private Vec2f mouse = Vec2f.ZERO;
		private boolean completed;
		private int lastNarrationIndex;

		SuggestionWindow(int i, int j, int k, List<Suggestion> list, boolean bl) {
			int l = i - 1;
			int m = CommandSuggestor.this.chatScreenSized ? j - 3 - Math.min(list.size(), CommandSuggestor.this.maxSuggestionSize) * 12 : j;
			this.area = new Rect2i(l, m, k + 1, Math.min(list.size(), CommandSuggestor.this.maxSuggestionSize) * 12);
			this.typedText = CommandSuggestor.this.textField.getText();
			this.lastNarrationIndex = bl ? -1 : 0;
			this.suggestions = list;
			this.select(0);
		}

		public void render(MatrixStack matrices, int mouseX, int mouseY) {
			int i = Math.min(this.suggestions.size(), CommandSuggestor.this.maxSuggestionSize);
			int j = -5592406;
			boolean bl = this.inWindowIndex > 0;
			boolean bl2 = this.suggestions.size() > this.inWindowIndex + i;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.mouse.x != (float)mouseX || this.mouse.y != (float)mouseY;
			if (bl4) {
				this.mouse = new Vec2f((float)mouseX, (float)mouseY);
			}

			if (bl3) {
				DrawableHelper.fill(
					matrices, this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), CommandSuggestor.this.color
				);
				DrawableHelper.fill(
					matrices,
					this.area.getX(),
					this.area.getY() + this.area.getHeight(),
					this.area.getX() + this.area.getWidth(),
					this.area.getY() + this.area.getHeight() + 1,
					CommandSuggestor.this.color
				);
				if (bl) {
					for (int k = 0; k < this.area.getWidth(); k++) {
						if (k % 2 == 0) {
							DrawableHelper.fill(matrices, this.area.getX() + k, this.area.getY() - 1, this.area.getX() + k + 1, this.area.getY(), -1);
						}
					}
				}

				if (bl2) {
					for (int l = 0; l < this.area.getWidth(); l++) {
						if (l % 2 == 0) {
							DrawableHelper.fill(
								matrices, this.area.getX() + l, this.area.getY() + this.area.getHeight(), this.area.getX() + l + 1, this.area.getY() + this.area.getHeight() + 1, -1
							);
						}
					}
				}
			}

			boolean bl5 = false;

			for (int m = 0; m < i; m++) {
				Suggestion suggestion = (Suggestion)this.suggestions.get(m + this.inWindowIndex);
				DrawableHelper.fill(
					matrices,
					this.area.getX(),
					this.area.getY() + 12 * m,
					this.area.getX() + this.area.getWidth(),
					this.area.getY() + 12 * m + 12,
					CommandSuggestor.this.color
				);
				if (mouseX > this.area.getX()
					&& mouseX < this.area.getX() + this.area.getWidth()
					&& mouseY > this.area.getY() + 12 * m
					&& mouseY < this.area.getY() + 12 * m + 12) {
					if (bl4) {
						this.select(m + this.inWindowIndex);
					}

					bl5 = true;
				}

				CommandSuggestor.this.textRenderer
					.drawWithShadow(
						matrices,
						suggestion.getText(),
						(float)(this.area.getX() + 1),
						(float)(this.area.getY() + 2 + 12 * m),
						m + this.inWindowIndex == this.selection ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.suggestions.get(this.selection)).getTooltip();
				if (message != null) {
					CommandSuggestor.this.owner.renderTooltip(matrices, Texts.toText(message), mouseX, mouseY);
				}
			}
		}

		public boolean mouseClicked(int x, int y, int button) {
			if (!this.area.contains(x, y)) {
				return false;
			} else {
				int i = (y - this.area.getY()) / 12 + this.inWindowIndex;
				if (i >= 0 && i < this.suggestions.size()) {
					this.select(i);
					this.complete();
				}

				return true;
			}
		}

		public boolean mouseScrolled(double amount) {
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
					(int)((double)this.inWindowIndex - amount), 0, Math.max(this.suggestions.size() - CommandSuggestor.this.maxSuggestionSize, 0)
				);
				return true;
			} else {
				return false;
			}
		}

		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (keyCode == 265) {
				this.scroll(-1);
				this.completed = false;
				return true;
			} else if (keyCode == 264) {
				this.scroll(1);
				this.completed = false;
				return true;
			} else if (keyCode == 258) {
				if (this.completed) {
					this.scroll(Screen.hasShiftDown() ? -1 : 1);
				}

				this.complete();
				return true;
			} else if (keyCode == 256) {
				this.discard();
				return true;
			} else {
				return false;
			}
		}

		public void scroll(int offset) {
			this.select(this.selection + offset);
			int i = this.inWindowIndex;
			int j = this.inWindowIndex + CommandSuggestor.this.maxSuggestionSize - 1;
			if (this.selection < i) {
				this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.suggestions.size() - CommandSuggestor.this.maxSuggestionSize, 0));
			} else if (this.selection > j) {
				this.inWindowIndex = MathHelper.clamp(
					this.selection + CommandSuggestor.this.inWindowIndexOffset - CommandSuggestor.this.maxSuggestionSize,
					0,
					Math.max(this.suggestions.size() - CommandSuggestor.this.maxSuggestionSize, 0)
				);
			}
		}

		public void select(int index) {
			this.selection = index;
			if (this.selection < 0) {
				this.selection = this.selection + this.suggestions.size();
			}

			if (this.selection >= this.suggestions.size()) {
				this.selection = this.selection - this.suggestions.size();
			}

			Suggestion suggestion = (Suggestion)this.suggestions.get(this.selection);
			CommandSuggestor.this.textField
				.setSuggestion(CommandSuggestor.getSuggestionSuffix(CommandSuggestor.this.textField.getText(), suggestion.apply(this.typedText)));
			if (this.lastNarrationIndex != this.selection) {
				NarratorManager.INSTANCE.narrate(this.getNarration());
			}
		}

		public void complete() {
			Suggestion suggestion = (Suggestion)this.suggestions.get(this.selection);
			CommandSuggestor.this.completingSuggestions = true;
			CommandSuggestor.this.textField.setText(suggestion.apply(this.typedText));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			CommandSuggestor.this.textField.setSelectionStart(i);
			CommandSuggestor.this.textField.setSelectionEnd(i);
			this.select(this.selection);
			CommandSuggestor.this.completingSuggestions = false;
			this.completed = true;
		}

		Text getNarration() {
			this.lastNarrationIndex = this.selection;
			Suggestion suggestion = (Suggestion)this.suggestions.get(this.selection);
			Message message = suggestion.getTooltip();
			return message != null
				? new TranslatableText("narration.suggestion.tooltip", this.selection + 1, this.suggestions.size(), suggestion.getText(), message)
				: new TranslatableText("narration.suggestion", this.selection + 1, this.suggestions.size(), suggestion.getText());
		}

		public void discard() {
			CommandSuggestor.this.window = null;
		}
	}
}
