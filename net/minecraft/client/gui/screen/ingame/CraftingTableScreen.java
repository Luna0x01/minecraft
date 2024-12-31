package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.class_3256;
import net.minecraft.class_3288;
import net.minecraft.class_3536;
import net.minecraft.class_4122;
import net.minecraft.client.gui.screen.RecipeBookScreen;
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
	private static final Identifier field_20399 = new Identifier("textures/gui/recipe_button.png");
	private final RecipeBookScreen field_16017 = new RecipeBookScreen();
	private boolean field_16018;
	private final PlayerInventory field_20398;

	public CraftingTableScreen(PlayerInventory playerInventory, World world) {
		this(playerInventory, world, BlockPos.ORIGIN);
	}

	public CraftingTableScreen(PlayerInventory playerInventory, World world, BlockPos blockPos) {
		super(new CraftingScreenHandler(playerInventory, world, blockPos));
		this.field_20398 = playerInventory;
	}

	@Override
	protected void init() {
		super.init();
		this.field_16018 = this.width < 379;
		this.field_16017.method_18793(this.width, this.height, this.client, this.field_16018, (class_3536)this.screenHandler);
		this.x = this.field_16017.method_14585(this.field_16018, this.width, this.backgroundWidth);
		this.field_20307.add(this.field_16017);
		this.addButton(
			new class_3256(10, this.x + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, field_20399) {
				@Override
				public void method_18374(double d, double e) {
					CraftingTableScreen.this.field_16017.method_18795(CraftingTableScreen.this.field_16018);
					CraftingTableScreen.this.field_16017.method_14587();
					CraftingTableScreen.this.x = CraftingTableScreen.this.field_16017
						.method_14585(CraftingTableScreen.this.field_16018, CraftingTableScreen.this.width, CraftingTableScreen.this.backgroundWidth);
					this.method_14476(CraftingTableScreen.this.x + 5, CraftingTableScreen.this.height / 2 - 49);
				}
			}
		);
	}

	@Nullable
	@Override
	public class_4122 getFocused() {
		return this.field_16017;
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
		this.textRenderer.method_18355(I18n.translate("container.crafting"), 28.0F, 6.0F, 4210752);
		this.textRenderer.method_18355(this.field_20398.getName().asFormattedString(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
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
	protected boolean method_1134(int i, int j, int k, int l, double d, double e) {
		return (!this.field_16018 || !this.field_16017.method_14590()) && super.method_1134(i, j, k, l, d, e);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_16017.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.field_16018 && this.field_16017.method_14590() ? true : super.mouseClicked(d, e, i);
		}
	}

	@Override
	protected boolean method_14549(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.backgroundWidth) || e >= (double)(j + this.backgroundHeight);
		return this.field_16017.method_18792(d, e, this.x, this.y, this.backgroundWidth, this.backgroundHeight, k) && bl;
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
