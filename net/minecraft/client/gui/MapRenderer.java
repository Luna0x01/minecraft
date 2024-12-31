package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3082;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class MapRenderer implements AutoCloseable {
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
		MapRenderer.MapTexture mapTexture = (MapRenderer.MapTexture)this.mapTextures.get(mapState.method_17914());
		if (mapTexture == null) {
			mapTexture = new MapRenderer.MapTexture(mapState);
			this.mapTextures.put(mapState.method_17914(), mapTexture);
		}

		return mapTexture;
	}

	@Nullable
	public MapRenderer.MapTexture method_13835(String string) {
		return (MapRenderer.MapTexture)this.mapTextures.get(string);
	}

	public void clearStateTextures() {
		for (MapRenderer.MapTexture mapTexture : this.mapTextures.values()) {
			mapTexture.close();
		}

		this.mapTextures.clear();
	}

	@Nullable
	public MapState method_13834(@Nullable MapRenderer.MapTexture mapTexture) {
		return mapTexture != null ? mapTexture.mapState : null;
	}

	public void close() {
		this.clearStateTextures();
	}

	class MapTexture implements AutoCloseable {
		private final MapState mapState;
		private final NativeImageBackedTexture texture;
		private final Identifier currentTexture;

		private MapTexture(MapState mapState) {
			this.mapState = mapState;
			this.texture = new NativeImageBackedTexture(128, 128, true);
			this.currentTexture = MapRenderer.this.textureManager.registerDynamicTexture("map/" + mapState.method_17914(), this.texture);
		}

		private void updateTexture() {
			for (int i = 0; i < 128; i++) {
				for (int j = 0; j < 128; j++) {
					int k = j + i * 128;
					int l = this.mapState.colors[k] & 255;
					if (l / 4 == 0) {
						this.texture.method_19449().method_19460(j, i, (k + k / 128 & 1) * 8 + 16 << 24);
					} else {
						this.texture.method_19449().method_19460(j, i, MaterialColor.COLORS[l / 4].getRenderColor(l & 3));
					}
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
			int k = 0;

			for (class_3082 lv : this.mapState.icons.values()) {
				if (!noIcons || lv.method_13824()) {
					MapRenderer.this.textureManager.bindTexture(MapRenderer.MAP_ICONS_TEXTURE);
					GlStateManager.pushMatrix();
					GlStateManager.translate(0.0F + (float)lv.method_13821() / 2.0F + 64.0F, 0.0F + (float)lv.method_13822() / 2.0F + 64.0F, -0.02F);
					GlStateManager.rotate((float)(lv.method_13823() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.scale(4.0F, 4.0F, 3.0F);
					GlStateManager.translate(-0.125F, 0.125F, 0.0F);
					byte b = lv.method_13819();
					float g = (float)(b % 16 + 0) / 16.0F;
					float h = (float)(b / 16 + 0) / 16.0F;
					float l = (float)(b % 16 + 1) / 16.0F;
					float m = (float)(b / 16 + 1) / 16.0F;
					bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					float n = -0.001F;
					bufferBuilder.vertex(-1.0, 1.0, (double)((float)k * -0.001F)).texture((double)g, (double)h).next();
					bufferBuilder.vertex(1.0, 1.0, (double)((float)k * -0.001F)).texture((double)l, (double)h).next();
					bufferBuilder.vertex(1.0, -1.0, (double)((float)k * -0.001F)).texture((double)l, (double)m).next();
					bufferBuilder.vertex(-1.0, -1.0, (double)((float)k * -0.001F)).texture((double)g, (double)m).next();
					tessellator.draw();
					GlStateManager.popMatrix();
					if (lv.method_17923() != null) {
						TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
						String string = lv.method_17923().asFormattedString();
						float o = (float)textRenderer.getStringWidth(string);
						float p = MathHelper.clamp(25.0F / o, 0.0F, 6.0F / (float)textRenderer.fontHeight);
						GlStateManager.pushMatrix();
						GlStateManager.translate(0.0F + (float)lv.method_13821() / 2.0F + 64.0F - o * p / 2.0F, 0.0F + (float)lv.method_13822() / 2.0F + 64.0F + 4.0F, -0.025F);
						GlStateManager.scale(p, p, 1.0F);
						InGameHud.fill(-1, -1, (int)o, textRenderer.fontHeight - 1, Integer.MIN_VALUE);
						GlStateManager.translate(0.0F, 0.0F, -0.1F);
						textRenderer.method_18355(string, 0.0F, 0.0F, -1);
						GlStateManager.popMatrix();
					}

					k++;
				}
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, -0.04F);
			GlStateManager.scale(1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}

		public void close() {
			this.texture.close();
		}
	}
}
