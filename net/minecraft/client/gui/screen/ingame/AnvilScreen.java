package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_4122;
import net.minecraft.class_4388;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class AnvilScreen extends HandledScreen implements ScreenHandlerListener {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/anvil.png");
	private final AnvilScreenHandler anvilScreenHandler;
	private TextFieldWidget renameTextField;
	private final PlayerInventory playerInventory;

	public AnvilScreen(PlayerInventory playerInventory, World world) {
		super(new AnvilScreenHandler(playerInventory, world, MinecraftClient.getInstance().player));
		this.playerInventory = playerInventory;
		this.anvilScreenHandler = (AnvilScreenHandler)this.screenHandler;
	}

	@Override
	public class_4122 getFocused() {
		return this.renameTextField.isFocused() ? this.renameTextField : null;
	}

	@Override
	protected void init() {
		super.init();
		this.client.field_19946.method_18191(true);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.renameTextField = new TextFieldWidget(0, this.textRenderer, i + 62, j + 24, 103, 12);
		this.renameTextField.setEditableColor(-1);
		this.renameTextField.setUneditableColor(-1);
		this.renameTextField.setHasBorder(false);
		this.renameTextField.setMaxLength(35);
		this.renameTextField.method_18387(this::method_18682);
		this.field_20307.add(this.renameTextField);
		this.screenHandler.removeListener(this);
		this.screenHandler.addListener(this);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.renameTextField.getText();
		this.init(client, width, height);
		this.renameTextField.setText(string);
	}

	@Override
	public void removed() {
		super.removed();
		this.client.field_19946.method_18191(false);
		this.screenHandler.removeListener(this);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.textRenderer.method_18355(I18n.translate("container.repair"), 60.0F, 6.0F, 4210752);
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
				int j = this.backgroundWidth - 8 - this.textRenderer.getStringWidth(string) - 2;
				int k = 69;
				fill(j - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
				this.textRenderer.drawWithShadow(string, (float)j, 69.0F, i);
			}
		}

		GlStateManager.enableLighting();
	}

	private void method_18682(int i, String string) {
		if (!string.isEmpty()) {
			String string2 = string;
			Slot slot = this.anvilScreenHandler.getSlot(0);
			if (slot != null && slot.hasStack() && !slot.getStack().hasCustomName() && string.equals(slot.getStack().getName().getString())) {
				string2 = "";
			}

			this.anvilScreenHandler.rename(string2);
			this.client.player.networkHandler.sendPacket(new class_4388(string2));
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		super.render(mouseX, mouseY, tickDelta);
		this.renderTooltip(mouseX, mouseY);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.renameTextField.method_18385(mouseX, mouseY, tickDelta);
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
	public void method_13643(ScreenHandler screenHandler, DefaultedList<ItemStack> defaultedList) {
		this.onScreenHandlerSlotUpdate(screenHandler, 0, screenHandler.getSlot(0).getStack());
	}

	@Override
	public void onScreenHandlerSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
		if (slotId == 0) {
			this.renameTextField.setText(stack.isEmpty() ? "" : stack.getName().getString());
			this.renameTextField.setEditable(!stack.isEmpty());
		}
	}

	@Override
	public void onScreenHandlerPropertyUpdate(ScreenHandler handler, int propertyId, int value) {
	}

	@Override
	public void onScreenHandlerInventoryUpdate(ScreenHandler handler, Inventory inventory) {
	}
}
