package net.minecraft.client.gui.screen.world;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.client.class_2847;
import net.minecraft.client.class_2848;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.resource.language.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectWorldScreen extends Screen implements IdentifiableBooleanConsumer {
	private static final Logger LOGGER = LogManager.getLogger();
	protected Screen parent;
	protected String title = "Select world";
	private String field_13356;
	private ButtonWidget deleteButton;
	private ButtonWidget selectButton;
	private ButtonWidget editButton;
	private ButtonWidget recreateButton;
	private class_2848 field_13358;

	public SelectWorldScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	public void init() {
		this.title = I18n.translate("selectWorld.title");
		this.field_13358 = new class_2848(this, this.client, this.width, this.height, 32, this.height - 64, 36);
		this.initButtons();
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.field_13358.handleMouse();
	}

	public void initButtons() {
		this.buttons.add(this.selectButton = new ButtonWidget(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.translate("selectWorld.select")));
		this.buttons.add(new ButtonWidget(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.translate("selectWorld.create")));
		this.buttons.add(this.editButton = new ButtonWidget(4, this.width / 2 - 154, this.height - 28, 72, 20, I18n.translate("selectWorld.edit")));
		this.buttons.add(this.deleteButton = new ButtonWidget(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.translate("selectWorld.delete")));
		this.buttons.add(this.recreateButton = new ButtonWidget(5, this.width / 2 + 4, this.height - 28, 72, 20, I18n.translate("selectWorld.recreate")));
		this.buttons.add(new ButtonWidget(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.translate("gui.cancel")));
		this.selectButton.active = false;
		this.deleteButton.active = false;
		this.editButton.active = false;
		this.recreateButton.active = false;
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			class_2847 lv = this.field_13358.method_12216();
			if (button.id == 2) {
				if (lv != null) {
					lv.method_12204();
				}
			} else if (button.id == 1) {
				if (lv != null) {
					lv.method_12202();
				}
			} else if (button.id == 3) {
				this.client.setScreen(new CreateWorldScreen(this));
			} else if (button.id == 4) {
				if (lv != null) {
					lv.method_12206();
				}
			} else if (button.id == 0) {
				this.client.setScreen(this.parent);
			} else if (button.id == 5 && lv != null) {
				lv.method_12208();
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.field_13356 = null;
		this.field_13358.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(mouseX, mouseY, tickDelta);
		if (this.field_13356 != null) {
			this.renderTooltip(Lists.newArrayList(Splitter.on("\n").split(this.field_13356)), mouseX, mouseY);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		this.field_13358.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);
		this.field_13358.mouseReleased(mouseX, mouseY, button);
	}

	public void method_12201(String string) {
		this.field_13356 = string;
	}

	public void method_12200(@Nullable class_2847 arg) {
		boolean bl = arg != null;
		this.selectButton.active = bl;
		this.deleteButton.active = bl;
		this.editButton.active = bl;
		this.recreateButton.active = bl;
	}
}
