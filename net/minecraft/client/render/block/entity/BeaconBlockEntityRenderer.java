package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class BeaconBlockEntityRenderer extends BlockEntityRenderer<BeaconBlockEntity> {
	private static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");

	public void render(BeaconBlockEntity beaconBlockEntity, double d, double e, double f, float g, int i) {
		float h = beaconBlockEntity.getBeamSpeed();
		GlStateManager.alphaFunc(516, 0.1F);
		if (h > 0.0F) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			GlStateManager.disableFog();
			List<BeaconBlockEntity.BeamSegment> list = beaconBlockEntity.getBeamSegments();
			int j = 0;

			for (int k = 0; k < list.size(); k++) {
				BeaconBlockEntity.BeamSegment beamSegment = (BeaconBlockEntity.BeamSegment)list.get(k);
				int l = j + beamSegment.getHeight();
				this.bindTexture(BEAM_TEXTURE);
				GL11.glTexParameterf(3553, 10242, 10497.0F);
				GL11.glTexParameterf(3553, 10243, 10497.0F);
				GlStateManager.disableLighting();
				GlStateManager.disableCull();
				GlStateManager.disableBlend();
				GlStateManager.depthMask(true);
				GlStateManager.blendFuncSeparate(770, 1, 1, 0);
				double m = (double)beaconBlockEntity.getEntityWorld().getLastUpdateTime() + (double)g;
				double n = MathHelper.fractionalPart(-m * 0.2 - (double)MathHelper.floor(-m * 0.1));
				float o = beamSegment.getColor()[0];
				float p = beamSegment.getColor()[1];
				float q = beamSegment.getColor()[2];
				double r = m * 0.025 * -1.5;
				double s = 0.2;
				double t = 0.5 + Math.cos(r + (Math.PI * 3.0 / 4.0)) * 0.2;
				double u = 0.5 + Math.sin(r + (Math.PI * 3.0 / 4.0)) * 0.2;
				double v = 0.5 + Math.cos(r + (Math.PI / 4)) * 0.2;
				double w = 0.5 + Math.sin(r + (Math.PI / 4)) * 0.2;
				double x = 0.5 + Math.cos(r + (Math.PI * 5.0 / 4.0)) * 0.2;
				double y = 0.5 + Math.sin(r + (Math.PI * 5.0 / 4.0)) * 0.2;
				double z = 0.5 + Math.cos(r + (Math.PI * 7.0 / 4.0)) * 0.2;
				double aa = 0.5 + Math.sin(r + (Math.PI * 7.0 / 4.0)) * 0.2;
				double ab = 0.0;
				double ac = 1.0;
				double ad = -1.0 + n;
				double ae = (double)((float)beamSegment.getHeight() * h) * 2.5 + ad;
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex(d + t, e + (double)l, f + u).texture(1.0, ae).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + t, e + (double)j, f + u).texture(1.0, ad).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + v, e + (double)j, f + w).texture(0.0, ad).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + v, e + (double)l, f + w).texture(0.0, ae).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + z, e + (double)l, f + aa).texture(1.0, ae).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + z, e + (double)j, f + aa).texture(1.0, ad).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + x, e + (double)j, f + y).texture(0.0, ad).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + x, e + (double)l, f + y).texture(0.0, ae).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + v, e + (double)l, f + w).texture(1.0, ae).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + v, e + (double)j, f + w).texture(1.0, ad).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + z, e + (double)j, f + aa).texture(0.0, ad).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + z, e + (double)l, f + aa).texture(0.0, ae).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + x, e + (double)l, f + y).texture(1.0, ae).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + x, e + (double)j, f + y).texture(1.0, ad).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + t, e + (double)j, f + u).texture(0.0, ad).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + t, e + (double)l, f + u).texture(0.0, ae).color(o, p, q, 1.0F).next();
				tessellator.draw();
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(770, 771, 1, 0);
				GlStateManager.depthMask(false);
				r = 0.2;
				s = 0.2;
				t = 0.8;
				u = 0.2;
				v = 0.2;
				w = 0.8;
				x = 0.8;
				y = 0.8;
				z = 0.0;
				aa = 1.0;
				ab = -1.0 + n;
				ac = (double)((float)beamSegment.getHeight() * h) + ab;
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex(d + 0.2, e + (double)l, f + 0.2).texture(1.0, ac).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.2, e + (double)j, f + 0.2).texture(1.0, ab).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.8, e + (double)j, f + 0.2).texture(0.0, ab).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.8, e + (double)l, f + 0.2).texture(0.0, ac).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.8, e + (double)l, f + 0.8).texture(1.0, ac).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.8, e + (double)j, f + 0.8).texture(1.0, ab).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.2, e + (double)j, f + 0.8).texture(0.0, ab).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.2, e + (double)l, f + 0.8).texture(0.0, ac).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.8, e + (double)l, f + 0.2).texture(1.0, ac).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.8, e + (double)j, f + 0.2).texture(1.0, ab).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.8, e + (double)j, f + 0.8).texture(0.0, ab).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.8, e + (double)l, f + 0.8).texture(0.0, ac).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.2, e + (double)l, f + 0.8).texture(1.0, ac).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.2, e + (double)j, f + 0.8).texture(1.0, ab).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.2, e + (double)j, f + 0.2).texture(0.0, ab).color(o, p, q, 0.125F).next();
				bufferBuilder.vertex(d + 0.2, e + (double)l, f + 0.2).texture(0.0, ac).color(o, p, q, 0.125F).next();
				tessellator.draw();
				GlStateManager.enableLighting();
				GlStateManager.enableTexture();
				GlStateManager.depthMask(true);
				j = l;
			}

			GlStateManager.enableFog();
		}
	}

	@Override
	public boolean rendersOutsideBoundingBox() {
		return true;
	}
}
