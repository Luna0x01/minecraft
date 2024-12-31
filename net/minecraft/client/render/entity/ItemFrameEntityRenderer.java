package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.class_4290;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ItemFrameEntityRenderer extends EntityRenderer<ItemFrameEntity> {
	private static final Identifier field_6497 = new Identifier("textures/map/map_background.png");
	private static final class_4290 field_20928 = new class_4290("item_frame", "map=false");
	private static final class_4290 field_20929 = new class_4290("item_frame", "map=true");
	private final MinecraftClient field_8000 = MinecraftClient.getInstance();
	private final HeldItemRenderer field_20930;

	public ItemFrameEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, HeldItemRenderer heldItemRenderer) {
		super(entityRenderDispatcher);
		this.field_20930 = heldItemRenderer;
	}

	public void render(ItemFrameEntity itemFrameEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		BlockPos blockPos = itemFrameEntity.getTilePos();
		double i = (double)blockPos.getX() - itemFrameEntity.x + d;
		double j = (double)blockPos.getY() - itemFrameEntity.y + e;
		double k = (double)blockPos.getZ() - itemFrameEntity.z + f;
		GlStateManager.translate(i + 0.5, j + 0.5, k + 0.5);
		GlStateManager.rotate(itemFrameEntity.pitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(180.0F - itemFrameEntity.yaw, 0.0F, 1.0F, 0.0F);
		this.dispatcher.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		BlockRenderManager blockRenderManager = this.field_8000.getBlockRenderManager();
		BakedModelManager bakedModelManager = blockRenderManager.getModels().getBakedModelManager();
		class_4290 lv = itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP ? field_20929 : field_20928;
		GlStateManager.pushMatrix();
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		if (this.field_13631) {
			GlStateManager.enableColorMaterial();
			GlStateManager.method_12309(this.method_12454(itemFrameEntity));
		}

		blockRenderManager.getModelRenderer().method_12350(bakedModelManager.method_19594(lv), 1.0F, 1.0F, 1.0F, 1.0F);
		if (this.field_13631) {
			GlStateManager.method_12315();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		if (itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP) {
			GlStateManager.pushLightingAttributes();
			DiffuseLighting.enableNormally();
		}

		GlStateManager.translate(0.0F, 0.0F, 0.4375F);
		this.method_4334(itemFrameEntity);
		if (itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP) {
			DiffuseLighting.disable();
			GlStateManager.popAttributes();
		}

		GlStateManager.enableLighting();
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
			boolean bl = itemStack.getItem() == Items.FILLED_MAP;
			int i = bl ? itemFrameEntity.rotation() % 4 * 2 : itemFrameEntity.rotation();
			GlStateManager.rotate((float)i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
			if (bl) {
				GlStateManager.disableLighting();
				this.dispatcher.textureManager.bindTexture(field_6497);
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				float f = 0.0078125F;
				GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);
				GlStateManager.translate(-64.0F, -64.0F, 0.0F);
				MapState mapState = FilledMapItem.method_16111(itemStack, itemFrameEntity.world);
				GlStateManager.translate(0.0F, 0.0F, -1.0F);
				if (mapState != null) {
					this.field_8000.field_3818.method_19090().draw(mapState, true);
				}
			} else {
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				this.field_20930.method_19380(itemStack, ModelTransformation.Mode.FIXED);
			}

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
				String string = itemFrameEntity.getHeldItemStack().getName().asFormattedString();
				this.renderLabelIfPresent(itemFrameEntity, string, d, e, f, 64);
			}
		}
	}
}
