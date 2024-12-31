package net.minecraft.client.gui.screen;

import com.google.common.base.Predicate;
import java.net.IDN;
import javax.annotation.Nullable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.ChatUtil;
import org.lwjgl.input.Keyboard;

public class AddServerScreen extends Screen {
	private final Screen parent;
	private final ServerInfo server;
	private TextFieldWidget addressField;
	private TextFieldWidget serverNameField;
	private ButtonWidget resourcePackOptionButton;
	private final Predicate<String> addressTextFilter = new Predicate<String>() {
		public boolean apply(@Nullable String string) {
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
					} catch (IllegalArgumentException var4) {
						return false;
					}
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
	public void init() {
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
		this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 96 + 18, I18n.translate("addServer.add")));
		this.buttons.add(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 120 + 18, I18n.translate("gui.cancel")));
		this.resourcePackOptionButton = this.addButton(
			new ButtonWidget(
				2,
				this.width / 2 - 100,
				this.height / 4 + 72,
				I18n.translate("addServer.resourcePack") + ": " + this.server.getResourcePack().getName().asFormattedString()
			)
		);
		this.serverNameField = new TextFieldWidget(0, this.textRenderer, this.width / 2 - 100, 66, 200, 20);
		this.serverNameField.setFocused(true);
		this.serverNameField.setText(this.server.name);
		this.addressField = new TextFieldWidget(1, this.textRenderer, this.width / 2 - 100, 106, 200, 20);
		this.addressField.setMaxLength(128);
		this.addressField.setText(this.server.address);
		this.addressField.setTextPredicate(this.addressTextFilter);
		((ButtonWidget)this.buttons.get(0)).active = !this.addressField.getText().isEmpty()
			&& this.addressField.getText().split(":").length > 0
			&& !this.serverNameField.getText().isEmpty();
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 2) {
				this.server
					.setResourcePackState(ServerInfo.ResourcePackState.values()[(this.server.getResourcePack().ordinal() + 1) % ServerInfo.ResourcePackState.values().length]);
				this.resourcePackOptionButton.message = I18n.translate("addServer.resourcePack") + ": " + this.server.getResourcePack().getName().asFormattedString();
			} else if (button.id == 1) {
				this.parent.confirmResult(false, 0);
			} else if (button.id == 0) {
				this.server.name = this.serverNameField.getText();
				this.server.address = this.addressField.getText();
				this.parent.confirmResult(true, 0);
			}
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		this.serverNameField.keyPressed(id, code);
		this.addressField.keyPressed(id, code);
		if (code == 15) {
			this.serverNameField.setFocused(!this.serverNameField.isFocused());
			this.addressField.setFocused(!this.addressField.isFocused());
		}

		if (code == 28 || code == 156) {
			this.buttonClicked((ButtonWidget)this.buttons.get(0));
		}

		((ButtonWidget)this.buttons.get(0)).active = !this.addressField.getText().isEmpty()
			&& this.addressField.getText().split(":").length > 0
			&& !this.serverNameField.getText().isEmpty();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		this.addressField.method_920(mouseX, mouseY, button);
		this.serverNameField.method_920(mouseX, mouseY, button);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("addServer.title"), this.width / 2, 17, 16777215);
		this.drawWithShadow(this.textRenderer, I18n.translate("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
		this.drawWithShadow(this.textRenderer, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
		this.serverNameField.render();
		this.addressField.render();
		super.render(mouseX, mouseY, tickDelta);
	}
}
