package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_4286;
import net.minecraft.class_4462;
import net.minecraft.client.gui.screen.resourcepack.AvailableResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackEntryWidget;
import net.minecraft.client.gui.screen.resourcepack.SelectedResourcePackListWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Util;

public class ResourcePackScreen extends Screen {
	private final Screen parent;
	@Nullable
	private AvailableResourcePackListWidget availablePacks;
	@Nullable
	private SelectedResourcePackListWidget selectedPacks;
	private boolean dirty;

	public ResourcePackScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	protected void init() {
		this.addButton(new OptionButtonWidget(2, this.width / 2 - 154, this.height - 48, I18n.translate("resourcePack.openFolder")) {
			@Override
			public void method_18374(double d, double e) {
				Util.getOperatingSystem().method_20235(ResourcePackScreen.this.client.method_18200());
			}
		});
		this.addButton(new OptionButtonWidget(1, this.width / 2 + 4, this.height - 48, I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				if (ResourcePackScreen.this.dirty) {
					List<class_4286> list = Lists.newArrayList();

					for (ResourcePackEntryWidget resourcePackEntryWidget : ResourcePackScreen.this.selectedPacks.method_18423()) {
						list.add(resourcePackEntryWidget.method_6807());
					}

					Collections.reverse(list);
					ResourcePackScreen.this.client.method_18199().method_21349(list);
					ResourcePackScreen.this.client.options.resourcePacks.clear();
					ResourcePackScreen.this.client.options.incompatibleResourcePacks.clear();

					for (class_4286 lv : list) {
						if (!lv.method_21367()) {
							ResourcePackScreen.this.client.options.resourcePacks.add(lv.method_21365());
							if (!lv.method_21363().method_21343()) {
								ResourcePackScreen.this.client.options.incompatibleResourcePacks.add(lv.method_21365());
							}
						}
					}

					ResourcePackScreen.this.client.options.save();
					ResourcePackScreen.this.client.reloadResources();
				}

				ResourcePackScreen.this.client.setScreen(ResourcePackScreen.this.parent);
			}
		});
		AvailableResourcePackListWidget availableResourcePackListWidget = this.availablePacks;
		SelectedResourcePackListWidget selectedResourcePackListWidget = this.selectedPacks;
		this.availablePacks = new AvailableResourcePackListWidget(this.client, 200, this.height);
		this.availablePacks.setXPos(this.width / 2 - 4 - 200);
		if (availableResourcePackListWidget != null) {
			this.availablePacks.method_18423().addAll(availableResourcePackListWidget.method_18423());
		}

		this.field_20307.add(this.availablePacks);
		this.selectedPacks = new SelectedResourcePackListWidget(this.client, 200, this.height);
		this.selectedPacks.setXPos(this.width / 2 + 4);
		if (selectedResourcePackListWidget != null) {
			this.selectedPacks.method_18423().addAll(selectedResourcePackListWidget.method_18423());
		}

		this.field_20307.add(this.selectedPacks);
		if (!this.dirty) {
			this.availablePacks.method_18423().clear();
			this.selectedPacks.method_18423().clear();
			class_4462<class_4286> lv = this.client.method_18199();
			lv.method_21347();
			List<class_4286> list = Lists.newArrayList(lv.method_21352());
			list.removeAll(lv.method_21354());

			for (class_4286 lv2 : list) {
				this.availablePacks.method_18829(new ResourcePackEntryWidget(this, lv2));
			}

			for (class_4286 lv3 : Lists.reverse(Lists.newArrayList(lv.method_21354()))) {
				this.selectedPacks.method_18829(new ResourcePackEntryWidget(this, lv3));
			}
		}
	}

	public void method_18806(ResourcePackEntryWidget resourcePackEntryWidget) {
		this.availablePacks.method_18423().remove(resourcePackEntryWidget);
		resourcePackEntryWidget.method_18820(this.selectedPacks);
		this.markDirty();
	}

	public void method_18808(ResourcePackEntryWidget resourcePackEntryWidget) {
		this.selectedPacks.method_18423().remove(resourcePackEntryWidget);
		this.availablePacks.method_18829(resourcePackEntryWidget);
		this.markDirty();
	}

	public boolean method_18810(ResourcePackEntryWidget resourcePackEntryWidget) {
		return this.selectedPacks.method_18423().contains(resourcePackEntryWidget);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderDirtBackground(0);
		this.availablePacks.render(mouseX, mouseY, tickDelta);
		this.selectedPacks.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, I18n.translate("resourcePack.title"), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.textRenderer, I18n.translate("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
		super.render(mouseX, mouseY, tickDelta);
	}

	public void markDirty() {
		this.dirty = true;
	}
}
