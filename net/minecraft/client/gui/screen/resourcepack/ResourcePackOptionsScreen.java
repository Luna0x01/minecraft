package net.minecraft.client.gui.screen.resourcepack;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class ResourcePackOptionsScreen extends GameOptionsScreen {
	private AvailableResourcePackListWidget availablePacks;
	private SelectedResourcePackListWidget enabledPacks;
	private boolean dirty;

	public ResourcePackOptionsScreen(Screen screen, GameOptions gameOptions) {
		super(screen, gameOptions, new TranslatableText("resourcePack.title"));
	}

	@Override
	protected void init() {
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 154,
				this.height - 48,
				150,
				20,
				I18n.translate("resourcePack.openFolder"),
				buttonWidget -> Util.getOperatingSystem().open(this.minecraft.getResourcePackDir())
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, I18n.translate("gui.done"), buttonWidget -> {
			if (this.dirty) {
				List<ClientResourcePackProfile> listx = Lists.newArrayList();

				for (ResourcePackListWidget.ResourcePackEntry resourcePackEntry : this.enabledPacks.children()) {
					listx.add(resourcePackEntry.getPack());
				}

				Collections.reverse(listx);
				this.minecraft.getResourcePackManager().setEnabledProfiles(listx);
				this.gameOptions.resourcePacks.clear();
				this.gameOptions.incompatibleResourcePacks.clear();

				for (ClientResourcePackProfile clientResourcePackProfile : listx) {
					if (!clientResourcePackProfile.isPinned()) {
						this.gameOptions.resourcePacks.add(clientResourcePackProfile.getName());
						if (!clientResourcePackProfile.getCompatibility().isCompatible()) {
							this.gameOptions.incompatibleResourcePacks.add(clientResourcePackProfile.getName());
						}
					}
				}

				this.gameOptions.write();
				this.minecraft.openScreen(this.parent);
				this.minecraft.reloadResources();
			} else {
				this.minecraft.openScreen(this.parent);
			}
		}));
		AvailableResourcePackListWidget availableResourcePackListWidget = this.availablePacks;
		SelectedResourcePackListWidget selectedResourcePackListWidget = this.enabledPacks;
		this.availablePacks = new AvailableResourcePackListWidget(this.minecraft, 200, this.height);
		this.availablePacks.setLeftPos(this.width / 2 - 4 - 200);
		if (availableResourcePackListWidget != null) {
			this.availablePacks.children().addAll(availableResourcePackListWidget.children());
		}

		this.children.add(this.availablePacks);
		this.enabledPacks = new SelectedResourcePackListWidget(this.minecraft, 200, this.height);
		this.enabledPacks.setLeftPos(this.width / 2 + 4);
		if (selectedResourcePackListWidget != null) {
			selectedResourcePackListWidget.children().forEach(resourcePackEntry -> {
				this.enabledPacks.children().add(resourcePackEntry);
				resourcePackEntry.method_24232(this.enabledPacks);
			});
		}

		this.children.add(this.enabledPacks);
		if (!this.dirty) {
			this.availablePacks.children().clear();
			this.enabledPacks.children().clear();
			ResourcePackManager<ClientResourcePackProfile> resourcePackManager = this.minecraft.getResourcePackManager();
			resourcePackManager.scanPacks();
			List<ClientResourcePackProfile> list = Lists.newArrayList(resourcePackManager.getProfiles());
			list.removeAll(resourcePackManager.getEnabledProfiles());

			for (ClientResourcePackProfile clientResourcePackProfile : list) {
				this.availablePacks.add(new ResourcePackListWidget.ResourcePackEntry(this.availablePacks, this, clientResourcePackProfile));
			}

			for (ClientResourcePackProfile clientResourcePackProfile2 : Lists.reverse(Lists.newArrayList(resourcePackManager.getEnabledProfiles()))) {
				this.enabledPacks.add(new ResourcePackListWidget.ResourcePackEntry(this.enabledPacks, this, clientResourcePackProfile2));
			}
		}
	}

	public void enable(ResourcePackListWidget.ResourcePackEntry resourcePackEntry) {
		this.availablePacks.children().remove(resourcePackEntry);
		resourcePackEntry.enable(this.enabledPacks);
		this.markDirty();
	}

	public void disable(ResourcePackListWidget.ResourcePackEntry resourcePackEntry) {
		this.enabledPacks.children().remove(resourcePackEntry);
		this.availablePacks.add(resourcePackEntry);
		this.markDirty();
	}

	public boolean isEnabled(ResourcePackListWidget.ResourcePackEntry resourcePackEntry) {
		return this.enabledPacks.children().contains(resourcePackEntry);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderDirtBackground(0);
		this.availablePacks.render(i, j, f);
		this.enabledPacks.render(i, j, f);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.font, I18n.translate("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
		super.render(i, j, f);
	}

	public void markDirty() {
		this.dirty = true;
	}
}
