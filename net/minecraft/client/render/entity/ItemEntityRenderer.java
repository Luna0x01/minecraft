package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ItemEntityRenderer extends EntityRenderer<ItemEntity> {
	private final ItemRenderer itemRenderer;
	private Random random = new Random();

	public ItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.itemRenderer = itemRenderer;
		this.shadowSize = 0.15F;
		this.shadowDarkness = 0.75F;
	}

	private int method_10221(ItemEntity itemEntity, double d, double e, double f, float g, BakedModel bakedModel) {
		ItemStack itemStack = itemEntity.getItemStack();
		Item item = itemStack.getItem();
		if (item == null) {
			return 0;
		} else {
			boolean bl = bakedModel.hasDepth();
			int i = this.method_10222(itemStack);
			float h = 0.25F;
			float j = MathHelper.sin(((float)itemEntity.getAge() + g) / 10.0F + itemEntity.hoverHeight) * 0.1F + 0.1F;
			float k = bakedModel.getTransformation().getTransformation(ModelTransformation.Mode.GROUND).scale.y;
			GlStateManager.translate((float)d, (float)e + j + 0.25F * k, (float)f);
			if (bl || this.dispatcher.options != null) {
				float l = (((float)itemEntity.getAge() + g) / 20.0F + itemEntity.hoverHeight) * (180.0F / (float)Math.PI);
				GlStateManager.rotate(l, 0.0F, 1.0F, 0.0F);
			}

			if (!bl) {
				float m = -0.0F * (float)(i - 1) * 0.5F;
				float n = -0.0F * (float)(i - 1) * 0.5F;
				float o = -0.046875F * (float)(i - 1) * 0.5F;
				GlStateManager.translate(m, n, o);
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			return i;
		}
	}

	private int method_10222(ItemStack itemStack) {
		int i = 1;
		if (itemStack.count > 48) {
			i = 5;
		} else if (itemStack.count > 32) {
			i = 4;
		} else if (itemStack.count > 16) {
			i = 3;
		} else if (itemStack.count > 1) {
			i = 2;
		}

		return i;
	}

	public void render(ItemEntity itemEntity, double d, double e, double f, float g, float h) {
		ItemStack itemStack = itemEntity.getItemStack();
		this.random.setSeed(187L);
		boolean bl = false;
		if (this.bindTexture(itemEntity)) {
			this.dispatcher.textureManager.getTexture(this.getTexture(itemEntity)).pushFilter(false, false);
			bl = true;
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.pushMatrix();
		BakedModel bakedModel = this.itemRenderer.getModels().getModel(itemStack);
		int i = this.method_10221(itemEntity, d, e, f, h, bakedModel);

		for (int j = 0; j < i; j++) {
			if (bakedModel.hasDepth()) {
				GlStateManager.pushMatrix();
				if (j > 0) {
					float k = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float l = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float m = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					GlStateManager.translate(k, l, m);
				}

				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				bakedModel.getTransformation().apply(ModelTransformation.Mode.GROUND);
				this.itemRenderer.renderItem(itemStack, bakedModel);
				GlStateManager.popMatrix();
			} else {
				GlStateManager.pushMatrix();
				bakedModel.getTransformation().apply(ModelTransformation.Mode.GROUND);
				this.itemRenderer.renderItem(itemStack, bakedModel);
				GlStateManager.popMatrix();
				float n = bakedModel.getTransformation().ground.scale.x;
				float o = bakedModel.getTransformation().ground.scale.y;
				float p = bakedModel.getTransformation().ground.scale.z;
				GlStateManager.translate(0.0F * n, 0.0F * o, 0.046875F * p);
			}
		}

		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		this.bindTexture(itemEntity);
		if (bl) {
			this.dispatcher.textureManager.getTexture(this.getTexture(itemEntity)).pop();
		}

		super.render(itemEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(ItemEntity itemEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
