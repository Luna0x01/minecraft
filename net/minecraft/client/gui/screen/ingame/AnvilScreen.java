package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class AnvilScreen extends HandledScreen implements ScreenHandlerListener {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/anvil.png");
	private AnvilScreenHandler anvilScreenHandler;
	private TextFieldWidget renameTextField;
	private PlayerInventory playerInventory;

	public AnvilScreen(PlayerInventory playerInventory, World world) {
		super(new AnvilScreenHandler(playerInventory, world, MinecraftClient.getInstance().player));
		this.playerInventory = playerInventory;
		this.anvilScreenHandler = (AnvilScreenHandler)this.screenHandler;
	}

	@Override
	public void init() {
		super.init();
		Keyboard.enableRepeatEvents(true);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.renameTextField = new TextFieldWidget(0, this.textRenderer, i + 62, j + 24, 103, 12);
		this.renameTextField.setEditableColor(-1);
		this.renameTextField.setUneditableColor(-1);
		this.renameTextField.setHasBorder(false);
		this.renameTextField.setMaxLength(30);
		this.screenHandler.removeListener(this);
		this.screenHandler.addListener(this);
	}

	@Override
	public void removed() {
		super.removed();
		Keyboard.enableRepeatEvents(false);
		this.screenHandler.removeListener(this);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.textRenderer.draw(I18n.translate("container.repair"), 60, 6, 4210752);
		if (this.anvilScreenHandler.repairCost > 0) {
			int i = 8453920;
			boolean bl = true;
			String string = I18n.translate("container.repair.cost", this.anvilScreenHandler.repairCost);
			if (this.anvilScreenHandler.repairCost >= 40 && !this.client.player.abilities.creativeMode) {
				string = I18n.translate("container.repair.expensive");
				i = 16736352;
			} else if (!this.anvilScreenHandler.getSlot(2).hasStack()) {
				bl = false;
			} else if (!this.anvilScreenHandler.getSlot(2).canTakeItems(this.playerInventory.player)) {
				i = 16736352;
			}

			if (bl) {
				int j = 0xFF000000 | (i & 16579836) >> 2 | i & 0xFF000000;
				int k = this.backgroundWidth - 8 - this.textRenderer.getStringWidth(string);
				int l = 67;
				if (this.textRenderer.isUnicode()) {
					fill(k - 3, l - 2, this.backgroundWidth - 7, l + 10, -16777216);
					fill(k - 2, l - 1, this.backgroundWidth - 8, l + 9, -12895429);
				} else {
					this.textRenderer.draw(string, k, l + 1, j);
					this.textRenderer.draw(string, k + 1, l, j);
					this.textRenderer.draw(string, k + 1, l + 1, j);
				}

				this.textRenderer.draw(string, k, l, i);
			}
		}

		GlStateManager.enableLighting();
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (this.renameTextField.keyPressed(id, code)) {
			this.sendRenameUpdates();
		} else {
			super.keyPressed(id, code);
		}
	}

	private void sendRenameUpdates() {
		String string = this.renameTextField.getText();
		Slot slot = this.anvilScreenHandler.getSlot(0);
		if (slot != null && slot.hasStack() && !slot.getStack().hasCustomName() && string.equals(slot.getStack().getCustomName())) {
			string = "";
		}

		this.anvilScreenHandler.rename(string);
		this.client.player.networkHandler.sendPacket(new CustomPayloadC2SPacket("MC|ItemName", new PacketByteBuf(Unpooled.buffer()).writeString(string)));
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		this.renameTextField.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		super.render(mouseX, mouseY, tickDelta);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.renameTextField.render();
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		this.drawTexture(i + 59, j + 20, 0, this.backgroundHeight + (this.anvilScreenHandler.getSlot(0).hasStack() ? 0 : 16), 110, 16);
		if ((this.anvilScreenHandler.getSlot(0).hasStack() || this.anvilScreenHandler.getSlot(1).hasStack()) && !this.anvilScreenHandler.getSlot(2).hasStack()) {
			this.drawTexture(i + 99, j + 45, this.backgroundWidth, 0, 28, 21);
		}
	}

	@Override
	public void updateScreenHandler(ScreenHandler handler, List<ItemStack> list) {
		this.onScreenHandlerSlotUpdate(handler, 0, handler.getSlot(0).getStack());
	}

	@Override
	public void onScreenHandlerSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
		if (slotId == 0) {
			this.renameTextField.setText(stack == null ? "" : stack.getCustomName());
			this.renameTextField.setEditable(stack != null);
			if (stack != null) {
				this.sendRenameUpdates();
			}
		}
	}

	@Override
	public void onScreenHandlerPropertyUpdate(ScreenHandler handler, int propertyId, int value) {
	}

	@Override
	public void onScreenHandlerInventoryUpdate(ScreenHandler handler, Inventory inventory) {
	}
}
