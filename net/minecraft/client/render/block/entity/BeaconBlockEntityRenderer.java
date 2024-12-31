package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BeaconBlockEntityRenderer extends BlockEntityRenderer<BeaconBlockEntity> {
	public static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");

	public void render(BeaconBlockEntity beaconBlockEntity, double d, double e, double f, float g, int i) {
		this.method_12408(
			d,
			e,
			f,
			(double)g,
			(double)beaconBlockEntity.getBeamSpeed(),
			beaconBlockEntity.getBeamSegments(),
			(double)beaconBlockEntity.getEntityWorld().getLastUpdateTime()
		);
	}

	public void method_12408(double d, double e, double f, double g, double h, List<BeaconBlockEntity.BeamSegment> list, double i) {
		GlStateManager.alphaFunc(516, 0.1F);
		this.bindTexture(BEAM_TEXTURE);
		if (h > 0.0) {
			GlStateManager.disableFog();
			int j = 0;

			for (int k = 0; k < list.size(); k++) {
				BeaconBlockEntity.BeamSegment beamSegment = (BeaconBlockEntity.BeamSegment)list.get(k);
				method_12406(d, e, f, g, h, i, j, beamSegment.getHeight(), beamSegment.getColor());
				j += beamSegment.getHeight();
			}

			GlStateManager.enableFog();
		}
	}

	public static void method_12406(double d, double e, double f, double g, double h, double i, int j, int k, float[] fs) {
		method_12407(d, e, f, g, h, i, j, k, fs, 0.2, 0.25);
	}

	public static void method_12407(double d, double e, double f, double g, double h, double i, int j, int k, float[] fs, double l, double m) {
		int n = j + k;
		GlStateManager.method_12294(3553, 10242, 10497);
		GlStateManager.method_12294(3553, 10243, 10497);
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.method_12288(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		double o = i + g;
		double p = k < 0 ? o : -o;
		double q = MathHelper.fractionalPart(p * 0.2 - (double)MathHelper.floor(p * 0.1));
		float r = fs[0];
		float s = fs[1];
		float t = fs[2];
		double u = o * 0.025 * -1.5;
		double v = 0.5 + Math.cos(u + (Math.PI * 3.0 / 4.0)) * l;
		double w = 0.5 + Math.sin(u + (Math.PI * 3.0 / 4.0)) * l;
		double x = 0.5 + Math.cos(u + (Math.PI / 4)) * l;
		double y = 0.5 + Math.sin(u + (Math.PI / 4)) * l;
		double z = 0.5 + Math.cos(u + (Math.PI * 5.0 / 4.0)) * l;
		double aa = 0.5 + Math.sin(u + (Math.PI * 5.0 / 4.0)) * l;
		double ab = 0.5 + Math.cos(u + (Math.PI * 7.0 / 4.0)) * l;
		double ac = 0.5 + Math.sin(u + (Math.PI * 7.0 / 4.0)) * l;
		double ad = 0.0;
		double ae = 1.0;
		double af = -1.0 + q;
		double ag = (double)k * h * (0.5 / l) + af;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(d + v, e + (double)n, f + w).texture(1.0, ag).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + v, e + (double)j, f + w).texture(1.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + x, e + (double)j, f + y).texture(0.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + x, e + (double)n, f + y).texture(0.0, ag).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + ab, e + (double)n, f + ac).texture(1.0, ag).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + ab, e + (double)j, f + ac).texture(1.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + z, e + (double)j, f + aa).texture(0.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + z, e + (double)n, f + aa).texture(0.0, ag).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + x, e + (double)n, f + y).texture(1.0, ag).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + x, e + (double)j, f + y).texture(1.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + ab, e + (double)j, f + ac).texture(0.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + ab, e + (double)n, f + ac).texture(0.0, ag).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + z, e + (double)n, f + aa).texture(1.0, ag).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + z, e + (double)j, f + aa).texture(1.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + v, e + (double)j, f + w).texture(0.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(d + v, e + (double)n, f + w).texture(0.0, ag).color(r, s, t, 1.0F).next();
		tessellator.draw();
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.depthMask(false);
		u = 0.5 - m;
		v = 0.5 - m;
		w = 0.5 + m;
		x = 0.5 - m;
		y = 0.5 - m;
		z = 0.5 + m;
		aa = 0.5 + m;
		ab = 0.5 + m;
		ac = 0.0;
		ad = 1.0;
		ae = -1.0 + q;
		af = (double)k * h + ae;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(d + u, e + (double)n, f + v).texture(1.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + u, e + (double)j, f + v).texture(1.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + w, e + (double)j, f + x).texture(0.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + w, e + (double)n, f + x).texture(0.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + aa, e + (double)n, f + ab).texture(1.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + aa, e + (double)j, f + ab).texture(1.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + y, e + (double)j, f + z).texture(0.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + y, e + (double)n, f + z).texture(0.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + w, e + (double)n, f + x).texture(1.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + w, e + (double)j, f + x).texture(1.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + aa, e + (double)j, f + ab).texture(0.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + aa, e + (double)n, f + ab).texture(0.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + y, e + (double)n, f + z).texture(1.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + y, e + (double)j, f + z).texture(1.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + u, e + (double)j, f + v).texture(0.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(d + u, e + (double)n, f + v).texture(0.0, af).color(r, s, t, 0.125F).next();
		tessellator.draw();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture();
		GlStateManager.depthMask(true);
	}

	public boolean method_12410(BeaconBlockEntity beaconBlockEntity) {
		return true;
	}
}
