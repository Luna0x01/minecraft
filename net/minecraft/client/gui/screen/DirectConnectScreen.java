package net.minecraft.client.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;

public class DirectConnectScreen extends Screen {
	private ButtonWidget field_20264;
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
	protected void init() {
		this.client.field_19946.method_18191(true);
		this.field_20264 = this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.translate("selectServer.select")) {
			@Override
			public void method_18374(double d, double e) {
				DirectConnectScreen.this.method_18580();
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				DirectConnectScreen.this.parent.confirmResult(false, 0);
			}
		});
		this.serverField = new TextFieldWidget(2, this.textRenderer, this.width / 2 - 100, 116, 200, 20);
		this.serverField.setMaxLength(128);
		this.serverField.setFocused(true);
		this.serverField.setText(this.client.options.lastServer);
		this.field_20307.add(this.serverField);
		this.method_18421(this.serverField);
		this.method_18581();
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.serverField.getText();
		this.init(client, width, height);
		this.serverField.setText(string);
	}

	private void method_18580() {
		this.info.address = this.serverField.getText();
		this.parent.confirmResult(true, 0);
	}

	@Override
	public void removed() {
		this.client.field_19946.method_18191(false);
		this.client.options.lastServer = this.serverField.getText();
		this.client.options.save();
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (this.serverField.charTyped(c, i)) {
			this.method_18581();
			return true;
		} else {
			return false;
		}
	}

	private void method_18581() {
		this.field_20264.active = !this.serverField.getText().isEmpty() && this.serverField.getText().split(":").length > 0;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 257 || i == 335) {
			this.method_18580();
			return true;
		} else if (super.keyPressed(i, j, k)) {
			this.method_18581();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("selectServer.direct"), this.width / 2, 20, 16777215);
		this.drawWithShadow(this.textRenderer, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
		this.serverField.method_18385(mouseX, mouseY, tickDelta);
		super.render(mouseX, mouseY, tickDelta);
	}
}
