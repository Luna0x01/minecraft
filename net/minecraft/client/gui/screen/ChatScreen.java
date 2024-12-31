package net.minecraft.client.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ChatScreen extends Screen {
	private String field_2389 = "";
	private int messageHistorySize = -1;
	protected TextFieldWidget chatField;
	private String originalChatText = "";
	private CommandSuggestor commandSuggestor;

	public ChatScreen(String string) {
		super(NarratorManager.EMPTY);
		this.originalChatText = string;
	}

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.messageHistorySize = this.minecraft.inGameHud.getChatHud().getMessageHistory().size();
		this.chatField = new TextFieldWidget(this.font, 4, this.height - 12, this.width - 4, 12, I18n.translate("chat.editBox")) {
			@Override
			protected String getNarrationMessage() {
				return super.getNarrationMessage() + ChatScreen.this.commandSuggestor.method_23958();
			}
		};
		this.chatField.setMaxLength(256);
		this.chatField.setHasBorder(false);
		this.chatField.setText(this.originalChatText);
		this.chatField.setChangedListener(this::onChatFieldUpdate);
		this.children.add(this.chatField);
		this.commandSuggestor = new CommandSuggestor(this.minecraft, this, this.chatField, this.font, false, false, 1, 10, true, -805306368);
		this.commandSuggestor.refresh();
		this.setInitialFocus(this.chatField);
	}

	@Override
	public void resize(MinecraftClient minecraftClient, int i, int j) {
		String string = this.chatField.getText();
		this.init(minecraftClient, i, j);
		this.setText(string);
		this.commandSuggestor.refresh();
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
		this.minecraft.inGameHud.getChatHud().resetScroll();
	}

	@Override
	public void tick() {
		this.chatField.tick();
	}

	private void onChatFieldUpdate(String string) {
		String string2 = this.chatField.getText();
		this.commandSuggestor.setWindowActive(!string2.equals(this.originalChatText));
		this.commandSuggestor.refresh();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.commandSuggestor.keyPressed(i, j, k)) {
			return true;
		} else if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i == 256) {
			this.minecraft.openScreen(null);
			return true;
		} else if (i == 257 || i == 335) {
			String string = this.chatField.getText().trim();
			if (!string.isEmpty()) {
				this.sendMessage(string);
			}

			this.minecraft.openScreen(null);
			return true;
		} else if (i == 265) {
			this.setChatFromHistory(-1);
			return true;
		} else if (i == 264) {
			this.setChatFromHistory(1);
			return true;
		} else if (i == 266) {
			this.minecraft.inGameHud.getChatHud().scroll((double)(this.minecraft.inGameHud.getChatHud().getVisibleLineCount() - 1));
			return true;
		} else if (i == 267) {
			this.minecraft.inGameHud.getChatHud().scroll((double)(-this.minecraft.inGameHud.getChatHud().getVisibleLineCount() + 1));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		if (f > 1.0) {
			f = 1.0;
		}

		if (f < -1.0) {
			f = -1.0;
		}

		if (this.commandSuggestor.mouseScrolled(f)) {
			return true;
		} else {
			if (!hasShiftDown()) {
				f *= 7.0;
			}

			this.minecraft.inGameHud.getChatHud().scroll(f);
			return true;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.commandSuggestor.mouseClicked((double)((int)d), (double)((int)e), i)) {
			return true;
		} else {
			if (i == 0) {
				Text text = this.minecraft.inGameHud.getChatHud().getText(d, e);
				if (text != null && this.handleComponentClicked(text)) {
					return true;
				}
			}

			return this.chatField.mouseClicked(d, e, i) ? true : super.mouseClicked(d, e, i);
		}
	}

	@Override
	protected void insertText(String string, boolean bl) {
		if (bl) {
			this.chatField.setText(string);
		} else {
			this.chatField.write(string);
		}
	}

	public void setChatFromHistory(int i) {
		int j = this.messageHistorySize + i;
		int k = this.minecraft.inGameHud.getChatHud().getMessageHistory().size();
		j = MathHelper.clamp(j, 0, k);
		if (j != this.messageHistorySize) {
			if (j == k) {
				this.messageHistorySize = k;
				this.chatField.setText(this.field_2389);
			} else {
				if (this.messageHistorySize == k) {
					this.field_2389 = this.chatField.getText();
				}

				this.chatField.setText((String)this.minecraft.inGameHud.getChatHud().getMessageHistory().get(j));
				this.commandSuggestor.setWindowActive(false);
				this.messageHistorySize = j;
			}
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.setFocused(this.chatField);
		this.chatField.setSelected(true);
		fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getTextBackgroundColor(Integer.MIN_VALUE));
		this.chatField.render(i, j, f);
		this.commandSuggestor.render(i, j);
		Text text = this.minecraft.inGameHud.getChatHud().getText((double)i, (double)j);
		if (text != null && text.getStyle().getHoverEvent() != null) {
			this.renderComponentHoverEffect(text, i, j);
		}

		super.render(i, j, f);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void setText(String string) {
		this.chatField.setText(string);
	}
}
