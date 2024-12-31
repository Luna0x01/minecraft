package net.minecraft.client.gui.screen.ingame;

import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.CommandBlockExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class CommandBlockScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private TextFieldWidget input;
	private TextFieldWidget output;
	private final CommandBlockExecutor executor;
	private ButtonWidget doneButton;
	private ButtonWidget cancelButton;
	private ButtonWidget trackingOutputToggleButton;
	private boolean trackingOutput;

	public CommandBlockScreen(CommandBlockExecutor commandBlockExecutor) {
		this.executor = commandBlockExecutor;
	}

	@Override
	public void tick() {
		this.input.tick();
	}

	@Override
	public void init() {
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
		this.buttons.add(this.doneButton = new ButtonWidget(0, this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.done")));
		this.buttons.add(this.cancelButton = new ButtonWidget(1, this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.cancel")));
		this.buttons.add(this.trackingOutputToggleButton = new ButtonWidget(4, this.width / 2 + 150 - 20, 150, 20, 20, "O"));
		this.input = new TextFieldWidget(2, this.textRenderer, this.width / 2 - 150, 50, 300, 20);
		this.input.setMaxLength(32767);
		this.input.setFocused(true);
		this.input.setText(this.executor.getCommand());
		this.output = new TextFieldWidget(3, this.textRenderer, this.width / 2 - 150, 150, 276, 20);
		this.output.setMaxLength(32767);
		this.output.setEditable(false);
		this.output.setText("-");
		this.trackingOutput = this.executor.isTrackingOutput();
		this.updateButtonTexts();
		this.doneButton.active = this.input.getText().trim().length() > 0;
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 1) {
				this.executor.setTrackOutput(this.trackingOutput);
				this.client.setScreen(null);
			} else if (button.id == 0) {
				PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
				packetByteBuf.writeByte(this.executor.getType());
				this.executor.writeEntityId(packetByteBuf);
				packetByteBuf.writeString(this.input.getText());
				packetByteBuf.writeBoolean(this.executor.isTrackingOutput());
				this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket("MC|AdvCdm", packetByteBuf));
				if (!this.executor.isTrackingOutput()) {
					this.executor.setLastOutput(null);
				}

				this.client.setScreen(null);
			} else if (button.id == 4) {
				this.executor.setTrackOutput(!this.executor.isTrackingOutput());
				this.updateButtonTexts();
			}
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		this.input.keyPressed(id, code);
		this.output.keyPressed(id, code);
		this.doneButton.active = this.input.getText().trim().length() > 0;
		if (code == 28 || code == 156) {
			this.buttonClicked(this.doneButton);
		} else if (code == 1) {
			this.buttonClicked(this.cancelButton);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		this.input.mouseClicked(mouseX, mouseY, button);
		this.output.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("advMode.setCommand"), this.width / 2, 20, 16777215);
		this.drawWithShadow(this.textRenderer, I18n.translate("advMode.command"), this.width / 2 - 150, 37, 10526880);
		this.input.render();
		int i = 75;
		int j = 0;
		this.drawWithShadow(this.textRenderer, I18n.translate("advMode.nearestPlayer"), this.width / 2 - 150, i + j++ * this.textRenderer.fontHeight, 10526880);
		this.drawWithShadow(this.textRenderer, I18n.translate("advMode.randomPlayer"), this.width / 2 - 150, i + j++ * this.textRenderer.fontHeight, 10526880);
		this.drawWithShadow(this.textRenderer, I18n.translate("advMode.allPlayers"), this.width / 2 - 150, i + j++ * this.textRenderer.fontHeight, 10526880);
		this.drawWithShadow(this.textRenderer, I18n.translate("advMode.allEntities"), this.width / 2 - 150, i + j++ * this.textRenderer.fontHeight, 10526880);
		this.drawWithShadow(this.textRenderer, "", this.width / 2 - 150, i + j++ * this.textRenderer.fontHeight, 10526880);
		if (this.output.getText().length() > 0) {
			i += j * this.textRenderer.fontHeight + 16;
			this.drawWithShadow(this.textRenderer, I18n.translate("advMode.previousOutput"), this.width / 2 - 150, i, 10526880);
			this.output.render();
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	private void updateButtonTexts() {
		if (this.executor.isTrackingOutput()) {
			this.trackingOutputToggleButton.message = "O";
			if (this.executor.getLastOutput() != null) {
				this.output.setText(this.executor.getLastOutput().asUnformattedString());
			}
		} else {
			this.trackingOutputToggleButton.message = "X";
			this.output.setText("-");
		}
	}
}
