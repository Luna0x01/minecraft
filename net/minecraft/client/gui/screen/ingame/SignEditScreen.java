package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.SharedConstants;
import org.lwjgl.input.Keyboard;

public class SignEditScreen extends Screen {
	private SignBlockEntity sign;
	private int ticksSinceOpened;
	private int currentRow;
	private ButtonWidget doneButton;

	public SignEditScreen(SignBlockEntity signBlockEntity) {
		this.sign = signBlockEntity;
	}

	@Override
	public void init() {
		this.buttons.clear();
		Keyboard.enableRepeatEvents(true);
		this.buttons.add(this.doneButton = new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 120, I18n.translate("gui.done")));
		this.sign.setEditable(false);
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.sendPacket(new UpdateSignC2SPacket(this.sign.getPos(), this.sign.text));
		}

		this.sign.setEditable(true);
	}

	@Override
	public void tick() {
		this.ticksSinceOpened++;
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 0) {
				this.sign.markDirty();
				this.client.setScreen(null);
			}
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (code == 200) {
			this.currentRow = this.currentRow - 1 & 3;
		}

		if (code == 208 || code == 28 || code == 156) {
			this.currentRow = this.currentRow + 1 & 3;
		}

		String string = this.sign.text[this.currentRow].asUnformattedString();
		if (code == 14 && !string.isEmpty()) {
			string = string.substring(0, string.length() - 1);
		}

		if (SharedConstants.isValidChar(id) && this.textRenderer.getStringWidth(string + id) <= 90) {
			string = string + id;
		}

		this.sign.text[this.currentRow] = new LiteralText(string);
		if (code == 1) {
			this.buttonClicked(this.doneButton);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("sign.edit"), this.width / 2, 40, 16777215);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)(this.width / 2), 0.0F, 50.0F);
		float f = 93.75F;
		GlStateManager.scale(-f, -f, -f);
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		Block block = this.sign.getBlock();
		if (block == Blocks.STANDING_SIGN) {
			float g = (float)(this.sign.getDataValue() * 360) / 16.0F;
			GlStateManager.rotate(g, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0625F, 0.0F);
		} else {
			int i = this.sign.getDataValue();
			float h = 0.0F;
			if (i == 2) {
				h = 180.0F;
			}

			if (i == 4) {
				h = 90.0F;
			}

			if (i == 5) {
				h = -90.0F;
			}

			GlStateManager.rotate(h, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0625F, 0.0F);
		}

		if (this.ticksSinceOpened / 6 % 2 == 0) {
			this.sign.lineBeingEdited = this.currentRow;
		}

		BlockEntityRenderDispatcher.INSTANCE.renderBlockEntity(this.sign, -0.5, -0.75, -0.5, 0.0F);
		this.sign.lineBeingEdited = -1;
		GlStateManager.popMatrix();
		super.render(mouseX, mouseY, tickDelta);
	}
}
