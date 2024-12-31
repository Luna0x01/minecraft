package net.minecraft.client.gui.screen;

import java.net.IDN;
import java.util.function.Predicate;
import net.minecraft.class_4122;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.ChatUtil;

public class AddServerScreen extends Screen {
	private ButtonWidget field_20268;
	private final Screen parent;
	private final ServerInfo server;
	private TextFieldWidget addressField;
	private TextFieldWidget serverNameField;
	private ButtonWidget resourcePackOptionButton;
	private final Predicate<String> field_20269 = string -> {
		if (ChatUtil.isEmpty(string)) {
			return true;
		} else {
			String[] strings = string.split(":");
			if (strings.length == 0) {
				return true;
			} else {
				try {
					String string2 = IDN.toASCII(strings[0]);
					return true;
				} catch (IllegalArgumentException var3) {
					return false;
				}
			}
		}
	};

	public AddServerScreen(Screen screen, ServerInfo serverInfo) {
		this.parent = screen;
		this.server = serverInfo;
	}

	@Override
	public void tick() {
		this.serverNameField.tick();
		this.addressField.tick();
	}

	@Override
	public class_4122 getFocused() {
		return this.addressField.isFocused() ? this.addressField : this.serverNameField;
	}

	@Override
	protected void init() {
		this.client.field_19946.method_18191(true);
		this.field_20268 = this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 96 + 18, I18n.translate("addServer.add")) {
			@Override
			public void method_18374(double d, double e) {
				AddServerScreen.this.method_18591();
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 120 + 18, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				AddServerScreen.this.parent.confirmResult(false, 0);
			}
		});
		this.resourcePackOptionButton = this.addButton(
			new ButtonWidget(
				2,
				this.width / 2 - 100,
				this.height / 4 + 72,
				I18n.translate("addServer.resourcePack") + ": " + this.server.getResourcePack().getName().asFormattedString()
			) {
				@Override
				public void method_18374(double d, double e) {
					AddServerScreen.this.server
						.setResourcePackState(
							ServerInfo.ResourcePackState.values()[(AddServerScreen.this.server.getResourcePack().ordinal() + 1) % ServerInfo.ResourcePackState.values().length]
						);
					AddServerScreen.this.resourcePackOptionButton.message = I18n.translate("addServer.resourcePack")
						+ ": "
						+ AddServerScreen.this.server.getResourcePack().getName().asFormattedString();
				}
			}
		);
		this.addressField = new TextFieldWidget(1, this.textRenderer, this.width / 2 - 100, 106, 200, 20) {
			@Override
			public void setFocused(boolean focused) {
				super.setFocused(focused);
				if (focused) {
					AddServerScreen.this.serverNameField.setFocused(false);
				}
			}
		};
		this.addressField.setMaxLength(128);
		this.addressField.setText(this.server.address);
		this.addressField.method_18389(this.field_20269);
		this.addressField.method_18387(this::method_18583);
		this.field_20307.add(this.addressField);
		this.serverNameField = new TextFieldWidget(0, this.textRenderer, this.width / 2 - 100, 66, 200, 20) {
			@Override
			public void setFocused(boolean focused) {
				super.setFocused(focused);
				if (focused) {
					AddServerScreen.this.addressField.setFocused(false);
				}
			}
		};
		this.serverNameField.setFocused(true);
		this.serverNameField.setText(this.server.name);
		this.serverNameField.method_18387(this::method_18583);
		this.field_20307.add(this.serverNameField);
		this.method_18608();
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.addressField.getText();
		String string2 = this.serverNameField.getText();
		this.init(client, width, height);
		this.addressField.setText(string);
		this.serverNameField.setText(string2);
	}

	private void method_18583(int i, String string) {
		this.method_18608();
	}

	@Override
	public void removed() {
		this.client.field_19946.method_18191(false);
	}

	private void method_18591() {
		this.server.name = this.serverNameField.getText();
		this.server.address = this.addressField.getText();
		this.parent.confirmResult(true, 0);
	}

	@Override
	public void method_18608() {
		this.field_20268.active = !this.addressField.getText().isEmpty()
			&& this.addressField.getText().split(":").length > 0
			&& !this.serverNameField.getText().isEmpty();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 258) {
			if (this.serverNameField.isFocused()) {
				this.addressField.setFocused(true);
			} else {
				this.serverNameField.setFocused(true);
			}

			return true;
		} else if ((i == 257 || i == 335) && this.field_20268.active) {
			this.method_18591();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("addServer.title"), this.width / 2, 17, 16777215);
		this.drawWithShadow(this.textRenderer, I18n.translate("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
		this.drawWithShadow(this.textRenderer, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
		this.serverNameField.method_18385(mouseX, mouseY, tickDelta);
		this.addressField.method_18385(mouseX, mouseY, tickDelta);
		super.render(mouseX, mouseY, tickDelta);
	}
}
