package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_3256;
import net.minecraft.class_3288;
import net.minecraft.class_3536;
import net.minecraft.class_4172;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.client.gui.screen.RecipeBookScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemAction;

public class FurnaceScreen extends HandledScreen implements class_3288 {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/furnace.png");
	private static final Identifier field_20405 = new Identifier("textures/gui/recipe_button.png");
	private final PlayerInventory playerInventory;
	private final Inventory furnaceInventory;
	public final class_4172 field_20404 = new class_4172();
	private boolean field_20403;

	public FurnaceScreen(PlayerInventory playerInventory, Inventory inventory) {
		super(new FurnaceScreenHandler(playerInventory, inventory));
		this.playerInventory = playerInventory;
		this.furnaceInventory = inventory;
	}

	@Override
	public void init() {
		super.init();
		this.field_20403 = this.width < 379;
		this.field_20404.method_18793(this.width, this.height, this.client, this.field_20403, (class_3536)this.screenHandler);
		this.x = this.field_20404.method_14585(this.field_20403, this.width, this.backgroundWidth);
		this.addButton(
			new class_3256(10, this.x + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, field_20405) {
				@Override
				public void method_18374(double d, double e) {
					FurnaceScreen.this.field_20404.method_18795(FurnaceScreen.this.field_20403);
					FurnaceScreen.this.field_20404.method_14587();
					FurnaceScreen.this.x = FurnaceScreen.this.field_20404
						.method_14585(FurnaceScreen.this.field_20403, FurnaceScreen.this.width, FurnaceScreen.this.backgroundWidth);
					this.method_14476(FurnaceScreen.this.x + 20, FurnaceScreen.this.height / 2 - 49);
				}
			}
		);
	}

	@Override
	public void tick() {
		super.tick();
		this.field_20404.method_14594();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		if (this.field_20404.method_14590() && this.field_20403) {
			this.drawBackground(tickDelta, mouseX, mouseY);
			this.field_20404.method_14575(mouseX, mouseY, tickDelta);
		} else {
			this.field_20404.method_14575(mouseX, mouseY, tickDelta);
			super.render(mouseX, mouseY, tickDelta);
			this.field_20404.method_14578(this.x, this.y, true, tickDelta);
		}

		this.renderTooltip(mouseX, mouseY);
		this.field_20404.method_14591(this.x, this.y, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		String string = this.furnaceInventory.getName().asFormattedString();
		this.textRenderer.method_18355(string, (float)(this.backgroundWidth / 2 - this.textRenderer.getStringWidth(string) / 2), 6.0F, 4210752);
		this.textRenderer.method_18355(this.playerInventory.getName().asFormattedString(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = this.x;
		int j = this.y;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		if (FurnaceBlockEntity.isLit(this.furnaceInventory)) {
			int k = this.getFuelProgress(13);
			this.drawTexture(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
		}

		int l = this.getCookProgress(24);
		this.drawTexture(i + 79, j + 34, 176, 14, l + 1, 16);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_20404.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.field_20403 && this.field_20404.method_14590() ? true : super.mouseClicked(d, e, i);
		}
	}

	@Override
	protected void method_1131(Slot slot, int i, int j, ItemAction itemAction) {
		super.method_1131(slot, i, j, itemAction);
		this.field_20404.method_14579(slot);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return this.field_20404.keyPressed(i, j, k) ? false : super.keyPressed(i, j, k);
	}

	@Override
	protected boolean method_14549(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.backgroundWidth) || e >= (double)(j + this.backgroundHeight);
		return this.field_20404.method_18792(d, e, this.x, this.y, this.backgroundWidth, this.backgroundHeight, k) && bl;
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.field_20404.charTyped(c, i) ? true : super.charTyped(c, i);
	}

	@Override
	public void method_14637() {
		this.field_20404.method_14597();
	}

	@Override
	public RecipeBookScreen method_14638() {
		return this.field_20404;
	}

	@Override
	public void removed() {
		this.field_20404.method_14573();
		super.removed();
	}

	private int getCookProgress(int cookBarSteps) {
		int i = this.furnaceInventory.getProperty(2);
		int j = this.furnaceInventory.getProperty(3);
		return j != 0 && i != 0 ? i * cookBarSteps / j : 0;
	}

	private int getFuelProgress(int fuelBarSteps) {
		int i = this.furnaceInventory.getProperty(1);
		if (i == 0) {
			i = 200;
		}

		return this.furnaceInventory.getProperty(0) * fuelBarSteps / i;
	}
}
