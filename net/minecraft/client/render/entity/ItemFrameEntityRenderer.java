package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ItemFrameEntityRenderer extends EntityRenderer<ItemFrameEntity> {
	private static final Identifier field_6497 = new Identifier("textures/map/map_background.png");
	private final MinecraftClient field_8000 = MinecraftClient.getInstance();
	private final ModelIdentifier field_11111 = new ModelIdentifier("item_frame", "normal");
	private final ModelIdentifier field_11112 = new ModelIdentifier("item_frame", "map");
	private final ItemRenderer field_11113;

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
		if (itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP) {
			bakedModel = bakedModelManager.getByIdentifier(this.field_11112);
		} else {
			bakedModel = bakedModelManager.getByIdentifier(this.field_11111);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		if (this.field_13631) {
			GlStateManager.enableColorMaterial();
			GlStateManager.method_12309(this.method_12454(itemFrameEntity));
		}

		blockRenderManager.getModelRenderer().method_12350(bakedModel, 1.0F, 1.0F, 1.0F, 1.0F);
		if (this.field_13631) {
			GlStateManager.method_12315();
			GlStateManager.disableColorMaterial();
		}

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

	@Nullable
	protected Identifier getTexture(ItemFrameEntity itemFrameEntity) {
		return null;
	}

	private void method_4334(ItemFrameEntity itemFrameEntity) {
		ItemStack itemStack = itemFrameEntity.getHeldItemStack();
		if (!itemStack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			boolean bl = itemStack.getItem() == Items.FILLED_MAP;
			int i = bl ? itemFrameEntity.rotation() % 4 * 2 : itemFrameEntity.rotation();
			GlStateManager.rotate((float)i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
			if (bl) {
				this.dispatcher.textureManager.bindTexture(field_6497);
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				float f = 0.0078125F;
				GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);
				GlStateManager.translate(-64.0F, -64.0F, 0.0F);
				MapState mapState = Items.FILLED_MAP.getMapState(itemStack, itemFrameEntity.world);
				GlStateManager.translate(0.0F, 0.0F, -1.0F);
				if (mapState != null) {
					this.field_8000.gameRenderer.getMapRenderer().draw(mapState, true);
				}
			} else {
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				GlStateManager.pushLightingAttributes();
				DiffuseLighting.enableNormally();
				this.field_11113.method_12458(itemStack, ModelTransformation.Mode.FIXED);
				DiffuseLighting.disable();
				GlStateManager.popAttributes();
			}

			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}
	}

	protected void method_10208(ItemFrameEntity itemFrameEntity, double d, double e, double f) {
		if (MinecraftClient.isHudEnabled()
			&& !itemFrameEntity.getHeldItemStack().isEmpty()
			&& itemFrameEntity.getHeldItemStack().hasCustomName()
			&& this.dispatcher.field_7998 == itemFrameEntity) {
			double g = itemFrameEntity.squaredDistanceTo(this.dispatcher.field_11098);
			float h = itemFrameEntity.isSneaking() ? 32.0F : 64.0F;
			if (!(g >= (double)(h * h))) {
				String string = itemFrameEntity.getHeldItemStack().getCustomName();
				this.renderLabelIfPresent(itemFrameEntity, string, d, e, f, 64);
			}
		}
	}
}
