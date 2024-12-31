package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.CompassSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ItemFrameEntityRenderer extends EntityRenderer<ItemFrameEntity> {
	private static final Identifier field_6497 = new Identifier("textures/map/map_background.png");
	private final MinecraftClient field_8000 = MinecraftClient.getInstance();
	private final ModelIdentifier field_11111 = new ModelIdentifier("item_frame", "normal");
	private final ModelIdentifier field_11112 = new ModelIdentifier("item_frame", "map");
	private ItemRenderer field_11113;

	public ItemFrameEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.field_11113 = itemRenderer;
	}

	public void render(ItemFrameEntity itemFrameEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		BlockPos blockPos = itemFrameEntity.getTilePos();
		double i = (double)blockPos.getX() - itemFrameEntity.x + d;
		double j = (double)blockPos.getY() - itemFrameEntity.y + e;
		double k = (double)blockPos.getZ() - itemFrameEntity.z + f;
		GlStateManager.translate(i + 0.5, j + 0.5, k + 0.5);
		GlStateManager.rotate(180.0F - itemFrameEntity.yaw, 0.0F, 1.0F, 0.0F);
		this.dispatcher.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		BlockRenderManager blockRenderManager = this.field_8000.getBlockRenderManager();
		BakedModelManager bakedModelManager = blockRenderManager.getModels().getBakedModelManager();
		BakedModel bakedModel;
		if (itemFrameEntity.getHeldItemStack() != null && itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP) {
			bakedModel = bakedModelManager.getByIdentifier(this.field_11112);
		} else {
			bakedModel = bakedModelManager.getByIdentifier(this.field_11111);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		blockRenderManager.getModelRenderer().render(bakedModel, 1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
		GlStateManager.translate(0.0F, 0.0F, 0.4375F);
		this.method_4334(itemFrameEntity);
		GlStateManager.popMatrix();
		this.method_10208(
			itemFrameEntity,
			d + (double)((float)itemFrameEntity.direction.getOffsetX() * 0.3F),
			e - 0.25,
			f + (double)((float)itemFrameEntity.direction.getOffsetZ() * 0.3F)
		);
	}

	protected Identifier getTexture(ItemFrameEntity itemFrameEntity) {
		return null;
	}

	private void method_4334(ItemFrameEntity itemFrameEntity) {
		ItemStack itemStack = itemFrameEntity.getHeldItemStack();
		if (itemStack != null) {
			ItemEntity itemEntity = new ItemEntity(itemFrameEntity.world, 0.0, 0.0, 0.0, itemStack);
			Item item = itemEntity.getItemStack().getItem();
			itemEntity.getItemStack().count = 1;
			itemEntity.hoverHeight = 0.0F;
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			int i = itemFrameEntity.rotation();
			if (item == Items.FILLED_MAP) {
				i = i % 4 * 2;
			}

			GlStateManager.rotate((float)i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
			if (item == Items.FILLED_MAP) {
				this.dispatcher.textureManager.bindTexture(field_6497);
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				float f = 0.0078125F;
				GlStateManager.scale(f, f, f);
				GlStateManager.translate(-64.0F, -64.0F, 0.0F);
				MapState mapState = Items.FILLED_MAP.getMapState(itemEntity.getItemStack(), itemFrameEntity.world);
				GlStateManager.translate(0.0F, 0.0F, -1.0F);
				if (mapState != null) {
					this.field_8000.gameRenderer.getMapRenderer().draw(mapState, true);
				}
			} else {
				Sprite sprite = null;
				if (item == Items.COMPASS) {
					sprite = this.field_8000.getSpriteAtlasTexture().getSprite(CompassSprite.field_11201);
					this.field_8000.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
					if (sprite instanceof CompassSprite) {
						CompassSprite compassSprite = (CompassSprite)sprite;
						double d = compassSprite.field_2150;
						double e = compassSprite.field_2151;
						compassSprite.field_2150 = 0.0;
						compassSprite.field_2151 = 0.0;
						compassSprite.method_5241(
							itemFrameEntity.world,
							itemFrameEntity.x,
							itemFrameEntity.z,
							(double)MathHelper.wrapDegrees((float)(180 + itemFrameEntity.direction.getHorizontal() * 90)),
							false,
							true
						);
						compassSprite.field_2150 = d;
						compassSprite.field_2151 = e;
					} else {
						sprite = null;
					}
				}

				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				if (!this.field_11113.hasDepth(itemEntity.getItemStack()) || item instanceof SkullItem) {
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.pushLightingAttributes();
				DiffuseLighting.enableNormally();
				this.field_11113.renderItem(itemEntity.getItemStack(), ModelTransformation.Mode.FIXED);
				DiffuseLighting.disable();
				GlStateManager.popAttributes();
				if (sprite != null && sprite.getSize() > 0) {
					sprite.update();
				}
			}

			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}
	}

	protected void method_10208(ItemFrameEntity itemFrameEntity, double d, double e, double f) {
		if (MinecraftClient.isHudEnabled()
			&& itemFrameEntity.getHeldItemStack() != null
			&& itemFrameEntity.getHeldItemStack().hasCustomName()
			&& this.dispatcher.field_7998 == itemFrameEntity) {
			float g = 1.6F;
			float h = 0.016666668F * g;
			double i = itemFrameEntity.squaredDistanceTo(this.dispatcher.field_11098);
			float j = itemFrameEntity.isSneaking() ? 32.0F : 64.0F;
			if (i < (double)(j * j)) {
				String string = itemFrameEntity.getHeldItemStack().getCustomName();
				if (itemFrameEntity.isSneaking()) {
					TextRenderer textRenderer = this.getFontRenderer();
					GlStateManager.pushMatrix();
					GlStateManager.translate((float)d + 0.0F, (float)e + itemFrameEntity.height + 0.5F, (float)f);
					GL11.glNormal3f(0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(-this.dispatcher.yaw, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(this.dispatcher.pitch, 1.0F, 0.0F, 0.0F);
					GlStateManager.scale(-h, -h, h);
					GlStateManager.disableLighting();
					GlStateManager.translate(0.0F, 0.25F / h, 0.0F);
					GlStateManager.depthMask(false);
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(770, 771);
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferBuilder = tessellator.getBuffer();
					int k = textRenderer.getStringWidth(string) / 2;
					GlStateManager.disableTexture();
					bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
					bufferBuilder.vertex((double)(-k - 1), -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
					bufferBuilder.vertex((double)(-k - 1), 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
					bufferBuilder.vertex((double)(k + 1), 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
					bufferBuilder.vertex((double)(k + 1), -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
					tessellator.draw();
					GlStateManager.enableTexture();
					GlStateManager.depthMask(true);
					textRenderer.draw(string, -textRenderer.getStringWidth(string) / 2, 0, 553648127);
					GlStateManager.enableLighting();
					GlStateManager.disableBlend();
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.popMatrix();
				} else {
					this.renderLabelIfPresent(itemFrameEntity, string, d, e, f, 64);
				}
			}
		}
	}
}
