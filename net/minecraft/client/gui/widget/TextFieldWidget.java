package net.minecraft.client.gui.widget;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;

public class TextFieldWidget extends DrawableHelper {
	private final int id;
	private final TextRenderer textRenderer;
	public int x;
	public int y;
	private final int width;
	private final int height;
	private String text = "";
	private int maxLength = 32;
	private int focusedTicks;
	private boolean hasBorder = true;
	private boolean focusUnlocked = true;
	private boolean focused;
	private boolean editable = true;
	private int firstCharacterIndex;
	private int selectionStart;
	private int selectionEnd;
	private int editableColor = 14737632;
	private int uneditableColor = 7368816;
	private boolean visible = true;
	private PagedEntryListWidget.Listener updateListener;
	private Predicate<String> textPredicate = Predicates.alwaysTrue();

	public TextFieldWidget(int i, TextRenderer textRenderer, int j, int k, int l, int m) {
		this.id = i;
		this.textRenderer = textRenderer;
		this.x = j;
		this.y = k;
		this.width = l;
		this.height = m;
	}

	public void setListener(PagedEntryListWidget.Listener listener) {
		this.updateListener = listener;
	}

	public void tick() {
		this.focusedTicks++;
	}

	public void setText(String text) {
		if (this.textPredicate.apply(text)) {
			if (text.length() > this.maxLength) {
				this.text = text.substring(0, this.maxLength);
			} else {
				this.text = text;
			}

			this.setCursorToEnd();
		}
	}

	public String getText() {
		return this.text;
	}

	public String getSelectedText() {
		int i = this.selectionStart < this.selectionEnd ? this.selectionStart : this.selectionEnd;
		int j = this.selectionStart < this.selectionEnd ? this.selectionEnd : this.selectionStart;
		return this.text.substring(i, j);
	}

	public void setTextPredicate(Predicate<String> textPredicate) {
		this.textPredicate = textPredicate;
	}

	public void write(String text) {
		String string = "";
		String string2 = SharedConstants.stripInvalidChars(text);
		int i = this.selectionStart < this.selectionEnd ? this.selectionStart : this.selectionEnd;
		int j = this.selectionStart < this.selectionEnd ? this.selectionEnd : this.selectionStart;
		int k = this.maxLength - this.text.length() - (i - j);
		if (!this.text.isEmpty()) {
			string = string + this.text.substring(0, i);
		}

		int l;
		if (k < string2.length()) {
			string = string + string2.substring(0, k);
			l = k;
		} else {
			string = string + string2;
			l = string2.length();
		}

		if (!this.text.isEmpty() && j < this.text.length()) {
			string = string + this.text.substring(j);
		}

		if (this.textPredicate.apply(string)) {
			this.text = string;
			this.moveCursor(i - this.selectionEnd + l);
			this.method_13838(this.id, this.text);
		}
	}

	public void method_13838(int i, String string) {
		if (this.updateListener != null) {
			this.updateListener.setStringValue(i, string);
		}
	}

