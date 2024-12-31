package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3082;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;

public class MapRenderer {
	private static final Identifier MAP_ICONS_TEXTURE = new Identifier("textures/map/map_icons.png");
	private final TextureManager textureManager;
	private final Map<String, MapRenderer.MapTexture> mapTextures = Maps.newHashMap();

	public MapRenderer(TextureManager textureManager) {
		this.textureManager = textureManager;
	}

	public void updateTexture(MapState mapState) {
		this.getMapTexture(mapState).updateTexture();
	}

	public void draw(MapState mapState, boolean noIcons) {
		this.getMapTexture(mapState).draw(noIcons);
	}

	private MapRenderer.MapTexture getMapTexture(MapState mapState) {
		MapRenderer.MapTexture mapTexture = (MapRenderer.MapTexture)this.mapTextures.get(mapState.id);
		if (mapTexture == null) {
			mapTexture = new MapRenderer.MapTexture(mapState);
			this.mapTextures.put(mapState.id, mapTexture);
		}

		return mapTexture;
	}

	@Nullable
	public MapRenderer.MapTexture method_13835(String string) {
		return (MapRenderer.MapTexture)this.mapTextures.get(string);
	}

	public void clearStateTextures() {
		for (MapRenderer.MapTexture mapTexture : this.mapTextures.values()) {
			this.textureManager.close(mapTexture.currentTexture);
		}

		this.mapTextures.clear();
	}

	@Nullable
	public MapState method_13834(@Nullable MapRenderer.MapTexture mapTexture) {
		return mapTexture != null ? mapTexture.mapState : null;
	}

	class MapTexture {
		private final MapState mapState;
		private final NativeImageBackedTexture texture;
		private final Identifier currentTexture;
		private final int[] colors;

		private MapTexture(MapState mapState) {
			this.mapState = mapState;
			this.texture = new NativeImageBackedTexture(128, 128);
			this.colors = this.texture.getPixels();
			this.currentTexture = MapRenderer.this.textureManager.registerDynamicTexture("map/" + mapState.id, this.texture);

			for (int i = 0; i < this.colors.length; i++) {
				this.colors[i] = 0;
			}
		}

		private void updateTexture() {
			for (int i = 0; i < 16384; i++) {
				int j = this.mapState.colors[i] & 255;
				if (j / 4 == 0) {
					this.colors[i] = (i + i / 128 & 1) * 8 + 16 << 24;
				} else {
					this.colors[i] = MaterialColor.COLORS[j / 4].getRenderColor(j & 3);
				}
			}

			this.texture.upload();
		}

		private void draw(boolean noIcons) {
			int i = 0;
			int j = 0;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			float f = 0.0F;
			MapRenderer.this.textureManager.bindTexture(this.currentTexture);
			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.ONE, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ZERO, GlStateManager.class_2866.ONE
			);
			GlStateManager.disableAlphaTest();
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex(0.0, 128.0, -0.01F).texture(0.0, 1.0).next();
			bufferBuilder.vertex(128.0, 128.0, -0.01F).texture(1.0, 1.0).next();
			bufferBuilder.vertex(128.0, 0.0, -0.01F).texture(1.0, 0.0).next();
			bufferBuilder.vertex(0.0, 0.0, -0.01F).texture(0.0, 0.0).next();
			tessellator.draw();
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
			MapRenderer.this.textureManager.bindTexture(MapRenderer.MAP_ICONS_TEXTURE);
			int k = 0;

			for (class_3082 lv : this.mapState.icons.values()) {
				if (!noIcons || lv.method_13824()) {
					GlStateManager.pushMatrix();
					GlStateManager.translate(0.0F + (float)lv.method_13821() / 2.0F + 64.0F, 0.0F + (float)lv.method_13822() / 2.0F + 64.0F, -0.02F);
					GlStateManager.rotate((float)(lv.method_13823() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.scale(4.0F, 4.0F, 3.0F);
					GlStateManager.translate(-0.125F, 0.125F, 0.0F);
					byte b = lv.method_13819();
					float g = (float)(b % 4 + 0) / 4.0F;
					float h = (float)(b / 4 + 0) / 4.0F;
					float l = (float)(b % 4 + 1) / 4.0F;
					float m = (float)(b / 4 + 1) / 4.0F;
					bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
					float n = -0.001F;
					bufferBuilder.vertex(-1.0, 1.0, (double)((float)k * -0.001F)).texture((double)g, (double)h).next();
					bufferBuilder.vertex(1.0, 1.0, (double)((float)k * -0.001F)).texture((double)l, (double)h).next();
					bufferBuilder.vertex(1.0, -1.0, (double)((float)k * -0.001F)).texture((double)l, (double)m).next();
					bufferBuilder.vertex(-1.0, -1.0, (double)((float)k * -0.001F)).texture((double)g, (double)m).next();
					tessellator.draw();
					GlStateManager.popMatrix();
					k++;
				}
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, -0.04F);
			GlStateManager.scale(1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}
	}
}
