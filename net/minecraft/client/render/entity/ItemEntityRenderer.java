package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.client.render.DiffuseLighting;
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
	private final Random random = new Random();

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

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			return i;
		}
	}

	private int method_10222(ItemStack itemStack) {
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

	public void render(ItemEntity itemEntity, double d, double e, double f, float g, float h) {
		ItemStack itemStack = itemEntity.getItemStack();
		int i = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getData();
		this.random.setSeed((long)i);
		boolean bl = false;
		if (this.bindTexture(itemEntity)) {
			this.dispatcher.textureManager.getTexture(this.getTexture(itemEntity)).pushFilter(false, false);
			bl = true;
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		DiffuseLighting.enableNormally();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.pushMatrix();
		BakedModel bakedModel = this.itemRenderer.method_12457(itemStack, itemEntity.world, null);
		int j = this.method_10221(itemEntity, d, e, f, h, bakedModel);
		float k = bakedModel.getTransformation().ground.scale.x;
		float l = bakedModel.getTransformation().ground.scale.y;
		float m = bakedModel.getTransformation().ground.scale.z;
		boolean bl2 = bakedModel.hasDepth();
		if (!bl2) {
			float n = -0.0F * (float)(j - 1) * 0.5F * k;
			float o = -0.0F * (float)(j - 1) * 0.5F * l;
			float p = -0.09375F * (float)(j - 1) * 0.5F * m;
			GlStateManager.translate(n, o, p);
		}

		if (this.field_13631) {
			GlStateManager.enableColorMaterial();
			GlStateManager.method_12309(this.method_12454(itemEntity));
		}

		for (int q = 0; q < j; q++) {
			if (bl2) {
				GlStateManager.pushMatrix();
				if (q > 0) {
					float r = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float s = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float t = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					GlStateManager.translate(r, s, t);
				}

				bakedModel.getTransformation().apply(ModelTransformation.Mode.GROUND);
				this.itemRenderer.renderItem(itemStack, bakedModel);
				GlStateManager.popMatrix();
			} else {
				GlStateManager.pushMatrix();
				if (q > 0) {
					float u = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					float v = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					GlStateManager.translate(u, v, 0.0F);
				}

				bakedModel.getTransformation().apply(ModelTransformation.Mode.GROUND);
				this.itemRenderer.renderItem(itemStack, bakedModel);
				GlStateManager.popMatrix();
				GlStateManager.translate(0.0F * k, 0.0F * l, 0.09375F * m);
			}
		}

		if (this.field_13631) {
			GlStateManager.method_12315();
			GlStateManager.disableColorMaterial();
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
