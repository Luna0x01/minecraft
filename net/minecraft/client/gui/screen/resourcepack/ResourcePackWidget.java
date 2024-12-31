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
	public void method_6700(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
		int p = this.getFormat();
		if (p != 3) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.fill(j - 1, k - 1, j + l - 9, k + m + 1, -8978432);
		}

		this.bindIcon();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		DrawableHelper.drawTexture(j, k, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		String string = this.getName();
		String string2 = this.getDescription();
		if (this.isVisible() && (this.client.options.touchscreen || bl)) {
			this.client.getTextureManager().bindTexture(RESOURCE_PACKS_TEX);
			DrawableHelper.fill(j, k, j + 32, k + 32, -1601138544);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int q = n - j;
			int r = o - k;
			if (p < 3) {
				string = INCOMPATIBLE_TEXT.asFormattedString();
				string2 = INCOMPATIBLE_OLD_TEXT.asFormattedString();
			} else if (p > 3) {
				string = INCOMPATIBLE_TEXT.asFormattedString();
				string2 = INCOMPATIBLE_NEW_TEXT.asFormattedString();
			}

			if (this.isNotSelected()) {
				if (q < 32) {
					DrawableHelper.drawTexture(j, k, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					DrawableHelper.drawTexture(j, k, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			} else {
				if (this.isSelected()) {
					if (q < 16) {
						DrawableHelper.drawTexture(j, k, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						DrawableHelper.drawTexture(j, k, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (this.canSortUp()) {
					if (q < 32 && q > 16 && r < 16) {
						DrawableHelper.drawTexture(j, k, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						DrawableHelper.drawTexture(j, k, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (this.canSortDown()) {
					if (q < 32 && q > 16 && r > 16) {
						DrawableHelper.drawTexture(j, k, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						DrawableHelper.drawTexture(j, k, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}
			}
		}

		int s = this.client.textRenderer.getStringWidth(string);
		if (s > 157) {
			string = this.client.textRenderer.trimToWidth(string, 157 - this.client.textRenderer.getStringWidth("...")) + "...";
		}

		this.client.textRenderer.drawWithShadow(string, (float)(j + 32 + 2), (float)(k + 1), 16777215);
		List<String> list = this.client.textRenderer.wrapLines(string2, 157);

		for (int t = 0; t < 2 && t < list.size(); t++) {
			this.client.textRenderer.drawWithShadow((String)list.get(t), (float)(j + 32 + 2), (float)(k + 12 + 10 * t), 8421504);
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
				if (j == 3) {
					this.screen.getContainingList(this).remove(this);
					this.screen.getSelectedPacks().add(i, this);
				} else {
					String string = I18n.translate("resourcePack.incompatible.confirm.title");
					String string2 = I18n.translate("resourcePack.incompatible.confirm." + (j > 3 ? "new" : "old"));
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
	public void method_9473(int i, int j, int k, float f) {
	}

	@Override
	public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
	}

	public boolean method_12199() {
		return false;
	}
}
