package com.mojang.blaze3d.platform;

import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL11;

public class GlStateManager {
	private static GlStateManager.AlphaTestState ALPHA_TEST = new GlStateManager.AlphaTestState();
	private static GlStateManager.BooleanState LIGHTING = new GlStateManager.BooleanState(2896);
	private static GlStateManager.BooleanState[] LIGHTING_STATES = new GlStateManager.BooleanState[8];
	private static GlStateManager.ColorMaterialState COLOR_MATERIAL = new GlStateManager.ColorMaterialState();
	private static GlStateManager.BlendFuncState BLEND = new GlStateManager.BlendFuncState();
	private static GlStateManager.DepthTestState DEPTH = new GlStateManager.DepthTestState();
	private static GlStateManager.FogState FOG = new GlStateManager.FogState();
	private static GlStateManager.CullFaceState CULL = new GlStateManager.CullFaceState();
	private static GlStateManager.PolygonOffsetState POLY_OFFSET = new GlStateManager.PolygonOffsetState();
	private static GlStateManager.LogicOpState COLOR_LOGIC = new GlStateManager.LogicOpState();
	private static GlStateManager.TexGenState TEX_GEN = new GlStateManager.TexGenState();
	private static GlStateManager.ClearState CLEAR = new GlStateManager.ClearState();
	private static GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
	private static GlStateManager.BooleanState NORMALIZE = new GlStateManager.BooleanState(2977);
	private static int activeTexture = 0;
	private static GlStateManager.Texture2DState[] TEXTURES = new GlStateManager.Texture2DState[8];
	private static int modelShadeMode = 7425;
	private static GlStateManager.BooleanState RESCALE_NORMAL = new GlStateManager.BooleanState(32826);
	private static GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();
	private static GlStateManager.Color4 COLOR = new GlStateManager.Color4();

	public static void pushLightingAttributes() {
		GL11.glPushAttrib(8256);
	}

	public static void popAttributes() {
		GL11.glPopAttrib();
	}

	public static void disableAlphaTest() {
		ALPHA_TEST.capState.disable();
	}

	public static void enableAlphaTest() {
		ALPHA_TEST.capState.enable();
	}

	public static void alphaFunc(int func, float ref) {
		if (func != ALPHA_TEST.func || ref != ALPHA_TEST.ref) {
			ALPHA_TEST.func = func;
			ALPHA_TEST.ref = ref;
			GL11.glAlphaFunc(func, ref);
		}
	}

	public static void enableLighting() {
		LIGHTING.enable();
	}

	public static void disableLighting() {
		LIGHTING.disable();
	}

	public static void enableLight(int index) {
		LIGHTING_STATES[index].enable();
	}

	public static void disableLight(int index) {
		LIGHTING_STATES[index].disable();
	}

	public static void enableColorMaterial() {
		COLOR_MATERIAL.capState.enable();
	}

	public static void disableColorMaterial() {
		COLOR_MATERIAL.capState.disable();
	}

	public static void colorMaterial(int face, int mode) {
		if (face != COLOR_MATERIAL.face || mode != COLOR_MATERIAL.mode) {
			COLOR_MATERIAL.face = face;
			COLOR_MATERIAL.mode = mode;
			GL11.glColorMaterial(face, mode);
		}
	}

	public static void disableDepthTest() {
		DEPTH.capState.disable();
	}

	public static void enableDepthTest() {
		DEPTH.capState.enable();
	}

	public static void depthFunc(int func) {
		if (func != DEPTH.func) {
			DEPTH.func = func;
			GL11.glDepthFunc(func);
		}
	}

	public static void depthMask(boolean mask) {
		if (mask != DEPTH.mask) {
			DEPTH.mask = mask;
			GL11.glDepthMask(mask);
		}
	}

	public static void disableBlend() {
		BLEND.capState.disable();
	}

	public static void enableBlend() {
		BLEND.capState.enable();
	}

	public static void blendFunc(int srcFactor, int dstFactor) {
		if (srcFactor != BLEND.srcFactorRGB || dstFactor != BLEND.dstFactorRGB) {
			BLEND.srcFactorRGB = srcFactor;
			BLEND.dstFactorRGB = dstFactor;
			GL11.glBlendFunc(srcFactor, dstFactor);
		}
	}

