package net.minecraft.client.render.entity;

import java.util.Random;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ItemEntityRenderer extends EntityRenderer<ItemEntity> {
	private final ItemRenderer itemRenderer;
	private final Random random = new Random();

	public ItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.itemRenderer = itemRenderer;
		this.shadowSize = 0.15F;
		this.shadowDarkness = 0.75F;
	}

	private int getRenderedAmount(ItemStack itemStack) {
		int i = 1;
		if (itemStack.getCount() > 48) {
			i = 5;
		} else if (itemStack.getCount() > 32) {
			i = 4;
		} else if (itemStack.getCount() > 16) {
			i = 3;
		} else if (itemStack.getCount() > 1) {
			i = 2;
		}

		return i;
	}

	public void render(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		ItemStack itemStack = itemEntity.getStack();
		int j = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getDamage();
		this.random.setSeed((long)j);
		BakedModel bakedModel = this.itemRenderer.getHeldItemModel(itemStack, itemEntity.world, null);
		boolean bl = bakedModel.hasDepth();
		int k = this.getRenderedAmount(itemStack);
		float h = 0.25F;
		float l = MathHelper.sin(((float)itemEntity.getAge() + g) / 10.0F + itemEntity.hoverHeight) * 0.1F + 0.1F;
		float m = bakedModel.getTransformation().getTransformation(ModelTransformation.Mode.field_4318).scale.getY();
		matrixStack.translate(0.0, (double)(l + 0.25F * m), 0.0);
		float n = ((float)itemEntity.getAge() + g) / 20.0F + itemEntity.hoverHeight;
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(n));
		float o = bakedModel.getTransformation().ground.scale.getX();
		float p = bakedModel.getTransformation().ground.scale.getY();
		float q = bakedModel.getTransformation().ground.scale.getZ();
		if (!bl) {
			float r = -0.0F * (float)(k - 1) * 0.5F * o;
			float s = -0.0F * (float)(k - 1) * 0.5F * p;
			float t = -0.09375F * (float)(k - 1) * 0.5F * q;
			matrixStack.translate((double)r, (double)s, (double)t);
		}

		for (int u = 0; u < k; u++) {
			matrixStack.push();
			if (u > 0) {
				if (bl) {
					float v = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float w = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float x = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					matrixStack.translate((double)v, (double)w, (double)x);
				} else {
					float y = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					float z = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					matrixStack.translate((double)y, (double)z, 0.0);
				}
			}

			this.itemRenderer
				.renderItem(itemStack, ModelTransformation.Mode.field_4318, false, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV, bakedModel);
			matrixStack.pop();
			if (!bl) {
				matrixStack.translate((double)(0.0F * o), (double)(0.0F * p), (double)(0.09375F * q));
			}
		}

		matrixStack.pop();
		super.render(itemEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(ItemEntity itemEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
