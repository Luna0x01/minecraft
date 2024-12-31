package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_3256;
import net.minecraft.class_3288;
import net.minecraft.client.gui.screen.RecipeBookScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingTableScreen extends HandledScreen implements class_3288 {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/crafting_table.png");
	private class_3256 field_16016;
	private final RecipeBookScreen field_16017 = new RecipeBookScreen();
	private boolean field_16018;

	public CraftingTableScreen(PlayerInventory playerInventory, World world) {
		this(playerInventory, world, BlockPos.ORIGIN);
	}

	public CraftingTableScreen(PlayerInventory playerInventory, World world, BlockPos blockPos) {
		super(new CraftingScreenHandler(playerInventory, world, blockPos));
	}

	@Override
	public void init() {
		super.init();
		this.field_16018 = this.width < 379;
		this.field_16017.method_14577(this.width, this.height, this.client, this.field_16018, ((CraftingScreenHandler)this.screenHandler).craftingInv);
		this.x = this.field_16017.method_14585(this.field_16018, this.width, this.backgroundWidth);
		this.field_16016 = new class_3256(10, this.x + 5, this.height / 2 - 49, 20, 18, 0, 168, 19, TEXTURE);
		this.buttons.add(this.field_16016);
	}

	@Override
	public void tick() {
		super.tick();
		this.field_16017.method_14594();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		if (this.field_16017.method_14590() && this.field_16018) {
			this.drawBackground(tickDelta, mouseX, mouseY);
			this.field_16017.method_14575(mouseX, mouseY, tickDelta);
		} else {
			this.field_16017.method_14575(mouseX, mouseY, tickDelta);
			super.render(mouseX, mouseY, tickDelta);
			this.field_16017.method_14578(this.x, this.y, true, tickDelta);
		}

		this.renderTooltip(mouseX, mouseY);
		this.field_16017.method_14591(this.x, this.y, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.textRenderer.draw(I18n.translate("container.crafting"), 28, 6, 4210752);
		this.textRenderer.draw(I18n.translate("container.inventory"), 8, this.backgroundHeight - 96 + 2, 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = this.x;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}

	@Override
	protected boolean isPointWithinBounds(int posX, int posY, int width, int height, int pointX, int pointY) {
		return (!this.field_16018 || !this.field_16017.method_14590()) && super.isPointWithinBounds(posX, posY, width, height, pointX, pointY);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if (!this.field_16017.method_14576(mouseX, mouseY, button)) {
			if (!this.field_16018 || !this.field_16017.method_14590()) {
				super.mouseClicked(mouseX, mouseY, button);
			}
		}
	}

	@Override
	protected boolean method_14549(int i, int j, int k, int l) {
		boolean bl = i < k || j < l || i >= k + this.backgroundWidth || j >= l + this.backgroundHeight;
		return this.field_16017.method_14592(i, j, this.x, this.y, this.backgroundWidth, this.backgroundHeight) && bl;
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == 10) {
			this.field_16017.method_14586(this.field_16018, ((CraftingScreenHandler)this.screenHandler).craftingInv);
			this.field_16017.method_14587();
			this.x = this.field_16017.method_14585(this.field_16018, this.width, this.backgroundWidth);
			this.field_16016.method_14476(this.x + 5, this.height / 2 - 49);
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (!this.field_16017.method_14574(id, code)) {
			super.keyPressed(id, code);
		}
	}

	@Override
	protected void method_1131(Slot slot, int i, int j, ItemAction itemAction) {
		super.method_1131(slot, i, j, itemAction);
		this.field_16017.method_14579(slot);
	}

	@Override
	public void method_14637() {
		this.field_16017.method_14597();
	}

	@Override
	public void removed() {
		this.field_16017.method_14573();
		super.removed();
	}

	@Override
	public RecipeBookScreen method_14638() {
		return this.field_16017;
	}
}
