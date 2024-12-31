package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import org.lwjgl.input.Keyboard;

public class DirectConnectScreen extends Screen {
	private final Screen parent;
	private final ServerInfo info;
	private TextFieldWidget serverField;

	public DirectConnectScreen(Screen screen, ServerInfo serverInfo) {
		this.parent = screen;
		this.info = serverInfo;
	}

	@Override
	public void tick() {
		this.serverField.tick();
	}

	@Override
	public void init() {
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
		this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.translate("selectServer.select")));
		this.buttons.add(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.translate("gui.cancel")));
		this.serverField = new TextFieldWidget(2, this.textRenderer, this.width / 2 - 100, 116, 200, 20);
		this.serverField.setMaxLength(128);
		this.serverField.setFocused(true);
		this.serverField.setText(this.client.options.lastServer);
		((ButtonWidget)this.buttons.get(0)).active = !this.serverField.getText().isEmpty() && this.serverField.getText().split(":").length > 0;
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
		this.client.options.lastServer = this.serverField.getText();
		this.client.options.save();
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 1) {
				this.parent.confirmResult(false, 0);
			} else if (button.id == 0) {
				this.info.address = this.serverField.getText();
				this.parent.confirmResult(true, 0);
			}
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (this.serverField.keyPressed(id, code)) {
			((ButtonWidget)this.buttons.get(0)).active = !this.serverField.getText().isEmpty() && this.serverField.getText().split(":").length > 0;
		} else if (code == 28 || code == 156) {
			this.buttonClicked((ButtonWidget)this.buttons.get(0));
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		this.serverField.method_920(mouseX, mouseY, button);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("selectServer.direct"), this.width / 2, 20, 16777215);
		this.drawWithShadow(this.textRenderer, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
		this.serverField.render();
		super.render(mouseX, mouseY, tickDelta);
	}
}
