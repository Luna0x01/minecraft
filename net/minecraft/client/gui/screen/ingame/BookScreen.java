package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BookScreen extends Screen {
	public static final int field_32328 = 16;
	public static final int field_32329 = 36;
	public static final int field_32330 = 30;
	public static final BookScreen.Contents EMPTY_PROVIDER = new BookScreen.Contents() {
		@Override
		public int getPageCount() {
			return 0;
		}

		@Override
		public StringVisitable getPageUnchecked(int index) {
			return StringVisitable.EMPTY;
		}
	};
	public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
	protected static final int field_32331 = 114;
	protected static final int field_32332 = 128;
	protected static final int field_32333 = 192;
	protected static final int field_32334 = 192;
	private BookScreen.Contents contents;
	private int pageIndex;
	private List<OrderedText> cachedPage = Collections.emptyList();
	private int cachedPageIndex = -1;
	private Text pageIndexText = LiteralText.EMPTY;
	private PageTurnWidget nextPageButton;
	private PageTurnWidget previousPageButton;
	private final boolean pageTurnSound;

	public BookScreen(BookScreen.Contents pageProvider) {
		this(pageProvider, true);
	}

	public BookScreen() {
		this(EMPTY_PROVIDER, false);
	}

	private BookScreen(BookScreen.Contents contents, boolean playPageTurnSound) {
		super(NarratorManager.EMPTY);
		this.contents = contents;
		this.pageTurnSound = playPageTurnSound;
	}

	public void setPageProvider(BookScreen.Contents pageProvider) {
		this.contents = pageProvider;
		this.pageIndex = MathHelper.clamp(this.pageIndex, 0, pageProvider.getPageCount());
		this.updatePageButtons();
		this.cachedPageIndex = -1;
	}

	public boolean setPage(int index) {
		int i = MathHelper.clamp(index, 0, this.contents.getPageCount() - 1);
		if (i != this.pageIndex) {
			this.pageIndex = i;
			this.updatePageButtons();
			this.cachedPageIndex = -1;
			return true;
		} else {
			return false;
		}
	}

	protected boolean jumpToPage(int page) {
		return this.setPage(page);
	}

	@Override
	protected void init() {
		this.addCloseButton();
		this.addPageButtons();
	}

	protected void addCloseButton() {
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, 196, 200, 20, ScreenTexts.DONE, button -> this.client.openScreen(null)));
	}

	protected void addPageButtons() {
		int i = (this.width - 192) / 2;
		int j = 2;
		this.nextPageButton = this.addDrawableChild(new PageTurnWidget(i + 116, 159, true, button -> this.goToNextPage(), this.pageTurnSound));
		this.previousPageButton = this.addDrawableChild(new PageTurnWidget(i + 43, 159, false, button -> this.goToPreviousPage(), this.pageTurnSound));
		this.updatePageButtons();
	}

	private int getPageCount() {
		return this.contents.getPageCount();
	}

	protected void goToPreviousPage() {
		if (this.pageIndex > 0) {
			this.pageIndex--;
		}

		this.updatePageButtons();
	}

	protected void goToNextPage() {
		if (this.pageIndex < this.getPageCount() - 1) {
			this.pageIndex++;
		}

		this.updatePageButtons();
	}

	private void updatePageButtons() {
		this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
		this.previousPageButton.visible = this.pageIndex > 0;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else {
			switch (keyCode) {
				case 266:
					this.previousPageButton.onPress();
					return true;
				case 267:
					this.nextPageButton.onPress();
					return true;
				default:
					return false;
			}
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, BOOK_TEXTURE);
		int i = (this.width - 192) / 2;
		int j = 2;
		this.drawTexture(matrices, i, 2, 0, 0, 192, 192);
		if (this.cachedPageIndex != this.pageIndex) {
			StringVisitable stringVisitable = this.contents.getPage(this.pageIndex);
			this.cachedPage = this.textRenderer.wrapLines(stringVisitable, 114);
			this.pageIndexText = new TranslatableText("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
		}

		this.cachedPageIndex = this.pageIndex;
		int k = this.textRenderer.getWidth(this.pageIndexText);
		this.textRenderer.draw(matrices, this.pageIndexText, (float)(i - k + 192 - 44), 18.0F, 0);
		int l = Math.min(128 / 9, this.cachedPage.size());

		for (int m = 0; m < l; m++) {
			OrderedText orderedText = (OrderedText)this.cachedPage.get(m);
			this.textRenderer.draw(matrices, orderedText, (float)(i + 36), (float)(32 + m * 9), 0);
		}

		Style style = this.getTextAt((double)mouseX, (double)mouseY);
		if (style != null) {
			this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			Style style = this.getTextAt(mouseX, mouseY);
			if (style != null && this.handleTextClick(style)) {
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean handleTextClick(Style style) {
		ClickEvent clickEvent = style.getClickEvent();
		if (clickEvent == null) {
			return false;
		} else if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
			String string = clickEvent.getValue();

			try {
				int i = Integer.parseInt(string) - 1;
				return this.jumpToPage(i);
			} catch (Exception var5) {
				return false;
			}
		} else {
			boolean bl = super.handleTextClick(style);
			if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				this.closeScreen();
			}

			return bl;
		}
	}

	protected void closeScreen() {
		this.client.openScreen(null);
	}

	@Nullable
	public Style getTextAt(double x, double y) {
		if (this.cachedPage.isEmpty()) {
			return null;
		} else {
			int i = MathHelper.floor(x - (double)((this.width - 192) / 2) - 36.0);
			int j = MathHelper.floor(y - 2.0 - 30.0);
			if (i >= 0 && j >= 0) {
				int k = Math.min(128 / 9, this.cachedPage.size());
				if (i <= 114 && j < 9 * k + k) {
					int l = j / 9;
					if (l >= 0 && l < this.cachedPage.size()) {
						OrderedText orderedText = (OrderedText)this.cachedPage.get(l);
						return this.client.textRenderer.getTextHandler().getStyleAt(orderedText, i);
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	static List<String> readPages(NbtCompound nbt) {
		Builder<String> builder = ImmutableList.builder();
		filterPages(nbt, builder::add);
		return builder.build();
	}

	public static void filterPages(NbtCompound nbt, Consumer<String> pageConsumer) {
		NbtList nbtList = nbt.getList("pages", 8).copy();
		IntFunction<String> intFunction;
		if (MinecraftClient.getInstance().shouldFilterText() && nbt.contains("filtered_pages", 10)) {
			NbtCompound nbtCompound = nbt.getCompound("filtered_pages");
			intFunction = page -> {
				String string = String.valueOf(page);
				return nbtCompound.contains(string) ? nbtCompound.getString(string) : nbtList.getString(page);
			};
		} else {
			intFunction = nbtList::getString;
		}

		for (int i = 0; i < nbtList.size(); i++) {
			pageConsumer.accept((String)intFunction.apply(i));
		}
	}

	public interface Contents {
		int getPageCount();

		StringVisitable getPageUnchecked(int index);

		default StringVisitable getPage(int index) {
			return index >= 0 && index < this.getPageCount() ? this.getPageUnchecked(index) : StringVisitable.EMPTY;
		}

		static BookScreen.Contents create(ItemStack stack) {
			if (stack.isOf(Items.WRITTEN_BOOK)) {
				return new BookScreen.WrittenBookContents(stack);
			} else {
				return (BookScreen.Contents)(stack.isOf(Items.WRITABLE_BOOK) ? new BookScreen.WritableBookContents(stack) : BookScreen.EMPTY_PROVIDER);
			}
		}
	}

	public static class WritableBookContents implements BookScreen.Contents {
		private final List<String> pages;

		public WritableBookContents(ItemStack stack) {
			this.pages = getPages(stack);
		}

		private static List<String> getPages(ItemStack stack) {
			NbtCompound nbtCompound = stack.getTag();
			return (List<String>)(nbtCompound != null ? BookScreen.readPages(nbtCompound) : ImmutableList.of());
		}

		@Override
		public int getPageCount() {
			return this.pages.size();
		}

		@Override
		public StringVisitable getPageUnchecked(int index) {
			return StringVisitable.plain((String)this.pages.get(index));
		}
	}

	public static class WrittenBookContents implements BookScreen.Contents {
		private final List<String> pages;

		public WrittenBookContents(ItemStack stack) {
			this.pages = getPages(stack);
		}

		private static List<String> getPages(ItemStack stack) {
			NbtCompound nbtCompound = stack.getTag();
			return (List<String>)(nbtCompound != null && WrittenBookItem.isValid(nbtCompound)
				? BookScreen.readPages(nbtCompound)
				: ImmutableList.of(Text.Serializer.toJson(new TranslatableText("book.invalid.tag").formatted(Formatting.DARK_RED))));
		}

		@Override
		public int getPageCount() {
			return this.pages.size();
		}

		@Override
		public StringVisitable getPageUnchecked(int index) {
			String string = (String)this.pages.get(index);

			try {
				StringVisitable stringVisitable = Text.Serializer.fromJson(string);
				if (stringVisitable != null) {
					return stringVisitable;
				}
			} catch (Exception var4) {
			}

			return StringVisitable.plain(string);
		}
	}
}
