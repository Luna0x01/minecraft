package net.minecraft.client.render.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.BannerBlockEntityModel;
import net.minecraft.client.texture.ColorMaskTexture;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class BannerBlockEntityRenderer extends BlockEntityRenderer<BannerBlockEntity> {
	private static final Map<String, BannerBlockEntityRenderer.TextureIdHolder> textures = Maps.newHashMap();
	private static final Identifier BASE_TEXTURE = new Identifier("textures/entity/banner_base.png");
	private BannerBlockEntityModel model = new BannerBlockEntityModel();

	public void render(BannerBlockEntity bannerBlockEntity, double d, double e, double f, float g, int i) {
		boolean bl = bannerBlockEntity.getEntityWorld() != null;
		boolean bl2 = !bl || bannerBlockEntity.getBlock() == Blocks.STANDING_BANNER;
		int j = bl ? bannerBlockEntity.getDataValue() : 0;
		long l = bl ? bannerBlockEntity.getEntityWorld().getLastUpdateTime() : 0L;
		GlStateManager.pushMatrix();
		float h = 0.6666667F;
		if (bl2) {
			GlStateManager.translate((float)d + 0.5F, (float)e + 0.75F * h, (float)f + 0.5F);
			float k = (float)(j * 360) / 16.0F;
			GlStateManager.rotate(-k, 0.0F, 1.0F, 0.0F);
			this.model.pillar.visible = true;
		} else {
			float n = 0.0F;
			if (j == 2) {
				n = 180.0F;
			}

			if (j == 4) {
				n = 90.0F;
			}

			if (j == 5) {
				n = -90.0F;
			}

			GlStateManager.translate((float)d + 0.5F, (float)e - 0.25F * h, (float)f + 0.5F);
			GlStateManager.rotate(-n, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
			this.model.pillar.visible = false;
		}

		BlockPos blockPos = bannerBlockEntity.getPos();
		float o = (float)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + (float)l + g;
		this.model.banner.posX = (-0.0125F + 0.01F * MathHelper.cos(o * (float) Math.PI * 0.02F)) * (float) Math.PI;
		GlStateManager.enableRescaleNormal();
		Identifier identifier = this.getTexture(bannerBlockEntity);
		if (identifier != null) {
			this.bindTexture(identifier);
			GlStateManager.pushMatrix();
			GlStateManager.scale(h, -h, -h);
			this.model.render();
			GlStateManager.popMatrix();
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	private Identifier getTexture(BannerBlockEntity blockEntity) {
		String string = blockEntity.getTextureIdentifier();
		if (string.isEmpty()) {
			return null;
		} else {
			BannerBlockEntityRenderer.TextureIdHolder textureIdHolder = (BannerBlockEntityRenderer.TextureIdHolder)textures.get(string);
			if (textureIdHolder == null) {
				if (textures.size() >= 256) {
					long l = System.currentTimeMillis();
					Iterator<String> iterator = textures.keySet().iterator();

					while (iterator.hasNext()) {
						String string2 = (String)iterator.next();
						BannerBlockEntityRenderer.TextureIdHolder textureIdHolder2 = (BannerBlockEntityRenderer.TextureIdHolder)textures.get(string2);
						if (l - textureIdHolder2.field_11012 > 60000L) {
							MinecraftClient.getInstance().getTextureManager().close(textureIdHolder2.texture);
							iterator.remove();
						}
					}

					if (textures.size() >= 256) {
						return null;
					}
				}

				List<BannerBlockEntity.BannerPattern> list = blockEntity.getPatterns();
				List<DyeColor> list2 = blockEntity.getColors();
				List<String> list3 = Lists.newArrayList();

				for (BannerBlockEntity.BannerPattern bannerPattern : list) {
					list3.add("textures/entity/banner/" + bannerPattern.getName() + ".png");
				}

				textureIdHolder = new BannerBlockEntityRenderer.TextureIdHolder();
				textureIdHolder.texture = new Identifier(string);
				MinecraftClient.getInstance().getTextureManager().loadTexture(textureIdHolder.texture, new ColorMaskTexture(BASE_TEXTURE, list3, list2));
				textures.put(string, textureIdHolder);
			}

			textureIdHolder.field_11012 = System.currentTimeMillis();
			return textureIdHolder.texture;
		}
	}

	static class TextureIdHolder {
		public long field_11012;
		public Identifier texture;

		private TextureIdHolder() {
		}
	}
}
