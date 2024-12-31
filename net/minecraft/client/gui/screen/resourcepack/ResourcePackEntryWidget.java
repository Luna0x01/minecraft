package net.minecraft.client.gui.screen.resourcepack;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.class_4286;
import net.minecraft.class_4461;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ResourcePackScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class ResourcePackEntryWidget extends EntryListWidget.Entry<ResourcePackEntryWidget> {
	private static final Identifier field_20468 = new Identifier("textures/gui/resource_packs.png");
	private static final Text field_20469 = new TranslatableText("resourcePack.incompatible");
	private static final Text field_20470 = new TranslatableText("resourcePack.incompatible.confirm.title");
	protected final MinecraftClient field_20466;
	protected final ResourcePackScreen field_20467;
	private final class_4286 field_7856;

	public ResourcePackEntryWidget(ResourcePackScreen resourcePackScreen, class_4286 arg) {
		this.field_20467 = resourcePackScreen;
		this.field_20466 = MinecraftClient.getInstance();
		this.field_7856 = arg;
	}

	public void method_18820(SelectedResourcePackListWidget selectedResourcePackListWidget) {
		this.method_6807().method_21368().method_21370(selectedResourcePackListWidget.method_18423(), this, ResourcePackEntryWidget::method_6807, true);
	}

	protected void method_6801() {
		this.field_7856.method_19556(this.field_20466.getTextureManager());
	}

	protected class_4461 method_18822() {
		return this.field_7856.method_21363();
	}

	protected String method_6799() {
		return this.field_7856.method_21362().asFormattedString();
	}

	protected String method_6800() {
		return this.field_7856.method_21358().asFormattedString();
	}

	public class_4286 method_6807() {
		return this.field_7856;
	}

	@Override
	public void method_6700(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.method_18403();
		int n = this.method_18404();
		class_4461 lv = this.method_18822();
		if (!lv.method_21343()) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.fill(n - 1, m - 1, n + i - 9, m + j + 1, -8978432);
		}

		this.method_6801();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		DrawableHelper.drawTexture(n, m, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		String string = this.method_6800();
		String string2 = this.method_6799();
		if (this.method_18823() && (this.field_20466.options.touchscreen || bl)) {
			this.field_20466.getTextureManager().bindTexture(field_20468);
			DrawableHelper.fill(n, m, n + 32, m + 32, -1601138544);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int o = k - n;
			int p = l - m;
			if (!lv.method_21343()) {
				string = field_20469.asFormattedString();
				string2 = lv.method_21345().asFormattedString();
			}

			if (this.method_18824()) {
				if (o < 32) {
					DrawableHelper.drawTexture(n, m, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					DrawableHelper.drawTexture(n, m, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			} else {
				if (this.method_18825()) {
					if (o < 16) {
						DrawableHelper.drawTexture(n, m, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						DrawableHelper.drawTexture(n, m, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (this.method_18826()) {
					if (o < 32 && o > 16 && p < 16) {
						DrawableHelper.drawTexture(n, m, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						DrawableHelper.drawTexture(n, m, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (this.method_18827()) {
					if (o < 32 && o > 16 && p > 16) {
						DrawableHelper.drawTexture(n, m, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						DrawableHelper.drawTexture(n, m, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}
			}
		}

		int q = this.field_20466.textRenderer.getStringWidth(string);
		if (q > 157) {
			string = this.field_20466.textRenderer.trimToWidth(string, 157 - this.field_20466.textRenderer.getStringWidth("...")) + "...";
		}

		this.field_20466.textRenderer.drawWithShadow(string, (float)(n + 32 + 2), (float)(m + 1), 16777215);
		List<String> list = this.field_20466.textRenderer.wrapLines(string2, 157);

		for (int r = 0; r < 2 && r < list.size(); r++) {
			this.field_20466.textRenderer.drawWithShadow((String)list.get(r), (float)(n + 32 + 2), (float)(m + 12 + 10 * r), 8421504);
		}
	}

	protected boolean method_18823() {
		return !this.field_7856.method_21367() || !this.field_7856.method_21366();
	}

	protected boolean method_18824() {
		return !this.field_20467.method_18810(this);
	}

	protected boolean method_18825() {
		return this.field_20467.method_18810(this) && !this.field_7856.method_21366();
	}

	protected boolean method_18826() {
		List<ResourcePackEntryWidget> list = this.method_18400().method_18423();
		int i = list.indexOf(this);
		return i > 0 && !((ResourcePackEntryWidget)list.get(i - 1)).field_7856.method_21367();
	}

	protected boolean method_18827() {
		List<ResourcePackEntryWidget> list = this.method_18400().method_18423();
		int i = list.indexOf(this);
		return i >= 0 && i < list.size() - 1 && !((ResourcePackEntryWidget)list.get(i + 1)).field_7856.method_21367();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		double f = d - (double)this.method_18404();
		double g = e - (double)this.method_18403();
		if (this.method_18823() && f <= 32.0) {
			if (this.method_18824()) {
				this.method_18828().markDirty();
				class_4461 lv = this.method_18822();
				if (lv.method_21343()) {
					this.method_18828().method_18806(this);
				} else {
					String string = field_20470.asFormattedString();
					String string2 = lv.method_21346().asFormattedString();
					this.field_20466.setScreen(new ConfirmScreen((bl, ix) -> {
						this.field_20466.setScreen(this.method_18828());
						if (bl) {
							this.method_18828().method_18806(this);
						}
					}, string, string2, 0));
				}

				return true;
			}

			if (f < 16.0 && this.method_18825()) {
				this.method_18828().method_18808(this);
				return true;
			}

			if (f > 16.0 && g < 16.0 && this.method_18826()) {
				List<ResourcePackEntryWidget> list = this.method_18400().method_18423();
				int j = list.indexOf(this);
				list.remove(this);
				list.add(j - 1, this);
				this.method_18828().markDirty();
				return true;
			}

			if (f > 16.0 && g > 16.0 && this.method_18827()) {
				List<ResourcePackEntryWidget> list2 = this.method_18400().method_18423();
				int k = list2.indexOf(this);
				list2.remove(this);
				list2.add(k + 1, this);
				this.method_18828().markDirty();
				return true;
			}
		}

		return false;
	}

	public ResourcePackScreen method_18828() {
		return this.field_20467;
	}
}
