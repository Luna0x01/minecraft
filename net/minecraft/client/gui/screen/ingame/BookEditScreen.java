package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import io.netty.buffer.Unpooled;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Texts;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class BookEditScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier TEXTURE = new Identifier("textures/gui/book.png");
	private final PlayerEntity reader;
	private final ItemStack item;
	private final boolean writeable;
	private boolean dirty;
	private boolean signing;
	private int tickCounter;
	private final int pageWidth = 192;
	private final int pageHeight = 192;
	private int totalPages = 1;
	private int currentPage;
	private NbtList pages;
	private String title = "";
	private List<Text> bookText;
	private int lastClickIndex = -1;
	private BookEditScreen.BookButton nextPageButton;
	private BookEditScreen.BookButton previousPageButton;
	private ButtonWidget doneButton;
	private ButtonWidget signButton;
	private ButtonWidget finalizeButton;
	private ButtonWidget cancelButton;

	public BookEditScreen(PlayerEntity playerEntity, ItemStack itemStack, boolean bl) {
		this.reader = playerEntity;
		this.item = itemStack;
		this.writeable = bl;
		if (itemStack.hasNbt()) {
			NbtCompound nbtCompound = itemStack.getNbt();
			this.pages = nbtCompound.getList("pages", 8).copy();
			this.totalPages = this.pages.size();
			if (this.totalPages < 1) {
				this.totalPages = 1;
			}
		}

		if (this.pages == null && bl) {
			this.pages = new NbtList();
			this.pages.add(new NbtString(""));
			this.totalPages = 1;
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.tickCounter++;
	}

	@Override
	public void init() {
		this.buttons.clear();
		Keyboard.enableRepeatEvents(true);
		if (this.writeable) {
			this.signButton = this.addButton(new ButtonWidget(3, this.width / 2 - 100, 196, 98, 20, I18n.translate("book.signButton")));
			this.doneButton = this.addButton(new ButtonWidget(0, this.width / 2 + 2, 196, 98, 20, I18n.translate("gui.done")));
			this.finalizeButton = this.addButton(new ButtonWidget(5, this.width / 2 - 100, 196, 98, 20, I18n.translate("book.finalizeButton")));
			this.cancelButton = this.addButton(new ButtonWidget(4, this.width / 2 + 2, 196, 98, 20, I18n.translate("gui.cancel")));
		} else {
			this.doneButton = this.addButton(new ButtonWidget(0, this.width / 2 - 100, 196, 200, 20, I18n.translate("gui.done")));
		}

		int i = (this.width - 192) / 2;
		int j = 2;
		this.nextPageButton = this.addButton(new BookEditScreen.BookButton(1, i + 120, 156, true));
		this.previousPageButton = this.addButton(new BookEditScreen.BookButton(2, i + 38, 156, false));
		this.updateButtons();
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	private void updateButtons() {
		this.nextPageButton.visible = !this.signing && (this.currentPage < this.totalPages - 1 || this.writeable);
		this.previousPageButton.visible = !this.signing && this.currentPage > 0;
		this.doneButton.visible = !this.writeable || !this.signing;
		if (this.writeable) {
			this.signButton.visible = !this.signing;
			this.cancelButton.visible = this.signing;
			this.finalizeButton.visible = this.signing;
			this.finalizeButton.active = !this.title.trim().isEmpty();
		}
	}

	private void finalizeBook(boolean signBook) {
		if (this.writeable && this.dirty) {
			if (this.pages != null) {
				while (this.pages.size() > 1) {
					String string = this.pages.getString(this.pages.size() - 1);
					if (!string.isEmpty()) {
						break;
					}

					this.pages.remove(this.pages.size() - 1);
				}

				if (this.item.hasNbt()) {
					NbtCompound nbtCompound = this.item.getNbt();
					nbtCompound.put("pages", this.pages);
				} else {
					this.item.putSubNbt("pages", this.pages);
				}

				String string2 = "MC|BEdit";
				if (signBook) {
					string2 = "MC|BSign";
					this.item.putSubNbt("author", new NbtString(this.reader.getTranslationKey()));
					this.item.putSubNbt("title", new NbtString(this.title.trim()));
				}

				PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
				packetByteBuf.writeItemStack(this.item);
				this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(string2, packetByteBuf));
			}
		}
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 0) {
				this.client.setScreen(null);
				this.finalizeBook(false);
			} else if (button.id == 3 && this.writeable) {
				this.signing = true;
			} else if (button.id == 1) {
				if (this.currentPage < this.totalPages - 1) {
					this.currentPage++;
				} else if (this.writeable) {
					this.appendNewPage();
					if (this.currentPage < this.totalPages - 1) {
						this.currentPage++;
					}
				}
			} else if (button.id == 2) {
				if (this.currentPage > 0) {
					this.currentPage--;
				}
			} else if (button.id == 5 && this.signing) {
				this.finalizeBook(true);
				this.client.setScreen(null);
			} else if (button.id == 4 && this.signing) {
				this.signing = false;
			}

			this.updateButtons();
		}
	}

	private void appendNewPage() {
		if (this.pages != null && this.pages.size() < 50) {
			this.pages.add(new NbtString(""));
			this.totalPages++;
			this.dirty = true;
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		super.keyPressed(id, code);
		if (this.writeable) {
			if (this.signing) {
				this.keyPressedSignMode(id, code);
			} else {
				this.keyPressedEditMode(id, code);
			}
		}
	}

	private void keyPressedEditMode(char character, int code) {
		if (Screen.isPaste(code)) {
			this.writeText(Screen.getClipboard());
		} else {
			switch (code) {
				case 14:
					String string = this.getCurrentPageContent();
					if (!string.isEmpty()) {
						this.setPageContent(string.substring(0, string.length() - 1));
					}

					return;
				case 28:
				case 156:
					this.writeText("\n");
					return;
				default:
					if (SharedConstants.isValidChar(character)) {
						this.writeText(Character.toString(character));
					}
			}
		}
	}

	private void keyPressedSignMode(char character, int code) {
		switch (code) {
			case 14:
				if (!this.title.isEmpty()) {
					this.title = this.title.substring(0, this.title.length() - 1);
					this.updateButtons();
				}

				return;
			case 28:
			case 156:
				if (!this.title.isEmpty()) {
					this.finalizeBook(true);
					this.client.setScreen(null);
				}

				return;
			default:
				if (this.title.length() < 16 && SharedConstants.isValidChar(character)) {
					this.title = this.title + Character.toString(character);
					this.updateButtons();
					this.dirty = true;
				}
		}
	}

	private String getCurrentPageContent() {
		return this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.size() ? this.pages.getString(this.currentPage) : "";
	}

	private void setPageContent(String newContent) {
		if (this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
			this.pages.set(this.currentPage, new NbtString(newContent));
			this.dirty = true;
		}
	}

	private void writeText(String text) {
		String string = this.getCurrentPageContent();
		String string2 = string + text;
		int i = this.textRenderer.getHeightSplit(string2 + "" + Formatting.BLACK + "_", 118);
		if (i <= 128 && string2.length() < 256) {
			this.setPageContent(string2);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - 192) / 2;
		int j = 2;
		this.drawTexture(i, 2, 0, 0, 192, 192);
		if (this.signing) {
			String string = this.title;
			if (this.writeable) {
				if (this.tickCounter / 6 % 2 == 0) {
					string = string + "" + Formatting.BLACK + "_";
				} else {
					string = string + "" + Formatting.GRAY + "_";
				}
			}

			String string2 = I18n.translate("book.editTitle");
			int k = this.textRenderer.getStringWidth(string2);
			this.textRenderer.draw(string2, i + 36 + (116 - k) / 2, 34, 0);
			int l = this.textRenderer.getStringWidth(string);
			this.textRenderer.draw(string, i + 36 + (116 - l) / 2, 50, 0);
			String string3 = I18n.translate("book.byAuthor", this.reader.getTranslationKey());
			int m = this.textRenderer.getStringWidth(string3);
			this.textRenderer.draw(Formatting.DARK_GRAY + string3, i + 36 + (116 - m) / 2, 60, 0);
			String string4 = I18n.translate("book.finalizeWarning");
			this.textRenderer.drawTrimmed(string4, i + 36, 82, 116, 0);
		} else {
			String string5 = I18n.translate("book.pageIndicator", this.currentPage + 1, this.totalPages);
			String string6 = "";
			if (this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
				string6 = this.pages.getString(this.currentPage);
			}

			if (this.writeable) {
				if (this.textRenderer.isRightToLeft()) {
					string6 = string6 + "_";
				} else if (this.tickCounter / 6 % 2 == 0) {
					string6 = string6 + "" + Formatting.BLACK + "_";
				} else {
					string6 = string6 + "" + Formatting.GRAY + "_";
				}
			} else if (this.lastClickIndex != this.currentPage) {
				if (WrittenBookItem.isValid(this.item.getNbt())) {
					try {
						Text text = Text.Serializer.deserializeText(string6);
						this.bookText = text != null ? Texts.wrapLines(text, 116, this.textRenderer, true, true) : null;
					} catch (JsonParseException var13) {
						this.bookText = null;
					}
				} else {
					LiteralText literalText = new LiteralText(Formatting.DARK_RED + "* Invalid book tag *");
					this.bookText = Lists.newArrayList(literalText);
				}

				this.lastClickIndex = this.currentPage;
			}

			int n = this.textRenderer.getStringWidth(string5);
			this.textRenderer.draw(string5, i - n + 192 - 44, 18, 0);
			if (this.bookText == null) {
				this.textRenderer.drawTrimmed(string6, i + 36, 34, 116, 0);
			} else {
				int o = Math.min(128 / this.textRenderer.fontHeight, this.bookText.size());

				for (int p = 0; p < o; p++) {
					Text text2 = (Text)this.bookText.get(p);
					this.textRenderer.draw(text2.asUnformattedString(), i + 36, 34 + p * this.textRenderer.fontHeight, 0);
				}

				Text text3 = this.getTextAt(mouseX, mouseY);
				if (text3 != null) {
					this.renderTextHoverEffect(text3, mouseX, mouseY);
				}
			}
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if (button == 0) {
			Text text = this.getTextAt(mouseX, mouseY);
			if (text != null && this.handleTextClick(text)) {
				return;
			}
		}

		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean handleTextClick(Text text) {
		ClickEvent clickEvent = text.getStyle().getClickEvent();
		if (clickEvent == null) {
			return false;
		} else if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
			String string = clickEvent.getValue();

			try {
				int i = Integer.parseInt(string) - 1;
				if (i >= 0 && i < this.totalPages && i != this.currentPage) {
					this.currentPage = i;
					this.updateButtons();
					return true;
				}
			} catch (Throwable var5) {
			}

			return false;
		} else {
			boolean bl = super.handleTextClick(text);
			if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				this.client.setScreen(null);
			}

			return bl;
		}
	}

	@Nullable
	public Text getTextAt(int x, int y) {
		if (this.bookText == null) {
			return null;
		} else {
			int i = x - (this.width - 192) / 2 - 36;
			int j = y - 2 - 16 - 16;
			if (i >= 0 && j >= 0) {
				int k = Math.min(128 / this.textRenderer.fontHeight, this.bookText.size());
				if (i <= 116 && j < this.client.textRenderer.fontHeight * k + k) {
					int l = j / this.client.textRenderer.fontHeight;
					if (l >= 0 && l < this.bookText.size()) {
						Text text = (Text)this.bookText.get(l);
						int m = 0;

						for (Text text2 : text) {
							if (text2 instanceof LiteralText) {
								m += this.client.textRenderer.getStringWidth(((LiteralText)text2).getRawString());
								if (m > i) {
									return text2;
								}
							}
						}
					}

					return null;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	static class BookButton extends ButtonWidget {
		private final boolean isRight;

		public BookButton(int i, int j, int k, boolean bl) {
			super(i, j, k, 23, 13, "");
			this.isRight = bl;
		}

		@Override
		public void method_891(MinecraftClient client, int i, int j, float f) {
			if (this.visible) {
				boolean bl = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				client.getTextureManager().bindTexture(BookEditScreen.TEXTURE);
				int k = 0;
				int l = 192;
				if (bl) {
					k += 23;
				}

				if (!this.isRight) {
					l += 13;
				}

				this.drawTexture(this.x, this.y, k, l, 23, 13);
			}
		}
	}
}
