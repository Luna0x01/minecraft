package net.minecraft.client.gui.widget;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_4122;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;

public class TextFieldWidget extends DrawableHelper implements class_4122 {
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
	private String field_20068;
	private BiConsumer<Integer, String> field_20069;
	private Predicate<String> field_20070 = Predicates.alwaysTrue();
	private BiFunction<String, Integer, String> field_20067 = (string, integer) -> string;

	public TextFieldWidget(int i, TextRenderer textRenderer, int j, int k, int l, int m) {
		this(i, textRenderer, j, k, l, m, null);
	}

	public TextFieldWidget(int i, TextRenderer textRenderer, int j, int k, int l, int m, @Nullable TextFieldWidget textFieldWidget) {
		this.id = i;
		this.textRenderer = textRenderer;
		this.x = j;
		this.y = k;
		this.width = l;
		this.height = m;
		if (textFieldWidget != null) {
			this.setText(textFieldWidget.getText());
		}
	}

	public void method_18387(BiConsumer<Integer, String> biConsumer) {
		this.field_20069 = biConsumer;
	}

	public void method_18388(BiFunction<String, Integer, String> biFunction) {
		this.field_20067 = biFunction;
	}

	public void tick() {
		this.focusedTicks++;
	}

	public void setText(String text) {
		if (this.field_20070.test(text)) {
			if (text.length() > this.maxLength) {
				this.text = text.substring(0, this.maxLength);
			} else {
				this.text = text;
			}

			this.method_13838(this.id, text);
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

	public void method_18389(Predicate<String> predicate) {
		this.field_20070 = predicate;
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

		if (this.field_20070.test(string)) {
			this.text = string;
			this.moveCursor(i - this.selectionEnd + l);
			this.method_13838(this.id, this.text);
		}
	}

	public void method_13838(int i, String string) {
		if (this.field_20069 != null) {
			this.field_20069.accept(i, string);
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

				if (this.field_20070.test(string)) {
					this.text = string;
					if (bl) {
						this.moveCursor(characterOffset);
					}

					this.method_13838(this.id, this.text);
				}
			}
		}
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
		this.method_18391(cursor);
		this.setSelectionEnd(this.selectionStart);
		this.method_13838(this.id, this.text);
	}

	public void method_18391(int i) {
		this.selectionStart = MathHelper.clamp(i, 0, this.text.length());
	}

	public void setCursorToStart() {
		this.setCursor(0);
	}

	public void setCursorToEnd() {
		this.setCursor(this.text.length());
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (!this.isVisible() || !this.isFocused()) {
			return false;
		} else if (Screen.isSelectAll(i)) {
			this.setCursorToEnd();
			this.setSelectionEnd(0);
			return true;
		} else if (Screen.isCopy(i)) {
			MinecraftClient.getInstance().field_19946.method_18187(this.getSelectedText());
			return true;
		} else if (Screen.isPaste(i)) {
			if (this.editable) {
				this.write(MinecraftClient.getInstance().field_19946.method_18177());
			}

			return true;
		} else if (Screen.isCut(i)) {
			MinecraftClient.getInstance().field_19946.method_18187(this.getSelectedText());
			if (this.editable) {
				this.write("");
			}

			return true;
		} else {
			switch (i) {
				case 259:
					if (Screen.hasControlDown()) {
						if (this.editable) {
							this.eraseWords(-1);
						}
					} else if (this.editable) {
						this.eraseCharacters(-1);
					}

					return true;
				case 260:
				case 264:
				case 265:
				case 266:
				case 267:
				default:
					return i != 256;
				case 261:
					if (Screen.hasControlDown()) {
						if (this.editable) {
							this.eraseWords(1);
						}
					} else if (this.editable) {
						this.eraseCharacters(1);
					}

					return true;
				case 262:
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
				case 263:
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
				case 268:
					if (Screen.hasShiftDown()) {
						this.setSelectionEnd(0);
					} else {
						this.setCursorToStart();
					}

					return true;
				case 269:
					if (Screen.hasShiftDown()) {
						this.setSelectionEnd(this.text.length());
					} else {
						this.setCursorToEnd();
					}

					return true;
			}
		}
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (!this.isVisible() || !this.isFocused()) {
			return false;
		} else if (SharedConstants.isValidChar(c)) {
			if (this.editable) {
				this.write(Character.toString(c));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (!this.isVisible()) {
			return false;
		} else {
			boolean bl = d >= (double)this.x && d < (double)(this.x + this.width) && e >= (double)this.y && e < (double)(this.y + this.height);
			if (this.focusUnlocked) {
				this.setFocused(bl);
			}

			if (this.focused && bl && i == 0) {
				int j = MathHelper.floor(d) - this.x;
				if (this.hasBorder) {
					j -= 4;
				}

				String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
				this.setCursor(this.textRenderer.trimToWidth(string, j).length() + this.firstCharacterIndex);
				return true;
			} else {
				return false;
			}
		}
	}

	public void method_18385(int i, int j, float f) {
		if (this.isVisible()) {
			if (this.hasBorder()) {
				fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
				fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
			}

			int k = this.editable ? this.editableColor : this.uneditableColor;
			int l = this.selectionStart - this.firstCharacterIndex;
			int m = this.selectionEnd - this.firstCharacterIndex;
			String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
			boolean bl = l >= 0 && l <= string.length();
			boolean bl2 = this.focused && this.focusedTicks / 6 % 2 == 0 && bl;
			int n = this.hasBorder ? this.x + 4 : this.x;
			int o = this.hasBorder ? this.y + (this.height - 8) / 2 : this.y;
			int p = n;
			if (m > string.length()) {
				m = string.length();
			}

			if (!string.isEmpty()) {
				String string2 = bl ? string.substring(0, l) : string;
				p = this.textRenderer.drawWithShadow((String)this.field_20067.apply(string2, this.firstCharacterIndex), (float)n, (float)o, k);
			}

			boolean bl3 = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxLength();
			int q = p;
			if (!bl) {
				q = l > 0 ? n + this.width : n;
			} else if (bl3) {
				q = p - 1;
				p--;
			}

			if (!string.isEmpty() && bl && l < string.length()) {
				p = this.textRenderer.drawWithShadow((String)this.field_20067.apply(string.substring(l), this.selectionStart), (float)p, (float)o, k);
			}

			if (!bl3 && this.field_20068 != null) {
				this.textRenderer.drawWithShadow(this.field_20068, (float)(q - 1), (float)o, -8355712);
			}

			if (bl2) {
				if (bl3) {
					DrawableHelper.fill(q, o - 1, q + 1, o + 1 + this.textRenderer.fontHeight, -3092272);
				} else {
					this.textRenderer.drawWithShadow("_", (float)q, (float)o, k);
				}
			}

			if (m != l) {
				int r = n + this.textRenderer.getStringWidth(string.substring(0, m));
				this.renderSelection(q, o - 1, r - 1, o + 1 + this.textRenderer.fontHeight);
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
			this.method_13838(this.id, this.text);
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

	@Override
	public void method_18428(boolean bl) {
		this.setFocused(bl);
	}

	@Override
	public boolean method_18427() {
		return true;
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

	public void method_18390(@Nullable String string) {
		this.field_20068 = string;
	}

	public int method_18392(int i) {
		return i > this.text.length() ? this.x : this.x + this.textRenderer.getStringWidth(this.text.substring(0, i));
	}
}
