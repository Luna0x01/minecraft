package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.Direction;

public class SignEditScreen extends Screen {
	private final SignBlockEntity sign;
	private int ticksSinceOpened;
	private int currentRow;
	private ButtonWidget doneButton;

	public SignEditScreen(SignBlockEntity signBlockEntity) {
		this.sign = signBlockEntity;
	}

	@Override
	protected void init() {
		this.client.field_19946.method_18191(true);
		this.doneButton = this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 120, I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				SignEditScreen.this.method_18747();
			}
		});
		this.sign.setEditable(false);
	}

	@Override
	public void removed() {
		this.client.field_19946.method_18191(false);
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.sendPacket(
				new UpdateSignC2SPacket(this.sign.getPos(), this.sign.method_16836(0), this.sign.method_16836(1), this.sign.method_16836(2), this.sign.method_16836(3))
			);
		}

		this.sign.setEditable(true);
	}

	@Override
	public void tick() {
		this.ticksSinceOpened++;
	}

	private void method_18747() {
		this.sign.markDirty();
		this.client.setScreen(null);
	}

	@Override
	public boolean charTyped(char c, int i) {
		String string = this.sign.method_16836(this.currentRow).getString();
		if (SharedConstants.isValidChar(c) && this.textRenderer.getStringWidth(string + c) <= 90) {
			string = string + c;
		}

		this.sign.method_16837(this.currentRow, new LiteralText(string));
		return true;
	}

	@Override
	public void method_18608() {
		this.method_18747();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 265) {
			this.currentRow = this.currentRow - 1 & 3;
			return true;
		} else if (i == 264 || i == 257 || i == 335) {
			this.currentRow = this.currentRow + 1 & 3;
			return true;
		} else if (i == 259) {
			String string = this.sign.method_16836(this.currentRow).getString();
			if (!string.isEmpty()) {
				string = string.substring(0, string.length() - 1);
				this.sign.method_16837(this.currentRow, new LiteralText(string));
			}

			return true;
		} else {
			return super.keyPressed(i, j, k);
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
		GlStateManager.scale(-93.75F, -93.75F, -93.75F);
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		BlockState blockState = this.sign.method_16783();
		float g;
		if (blockState.getBlock() == Blocks.SIGN) {
			g = (float)((Integer)blockState.getProperty(StandingSignBlock.field_18517) * 360) / 16.0F;
		} else {
			g = ((Direction)blockState.getProperty(WallSignBlock.FACING)).method_12578();
		}

		GlStateManager.rotate(g, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(0.0F, -1.0625F, 0.0F);
		if (this.ticksSinceOpened / 6 % 2 == 0) {
			this.sign.lineBeingEdited = this.currentRow;
		}

		BlockEntityRenderDispatcher.INSTANCE.renderBlockEntity(this.sign, -0.5, -0.75, -0.5, 0.0F);
		this.sign.lineBeingEdited = -1;
		GlStateManager.popMatrix();
		super.render(mouseX, mouseY, tickDelta);
	}
}
