package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class InventoryScreen extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
	private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
	private float mouseX;
	private float mouseY;
	private final RecipeBookWidget recipeBook = new RecipeBookWidget();
	private boolean open;
	private boolean narrow;
	private boolean mouseDown;

	public InventoryScreen(PlayerEntity player) {
		super(player.playerScreenHandler, player.getInventory(), new TranslatableText("container.crafting"));
		this.passEvents = true;
		this.titleX = 97;
	}

	@Override
	public void handledScreenTick() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			this.client.openScreen(new CreativeInventoryScreen(this.client.player));
		} else {
			this.recipeBook.update();
		}
	}

	@Override
	protected void init() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			this.client.openScreen(new CreativeInventoryScreen(this.client.player));
		} else {
			super.init();
			this.narrow = this.width < 379;
			this.recipeBook.initialize(this.width, this.height, this.client, this.narrow, this.handler);
			this.open = true;
			this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
			this.addDrawableChild(new TexturedButtonWidget(this.x + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, button -> {
				this.recipeBook.toggleOpen();
				this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
				((TexturedButtonWidget)button).setPos(this.x + 104, this.height / 2 - 22);
				this.mouseDown = true;
			}));
			this.addSelectableChild(this.recipeBook);
			this.setInitialFocus(this.recipeBook);
		}
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		this.textRenderer.draw(matrices, this.title, (float)this.titleX, (float)this.titleY, 4210752);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.drawStatusEffects = !this.recipeBook.isOpen();
		if (this.recipeBook.isOpen() && this.narrow) {
			this.drawBackground(matrices, delta, mouseX, mouseY);
			this.recipeBook.render(matrices, mouseX, mouseY, delta);
		} else {
			this.recipeBook.render(matrices, mouseX, mouseY, delta);
			super.render(matrices, mouseX, mouseY, delta);
			this.recipeBook.drawGhostSlots(matrices, this.x, this.y, false, delta);
		}

		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
		this.recipeBook.drawTooltip(matrices, this.x, this.y, mouseX, mouseY);
		this.mouseX = (float)mouseX;
		this.mouseY = (float)mouseY;
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
		int i = this.x;
		int j = this.y;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		drawEntity(i + 51, j + 75, 30, (float)(i + 51) - this.mouseX, (float)(j + 75 - 50) - this.mouseY, this.client.player);
	}

	public static void drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
		float f = (float)Math.atan((double)(mouseX / 40.0F));
		float g = (float)Math.atan((double)(mouseY / 40.0F));
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.push();
		matrixStack.translate((double)x, (double)y, 1050.0);
		matrixStack.scale(1.0F, 1.0F, -1.0F);
		RenderSystem.applyModelViewMatrix();
		MatrixStack matrixStack2 = new MatrixStack();
		matrixStack2.translate(0.0, 0.0, 1000.0);
		matrixStack2.scale((float)size, (float)size, (float)size);
		Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
		Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
		quaternion.hamiltonProduct(quaternion2);
		matrixStack2.multiply(quaternion);
		float h = entity.bodyYaw;
		float i = entity.getYaw();
		float j = entity.getPitch();
		float k = entity.prevHeadYaw;
		float l = entity.headYaw;
		entity.bodyYaw = 180.0F + f * 20.0F;
		entity.setYaw(180.0F + f * 40.0F);
		entity.setPitch(-g * 20.0F);
		entity.headYaw = entity.getYaw();
		entity.prevHeadYaw = entity.getYaw();
		DiffuseLighting.method_34742();
		EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
		quaternion2.conjugate();
		entityRenderDispatcher.setRotation(quaternion2);
		entityRenderDispatcher.setRenderShadows(false);
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack2, immediate, 15728880));
		immediate.draw();
		entityRenderDispatcher.setRenderShadows(true);
		entity.bodyYaw = h;
		entity.setYaw(i);
		entity.setPitch(j);
		entity.prevHeadYaw = k;
		entity.headYaw = l;
		matrixStack.pop();
		RenderSystem.applyModelViewMatrix();
		DiffuseLighting.enableGuiDepthLighting();
	}

	@Override
	protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
		return (!this.narrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(x, y, width, height, pointX, pointY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
			this.setFocused(this.recipeBook);
			return true;
		} else {
			return this.narrow && this.recipeBook.isOpen() ? false : super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.mouseDown) {
			this.mouseDown = false;
			return true;
		} else {
			return super.mouseReleased(mouseX, mouseY, button);
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		boolean bl = mouseX < (double)left
			|| mouseY < (double)top
			|| mouseX >= (double)(left + this.backgroundWidth)
			|| mouseY >= (double)(top + this.backgroundHeight);
		return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, button) && bl;
	}

	@Override
	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
		super.onMouseClick(slot, slotId, button, actionType);
		this.recipeBook.slotClicked(slot);
	}

	@Override
	public void refreshRecipeBook() {
		this.recipeBook.refresh();
	}

	@Override
	public void removed() {
		if (this.open) {
			this.recipeBook.close();
		}

		super.removed();
	}

	@Override
	public RecipeBookWidget getRecipeBookWidget() {
		return this.recipeBook;
	}
}
