package net.minecraft.client.gui.screen.ingame;

import io.netty.buffer.Unpooled;
import javax.annotation.Nullable;
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

public class class_2845 extends Screen implements class_2844 {
	private TextFieldWidget field_13342;
	private TextFieldWidget field_13343;
	private final CommandBlockExecutor field_13344;
	private ButtonWidget done;
	private ButtonWidget cancel;
	private ButtonWidget field_13347;
	private boolean field_13348;
	private PathNodeMaker field_13349;

	public class_2845(CommandBlockExecutor commandBlockExecutor) {
		this.field_13344 = commandBlockExecutor;
	}

	@Override
	public void tick() {
		this.field_13342.tick();
	}

	@Override
	public void init() {
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
		this.done = this.addButton(new ButtonWidget(0, this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.done")));
		this.cancel = this.addButton(new ButtonWidget(1, this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.cancel")));
		this.field_13347 = this.addButton(new ButtonWidget(4, this.width / 2 + 150 - 20, 150, 20, 20, "O"));
		this.field_13342 = new TextFieldWidget(2, this.textRenderer, this.width / 2 - 150, 50, 300, 20);
		this.field_13342.setMaxLength(32500);
		this.field_13342.setFocused(true);
		this.field_13342.setText(this.field_13344.getCommand());
		this.field_13343 = new TextFieldWidget(3, this.textRenderer, this.width / 2 - 150, 150, 276, 20);
		this.field_13343.setMaxLength(32500);
		this.field_13343.setEditable(false);
		this.field_13343.setText("-");
		this.field_13348 = this.field_13344.isTrackingOutput();
		this.method_12197();
		this.done.active = !this.field_13342.getText().trim().isEmpty();
		this.field_13349 = new PathNodeMaker(this.field_13342, true) {
			@Nullable
			@Override
			public BlockPos method_12186() {
				return class_2845.this.field_13344.getBlockPos();
			}
		};
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 1) {
				this.field_13344.setTrackOutput(this.field_13348);
				this.client.setScreen(null);
			} else if (button.id == 0) {
				PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
				packetByteBuf.writeByte(this.field_13344.getType());
				this.field_13344.writeEntityId(packetByteBuf);
				packetByteBuf.writeString(this.field_13342.getText());
				packetByteBuf.writeBoolean(this.field_13344.isTrackingOutput());
				this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket("MC|AdvCmd", packetByteBuf));
				if (!this.field_13344.isTrackingOutput()) {
					this.field_13344.setLastOutput(null);
				}

				this.client.setScreen(null);
			} else if (button.id == 4) {
				this.field_13344.setTrackOutput(!this.field_13344.isTrackingOutput());
				this.method_12197();
			}
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		this.field_13349.method_12188();
		if (code == 15) {
			this.field_13349.method_12183();
		} else {
			this.field_13349.method_12187();
		}

		this.field_13342.keyPressed(id, code);
		this.field_13343.keyPressed(id, code);
		this.done.active = !this.field_13342.getText().trim().isEmpty();
		if (code == 28 || code == 156) {
			this.buttonClicked(this.done);
		} else if (code == 1) {
			this.buttonClicked(this.cancel);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		this.field_13342.method_920(mouseX, mouseY, button);
		this.field_13343.method_920(mouseX, mouseY, button);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("advMode.setCommand"), this.width / 2, 20, 16777215);
		this.drawWithShadow(this.textRenderer, I18n.translate("advMode.command"), this.width / 2 - 150, 40, 10526880);
		this.field_13342.render();
		int i = 75;
		int j = 0;
		this.drawWithShadow(this.textRenderer, I18n.translate("advMode.nearestPlayer"), this.width / 2 - 140, i + j++ * this.textRenderer.fontHeight, 10526880);
		this.drawWithShadow(this.textRenderer, I18n.translate("advMode.randomPlayer"), this.width / 2 - 140, i + j++ * this.textRenderer.fontHeight, 10526880);
		this.drawWithShadow(this.textRenderer, I18n.translate("advMode.allPlayers"), this.width / 2 - 140, i + j++ * this.textRenderer.fontHeight, 10526880);
		this.drawWithShadow(this.textRenderer, I18n.translate("advMode.allEntities"), this.width / 2 - 140, i + j++ * this.textRenderer.fontHeight, 10526880);
		this.drawWithShadow(this.textRenderer, I18n.translate("advMode.self"), this.width / 2 - 140, i + j++ * this.textRenderer.fontHeight, 10526880);
		if (!this.field_13343.getText().isEmpty()) {
			i += j * this.textRenderer.fontHeight + 20;
			this.drawWithShadow(this.textRenderer, I18n.translate("advMode.previousOutput"), this.width / 2 - 150, i, 10526880);
			this.field_13343.render();
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	private void method_12197() {
		if (this.field_13344.isTrackingOutput()) {
			this.field_13347.message = "O";
			if (this.field_13344.getLastOutput() != null) {
				this.field_13343.setText(this.field_13344.getLastOutput().asUnformattedString());
			}
		} else {
			this.field_13347.message = "X";
			this.field_13343.setText("-");
		}
	}

	@Override
	public void method_12182(String... strings) {
		this.field_13349.method_12185(strings);
	}
}
