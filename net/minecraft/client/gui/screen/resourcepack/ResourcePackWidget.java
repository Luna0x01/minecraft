package net.minecraft.client.gui.screen.resourcepack;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ResourcePackScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public abstract class ResourcePackWidget implements EntryListWidget.Entry {
	private static final Identifier RESOURCE_PACKS_TEX = new Identifier("textures/gui/resource_packs.png");
	private static final Text INCOMPATIBLE_TEXT = new TranslatableText("resourcePack.incompatible");
	private static final Text INCOMPATIBLE_OLD_TEXT = new TranslatableText("resourcePack.incompatible.old");
	private static final Text INCOMPATIBLE_NEW_TEXT = new TranslatableText("resourcePack.incompatible.new");
	protected final MinecraftClient client;
	protected final ResourcePackScreen screen;

	public ResourcePackWidget(ResourcePackScreen resourcePackScreen) {
		this.screen = resourcePackScreen;
		this.client = MinecraftClient.getInstance();
	}

	@Override
	public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
		int i = this.getFormat();
		if (i != 2) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.fill(x - 1, y - 1, x + rowWidth - 9, y + rowHeight + 1, -8978432);
		}

		this.bindIcon();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		DrawableHelper.drawTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		String string = this.getName();
		String string2 = this.getDescription();
		if (this.isVisible() && (this.client.options.touchscreen || hovered)) {
			this.client.getTextureManager().bindTexture(RESOURCE_PACKS_TEX);
			DrawableHelper.fill(x, y, x + 32, y + 32, -1601138544);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int j = mouseX - x;
			int k = mouseY - y;
			if (i < 2) {
				string = INCOMPATIBLE_TEXT.asFormattedString();
				string2 = INCOMPATIBLE_OLD_TEXT.asFormattedString();
			} else if (i > 2) {
				string = INCOMPATIBLE_TEXT.asFormattedString();
				string2 = INCOMPATIBLE_NEW_TEXT.asFormattedString();
			}

			if (this.isNotSelected()) {
				if (j < 32) {
					DrawableHelper.drawTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					DrawableHelper.drawTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			} else {
				if (this.isSelected()) {
					if (j < 16) {
						DrawableHelper.drawTexture(x, y, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						DrawableHelper.drawTexture(x, y, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (this.canSortUp()) {
					if (j < 32 && j > 16 && k < 16) {
						DrawableHelper.drawTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						DrawableHelper.drawTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (this.canSortDown()) {
					if (j < 32 && j > 16 && k > 16) {
						DrawableHelper.drawTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						DrawableHelper.drawTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}
			}
		}

		int l = this.client.textRenderer.getStringWidth(string);
		if (l > 157) {
			string = this.client.textRenderer.trimToWidth(string, 157 - this.client.textRenderer.getStringWidth("...")) + "...";
		}

		this.client.textRenderer.drawWithShadow(string, (float)(x + 32 + 2), (float)(y + 1), 16777215);
		List<String> list = this.client.textRenderer.wrapLines(string2, 157);

		for (int m = 0; m < 2 && m < list.size(); m++) {
			this.client.textRenderer.drawWithShadow((String)list.get(m), (float)(x + 32 + 2), (float)(y + 12 + 10 * m), 8421504);
		}
	}

	protected abstract int getFormat();

	protected abstract String getDescription();

	protected abstract String getName();

	protected abstract void bindIcon();

	protected boolean isVisible() {
		return true;
	}

	protected boolean isNotSelected() {
		return !this.screen.isPackSelected(this);
	}

	protected boolean isSelected() {
		return this.screen.isPackSelected(this);
	}

	protected boolean canSortUp() {
		List<ResourcePackWidget> list = this.screen.getContainingList(this);
		int i = list.indexOf(this);
		return i > 0 && ((ResourcePackWidget)list.get(i - 1)).isVisible();
	}

	protected boolean canSortDown() {
		List<ResourcePackWidget> list = this.screen.getContainingList(this);
		int i = list.indexOf(this);
		return i >= 0 && i < list.size() - 1 && ((ResourcePackWidget)list.get(i + 1)).isVisible();
	}

	@Override
	public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
		if (this.isVisible() && x <= 32) {
			if (this.isNotSelected()) {
				this.screen.markDirty();
				final int i = ((ResourcePackWidget)this.screen.getSelectedPacks().get(0)).method_12199() ? 1 : 0;
				int j = this.getFormat();
				if (j == 2) {
					this.screen.getContainingList(this).remove(this);
					this.screen.getSelectedPacks().add(i, this);
				} else {
					String string = I18n.translate("resourcePack.incompatible.confirm.title");
					String string2 = I18n.translate("resourcePack.incompatible.confirm." + (j > 2 ? "new" : "old"));
					this.client.setScreen(new ConfirmScreen(new IdentifiableBooleanConsumer() {
						@Override
						public void confirmResult(boolean confirmed, int id) {
							List<ResourcePackWidget> list = ResourcePackWidget.this.screen.getContainingList(ResourcePackWidget.this);
							ResourcePackWidget.this.client.setScreen(ResourcePackWidget.this.screen);
							if (confirmed) {
								list.remove(ResourcePackWidget.this);
								ResourcePackWidget.this.screen.getSelectedPacks().add(i, ResourcePackWidget.this);
							}
						}
					}, string, string2, 0));
				}

				return true;
			}

			if (x < 16 && this.isSelected()) {
				this.screen.getContainingList(this).remove(this);
				this.screen.getAvailablePacks().add(0, this);
				this.screen.markDirty();
				return true;
			}

			if (x > 16 && y < 16 && this.canSortUp()) {
				List<ResourcePackWidget> list = this.screen.getContainingList(this);
				int k = list.indexOf(this);
				list.remove(this);
				list.add(k - 1, this);
				this.screen.markDirty();
				return true;
			}

			if (x > 16 && y > 16 && this.canSortDown()) {
				List<ResourcePackWidget> list2 = this.screen.getContainingList(this);
				int l = list2.indexOf(this);
				list2.remove(this);
				list2.add(l + 1, this);
				this.screen.markDirty();
				return true;
			}
		}

		return false;
	}

	@Override
	public void updatePosition(int index, int x, int y) {
	}

	@Override
	public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
	}

	public boolean method_12199() {
		return false;
	}
}
