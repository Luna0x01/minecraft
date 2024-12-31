package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_3256;
import net.minecraft.class_3288;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.RecipeBookScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.ItemAction;

public class SurvivalInventoryScreen extends InventoryScreen implements class_3288 {
	private float mouseX;
	private float mouseY;
	private class_3256 field_16021;
	private final RecipeBookScreen field_16022 = new RecipeBookScreen();
	private boolean field_16019;
	private boolean field_16020;

	public SurvivalInventoryScreen(PlayerEntity playerEntity) {
		super(playerEntity.playerScreenHandler);
		this.passEvents = true;
	}

	@Override
	public void tick() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			this.client.setScreen(new CreativeInventoryScreen(this.client.player));
		}

		this.field_16022.method_14594();
	}

	@Override
	public void init() {
		this.buttons.clear();
		if (this.client.interactionManager.hasCreativeInventory()) {
			this.client.setScreen(new CreativeInventoryScreen(this.client.player));
		} else {
			super.init();
		}

		this.field_16019 = this.width < 379;
		this.field_16022.method_14577(this.width, this.height, this.client, this.field_16019, ((PlayerScreenHandler)this.screenHandler).craftingInventory);
		this.x = this.field_16022.method_14585(this.field_16019, this.width, this.backgroundWidth);
		this.field_16021 = new class_3256(10, this.x + 104, this.height / 2 - 22, 20, 18, 178, 0, 19, INVENTORY_TEXTURE);
		this.buttons.add(this.field_16021);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.textRenderer.draw(I18n.translate("container.crafting"), 97, 8, 4210752);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.offsetGuiForEffects = !this.field_16022.method_14590();
		if (this.field_16022.method_14590() && this.field_16019) {
			this.drawBackground(tickDelta, mouseX, mouseY);
			this.field_16022.method_14575(mouseX, mouseY, tickDelta);
		} else {
			this.field_16022.method_14575(mouseX, mouseY, tickDelta);
			super.render(mouseX, mouseY, tickDelta);
			this.field_16022.method_14578(this.x, this.y, false, tickDelta);
		}

		this.renderTooltip(mouseX, mouseY);
		this.field_16022.method_14591(this.x, this.y, mouseX, mouseY);
		this.mouseX = (float)mouseX;
		this.mouseY = (float)mouseY;
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(INVENTORY_TEXTURE);
		int i = this.x;
		int j = this.y;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		renderEntity(i + 51, j + 75, 30, (float)(i + 51) - this.mouseX, (float)(j + 75 - 50) - this.mouseY, this.client.player);
	}

	public static void renderEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x, (float)y, 50.0F);
		GlStateManager.scale((float)(-size), (float)size, (float)size);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		float f = entity.bodyYaw;
		float g = entity.yaw;
		float h = entity.pitch;
		float i = entity.prevHeadYaw;
		float j = entity.headYaw;
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		DiffuseLighting.enableNormally();
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		entity.bodyYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
		entity.yaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
		entity.pitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
		entity.headYaw = entity.yaw;
		entity.prevHeadYaw = entity.yaw;
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
		entityRenderDispatcher.setYaw(180.0F);
		entityRenderDispatcher.setRenderShadows(false);
		entityRenderDispatcher.method_12446(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, false);
		entityRenderDispatcher.setRenderShadows(true);
		entity.bodyYaw = f;
		entity.yaw = g;
		entity.pitch = h;
		entity.prevHeadYaw = i;
		entity.headYaw = j;
		GlStateManager.popMatrix();
		DiffuseLighting.disable();
		GlStateManager.disableRescaleNormal();
		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.textureUnit);
	}

	@Override
	protected boolean isPointWithinBounds(int posX, int posY, int width, int height, int pointX, int pointY) {
		return (!this.field_16019 || !this.field_16022.method_14590()) && super.isPointWithinBounds(posX, posY, width, height, pointX, pointY);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if (!this.field_16022.method_14576(mouseX, mouseY, button)) {
			if (!this.field_16019 || !this.field_16022.method_14590()) {
				super.mouseClicked(mouseX, mouseY, button);
			}
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		if (this.field_16020) {
			this.field_16020 = false;
		} else {
			super.mouseReleased(mouseX, mouseY, button);
		}
	}

	@Override
	protected boolean method_14549(int i, int j, int k, int l) {
		boolean bl = i < k || j < l || i >= k + this.backgroundWidth || j >= l + this.backgroundHeight;
		return this.field_16022.method_14592(i, j, this.x, this.y, this.backgroundWidth, this.backgroundHeight) && bl;
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == 10) {
			this.field_16022.method_14586(this.field_16019, ((PlayerScreenHandler)this.screenHandler).craftingInventory);
			this.field_16022.method_14587();
			this.x = this.field_16022.method_14585(this.field_16019, this.width, this.backgroundWidth);
			this.field_16021.method_14476(this.x + 104, this.height / 2 - 22);
			this.field_16020 = true;
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (!this.field_16022.method_14574(id, code)) {
			super.keyPressed(id, code);
		}
	}

	@Override
	protected void method_1131(Slot slot, int i, int j, ItemAction itemAction) {
		super.method_1131(slot, i, j, itemAction);
		this.field_16022.method_14579(slot);
	}

	@Override
	public void method_14637() {
		this.field_16022.method_14597();
	}

	@Override
	public void removed() {
		this.field_16022.method_14573();
		super.removed();
	}

	@Override
	public RecipeBookScreen method_14638() {
		return this.field_16022;
	}
}
