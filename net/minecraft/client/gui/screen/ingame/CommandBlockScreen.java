package net.minecraft.client.gui.screen.ingame;

import io.netty.buffer.Unpooled;
import javax.annotation.Nullable;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.class_2844;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CommandBlockExecutor;
import org.lwjgl.input.Keyboard;

public class CommandBlockScreen extends Screen implements class_2844 {
	private TextFieldWidget input;
	private TextFieldWidget output;
	private final CommandBlockBlockEntity field_13331;
	private ButtonWidget doneButton;
	private ButtonWidget cancelButton;
	private ButtonWidget trackingOutputToggleButton;
	private ButtonWidget field_13332;
	private ButtonWidget field_13333;
	private ButtonWidget field_13334;
	private boolean trackingOutput;
	private CommandBlockBlockEntity.class_2736 field_13335 = CommandBlockBlockEntity.class_2736.REDSTONE;
	private PathNodeMaker field_13336;
	private boolean field_13337;
	private boolean field_13338;

	public CommandBlockScreen(CommandBlockBlockEntity commandBlockBlockEntity) {
		this.field_13331 = commandBlockBlockEntity;
	}

	@Override
	public void tick() {
		this.input.tick();
	}

	@Override
	public void init() {
		final CommandBlockExecutor commandBlockExecutor = this.field_13331.getCommandExecutor();
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
		this.doneButton = this.addButton(new ButtonWidget(0, this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.done")));
		this.cancelButton = this.addButton(new ButtonWidget(1, this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.cancel")));
		this.trackingOutputToggleButton = this.addButton(new ButtonWidget(4, this.width / 2 + 150 - 20, 135, 20, 20, "O"));
		this.field_13332 = this.addButton(new ButtonWidget(5, this.width / 2 - 50 - 100 - 4, 165, 100, 20, I18n.translate("advMode.mode.sequence")));
		this.field_13333 = this.addButton(new ButtonWidget(6, this.width / 2 - 50, 165, 100, 20, I18n.translate("advMode.mode.unconditional")));
		this.field_13334 = this.addButton(new ButtonWidget(7, this.width / 2 + 50 + 4, 165, 100, 20, I18n.translate("advMode.mode.redstoneTriggered")));
		this.input = new TextFieldWidget(2, this.textRenderer, this.width / 2 - 150, 50, 300, 20);
		this.input.setMaxLength(32500);
		this.input.setFocused(true);
		this.output = new TextFieldWidget(3, this.textRenderer, this.width / 2 - 150, 135, 276, 20);
		this.output.setMaxLength(32500);
		this.output.setEditable(false);
		this.output.setText("-");
		this.doneButton.active = false;
		this.trackingOutputToggleButton.active = false;
		this.field_13332.active = false;
		this.field_13333.active = false;
		this.field_13334.active = false;
		this.field_13336 = new PathNodeMaker(this.input, true) {
			@Nullable
			@Override
			public BlockPos method_12186() {
				return commandBlockExecutor.getBlockPos();
			}
		};
	}

	public void method_12191() {
		CommandBlockExecutor commandBlockExecutor = this.field_13331.getCommandExecutor();
		this.input.setText(commandBlockExecutor.getCommand());
		this.trackingOutput = commandBlockExecutor.isTrackingOutput();
		this.field_13335 = this.field_13331.method_11657();
		this.field_13337 = this.field_13331.method_11658();
		this.field_13338 = this.field_13331.method_11654();
		this.updateButtonTexts();
		this.method_12192();
		this.method_12194();
		this.method_12195();
		this.doneButton.active = true;
		this.trackingOutputToggleButton.active = true;
		this.field_13332.active = true;
		this.field_13333.active = true;
		this.field_13334.active = true;
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			CommandBlockExecutor commandBlockExecutor = this.field_13331.getCommandExecutor();
			if (button.id == 1) {
				commandBlockExecutor.setTrackOutput(this.trackingOutput);
				this.client.setScreen(null);
			} else if (button.id == 0) {
				PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
				commandBlockExecutor.writeEntityId(packetByteBuf);
				packetByteBuf.writeString(this.input.getText());
				packetByteBuf.writeBoolean(commandBlockExecutor.isTrackingOutput());
				packetByteBuf.writeString(this.field_13335.name());
				packetByteBuf.writeBoolean(this.field_13337);
				packetByteBuf.writeBoolean(this.field_13338);
				this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket("MC|AutoCmd", packetByteBuf));
				if (!commandBlockExecutor.isTrackingOutput()) {
					commandBlockExecutor.setLastOutput(null);
				}

				this.client.setScreen(null);
			} else if (button.id == 4) {
				commandBlockExecutor.setTrackOutput(!commandBlockExecutor.isTrackingOutput());
				this.updateButtonTexts();
			} else if (button.id == 5) {
				this.method_12193();
				this.method_12192();
			} else if (button.id == 6) {
				this.field_13337 = !this.field_13337;
				this.method_12194();
			} else if (button.id == 7) {
				this.field_13338 = !this.field_13338;
				this.method_12195();
			}
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		this.field_13336.method_12188();
		if (code == 15) {
			this.field_13336.method_12183();
		} else {
			this.field_13336.method_12187();
		}

		this.input.keyPressed(id, code);
		this.output.keyPressed(id, code);
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
		if (!this.output.getText().isEmpty()) {
			i += j * this.textRenderer.fontHeight + 1;
			this.drawWithShadow(this.textRenderer, I18n.translate("advMode.previousOutput"), this.width / 2 - 150, i, 10526880);
			this.output.render();
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	private void updateButtonTexts() {
		CommandBlockExecutor commandBlockExecutor = this.field_13331.getCommandExecutor();
		if (commandBlockExecutor.isTrackingOutput()) {
			this.trackingOutputToggleButton.message = "O";
			if (commandBlockExecutor.getLastOutput() != null) {
				this.output.setText(commandBlockExecutor.getLastOutput().asUnformattedString());
			}
		} else {
			this.trackingOutputToggleButton.message = "X";
			this.output.setText("-");
		}
	}

	private void method_12192() {
		switch (this.field_13335) {
			case SEQUENCE:
				this.field_13332.message = I18n.translate("advMode.mode.sequence");
				break;
			case AUTO:
				this.field_13332.message = I18n.translate("advMode.mode.auto");
				break;
			case REDSTONE:
				this.field_13332.message = I18n.translate("advMode.mode.redstone");
		}
	}

	private void method_12193() {
		switch (this.field_13335) {
			case SEQUENCE:
				this.field_13335 = CommandBlockBlockEntity.class_2736.AUTO;
				break;
			case AUTO:
				this.field_13335 = CommandBlockBlockEntity.class_2736.REDSTONE;
				break;
			case REDSTONE:
				this.field_13335 = CommandBlockBlockEntity.class_2736.SEQUENCE;
		}
	}

	private void method_12194() {
		if (this.field_13337) {
			this.field_13333.message = I18n.translate("advMode.mode.conditional");
		} else {
			this.field_13333.message = I18n.translate("advMode.mode.unconditional");
		}
	}

	private void method_12195() {
		if (this.field_13338) {
			this.field_13334.message = I18n.translate("advMode.mode.autoexec.bat");
		} else {
			this.field_13334.message = I18n.translate("advMode.mode.redstoneTriggered");
		}
	}

	@Override
	public void method_12182(String... strings) {
		this.field_13336.method_12185(strings);
	}
}
