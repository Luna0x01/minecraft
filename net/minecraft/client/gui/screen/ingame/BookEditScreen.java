package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_4385;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Texts;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	private final Hand field_20386;

	public BookEditScreen(PlayerEntity playerEntity, ItemStack itemStack, boolean bl, Hand hand) {
		this.reader = playerEntity;
		this.item = itemStack;
		this.writeable = bl;
		this.field_20386 = hand;
		if (itemStack.hasNbt()) {
			NbtCompound nbtCompound = itemStack.getNbt();
			this.pages = nbtCompound.getList("pages", 8).copy();
			this.totalPages = this.pages.size();
			if (this.totalPages < 1) {
				this.pages.add((NbtElement)(new NbtString("")));
				this.totalPages = 1;
			}
		}

		if (this.pages == null && bl) {
			this.pages = new NbtList();
			this.pages.add((NbtElement)(new NbtString("")));
			this.totalPages = 1;
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.tickCounter++;
	}

	@Override
	protected void init() {
		this.client.field_19946.method_18191(true);
		if (this.writeable) {
			this.signButton = this.addButton(new ButtonWidget(3, this.width / 2 - 100, 196, 98, 20, I18n.translate("book.signButton")) {
				@Override
				public void method_18374(double d, double e) {
					BookEditScreen.this.signing = true;
					BookEditScreen.this.updateButtons();
				}
			});
			this.doneButton = this.addButton(new ButtonWidget(0, this.width / 2 + 2, 196, 98, 20, I18n.translate("gui.done")) {
				@Override
				public void method_18374(double d, double e) {
					BookEditScreen.this.client.setScreen(null);
					BookEditScreen.this.finalizeBook(false);
				}
			});
			this.finalizeButton = this.addButton(new ButtonWidget(5, this.width / 2 - 100, 196, 98, 20, I18n.translate("book.finalizeButton")) {
				@Override
				public void method_18374(double d, double e) {
					if (BookEditScreen.this.signing) {
						BookEditScreen.this.finalizeBook(true);
						BookEditScreen.this.client.setScreen(null);
					}
				}
			});
			this.cancelButton = this.addButton(new ButtonWidget(4, this.width / 2 + 2, 196, 98, 20, I18n.translate("gui.cancel")) {
				@Override
				public void method_18374(double d, double e) {
					if (BookEditScreen.this.signing) {
						BookEditScreen.this.signing = false;
					}

					BookEditScreen.this.updateButtons();
				}
			});
		} else {
			this.doneButton = this.addButton(new ButtonWidget(0, this.width / 2 - 100, 196, 200, 20, I18n.translate("gui.done")) {
				@Override
				public void method_18374(double d, double e) {
					BookEditScreen.this.client.setScreen(null);
					BookEditScreen.this.finalizeBook(false);
				}
			});
		}

		int i = (this.width - 192) / 2;
		int j = 2;
		this.nextPageButton = this.addButton(new BookEditScreen.BookButton(1, i + 120, 156, true) {
			@Override
			public void method_18374(double d, double e) {
				if (BookEditScreen.this.currentPage < BookEditScreen.this.totalPages - 1) {
					BookEditScreen.this.currentPage++;
				} else if (BookEditScreen.this.writeable) {
					BookEditScreen.this.appendNewPage();
					if (BookEditScreen.this.currentPage < BookEditScreen.this.totalPages - 1) {
						BookEditScreen.this.currentPage++;
					}
				}

				BookEditScreen.this.updateButtons();
			}
		});
		this.previousPageButton = this.addButton(new BookEditScreen.BookButton(2, i + 38, 156, false) {
			@Override
			public void method_18374(double d, double e) {
				if (BookEditScreen.this.currentPage > 0) {
					BookEditScreen.this.currentPage--;
				}

				BookEditScreen.this.updateButtons();
			}
		});
		this.updateButtons();
	}

	@Override
	public void removed() {
		this.client.field_19946.method_18191(false);
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

				this.item.addNbt("pages", this.pages);
				if (signBook) {
					this.item.addNbt("author", new NbtString(this.reader.getGameProfile().getName()));
					this.item.addNbt("title", new NbtString(this.title.trim()));
				}

				this.client.getNetworkHandler().sendPacket(new class_4385(this.item, signBook, this.field_20386));
			}
		}
	}

	private void appendNewPage() {
		if (this.pages != null && this.pages.size() < 50) {
			this.pages.add((NbtElement)(new NbtString("")));
			this.totalPages++;
			this.dirty = true;
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else if (this.writeable) {
			return this.signing ? this.method_18709(i, j, k) : this.method_18706(i, j, k);
		} else {
			return false;
		}
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (super.charTyped(c, i)) {
			return true;
		} else if (this.writeable) {
			if (this.signing) {
				if (this.title.length() < 16 && SharedConstants.isValidChar(c)) {
					this.title = this.title + Character.toString(c);
					this.updateButtons();
					this.dirty = true;
					return true;
				} else {
					return false;
				}
			} else if (SharedConstants.isValidChar(c)) {
				this.writeText(Character.toString(c));
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean method_18706(int i, int j, int k) {
		if (Screen.isPaste(i)) {
			this.writeText(this.client.field_19946.method_18177());
			return true;
		} else {
			switch (i) {
				case 257:
				case 335:
					this.writeText("\n");
					return true;
				case 259:
					String string = this.getCurrentPageContent();
					if (!string.isEmpty()) {
						this.setPageContent(string.substring(0, string.length() - 1));
					}

					return true;
				default:
					return false;
			}
		}
	}

	private boolean method_18709(int i, int j, int k) {
		switch (i) {
			case 257:
			case 335:
				if (!this.title.isEmpty()) {
					this.finalizeBook(true);
					this.client.setScreen(null);
				}

				return true;
			case 259:
				if (!this.title.isEmpty()) {
					this.title = this.title.substring(0, this.title.length() - 1);
					this.updateButtons();
				}

				return true;
			default:
				return false;
		}
	}

	private String getCurrentPageContent() {
		return this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.size() ? this.pages.getString(this.currentPage) : "";
	}

	private void setPageContent(String newContent) {
		if (this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
			this.pages.set(this.currentPage, (NbtElement)(new NbtString(newContent)));
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
			this.textRenderer.method_18355(string2, (float)(i + 36 + (116 - k) / 2), 34.0F, 0);
			int l = this.textRenderer.getStringWidth(string);
			this.textRenderer.method_18355(string, (float)(i + 36 + (116 - l) / 2), 50.0F, 0);
			String string3 = I18n.translate("book.byAuthor", this.reader.method_15540().getString());
			int m = this.textRenderer.getStringWidth(string3);
			this.textRenderer.method_18355(Formatting.DARK_GRAY + string3, (float)(i + 36 + (116 - m) / 2), 60.0F, 0);
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
					this.bookText = Lists.newArrayList(new TranslatableText("book.invalid.tag").formatted(Formatting.DARK_RED));
				}

				this.lastClickIndex = this.currentPage;
			}

			int n = this.textRenderer.getStringWidth(string5);
			this.textRenderer.method_18355(string5, (float)(i - n + 192 - 44), 18.0F, 0);
			if (this.bookText == null) {
				this.textRenderer.drawTrimmed(string6, i + 36, 34, 116, 0);
			} else {
				int o = Math.min(128 / this.textRenderer.fontHeight, this.bookText.size());

				for (int p = 0; p < o; p++) {
					Text text2 = (Text)this.bookText.get(p);
					this.textRenderer.method_18355(text2.asFormattedString(), (float)(i + 36), (float)(34 + p * this.textRenderer.fontHeight), 0);
				}

				Text text3 = this.method_18705((double)mouseX, (double)mouseY);
				if (text3 != null) {
					this.renderTextHoverEffect(text3, mouseX, mouseY);
				}
			}
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			Text text = this.method_18705(d, e);
			if (text != null && this.handleTextClick(text)) {
				return true;
			}
		}

		return super.mouseClicked(d, e, i);
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
	public Text method_18705(double d, double e) {
		if (this.bookText == null) {
			return null;
		} else {
			int i = MathHelper.floor(d - (double)((this.width - 192) / 2) - 36.0);
			int j = MathHelper.floor(e - 2.0 - 16.0 - 16.0);
			if (i >= 0 && j >= 0) {
				int k = Math.min(128 / this.textRenderer.fontHeight, this.bookText.size());
				if (i <= 116 && j < this.client.textRenderer.fontHeight * k + k) {
					int l = j / this.client.textRenderer.fontHeight;
					if (l >= 0 && l < this.bookText.size()) {
						Text text = (Text)this.bookText.get(l);
						int m = 0;

						for (Text text2 : text) {
							if (text2 instanceof LiteralText) {
								m += this.client.textRenderer.getStringWidth(text2.asFormattedString());
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

	abstract static class BookButton extends ButtonWidget {
		private final boolean isRight;

		public BookButton(int i, int j, int k, boolean bl) {
			super(i, j, k, 23, 13, "");
			this.isRight = bl;
		}

		@Override
		public void method_891(int i, int j, float f) {
			if (this.visible) {
				boolean bl = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				MinecraftClient.getInstance().getTextureManager().bindTexture(BookEditScreen.TEXTURE);
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