	public void eraseWords(int wordOffset) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.selectionStart) {
				this.write("");
			} else {
				this.eraseCharacters(this.getWordSkipPosition(wordOffset) - this.selectionStart);
			}
		}
	}

	public void eraseCharacters(int characterOffset) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.selectionStart) {
				this.write("");
			} else {
				boolean bl = characterOffset < 0;
				int i = bl ? this.selectionStart + characterOffset : this.selectionStart;
				int j = bl ? this.selectionStart : this.selectionStart + characterOffset;
				String string = "";
				if (i >= 0) {
					string = this.text.substring(0, i);
				}

				if (j < this.text.length()) {
					string = string + this.text.substring(j);
				}

				if (this.textPredicate.apply(string)) {
					this.text = string;
					if (bl) {
						this.moveCursor(characterOffset);
					}

					this.method_13838(this.id, this.text);
				}
			}
		}
	}

	public int getId() {
		return this.id;
	}

	public int getWordSkipPosition(int wordOffset) {
		return this.getWordSkipPosition(wordOffset, this.getCursor());
	}

	public int getWordSkipPosition(int wordOffset, int cursorPosition) {
		return this.getWordSkipPosition(wordOffset, cursorPosition, true);
	}

	public int getWordSkipPosition(int wordOffset, int cursorPosition, boolean skipOverSpaces) {
		int i = cursorPosition;
		boolean bl = wordOffset < 0;
		int j = Math.abs(wordOffset);

		for (int k = 0; k < j; k++) {
			if (!bl) {
				int l = this.text.length();
				i = this.text.indexOf(32, i);
				if (i == -1) {
					i = l;
				} else {
					while (skipOverSpaces && i < l && this.text.charAt(i) == ' ') {
						i++;
					}
				}
			} else {
				while (skipOverSpaces && i > 0 && this.text.charAt(i - 1) == ' ') {
					i--;
				}

				while (i > 0 && this.text.charAt(i - 1) != ' ') {
					i--;
				}
			}
		}

		return i;
	}

	public void moveCursor(int offset) {
		this.setCursor(this.selectionEnd + offset);
	}

	public void setCursor(int cursor) {
		this.selectionStart = cursor;
		int i = this.text.length();
		this.selectionStart = MathHelper.clamp(this.selectionStart, 0, i);
		this.setSelectionEnd(this.selectionStart);
	}

	public void setCursorToStart() {
		this.setCursor(0);
	}

	public void setCursorToEnd() {
		this.setCursor(this.text.length());
	}

	public boolean keyPressed(char character, int code) {
		if (!this.focused) {
			return false;
		} else if (Screen.isSelectAll(code)) {
			this.setCursorToEnd();
			this.setSelectionEnd(0);
			return true;
		} else if (Screen.isCopy(code)) {
			Screen.setClipboard(this.getSelectedText());
			return true;
		} else if (Screen.isPaste(code)) {
			if (this.editable) {
				this.write(Screen.getClipboard());
			}

			return true;
		} else if (Screen.isCut(code)) {
			Screen.setClipboard(this.getSelectedText());
			if (this.editable) {
				this.write("");
			}

			return true;
		} else {
			switch (code) {
				case 14:
					if (Screen.hasControlDown()) {
						if (this.editable) {
							this.eraseWords(-1);
						}
					} else if (this.editable) {
						this.eraseCharacters(-1);
					}

					return true;
				case 199:
					if (Screen.hasShiftDown()) {
						this.setSelectionEnd(0);
					} else {
						this.setCursorToStart();
					}

					return true;
				case 203:
					if (Screen.hasShiftDown()) {
						if (Screen.hasControlDown()) {
							this.setSelectionEnd(this.getWordSkipPosition(-1, this.getSelectionEnd()));
						} else {
							this.setSelectionEnd(this.getSelectionEnd() - 1);
						}
					} else if (Screen.hasControlDown()) {
						this.setCursor(this.getWordSkipPosition(-1));
					} else {
						this.moveCursor(-1);
					}

					return true;
				case 205:
					if (Screen.hasShiftDown()) {
						if (Screen.hasControlDown()) {
							this.setSelectionEnd(this.getWordSkipPosition(1, this.getSelectionEnd()));
						} else {
							this.setSelectionEnd(this.getSelectionEnd() + 1);
						}
					} else if (Screen.hasControlDown()) {
						this.setCursor(this.getWordSkipPosition(1));
					} else {
						this.moveCursor(1);
					}

					return true;
				case 207:
					if (Screen.hasShiftDown()) {
						this.setSelectionEnd(this.text.length());
					} else {
						this.setCursorToEnd();
					}

					return true;
				case 211:
					if (Screen.hasControlDown()) {
						if (this.editable) {
							this.eraseWords(1);
						}
					} else if (this.editable) {
						this.eraseCharacters(1);
					}

					return true;
				default:
					if (SharedConstants.isValidChar(character)) {
						if (this.editable) {
							this.write(Character.toString(character));
						}

						return true;
					} else {
						return false;
					}
			}
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
		boolean bl = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
		if (this.focusUnlocked) {
			this.setFocused(bl);
		}

		if (this.focused && bl && button == 0) {
			int i = mouseX - this.x;
			if (this.hasBorder) {
				i -= 4;
			}

			String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
			this.setCursor(this.textRenderer.trimToWidth(string, i).length() + this.firstCharacterIndex);
		}
	}

	public void render() {
		if (this.isVisible()) {
			if (this.hasBorder()) {
				fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
				fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
			}

			int i = this.editable ? this.editableColor : this.uneditableColor;
			int j = this.selectionStart - this.firstCharacterIndex;
			int k = this.selectionEnd - this.firstCharacterIndex;
			String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
			boolean bl = j >= 0 && j <= string.length();
			boolean bl2 = this.focused && this.focusedTicks / 6 % 2 == 0 && bl;
			int l = this.hasBorder ? this.x + 4 : this.x;
			int m = this.hasBorder ? this.y + (this.height - 8) / 2 : this.y;
			int n = l;
			if (k > string.length()) {
				k = string.length();
			}

			if (!string.isEmpty()) {
				String string2 = bl ? string.substring(0, j) : string;
				n = this.textRenderer.drawWithShadow(string2, (float)l, (float)m, i);
			}

			boolean bl3 = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxLength();
			int o = n;
			if (!bl) {
				o = j > 0 ? l + this.width : l;
			} else if (bl3) {
				o = n - 1;
				n--;
			}

			if (!string.isEmpty() && bl && j < string.length()) {
				n = this.textRenderer.drawWithShadow(string.substring(j), (float)n, (float)m, i);
			}

			if (bl2) {
				if (bl3) {
					DrawableHelper.fill(o, m - 1, o + 1, m + 1 + this.textRenderer.fontHeight, -3092272);
				} else {
					this.textRenderer.drawWithShadow("_", (float)o, (float)m, i);
				}
			}

			if (k != j) {
				int p = l + this.textRenderer.getStringWidth(string.substring(0, k));
				this.renderSelection(o, m - 1, p - 1, m + 1 + this.textRenderer.fontHeight);
			}
		}
	}

	private void renderSelection(int x1, int y1, int x2, int y2) {
		if (x1 < x2) {
			int i = x1;
			x1 = x2;
			x2 = i;
		}

		if (y1 < y2) {
			int j = y1;
			y1 = y2;
			y2 = j;
		}

		if (x2 > this.x + this.width) {
			x2 = this.x + this.width;
		}

		if (x1 > this.x + this.width) {
			x1 = this.x + this.width;
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture();
		GlStateManager.enableColorLogic();
		GlStateManager.method_9807(GlStateManager.class_2868.OR_REVERSE);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex((double)x1, (double)y2, 0.0).next();
		bufferBuilder.vertex((double)x2, (double)y2, 0.0).next();
		bufferBuilder.vertex((double)x2, (double)y1, 0.0).next();
		bufferBuilder.vertex((double)x1, (double)y1, 0.0).next();
		tessellator.draw();
		GlStateManager.disableColorLogic();
		GlStateManager.enableTexture();
	}

	public void setMaxLength(int maximumLength) {
		this.maxLength = maximumLength;
		if (this.text.length() > maximumLength) {
			this.text = this.text.substring(0, maximumLength);
		}
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public int getCursor() {
		return this.selectionStart;
	}

	public boolean hasBorder() {
		return this.hasBorder;
	}

	public void setHasBorder(boolean hasBorder) {
		this.hasBorder = hasBorder;
	}

	public void setEditableColor(int color) {
		this.editableColor = color;
	}

	public void setUneditableColor(int color) {
		this.uneditableColor = color;
	}

	public void setFocused(boolean focused) {
		if (focused && !this.focused) {
			this.focusedTicks = 0;
		}

		this.focused = focused;
	}

	public boolean isFocused() {
		return this.focused;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public int getSelectionEnd() {
		return this.selectionEnd;
	}

	public int getInnerWidth() {
		return this.hasBorder() ? this.width - 8 : this.width;
	}

	public void setSelectionEnd(int index) {
		int i = this.text.length();
		if (index > i) {
			index = i;
		}

		if (index < 0) {
			index = 0;
		}

		this.selectionEnd = index;
		if (this.textRenderer != null) {
			if (this.firstCharacterIndex > i) {
				this.firstCharacterIndex = i;
			}

			int j = this.getInnerWidth();
			String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), j);
			int k = string.length() + this.firstCharacterIndex;
			if (index == this.firstCharacterIndex) {
				this.firstCharacterIndex = this.firstCharacterIndex - this.textRenderer.trimToWidth(this.text, j, true).length();
			}

			if (index > k) {
				this.firstCharacterIndex += index - k;
			} else if (index <= this.firstCharacterIndex) {
				this.firstCharacterIndex = this.firstCharacterIndex - (this.firstCharacterIndex - index);
			}

			this.firstCharacterIndex = MathHelper.clamp(this.firstCharacterIndex, 0, i);
		}
	}

	public void setFocusUnlocked(boolean focusUnlocked) {
		this.focusUnlocked = focusUnlocked;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
