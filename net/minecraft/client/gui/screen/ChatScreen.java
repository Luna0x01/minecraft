package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ChatScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private String originalChatText = "";
	private int messageHistorySize = -1;
	private boolean foundNames;
	private boolean waiting;
	private int currentSelection;
	private List<String> suggestions = Lists.newArrayList();
	protected TextFieldWidget chatField;
	private String lastChatFieldText = "";

	public ChatScreen() {
	}

	public ChatScreen(String string) {
		this.lastChatFieldText = string;
	}

	@Override
	public void init() {
		Keyboard.enableRepeatEvents(true);
		this.messageHistorySize = this.client.inGameHud.getChatHud().getMessageHistory().size();
		this.chatField = new TextFieldWidget(0, this.textRenderer, 4, this.height - 12, this.width - 4, 12);
		this.chatField.setMaxLength(100);
		this.chatField.setHasBorder(false);
		this.chatField.setFocused(true);
		this.chatField.setText(this.lastChatFieldText);
		this.chatField.setFocusUnlocked(false);
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
		this.client.inGameHud.getChatHud().resetScroll();
	}

	@Override
	public void tick() {
		this.chatField.tick();
	}

	@Override
	protected void keyPressed(char id, int code) {
		this.waiting = false;
		if (code == 15) {
			this.showSuggestion();
		} else {
			this.foundNames = false;
		}

		if (code == 1) {
			this.client.setScreen(null);
		} else if (code == 28 || code == 156) {
			String string = this.chatField.getText().trim();
			if (string.length() > 0) {
				this.sendMessage(string);
			}

			this.client.setScreen(null);
		} else if (code == 200) {
			this.setChatFromHistory(-1);
		} else if (code == 208) {
			this.setChatFromHistory(1);
		} else if (code == 201) {
			this.client.inGameHud.getChatHud().scroll(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1);
		} else if (code == 209) {
			this.client.inGameHud.getChatHud().scroll(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1);
		} else {
			this.chatField.keyPressed(id, code);
		}
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		int i = Mouse.getEventDWheel();
		if (i != 0) {
			if (i > 1) {
				i = 1;
			}

			if (i < -1) {
				i = -1;
			}

			if (!hasShiftDown()) {
				i *= 7;
			}

			this.client.inGameHud.getChatHud().scroll(i);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if (button == 0) {
			Text text = this.client.inGameHud.getChatHud().getTextAt(Mouse.getX(), Mouse.getY());
			if (this.handleTextClick(text)) {
				return;
			}
		}

		this.chatField.mouseClicked(mouseX, mouseY, button);
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void insertText(String text, boolean override) {
		if (override) {
			this.chatField.setText(text);
		} else {
			this.chatField.write(text);
		}
	}

	public void showSuggestion() {
		if (this.foundNames) {
			this.chatField.eraseCharacters(this.chatField.getWordSkipPosition(-1, this.chatField.getCursor(), false) - this.chatField.getCursor());
			if (this.currentSelection >= this.suggestions.size()) {
				this.currentSelection = 0;
			}
		} else {
			int i = this.chatField.getWordSkipPosition(-1, this.chatField.getCursor(), false);
			this.suggestions.clear();
			this.currentSelection = 0;
			String string = this.chatField.getText().substring(i).toLowerCase();
			String string2 = this.chatField.getText().substring(0, this.chatField.getCursor());
			this.requestAutocomplete(string2, string);
			if (this.suggestions.isEmpty()) {
				return;
			}

			this.foundNames = true;
			this.chatField.eraseCharacters(i - this.chatField.getCursor());
		}

		if (this.suggestions.size() > 1) {
			StringBuilder stringBuilder = new StringBuilder();

			for (String string3 : this.suggestions) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(", ");
				}

				stringBuilder.append(string3);
			}

			this.client.inGameHud.getChatHud().addMessage(new LiteralText(stringBuilder.toString()), 1);
		}

		this.chatField.write((String)this.suggestions.get(this.currentSelection++));
	}

	private void requestAutocomplete(String partialMessage, String nextWord) {
		if (partialMessage.length() >= 1) {
			BlockPos blockPos = null;
			if (this.client.result != null && this.client.result.type == BlockHitResult.Type.BLOCK) {
				blockPos = this.client.result.getBlockPos();
			}

			this.client.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(partialMessage, blockPos));
			this.waiting = true;
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
				this.messageHistorySize = i;
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		fill(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
		this.chatField.render();
		Text text = this.client.inGameHud.getChatHud().getTextAt(Mouse.getX(), Mouse.getY());
		if (text != null && text.getStyle().getHoverEvent() != null) {
			this.renderTextHoverEffect(text, mouseX, mouseY);
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	public void setSuggestions(String[] suggestions) {
		if (this.waiting) {
			this.foundNames = false;
			this.suggestions.clear();

			for (String string : suggestions) {
				if (string.length() > 0) {
					this.suggestions.add(string);
				}
			}

			String string2 = this.chatField.getText().substring(this.chatField.getWordSkipPosition(-1, this.chatField.getCursor(), false));
			String string3 = StringUtils.getCommonPrefix(suggestions);
			if (string3.length() > 0 && !string2.equalsIgnoreCase(string3)) {
				this.chatField.eraseCharacters(this.chatField.getWordSkipPosition(-1, this.chatField.getCursor(), false) - this.chatField.getCursor());
				this.chatField.write(string3);
			} else if (this.suggestions.size() > 0) {
				this.foundNames = true;
				this.showSuggestion();
			}
		}
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}
}
