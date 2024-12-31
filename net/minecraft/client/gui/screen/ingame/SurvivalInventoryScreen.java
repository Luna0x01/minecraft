package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.AchievementsScreen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class SurvivalInventoryScreen extends InventoryScreen {
	private float mouseX;
	private float mouseY;

	public SurvivalInventoryScreen(PlayerEntity playerEntity) {
		super(playerEntity.playerScreenHandler);
		this.passEvents = true;
	}

	@Override
	public void tick() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			this.client.setScreen(new CreativeInventoryScreen(this.client.player));
		}

		this.applyStatusEffectOffset();
	}

	@Override
	public void init() {
		this.buttons.clear();
		if (this.client.interactionManager.hasCreativeInventory()) {
			this.client.setScreen(new CreativeInventoryScreen(this.client.player));
		} else {
			super.init();
		}
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.textRenderer.draw(I18n.translate("container.crafting"), 86, 16, 4210752);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		super.render(mouseX, mouseY, tickDelta);
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
		entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F);
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
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == 0) {
			this.client.setScreen(new AchievementsScreen(this, this.client.player.getStatHandler()));
		}

		if (button.id == 1) {
			this.client.setScreen(new StatsScreen(this, this.client.player.getStatHandler()));
		}
	}
}