	public static void blendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
		if (srcFactorRGB != BLEND.srcFactorRGB
			|| dstFactorRGB != BLEND.dstFactorRGB
			|| srcFactorAlpha != BLEND.srcFactorAlpha
			|| dstFactorAlpha != BLEND.dstFactorAlpha) {
			BLEND.srcFactorRGB = srcFactorRGB;
			BLEND.dstFactorRGB = dstFactorRGB;
			BLEND.srcFactorAlpha = srcFactorAlpha;
			BLEND.dstFactorAlpha = dstFactorAlpha;
			GLX.glBlendFuncSeparate(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
		}
	}

	public static void enableFog() {
		FOG.capState.enable();
	}

	public static void disableFog() {
		FOG.capState.disable();
	}

	public static void fogMode(int mode) {
		if (mode != FOG.mode) {
			FOG.mode = mode;
			GL11.glFogi(2917, mode);
		}
	}

	public static void fogDensity(float density) {
		if (density != FOG.density) {
			FOG.density = density;
			GL11.glFogf(2914, density);
		}
	}

	public static void fogStart(float start) {
		if (start != FOG.start) {
			FOG.start = start;
			GL11.glFogf(2915, start);
		}
	}

	public static void fogEnd(float end) {
		if (end != FOG.end) {
			FOG.end = end;
			GL11.glFogf(2916, end);
		}
	}

	public static void enableCull() {
		CULL.capState.enable();
	}

	public static void disableCull() {
		CULL.capState.disable();
	}

	public static void cullFace(int mode) {
		if (mode != CULL.mode) {
			CULL.mode = mode;
			GL11.glCullFace(mode);
		}
	}

	public static void enablePolyOffset() {
		POLY_OFFSET.capFill.enable();
	}

	public static void disablePolyOffset() {
		POLY_OFFSET.capFill.disable();
	}

	public static void polygonOffset(float factor, float units) {
		if (factor != POLY_OFFSET.factor || units != POLY_OFFSET.units) {
			POLY_OFFSET.factor = factor;
			POLY_OFFSET.units = units;
			GL11.glPolygonOffset(factor, units);
		}
	}

	public static void enableColorLogic() {
		COLOR_LOGIC.capState.enable();
	}

	public static void disableColorLogic() {
		COLOR_LOGIC.capState.disable();
	}

	public static void logicOp(int op) {
		if (op != COLOR_LOGIC.op) {
			COLOR_LOGIC.op = op;
			GL11.glLogicOp(op);
		}
	}

	public static void enableTexCoord(GlStateManager.TexCoord coord) {
		getGenCoordState(coord).capState.enable();
	}

	public static void disableTexCoord(GlStateManager.TexCoord coord) {
		getGenCoordState(coord).capState.disable();
	}

	public static void genTex(GlStateManager.TexCoord coord, int mode) {
		GlStateManager.TexGenCoordState texGenCoordState = getGenCoordState(coord);
		if (mode != texGenCoordState.mode) {
			texGenCoordState.mode = mode;
			GL11.glTexGeni(texGenCoordState.coord, 9472, mode);
		}
	}

	public static void genTex(GlStateManager.TexCoord coord, int mode, FloatBuffer buffer) {
		GL11.glTexGen(getGenCoordState(coord).coord, mode, buffer);
	}

	private static GlStateManager.TexGenCoordState getGenCoordState(GlStateManager.TexCoord coord) {
		switch (coord) {
			case S:
				return TEX_GEN.s;
			case T:
				return TEX_GEN.t;
			case R:
				return TEX_GEN.r;
			case Q:
				return TEX_GEN.q;
			default:
				return TEX_GEN.s;
		}
	}

	public static void activeTexture(int texture) {
		if (activeTexture != texture - GLX.textureUnit) {
			activeTexture = texture - GLX.textureUnit;
			GLX.gl13ActiveTexture(texture);
		}
	}

	public static void enableTexture() {
		TEXTURES[activeTexture].capState.enable();
	}

	public static void disableTexture() {
		TEXTURES[activeTexture].capState.disable();
	}

	public static int getTexLevelParameter() {
		return GL11.glGenTextures();
	}

	public static void deleteTexture(int texture) {
		GL11.glDeleteTextures(texture);

		for (GlStateManager.Texture2DState texture2DState : TEXTURES) {
			if (texture2DState.boundTexture == texture) {
				texture2DState.boundTexture = -1;
			}
		}
	}

