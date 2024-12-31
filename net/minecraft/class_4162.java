package net.minecraft;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.CommandBlockExecutor;

public abstract class class_4162 extends Screen {
	protected TextFieldWidget field_20357;
	protected TextFieldWidget field_20358;
	protected ButtonWidget field_20359;
	protected ButtonWidget field_20360;
	protected ButtonWidget field_20361;
	protected boolean field_20362;
	protected final List<String> field_20363 = Lists.newArrayList();
	protected int field_20364;
	protected int field_20365;
	protected ParseResults<class_3965> field_20366;
	protected CompletableFuture<Suggestions> field_20367;
	protected class_4162.class_4163 field_20368;
	private boolean field_20369;

	@Override
	public void tick() {
		this.field_20357.tick();
	}

	abstract CommandBlockExecutor method_18663();

	abstract int method_18664();

	@Override
	protected void init() {
		this.client.field_19946.method_18191(true);
		this.field_20359 = this.addButton(new ButtonWidget(0, this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				class_4162.this.method_18666();
			}
		});
		this.field_20360 = this.addButton(new ButtonWidget(1, this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				class_4162.this.method_18667();
			}
		});
		this.field_20361 = this.addButton(new ButtonWidget(4, this.width / 2 + 150 - 20, this.method_18664(), 20, 20, "O") {
			@Override
			public void method_18374(double d, double e) {
				CommandBlockExecutor commandBlockExecutor = class_4162.this.method_18663();
				commandBlockExecutor.setTrackOutput(!commandBlockExecutor.isTrackingOutput());
				class_4162.this.method_18665();
			}
		});
		this.field_20357 = new TextFieldWidget(2, this.textRenderer, this.width / 2 - 150, 50, 300, 20) {
			@Override
			public void setFocused(boolean focused) {
				super.setFocused(focused);
				if (focused) {
					class_4162.this.field_20358.setFocused(false);
				}
			}
		};
		this.field_20357.setMaxLength(32500);
		this.field_20357.method_18388(this::method_18654);
		this.field_20357.method_18387(this::method_18648);
		this.field_20307.add(this.field_20357);
		this.field_20358 = new TextFieldWidget(3, this.textRenderer, this.width / 2 - 150, this.method_18664(), 276, 20) {
			@Override
			public void setFocused(boolean focused) {
				super.setFocused(focused);
				if (focused) {
					class_4162.this.field_20357.setFocused(false);
				}
			}
		};
		this.field_20358.setMaxLength(32500);
		this.field_20358.setEditable(false);
		this.field_20358.setText("-");
		this.field_20307.add(this.field_20358);
		this.field_20357.setFocused(true);
		this.method_18421(this.field_20357);
		this.method_18668();
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.field_20357.getText();
		this.init(client, width, height);
		this.method_18653(string);
		this.method_18668();
	}

	protected void method_18665() {
		if (this.method_18663().isTrackingOutput()) {
			this.field_20361.message = "O";
			this.field_20358.setText(this.method_18663().getLastOutput().getString());
		} else {
			this.field_20361.message = "X";
			this.field_20358.setText("-");
		}
	}

	protected void method_18666() {
		CommandBlockExecutor commandBlockExecutor = this.method_18663();
		this.method_18650(commandBlockExecutor);
		if (!commandBlockExecutor.isTrackingOutput()) {
			commandBlockExecutor.setLastOutput(null);
		}

		this.client.setScreen(null);
	}

	@Override
	public void removed() {
		this.client.field_19946.method_18191(false);
	}

	protected abstract void method_18650(CommandBlockExecutor commandBlockExecutor);

	protected void method_18667() {
		this.method_18663().setTrackOutput(this.field_20362);
		this.client.setScreen(null);
	}

	@Override
	public void method_18608() {
		this.method_18667();
	}

	private void method_18648(int i, String string) {
		this.method_18668();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 257 || i == 335) {
			this.method_18666();
			return true;
		} else if (this.field_20368 != null && this.field_20368.method_18679(i, j, k)) {
			return true;
		} else {
			if (i == 258) {
				this.method_18669();
			}

			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public boolean mouseScrolled(double d) {
		return this.field_20368 != null && this.field_20368.method_18673(MathHelper.clamp(d, -1.0, 1.0)) ? true : super.mouseScrolled(d);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.field_20368 != null && this.field_20368.method_18676((int)d, (int)e, i) ? true : super.mouseClicked(d, e, i);
	}

	protected void method_18668() {
		this.field_20366 = null;
		if (!this.field_20369) {
			this.field_20357.method_18390(null);
			this.field_20368 = null;
		}

		this.field_20363.clear();
		CommandDispatcher<class_3965> commandDispatcher = this.client.player.networkHandler.method_18963();
		String string = this.field_20357.getText();
		StringReader stringReader = new StringReader(string);
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		this.field_20366 = commandDispatcher.parse(stringReader, this.client.player.networkHandler.method_18961());
		if (this.field_20368 == null || !this.field_20369) {
			StringReader stringReader2 = new StringReader(string.substring(0, Math.min(string.length(), this.field_20357.getCursor())));
			if (stringReader2.canRead() && stringReader2.peek() == '/') {
				stringReader2.skip();
			}

			ParseResults<class_3965> parseResults = commandDispatcher.parse(stringReader2, this.client.player.networkHandler.method_18961());
			this.field_20367 = commandDispatcher.getCompletionSuggestions(parseResults);
			this.field_20367.thenRun(() -> {
				if (this.field_20367.isDone()) {
					this.method_18670();
				}
			});
		}
	}

	private void method_18670() {
		if (((Suggestions)this.field_20367.join()).isEmpty()
			&& !this.field_20366.getExceptions().isEmpty()
			&& this.field_20357.getCursor() == this.field_20357.getText().length()) {
			int i = 0;

			for (Entry<CommandNode<class_3965>, CommandSyntaxException> entry : this.field_20366.getExceptions().entrySet()) {
				CommandSyntaxException commandSyntaxException = (CommandSyntaxException)entry.getValue();
				if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
					i++;
				} else {
					this.field_20363.add(commandSyntaxException.getMessage());
				}
			}

			if (i > 0) {
				this.field_20363.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
			}
		}

		this.field_20364 = 0;
		this.field_20365 = this.width;
		if (this.field_20363.isEmpty()) {
			this.method_18649(Formatting.GRAY);
		}

		this.field_20368 = null;
		if (this.client.options.field_19978) {
			this.method_18669();
		}
	}

	private String method_18654(String string, int i) {
		return this.field_20366 != null ? ChatScreen.method_18536(this.field_20366, string, i) : string;
	}

	private void method_18649(Formatting formatting) {
		CommandContextBuilder<class_3965> commandContextBuilder = this.field_20366.getContext();
		CommandContextBuilder<class_3965> commandContextBuilder2 = commandContextBuilder.getLastChild();
		if (!commandContextBuilder2.getNodes().isEmpty()) {
			CommandNode<class_3965> commandNode;
			int i;
			if (this.field_20366.getReader().canRead()) {
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
				this.field_20363.addAll(list);
				this.field_20364 = MathHelper.clamp(
					this.field_20357.method_18392(i) + this.textRenderer.getStringWidth(" "),
					0,
					this.field_20357.method_18392(0) + this.textRenderer.getStringWidth(" ") + this.field_20357.getInnerWidth() - m
				);
				this.field_20365 = m;
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("advMode.setCommand"), this.width / 2, 20, 16777215);
		this.drawWithShadow(this.textRenderer, I18n.translate("advMode.command"), this.width / 2 - 150, 40, 10526880);
		this.field_20357.method_18385(mouseX, mouseY, tickDelta);
		int i = 75;
		if (!this.field_20358.getText().isEmpty()) {
			i += 5 * this.textRenderer.fontHeight + 1 + this.method_18664() - 135;
			this.drawWithShadow(this.textRenderer, I18n.translate("advMode.previousOutput"), this.width / 2 - 150, i + 4, 10526880);
			this.field_20358.method_18385(mouseX, mouseY, tickDelta);
		}

		super.render(mouseX, mouseY, tickDelta);
		if (this.field_20368 != null) {
			this.field_20368.method_18675(mouseX, mouseY);
		} else {
			i = 0;

			for (String string : this.field_20363) {
				fill(this.field_20364 - 1, 72 + 12 * i, this.field_20364 + this.field_20365 + 1, 84 + 12 * i, Integer.MIN_VALUE);
				this.textRenderer.drawWithShadow(string, (float)this.field_20364, (float)(74 + 12 * i), -1);
				i++;
			}
		}
	}

	public void method_18669() {
		if (this.field_20367 != null && this.field_20367.isDone()) {
			Suggestions suggestions = (Suggestions)this.field_20367.join();
			if (!suggestions.isEmpty()) {
				int i = 0;

				for (Suggestion suggestion : suggestions.getList()) {
					i = Math.max(i, this.textRenderer.getStringWidth(suggestion.getText()));
				}

				int j = MathHelper.clamp(
					this.field_20357.method_18392(suggestions.getRange().getStart()) + this.textRenderer.getStringWidth(" "),
					0,
					this.field_20357.method_18392(0) + this.textRenderer.getStringWidth(" ") + this.field_20357.getInnerWidth() - i
				);
				this.field_20368 = new class_4162.class_4163(j, 72, i, suggestions);
			}
		}
	}

	protected void method_18653(String string) {
		this.field_20357.setText(string);
	}

	@Nullable
	private static String method_18657(String string, String string2) {
		return string2.startsWith(string) ? string2.substring(string.length()) : null;
	}

	class class_4163 {
		private final class_4228 field_20376;
		private final Suggestions field_20377;
		private final String field_20378;
		private int field_20379;
		private int field_20380;
		private Vec2f field_20381 = Vec2f.ZERO;
		private boolean field_20382;

		private class_4163(int i, int j, int k, Suggestions suggestions) {
			this.field_20376 = new class_4228(i - 1, j, k + 1, Math.min(suggestions.getList().size(), 7) * 12);
			this.field_20377 = suggestions;
			this.field_20378 = class_4162.this.field_20357.getText();
			this.method_18678(0);
		}

		public void method_18675(int i, int j) {
			int k = Math.min(this.field_20377.getList().size(), 7);
			int l = Integer.MIN_VALUE;
			int m = -5592406;
			boolean bl = this.field_20379 > 0;
			boolean bl2 = this.field_20377.getList().size() > this.field_20379 + k;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.field_20381.x != (float)i || this.field_20381.y != (float)j;
			if (bl4) {
				this.field_20381 = new Vec2f((float)i, (float)j);
			}

			if (bl3) {
				DrawableHelper.fill(
					this.field_20376.method_19177(),
					this.field_20376.method_19178() - 1,
					this.field_20376.method_19177() + this.field_20376.method_19180(),
					this.field_20376.method_19178(),
					Integer.MIN_VALUE
				);
				DrawableHelper.fill(
					this.field_20376.method_19177(),
					this.field_20376.method_19178() + this.field_20376.method_19181(),
					this.field_20376.method_19177() + this.field_20376.method_19180(),
					this.field_20376.method_19178() + this.field_20376.method_19181() + 1,
					Integer.MIN_VALUE
				);
				if (bl) {
					for (int n = 0; n < this.field_20376.method_19180(); n++) {
						if (n % 2 == 0) {
							DrawableHelper.fill(
								this.field_20376.method_19177() + n, this.field_20376.method_19178() - 1, this.field_20376.method_19177() + n + 1, this.field_20376.method_19178(), -1
							);
						}
					}
				}

				if (bl2) {
					for (int o = 0; o < this.field_20376.method_19180(); o++) {
						if (o % 2 == 0) {
							DrawableHelper.fill(
								this.field_20376.method_19177() + o,
								this.field_20376.method_19178() + this.field_20376.method_19181(),
								this.field_20376.method_19177() + o + 1,
								this.field_20376.method_19178() + this.field_20376.method_19181() + 1,
								-1
							);
						}
					}
				}
			}

			boolean bl5 = false;

			for (int p = 0; p < k; p++) {
				Suggestion suggestion = (Suggestion)this.field_20377.getList().get(p + this.field_20379);
				DrawableHelper.fill(
					this.field_20376.method_19177(),
					this.field_20376.method_19178() + 12 * p,
					this.field_20376.method_19177() + this.field_20376.method_19180(),
					this.field_20376.method_19178() + 12 * p + 12,
					Integer.MIN_VALUE
				);
				if (i > this.field_20376.method_19177()
					&& i < this.field_20376.method_19177() + this.field_20376.method_19180()
					&& j > this.field_20376.method_19178() + 12 * p
					&& j < this.field_20376.method_19178() + 12 * p + 12) {
					if (bl4) {
						this.method_18678(p + this.field_20379);
					}

					bl5 = true;
				}

				class_4162.this.textRenderer
					.drawWithShadow(
						suggestion.getText(),
						(float)(this.field_20376.method_19177() + 1),
						(float)(this.field_20376.method_19178() + 2 + 12 * p),
						p + this.field_20379 == this.field_20380 ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.field_20377.getList().get(this.field_20380)).getTooltip();
				if (message != null) {
					class_4162.this.renderTooltip(ChatSerializer.method_20187(message).asFormattedString(), i, j);
				}
			}
		}

		public boolean method_18676(int i, int j, int k) {
			if (!this.field_20376.method_19179(i, j)) {
				return false;
			} else {
				int l = (j - this.field_20376.method_19178()) / 12 + this.field_20379;
				if (l >= 0 && l < this.field_20377.getList().size()) {
					this.method_18678(l);
					this.method_18672();
				}

				return true;
			}
		}

		public boolean method_18673(double d) {
			int i = (int)(
				class_4162.this.client.field_19945.method_18249()
					* (double)class_4162.this.client.field_19944.method_18321()
					/ (double)class_4162.this.client.field_19944.method_18319()
			);
			int j = (int)(
				class_4162.this.client.field_19945.method_18250()
					* (double)class_4162.this.client.field_19944.method_18322()
					/ (double)class_4162.this.client.field_19944.method_18320()
			);
			if (this.field_20376.method_19179(i, j)) {
				this.field_20379 = MathHelper.clamp((int)((double)this.field_20379 - d), 0, Math.max(this.field_20377.getList().size() - 7, 0));
				return true;
			} else {
				return false;
			}
		}

		public boolean method_18679(int i, int j, int k) {
			if (i == 265) {
				this.method_18674(-1);
				this.field_20382 = false;
				return true;
			} else if (i == 264) {
				this.method_18674(1);
				this.field_20382 = false;
				return true;
			} else if (i == 258) {
				if (this.field_20382) {
					this.method_18674(Screen.hasShiftDown() ? -1 : 1);
				}

				this.method_18672();
				return true;
			} else if (i == 256) {
				this.method_18677();
				return true;
			} else {
				return false;
			}
		}

		public void method_18674(int i) {
			this.method_18678(this.field_20380 + i);
			int j = this.field_20379;
			int k = this.field_20379 + 7 - 1;
			if (this.field_20380 < j) {
				this.field_20379 = MathHelper.clamp(this.field_20380, 0, Math.max(this.field_20377.getList().size() - 7, 0));
			} else if (this.field_20380 > k) {
				this.field_20379 = MathHelper.clamp(this.field_20380 - 7, 0, Math.max(this.field_20377.getList().size() - 7, 0));
			}
		}

		public void method_18678(int i) {
			this.field_20380 = i;
			if (this.field_20380 < 0) {
				this.field_20380 = this.field_20380 + this.field_20377.getList().size();
			}

			if (this.field_20380 >= this.field_20377.getList().size()) {
				this.field_20380 = this.field_20380 - this.field_20377.getList().size();
			}

			Suggestion suggestion = (Suggestion)this.field_20377.getList().get(this.field_20380);
			class_4162.this.field_20357.method_18390(class_4162.method_18657(class_4162.this.field_20357.getText(), suggestion.apply(this.field_20378)));
		}

		public void method_18672() {
			Suggestion suggestion = (Suggestion)this.field_20377.getList().get(this.field_20380);
			class_4162.this.field_20369 = true;
			class_4162.this.method_18653(suggestion.apply(this.field_20378));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			class_4162.this.field_20357.method_18391(i);
			class_4162.this.field_20357.setSelectionEnd(i);
			this.method_18678(this.field_20380);
			class_4162.this.field_20369 = false;
			this.field_20382 = true;
		}

		public void method_18677() {
			class_4162.this.field_20368 = null;
		}
	}
}
