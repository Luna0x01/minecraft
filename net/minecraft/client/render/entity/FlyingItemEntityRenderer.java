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
		GlStateManager.rotate(-this.dispatcher.yaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float)(this.dispatcher.options.perspective == 2 ? -1 : 1) * this.dispatcher.pitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		if (this.field_13631) {
			GlStateManager.enableColorMaterial();
			GlStateManager.method_12309(this.method_12454(entity));
		}

		this.itemRenderer.method_12458(this.createStack(entity), ModelTransformation.Mode.GROUND);
		if (this.field_13631) {
			GlStateManager.method_12315();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.render(entity, x, y, z, yaw, tickDelta);
	}

	public ItemStack createStack(T entity) {
		return new ItemStack(this.item);
	}

	@Override
	protected Identifier getTexture(Entity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