	public static void bindTexture(int texture) {
		if (texture != TEXTURES[activeTexture].boundTexture) {
			TEXTURES[activeTexture].boundTexture = texture;
			GL11.glBindTexture(3553, texture);
		}
	}

	public static void enableNormalize() {
		NORMALIZE.enable();
	}

	public static void disableNormalize() {
		NORMALIZE.disable();
	}

	public static void shadeModel(int mode) {
		if (mode != modelShadeMode) {
			modelShadeMode = mode;
			GL11.glShadeModel(mode);
		}
	}

	public static void enableRescaleNormal() {
		RESCALE_NORMAL.enable();
	}

	public static void disableRescaleNormal() {
		RESCALE_NORMAL.disable();
	}

	public static void viewport(int x, int y, int width, int height) {
		GL11.glViewport(x, y, width, height);
	}

	public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		if (red != COLOR_MASK.red || green != COLOR_MASK.green || blue != COLOR_MASK.blue || alpha != COLOR_MASK.alpha) {
			COLOR_MASK.red = red;
			COLOR_MASK.green = green;
			COLOR_MASK.blue = blue;
			COLOR_MASK.alpha = alpha;
			GL11.glColorMask(red, green, blue, alpha);
		}
	}

	public static void clearDepth(double depth) {
		if (depth != CLEAR.clearDepth) {
			CLEAR.clearDepth = depth;
			GL11.glClearDepth(depth);
		}
	}

	public static void clearColor(float red, float green, float blue, float alpha) {
		if (red != CLEAR.color.red || green != CLEAR.color.green || blue != CLEAR.color.blue || alpha != CLEAR.color.alpha) {
			CLEAR.color.red = red;
			CLEAR.color.green = green;
			CLEAR.color.blue = blue;
			CLEAR.color.alpha = alpha;
			GL11.glClearColor(red, green, blue, alpha);
		}
	}

	public static void clear(int mode) {
		GL11.glClear(mode);
	}

	public static void matrixMode(int mode) {
		GL11.glMatrixMode(mode);
	}

	public static void loadIdentity() {
		GL11.glLoadIdentity();
	}

	public static void pushMatrix() {
		GL11.glPushMatrix();
	}

	public static void popMatrix() {
		GL11.glPopMatrix();
	}

	public static void getFloat(int mode, FloatBuffer buffer) {
		GL11.glGetFloat(mode, buffer);
	}

	public static void ortho(double l, double r, double b, double t, double n, double f) {
		GL11.glOrtho(l, r, b, t, n, f);
	}

	public static void rotate(float angle, float x, float y, float z) {
		GL11.glRotatef(angle, x, y, z);
	}

	public static void scale(float x, float y, float z) {
		GL11.glScalef(x, y, z);
	}

	public static void scale(double x, double y, double z) {
		GL11.glScaled(x, y, z);
	}

	public static void translate(float x, float y, float z) {
		GL11.glTranslatef(x, y, z);
	}

	public static void translate(double x, double y, double z) {
		GL11.glTranslated(x, y, z);
	}

	public static void multiMatrix(FloatBuffer buffer) {
		GL11.glMultMatrix(buffer);
	}

	public static void color(float red, float green, float blue, float alpha) {
		if (red != COLOR.red || green != COLOR.green || blue != COLOR.blue || alpha != COLOR.alpha) {
			COLOR.red = red;
			COLOR.green = green;
			COLOR.blue = blue;
			COLOR.alpha = alpha;
			GL11.glColor4f(red, green, blue, alpha);
		}
	}

	public static void color(float red, float green, float blue) {
		color(red, green, blue, 1.0F);
	}

	public static void clearColor() {
		COLOR.red = COLOR.green = COLOR.blue = COLOR.alpha = -1.0F;
	}

	public static void callList(int listId) {
		GL11.glCallList(listId);
	}

	static {
		for (int i = 0; i < 8; i++) {
			LIGHTING_STATES[i] = new GlStateManager.BooleanState(16384 + i);
		}

		for (int j = 0; j < 8; j++) {
			TEXTURES[j] = new GlStateManager.Texture2DState();
		}
	}

	static class AlphaTestState {
		public GlStateManager.BooleanState capState = new GlStateManager.BooleanState(3008);
		public int func = 519;
		public float ref = -1.0F;

		private AlphaTestState() {
		}
	}

	static class BlendFuncState {
		public GlStateManager.BooleanState capState = new GlStateManager.BooleanState(3042);
		public int srcFactorRGB = 1;
		public int dstFactorRGB = 0;
		public int srcFactorAlpha = 1;
		public int dstFactorAlpha = 0;

		private BlendFuncState() {
		}
	}

	static class BooleanState {
		private final int id;
		private boolean cachedState = false;

		public BooleanState(int i) {
			this.id = i;
		}

		public void disable() {
			this.setState(false);
		}

		public void enable() {
			this.setState(true);
		}

		public void setState(boolean state) {
			if (state != this.cachedState) {
				this.cachedState = state;
				if (state) {
					GL11.glEnable(this.id);
				} else {
					GL11.glDisable(this.id);
				}
			}
		}
	}

	static class ClearState {
		public double clearDepth = 1.0;
		public GlStateManager.Color4 color = new GlStateManager.Color4(0.0F, 0.0F, 0.0F, 0.0F);
		public int clearStencil = 0;

		private ClearState() {
		}
	}

	static class Color4 {
		public float red = 1.0F;
		public float green = 1.0F;
		public float blue = 1.0F;
		public float alpha = 1.0F;

		public Color4() {
		}

		public Color4(float f, float g, float h, float i) {
			this.red = f;
			this.green = g;
			this.blue = h;
			this.alpha = i;
		}
	}

	static class ColorMask {
		public boolean red = true;
		public boolean green = true;
		public boolean blue = true;
		public boolean alpha = true;

		private ColorMask() {
		}
	}

	static class ColorMaterialState {
		public GlStateManager.BooleanState capState = new GlStateManager.BooleanState(2903);
		public int face = 1032;
		public int mode = 5634;

		private ColorMaterialState() {
		}
	}

	static class CullFaceState {
		public GlStateManager.BooleanState capState = new GlStateManager.BooleanState(2884);
		public int mode = 1029;

		private CullFaceState() {
		}
	}

	static class DepthTestState {
		public GlStateManager.BooleanState capState = new GlStateManager.BooleanState(2929);
		public boolean mask = true;
		public int func = 513;

		private DepthTestState() {
		}
	}

	static class FogState {
		public GlStateManager.BooleanState capState = new GlStateManager.BooleanState(2912);
		public int mode = 2048;
		public float density = 1.0F;
		public float start = 0.0F;
		public float end = 1.0F;

		private FogState() {
		}
	}

	static class LogicOpState {
		public GlStateManager.BooleanState capState = new GlStateManager.BooleanState(3058);
		public int op = 5379;

		private LogicOpState() {
		}
	}

	static class PolygonOffsetState {
		public GlStateManager.BooleanState capFill = new GlStateManager.BooleanState(32823);
		public GlStateManager.BooleanState capLine = new GlStateManager.BooleanState(10754);
		public float factor = 0.0F;
		public float units = 0.0F;

		private PolygonOffsetState() {
		}
	}

	static class StencilState {
		public GlStateManager.StencilSubState capState = new GlStateManager.StencilSubState();
		public int mask = -1;
		public int sfail = 7680;
		public int dpfail = 7680;
		public int dppass = 7680;

		private StencilState() {
		}
	}

	static class StencilSubState {
		public int func = 519;
		public int ref = 0;
		public int mask = -1;

		private StencilSubState() {
		}
	}

	public static enum TexCoord {
		S,
		T,
		R,
		Q;
	}

	static class TexGenCoordState {
		public GlStateManager.BooleanState capState;
		public int coord;
		public int mode = -1;

		public TexGenCoordState(int i, int j) {
			this.coord = i;
			this.capState = new GlStateManager.BooleanState(j);
		}
	}

	static class TexGenState {
		public GlStateManager.TexGenCoordState s = new GlStateManager.TexGenCoordState(8192, 3168);
		public GlStateManager.TexGenCoordState t = new GlStateManager.TexGenCoordState(8193, 3169);
		public GlStateManager.TexGenCoordState r = new GlStateManager.TexGenCoordState(8194, 3170);
		public GlStateManager.TexGenCoordState q = new GlStateManager.TexGenCoordState(8195, 3171);

		private TexGenState() {
		}
	}

	static class Texture2DState {
		public GlStateManager.BooleanState capState = new GlStateManager.BooleanState(3553);
		public int boundTexture = 0;

		private Texture2DState() {
		}
	}
}
