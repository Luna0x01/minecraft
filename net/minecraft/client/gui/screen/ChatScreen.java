package net.minecraft.client.gui.screen;

import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2844;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ChatScreen extends Screen implements class_2844 {
	private static final Logger LOGGER = LogManager.getLogger();
	private String originalChatText = "";
	private int messageHistorySize = -1;
	private PathNodeMaker field_13320;
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
		this.chatField.setMaxLength(256);
		this.chatField.setHasBorder(false);
		this.chatField.setFocused(true);
		this.chatField.setText(this.lastChatFieldText);
		this.chatField.setFocusUnlocked(false);
		this.field_13320 = new ChatScreen.class_2843(this.chatField);
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
		this.field_13320.method_12188();
		if (code == 15) {
			this.field_13320.method_12183();
		} else {
			this.field_13320.method_12187();
		}

		if (code == 1) {
			this.client.setScreen(null);
		} else if (code == 28 || code == 156) {
			String string = this.chatField.getText().trim();
			if (!string.isEmpty()) {
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
			if (text != null && this.handleTextClick(text)) {
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

	@Override
	public boolean shouldPauseGame() {
		return false;
	}

	@Override
	public void method_12182(String... strings) {
		this.field_13320.method_12185(strings);
	}

	public static class class_2843 extends PathNodeMaker {
		private final MinecraftClient field_13321 = MinecraftClient.getInstance();

		public class_2843(TextFieldWidget textFieldWidget) {
			super(textFieldWidget, false);
		}

		@Override
		public void method_12183() {
			super.method_12183();
			if (this.field_13328.size() > 1) {
				StringBuilder stringBuilder = new StringBuilder();

				for (String string : this.field_13328) {
					if (stringBuilder.length() > 0) {
						stringBuilder.append(", ");
					}

					stringBuilder.append(string);
				}

				this.field_13321.inGameHud.getChatHud().addMessage(new LiteralText(stringBuilder.toString()), 1);
			}
		}

		@Nullable
		@Override
		public BlockPos method_12186() {
			BlockPos blockPos = null;
			if (this.field_13321.result != null && this.field_13321.result.type == BlockHitResult.Type.BLOCK) {
				blockPos = this.field_13321.result.getBlockPos();
			}

			return blockPos;
		}
	}
}
