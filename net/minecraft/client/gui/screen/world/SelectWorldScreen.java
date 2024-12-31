package net.minecraft.client.gui.screen.world;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.client.class_2847;
import net.minecraft.client.class_2848;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectWorldScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	protected Screen parent;
	protected String title = "Select world";
	private String field_13356;
	private ButtonWidget deleteButton;
	private ButtonWidget selectButton;
	private ButtonWidget editButton;
	private ButtonWidget recreateButton;
	protected TextFieldWidget field_20497;
	private class_2848 field_13358;

	public SelectWorldScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	public boolean mouseScrolled(double d) {
		return this.field_13358.mouseScrolled(d);
	}

	@Override
	public void tick() {
		this.field_20497.tick();
	}

	@Override
	protected void init() {
		this.client.field_19946.method_18191(true);
		this.title = I18n.translate("selectWorld.title");
		this.field_20497 = new TextFieldWidget(0, this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.field_20497) {
			@Override
			public void setFocused(boolean focused) {
				super.setFocused(true);
			}
		};
		this.field_20497.method_18387((integer, string) -> this.field_13358.method_18898(() -> string, false));
		this.field_13358 = new class_2848(this, this.client, this.width, this.height, 48, this.height - 64, 36, () -> this.field_20497.getText(), this.field_13358);
		this.selectButton = this.addButton(new ButtonWidget(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.translate("selectWorld.select")) {
			@Override
			public void method_18374(double d, double e) {
				class_2847 lv = SelectWorldScreen.this.field_13358.method_12216();
				if (lv != null) {
					lv.method_12202();
				}
			}
		});
		this.addButton(new ButtonWidget(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.translate("selectWorld.create")) {
			@Override
			public void method_18374(double d, double e) {
				SelectWorldScreen.this.client.setScreen(new CreateWorldScreen(SelectWorldScreen.this));
			}
		});
		this.editButton = this.addButton(new ButtonWidget(4, this.width / 2 - 154, this.height - 28, 72, 20, I18n.translate("selectWorld.edit")) {
			@Override
			public void method_18374(double d, double e) {
				class_2847 lv = SelectWorldScreen.this.field_13358.method_12216();
				if (lv != null) {
					lv.method_12206();
				}
			}
		});
		this.deleteButton = this.addButton(new ButtonWidget(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.translate("selectWorld.delete")) {
			@Override
			public void method_18374(double d, double e) {
				class_2847 lv = SelectWorldScreen.this.field_13358.method_12216();
				if (lv != null) {
					lv.method_12204();
				}
			}
		});
		this.recreateButton = this.addButton(new ButtonWidget(5, this.width / 2 + 4, this.height - 28, 72, 20, I18n.translate("selectWorld.recreate")) {
			@Override
			public void method_18374(double d, double e) {
				class_2847 lv = SelectWorldScreen.this.field_13358.method_12216();
				if (lv != null) {
					lv.method_12208();
				}
			}
		});
		this.addButton(new ButtonWidget(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				SelectWorldScreen.this.client.setScreen(SelectWorldScreen.this.parent);
			}
		});
		this.selectButton.active = false;
		this.deleteButton.active = false;
		this.editButton.active = false;
		this.recreateButton.active = false;
		this.field_20307.add(this.field_20497);
		this.field_20307.add(this.field_13358);
		this.field_20497.setFocused(true);
		this.field_20497.setFocusUnlocked(false);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return super.keyPressed(i, j, k) ? true : this.field_20497.keyPressed(i, j, k);
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.field_20497.charTyped(c, i);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.field_13356 = null;
		this.field_13358.render(mouseX, mouseY, tickDelta);
		this.field_20497.method_18385(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 8, 16777215);
		super.render(mouseX, mouseY, tickDelta);
		if (this.field_13356 != null) {
			this.renderTooltip(Lists.newArrayList(Splitter.on("\n").split(this.field_13356)), mouseX, mouseY);
		}
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

	@Override
	public void removed() {
		if (this.field_13358 != null) {
			this.field_13358.method_18423().forEach(class_2847::close);
		}
	}
}
