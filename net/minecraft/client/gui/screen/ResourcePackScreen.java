package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import java.io.File;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.class_2846;
import net.minecraft.client.gui.screen.resourcepack.AvailableResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.DefaultResourcePackEntryWidget;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackEntryWidget;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackWidget;
import net.minecraft.client.gui.screen.resourcepack.SelectedResourcePackListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.ResourcePackLoader;
import net.minecraft.client.resource.language.I18n;

public class ResourcePackScreen extends Screen {
	private final Screen parent;
	private List<ResourcePackWidget> availablePackList;
	private List<ResourcePackWidget> selectedPackList;
	private AvailableResourcePackListWidget availablePacks;
	private SelectedResourcePackListWidget selectedPacks;
	private boolean dirty;

	public ResourcePackScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	public void init() {
		this.buttons.add(new OptionButtonWidget(2, this.width / 2 - 154, this.height - 48, I18n.translate("resourcePack.openFolder")));
		this.buttons.add(new OptionButtonWidget(1, this.width / 2 + 4, this.height - 48, I18n.translate("gui.done")));
		if (!this.dirty) {
			this.availablePackList = Lists.newArrayList();
			this.selectedPackList = Lists.newArrayList();
			ResourcePackLoader resourcePackLoader = this.client.getResourcePackLoader();
			resourcePackLoader.initResourcePacks();
			List<ResourcePackLoader.Entry> list = Lists.newArrayList(resourcePackLoader.getAvailableResourcePacks());
			list.removeAll(resourcePackLoader.getSelectedResourcePacks());

			for (ResourcePackLoader.Entry entry : list) {
				this.availablePackList.add(new ResourcePackEntryWidget(this, entry));
			}

			ResourcePackLoader.Entry entry2 = resourcePackLoader.method_12499();
			if (entry2 != null) {
				this.selectedPackList.add(new class_2846(this, resourcePackLoader.getServerContainer()));
			}

			for (ResourcePackLoader.Entry entry3 : Lists.reverse(resourcePackLoader.getSelectedResourcePacks())) {
				this.selectedPackList.add(new ResourcePackEntryWidget(this, entry3));
			}

			this.selectedPackList.add(new DefaultResourcePackEntryWidget(this));
		}

		this.availablePacks = new AvailableResourcePackListWidget(this.client, 200, this.height, this.availablePackList);
		this.availablePacks.setXPos(this.width / 2 - 4 - 200);
		this.availablePacks.setButtonIds(7, 8);
		this.selectedPacks = new SelectedResourcePackListWidget(this.client, 200, this.height, this.selectedPackList);
		this.selectedPacks.setXPos(this.width / 2 + 4);
		this.selectedPacks.setButtonIds(7, 8);
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.selectedPacks.handleMouse();
		this.availablePacks.handleMouse();
	}

	public boolean isPackSelected(ResourcePackWidget resourcePack) {
		return this.selectedPackList.contains(resourcePack);
	}

	public List<ResourcePackWidget> getContainingList(ResourcePackWidget resourcePack) {
		return this.isPackSelected(resourcePack) ? this.selectedPackList : this.availablePackList;
	}

	public List<ResourcePackWidget> getAvailablePacks() {
		return this.availablePackList;
	}

	public List<ResourcePackWidget> getSelectedPacks() {
		return this.selectedPackList;
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 2) {
				File file = this.client.getResourcePackLoader().getResourcePackDir();
				GLX.method_12553(file);
			} else if (button.id == 1) {
				if (this.dirty) {
					List<ResourcePackLoader.Entry> list = Lists.newArrayList();

					for (ResourcePackWidget resourcePackWidget : this.selectedPackList) {
						if (resourcePackWidget instanceof ResourcePackEntryWidget) {
							list.add(((ResourcePackEntryWidget)resourcePackWidget).getEntry());
						}
					}

					Collections.reverse(list);
					this.client.getResourcePackLoader().setSelectedResourcePacks(list);
					this.client.options.resourcePacks.clear();
					this.client.options.incompatibleResourcePacks.clear();

					for (ResourcePackLoader.Entry entry : list) {
						this.client.options.resourcePacks.add(entry.getName());
						if (entry.getFormat() != 3) {
							this.client.options.incompatibleResourcePacks.add(entry.getName());
						}
					}

					this.client.options.save();
					this.client.reloadResources();
				}

				this.client.setScreen(this.parent);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		this.availablePacks.mouseClicked(mouseX, mouseY, button);
		this.selectedPacks.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);
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
