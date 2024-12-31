package net.minecraft.client.gui.screen.world;

import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorageAccess;
import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;

public class EditWorldScreen extends Screen {
	private final Screen parent;
	private TextFieldWidget searchField;
	private final String levelName;

	public EditWorldScreen(Screen screen, String string) {
		this.parent = screen;
		this.levelName = string;
	}

	@Override
	public void tick() {
		this.searchField.tick();
	}

	@Override
	public void init() {
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
		ButtonWidget buttonWidget = this.addButton(new ButtonWidget(3, this.width / 2 - 100, this.height / 4 + 24 + 12, I18n.translate("selectWorld.edit.resetIcon")));
		this.buttons.add(new ButtonWidget(4, this.width / 2 - 100, this.height / 4 + 48 + 12, I18n.translate("selectWorld.edit.openFolder")));
		this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.translate("selectWorld.edit.save")));
		this.buttons.add(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.translate("gui.cancel")));
		buttonWidget.active = this.client.getCurrentSave().method_11957(this.levelName, "icon.png").isFile();
		LevelStorageAccess levelStorageAccess = this.client.getCurrentSave();
		LevelProperties levelProperties = levelStorageAccess.getLevelProperties(this.levelName);
		String string = levelProperties == null ? "" : levelProperties.getLevelName();
		this.searchField = new TextFieldWidget(2, this.textRenderer, this.width / 2 - 100, 60, 200, 20);
		this.searchField.setFocused(true);
		this.searchField.setText(string);
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 1) {
				this.client.setScreen(this.parent);
			} else if (button.id == 0) {
				LevelStorageAccess levelStorageAccess = this.client.getCurrentSave();
				levelStorageAccess.renameLevel(this.levelName, this.searchField.getText().trim());
				this.client.setScreen(this.parent);
			} else if (button.id == 3) {
				LevelStorageAccess levelStorageAccess2 = this.client.getCurrentSave();
				FileUtils.deleteQuietly(levelStorageAccess2.method_11957(this.levelName, "icon.png"));
				button.active = false;
			} else if (button.id == 4) {
				LevelStorageAccess levelStorageAccess3 = this.client.getCurrentSave();
				GLX.method_12553(levelStorageAccess3.method_11957(this.levelName, "icon.png").getParentFile());
			}
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		this.searchField.keyPressed(id, code);
		((ButtonWidget)this.buttons.get(2)).active = !this.searchField.getText().trim().isEmpty();
		if (code == 28 || code == 156) {
			this.buttonClicked((ButtonWidget)this.buttons.get(2));
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		this.searchField.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("selectWorld.edit.title"), this.width / 2, 20, 16777215);
		this.drawWithShadow(this.textRenderer, I18n.translate("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
		this.searchField.render();
		super.render(mouseX, mouseY, tickDelta);
	}
}
