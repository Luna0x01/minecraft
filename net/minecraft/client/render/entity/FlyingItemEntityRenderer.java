package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class FlyingItemEntityRenderer<T extends Entity> extends EntityRenderer<T> {
	protected final Item item;
	private final ItemRenderer itemRenderer;

	public FlyingItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, Item item, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.item = item;
		this.itemRenderer = itemRenderer;
	}

	@Override
	public void render(T entity, double x, double y, double z, float yaw, float tickDelta) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x, (float)y, (float)z);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(0.5F, 0.5F, 0.5F);
		GlStateManager.rotate(-this.dispatcher.yaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(this.dispatcher.pitch, 1.0F, 0.0F, 0.0F);
		this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.itemRenderer.renderItem(this.createStack(entity), ModelTransformation.Mode.GROUND);
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.render(entity, x, y, z, yaw, tickDelta);
	}

	public ItemStack createStack(T entity) {
		return new ItemStack(this.item, 1, 0);
	}

	@Override
	protected Identifier getTexture(Entity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
