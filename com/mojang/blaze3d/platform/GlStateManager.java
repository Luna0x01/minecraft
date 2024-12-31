package com.mojang.blaze3d.platform;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.render.DiffuseLighting;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Quaternion;

public class GlStateManager {
	private static final FloatBuffer field_13464 = BufferUtils.createFloatBuffer(16);
	private static final FloatBuffer field_13465 = BufferUtils.createFloatBuffer(4);
	private static final GlStateManager.AlphaTestState ALPHA_TEST = new GlStateManager.AlphaTestState();
	private static final GlStateManager.BooleanState LIGHTING = new GlStateManager.BooleanState(2896);
	private static final GlStateManager.BooleanState[] LIGHTING_STATES = new GlStateManager.BooleanState[8];
	private static final GlStateManager.ColorMaterialState COLOR_MATERIAL;
	private static final GlStateManager.BlendFuncState BLEND;
	private static final GlStateManager.DepthTestState DEPTH;
	private static final GlStateManager.FogState FOG;
	private static final GlStateManager.CullFaceState CULL;
	private static final GlStateManager.PolygonOffsetState POLY_OFFSET;
	private static final GlStateManager.LogicOpState COLOR_LOGIC;
	private static final GlStateManager.TexGenState TEX_GEN;
	private static final GlStateManager.ClearState CLEAR;
	private static final GlStateManager.StencilState STENCIL;
	private static final GlStateManager.BooleanState NORMALIZE;
	private static int activeTexture;
	private static final GlStateManager.Texture2DState[] TEXTURES;
	private static int modelShadeMode;
	private static final GlStateManager.BooleanState RESCALE_NORMAL;
	private static final GlStateManager.ColorMask COLOR_MASK;
	private static final GlStateManager.Color4 COLOR;

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

	public static void method_12281(int i, int j, FloatBuffer floatBuffer) {
		GL11.glLight(i, j, floatBuffer);
	}

	public static void method_12282(int i, FloatBuffer floatBuffer) {
		GL11.glLightModel(i, floatBuffer);
	}

	public static void method_12272(float f, float g, float h) {
		GL11.glNormal3f(f, g, h);
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

	public static void method_12287(GlStateManager.class_2870 arg, GlStateManager.class_2866 arg2) {
		blendFunc(arg.field_13529, arg2.field_13485);
	}

	public static void blendFunc(int srcFactor, int dstFactor) {
		if (srcFactor != BLEND.srcFactorRGB || dstFactor != BLEND.dstFactorRGB) {
			BLEND.srcFactorRGB = srcFactor;
			BLEND.dstFactorRGB = dstFactor;
			GL11.glBlendFunc(srcFactor, dstFactor);
		}
	}

	public static void method_12288(GlStateManager.class_2870 arg, GlStateManager.class_2866 arg2, GlStateManager.class_2870 arg3, GlStateManager.class_2866 arg4) {
		blendFuncSeparate(arg.field_13529, arg2.field_13485, arg3.field_13529, arg4.field_13485);
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

	public static void method_12305(int i) {
		GL14.glBlendEquation(i);
	}

	public static void method_12309(int i) {
		field_13465.put(0, (float)(i >> 16 & 0xFF) / 255.0F);
		field_13465.put(1, (float)(i >> 8 & 0xFF) / 255.0F);
		field_13465.put(2, (float)(i >> 0 & 0xFF) / 255.0F);
		field_13465.put(3, (float)(i >> 24 & 0xFF) / 255.0F);
		method_12297(8960, 8705, field_13465);
		method_12274(8960, 8704, 34160);
		method_12274(8960, 34161, 7681);
		method_12274(8960, 34176, 34166);
		method_12274(8960, 34192, 768);
		method_12274(8960, 34162, 7681);
		method_12274(8960, 34184, 5890);
		method_12274(8960, 34200, 770);
	}

	public static void method_12315() {
		method_12274(8960, 8704, 8448);
		method_12274(8960, 34161, 8448);
		method_12274(8960, 34162, 8448);
		method_12274(8960, 34176, 5890);
		method_12274(8960, 34184, 5890);
		method_12274(8960, 34192, 768);
		method_12274(8960, 34200, 770);
	}

	public static void enableFog() {
		FOG.capState.enable();
	}

	public static void disableFog() {
		FOG.capState.disable();
	}

	public static void method_12285(GlStateManager.class_2867 arg) {
		fogMode(arg.field_13490);
	}

	private static void fogMode(int mode) {
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

	public static void method_12298(int i, FloatBuffer floatBuffer) {
		GL11.glFog(i, floatBuffer);
	}

	public static void method_12300(int i, int j) {
		GL11.glFogi(i, j);
	}

	public static void enableCull() {
		CULL.capState.enable();
	}

	public static void disableCull() {
		CULL.capState.disable();
	}

	public static void method_12284(GlStateManager.class_2865 arg) {
		cullFace(arg.field_13469);
	}

	private static void cullFace(int mode) {
		if (mode != CULL.mode) {
			CULL.mode = mode;
			GL11.glCullFace(mode);
		}
	}

	public static void method_12306(int i, int j) {
		GL11.glPolygonMode(i, j);
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

	public static void method_9807(GlStateManager.class_2868 arg) {
		logicOp(arg.field_13508);
	}

	public static void logicOp(int op) {
		if (op != COLOR_LOGIC.op) {
			COLOR_LOGIC.op = op;
			GL11.glLogicOp(op);
		}
	}

	public static void method_12289(GlStateManager.TexCoord texCoord) {
		getGenCoordState(texCoord).capState.enable();
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

	public static void method_12297(int i, int j, FloatBuffer floatBuffer) {
		GL11.glTexEnv(i, j, floatBuffer);
	}

	public static void method_12274(int i, int j, int k) {
		GL11.glTexEnvi(i, j, k);
	}

	public static void method_12273(int i, int j, float f) {
		GL11.glTexEnvf(i, j, f);
	}

	public static void method_12293(int i, int j, float f) {
		GL11.glTexParameterf(i, j, f);
	}

	public static void method_12294(int i, int j, int k) {
		GL11.glTexParameteri(i, j, k);
	}

	public static int method_12301(int i, int j, int k) {
		return GL11.glGetTexLevelParameteri(i, j, k);
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

	public static void method_12276(int i, int j, int k, int l, int m, int n, int o, int p, @Nullable IntBuffer intBuffer) {
		GL11.glTexImage2D(i, j, k, l, m, n, o, p, intBuffer);
	}

	public static void method_12295(int i, int j, int k, int l, int m, int n, int o, int p, IntBuffer intBuffer) {
		GL11.glTexSubImage2D(i, j, k, l, m, n, o, p, intBuffer);
	}

	public static void method_12275(int i, int j, int k, int l, int m, int n, int o, int p) {
		GL11.glCopyTexSubImage2D(i, j, k, l, m, n, o, p);
	}

	public static void method_12278(int i, int j, int k, int l, IntBuffer intBuffer) {
		GL11.glGetTexImage(i, j, k, l, intBuffer);
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

	public static void method_12291(Quaternion quaternion) {
		multiMatrix(method_12290(field_13464, quaternion));
	}

	public static FloatBuffer method_12290(FloatBuffer floatBuffer, Quaternion quaternion) {
		floatBuffer.clear();
		float f = quaternion.x * quaternion.x;
		float g = quaternion.x * quaternion.y;
		float h = quaternion.x * quaternion.z;
		float i = quaternion.x * quaternion.w;
		float j = quaternion.y * quaternion.y;
		float k = quaternion.y * quaternion.z;
		float l = quaternion.y * quaternion.w;
		float m = quaternion.z * quaternion.z;
		float n = quaternion.z * quaternion.w;
		floatBuffer.put(1.0F - 2.0F * (j + m));
		floatBuffer.put(2.0F * (g + n));
		floatBuffer.put(2.0F * (h - l));
		floatBuffer.put(0.0F);
		floatBuffer.put(2.0F * (g - n));
		floatBuffer.put(1.0F - 2.0F * (f + m));
		floatBuffer.put(2.0F * (k + i));
		floatBuffer.put(0.0F);
		floatBuffer.put(2.0F * (h + l));
		floatBuffer.put(2.0F * (k - i));
		floatBuffer.put(1.0F - 2.0F * (f + j));
		floatBuffer.put(0.0F);
		floatBuffer.put(0.0F);
		floatBuffer.put(0.0F);
		floatBuffer.put(0.0F);
		floatBuffer.put(1.0F);
		floatBuffer.rewind();
		return floatBuffer;
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

	public static void method_12292(float f, float g) {
		GL11.glTexCoord2f(f, g);
	}

	public static void method_12308(float f, float g, float h) {
		GL11.glVertex3f(f, g, h);
	}

	public static void clearColor() {
		COLOR.red = -1.0F;
		COLOR.green = -1.0F;
		COLOR.blue = -1.0F;
		COLOR.alpha = -1.0F;
	}

	public static void method_12280(int i, int j, ByteBuffer byteBuffer) {
		GL11.glNormalPointer(i, j, byteBuffer);
	}

	public static void method_12302(int i, int j, int k, int l) {
		GL11.glTexCoordPointer(i, j, k, (long)l);
	}

	public static void method_12279(int i, int j, int k, ByteBuffer byteBuffer) {
		GL11.glTexCoordPointer(i, j, k, byteBuffer);
	}

	public static void method_12307(int i, int j, int k, int l) {
		GL11.glVertexPointer(i, j, k, (long)l);
	}

	public static void method_12296(int i, int j, int k, ByteBuffer byteBuffer) {
		GL11.glVertexPointer(i, j, k, byteBuffer);
	}

	public static void method_12311(int i, int j, int k, int l) {
		GL11.glColorPointer(i, j, k, (long)l);
	}

	public static void method_12303(int i, int j, int k, ByteBuffer byteBuffer) {
		GL11.glColorPointer(i, j, k, byteBuffer);
	}

	public static void method_12316(int i) {
		GL11.glDisableClientState(i);
	}

	public static void method_12317(int i) {
		GL11.glEnableClientState(i);
	}

	public static void method_12318(int i) {
		GL11.glBegin(i);
	}

	public static void method_12269() {
		GL11.glEnd();
	}

	public static void method_12313(int i, int j, int k) {
		GL11.glDrawArrays(i, j, k);
	}

	public static void method_12304(float f) {
		GL11.glLineWidth(f);
	}

	public static void callList(int listId) {
		GL11.glCallList(listId);
	}

	public static void method_12310(int i, int j) {
		GL11.glDeleteLists(i, j);
	}

	public static void method_12312(int i, int j) {
		GL11.glNewList(i, j);
	}

	public static void method_12270() {
		GL11.glEndList();
	}

	public static int method_12319(int i) {
		return GL11.glGenLists(i);
	}

	public static void method_12314(int i, int j) {
		GL11.glPixelStorei(i, j);
	}

	public static void method_12277(int i, int j, int k, int l, int m, int n, IntBuffer intBuffer) {
		GL11.glReadPixels(i, j, k, l, m, n, intBuffer);
	}

	public static int method_12271() {
		return GL11.glGetError();
	}

	public static String method_12320(int i) {
		return GL11.glGetString(i);
	}

	public static void method_12283(int i, IntBuffer intBuffer) {
		GL11.glGetInteger(i, intBuffer);
	}

	public static int method_12321(int i) {
		return GL11.glGetInteger(i);
	}

	public static void method_12286(GlStateManager.class_2869 arg) {
		arg.method_12322();
	}

	public static void method_12299(GlStateManager.class_2869 arg) {
		arg.method_12323();
	}

	static {
		for (int i = 0; i < 8; i++) {
			LIGHTING_STATES[i] = new GlStateManager.BooleanState(16384 + i);
		}

		COLOR_MATERIAL = new GlStateManager.ColorMaterialState();
		BLEND = new GlStateManager.BlendFuncState();
		DEPTH = new GlStateManager.DepthTestState();
		FOG = new GlStateManager.FogState();
		CULL = new GlStateManager.CullFaceState();
		POLY_OFFSET = new GlStateManager.PolygonOffsetState();
		COLOR_LOGIC = new GlStateManager.LogicOpState();
		TEX_GEN = new GlStateManager.TexGenState();
		CLEAR = new GlStateManager.ClearState();
		STENCIL = new GlStateManager.StencilState();
		NORMALIZE = new GlStateManager.BooleanState(2977);
		TEXTURES = new GlStateManager.Texture2DState[8];

		for (int j = 0; j < 8; j++) {
			TEXTURES[j] = new GlStateManager.Texture2DState();
		}

		modelShadeMode = 7425;
		RESCALE_NORMAL = new GlStateManager.BooleanState(32826);
		COLOR_MASK = new GlStateManager.ColorMask();
		COLOR = new GlStateManager.Color4();
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
		private boolean cachedState;

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

		private ClearState() {
		}
	}

	static class Color4 {
		public float red = 1.0F;
		public float green = 1.0F;
		public float blue = 1.0F;
		public float alpha = 1.0F;

		public Color4() {
			this(1.0F, 1.0F, 1.0F, 1.0F);
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
		public float start;
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
		public float factor;
		public float units;

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
		public int boundTexture;

		private Texture2DState() {
		}
	}

	public static enum class_2865 {
		FRONT(1028),
		BACK(1029),
		FRONT_AND_BACK(1032);

		public final int field_13469;

		private class_2865(int j) {
			this.field_13469 = j;
		}
	}

	public static enum class_2866 {
		CONSTANT_ALPHA(32771),
		CONSTANT_COLOR(32769),
		DST_ALPHA(772),
		DST_COLOR(774),
		ONE(1),
		ONE_MINUS_CONSTANT_ALPHA(32772),
		ONE_MINUS_CONSTANT_COLOR(32770),
		ONE_MINUS_DST_ALPHA(773),
		ONE_MINUS_DST_COLOR(775),
		ONE_MINUS_SRC_ALPHA(771),
		ONE_MINUS_SRC_COLOR(769),
		SRC_ALPHA(770),
		SRC_COLOR(768),
		ZERO(0);

		public final int field_13485;

		private class_2866(int j) {
			this.field_13485 = j;
		}
	}

	public static enum class_2867 {
		LINEAR(9729),
		EXP(2048),
		EXP2(2049);

		public final int field_13490;

		private class_2867(int j) {
			this.field_13490 = j;
		}
	}

	public static enum class_2868 {
		AND(5377),
		AND_INVERTED(5380),
		AND_REVERSE(5378),
		CLEAR(5376),
		COPY(5379),
		COPY_INVERTED(5388),
		EQUIV(5385),
		INVERT(5386),
		NAND(5390),
		NOOP(5381),
		NOR(5384),
		OR(5383),
		OR_INVERTED(5389),
		OR_REVERSE(5387),
		SET(5391),
		XOR(5382);

		public final int field_13508;

		private class_2868(int j) {
			this.field_13508 = j;
		}
	}

	public static enum class_2869 {
		DEFAULT {
			@Override
			public void method_12322() {
				GlStateManager.disableAlphaTest();
				GlStateManager.alphaFunc(519, 0.0F);
				GlStateManager.disableLighting();
				GlStateManager.method_12282(2899, DiffuseLighting.method_845(0.2F, 0.2F, 0.2F, 1.0F));

				for (int i = 0; i < 8; i++) {
					GlStateManager.disableLight(i);
					GlStateManager.method_12281(16384 + i, 4608, DiffuseLighting.method_845(0.0F, 0.0F, 0.0F, 1.0F));
					GlStateManager.method_12281(16384 + i, 4611, DiffuseLighting.method_845(0.0F, 0.0F, 1.0F, 0.0F));
					if (i == 0) {
						GlStateManager.method_12281(16384 + i, 4609, DiffuseLighting.method_845(1.0F, 1.0F, 1.0F, 1.0F));
						GlStateManager.method_12281(16384 + i, 4610, DiffuseLighting.method_845(1.0F, 1.0F, 1.0F, 1.0F));
					} else {
						GlStateManager.method_12281(16384 + i, 4609, DiffuseLighting.method_845(0.0F, 0.0F, 0.0F, 1.0F));
						GlStateManager.method_12281(16384 + i, 4610, DiffuseLighting.method_845(0.0F, 0.0F, 0.0F, 1.0F));
					}
				}

				GlStateManager.disableColorMaterial();
				GlStateManager.colorMaterial(1032, 5634);
				GlStateManager.disableDepthTest();
				GlStateManager.depthFunc(513);
				GlStateManager.depthMask(true);
				GlStateManager.disableBlend();
				GlStateManager.method_12287(GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO);
				GlStateManager.method_12288(GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO);
				GlStateManager.method_12305(32774);
				GlStateManager.disableFog();
				GlStateManager.method_12300(2917, 2048);
				GlStateManager.fogDensity(1.0F);
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(1.0F);
				GlStateManager.method_12298(2918, DiffuseLighting.method_845(0.0F, 0.0F, 0.0F, 0.0F));
				if (GLContext.getCapabilities().GL_NV_fog_distance) {
					GlStateManager.method_12300(2917, 34140);
				}

				GlStateManager.polygonOffset(0.0F, 0.0F);
				GlStateManager.disableColorLogic();
				GlStateManager.logicOp(5379);
				GlStateManager.disableTexCoord(GlStateManager.TexCoord.S);
				GlStateManager.genTex(GlStateManager.TexCoord.S, 9216);
				GlStateManager.genTex(GlStateManager.TexCoord.S, 9474, DiffuseLighting.method_845(1.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.genTex(GlStateManager.TexCoord.S, 9217, DiffuseLighting.method_845(1.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.disableTexCoord(GlStateManager.TexCoord.T);
				GlStateManager.genTex(GlStateManager.TexCoord.T, 9216);
				GlStateManager.genTex(GlStateManager.TexCoord.T, 9474, DiffuseLighting.method_845(0.0F, 1.0F, 0.0F, 0.0F));
				GlStateManager.genTex(GlStateManager.TexCoord.T, 9217, DiffuseLighting.method_845(0.0F, 1.0F, 0.0F, 0.0F));
				GlStateManager.disableTexCoord(GlStateManager.TexCoord.R);
				GlStateManager.genTex(GlStateManager.TexCoord.R, 9216);
				GlStateManager.genTex(GlStateManager.TexCoord.R, 9474, DiffuseLighting.method_845(0.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.genTex(GlStateManager.TexCoord.R, 9217, DiffuseLighting.method_845(0.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.disableTexCoord(GlStateManager.TexCoord.Q);
				GlStateManager.genTex(GlStateManager.TexCoord.Q, 9216);
				GlStateManager.genTex(GlStateManager.TexCoord.Q, 9474, DiffuseLighting.method_845(0.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.genTex(GlStateManager.TexCoord.Q, 9217, DiffuseLighting.method_845(0.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.activeTexture(0);
				GlStateManager.method_12294(3553, 10240, 9729);
				GlStateManager.method_12294(3553, 10241, 9986);
				GlStateManager.method_12294(3553, 10242, 10497);
				GlStateManager.method_12294(3553, 10243, 10497);
				GlStateManager.method_12294(3553, 33085, 1000);
				GlStateManager.method_12294(3553, 33083, 1000);
				GlStateManager.method_12294(3553, 33082, -1000);
				GlStateManager.method_12293(3553, 34049, 0.0F);
				GlStateManager.method_12274(8960, 8704, 8448);
				GlStateManager.method_12297(8960, 8705, DiffuseLighting.method_845(0.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.method_12274(8960, 34161, 8448);
				GlStateManager.method_12274(8960, 34162, 8448);
				GlStateManager.method_12274(8960, 34176, 5890);
				GlStateManager.method_12274(8960, 34177, 34168);
				GlStateManager.method_12274(8960, 34178, 34166);
				GlStateManager.method_12274(8960, 34184, 5890);
				GlStateManager.method_12274(8960, 34185, 34168);
				GlStateManager.method_12274(8960, 34186, 34166);
				GlStateManager.method_12274(8960, 34192, 768);
				GlStateManager.method_12274(8960, 34193, 768);
				GlStateManager.method_12274(8960, 34194, 770);
				GlStateManager.method_12274(8960, 34200, 770);
				GlStateManager.method_12274(8960, 34201, 770);
				GlStateManager.method_12274(8960, 34202, 770);
				GlStateManager.method_12273(8960, 34163, 1.0F);
				GlStateManager.method_12273(8960, 3356, 1.0F);
				GlStateManager.disableNormalize();
				GlStateManager.shadeModel(7425);
				GlStateManager.disableRescaleNormal();
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.clearDepth(1.0);
				GlStateManager.method_12304(1.0F);
				GlStateManager.method_12272(0.0F, 0.0F, 1.0F);
				GlStateManager.method_12306(1028, 6914);
				GlStateManager.method_12306(1029, 6914);
			}

			@Override
			public void method_12323() {
			}
		},
		PLAYER_SKIN {
			@Override
			public void method_12322() {
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(770, 771, 1, 0);
			}

			@Override
			public void method_12323() {
				GlStateManager.disableBlend();
			}
		},
		TRANSPARENT_MODEL {
			@Override
			public void method_12322() {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
				GlStateManager.depthMask(false);
				GlStateManager.enableBlend();
				GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
				GlStateManager.alphaFunc(516, 0.003921569F);
			}

			@Override
			public void method_12323() {
				GlStateManager.disableBlend();
				GlStateManager.alphaFunc(516, 0.1F);
				GlStateManager.depthMask(true);
			}
		};

		private class_2869() {
		}

		public abstract void method_12322();

		public abstract void method_12323();
	}

	public static enum class_2870 {
		CONSTANT_ALPHA(32771),
		CONSTANT_COLOR(32769),
		DST_ALPHA(772),
		DST_COLOR(774),
		ONE(1),
		ONE_MINUS_CONSTANT_ALPHA(32772),
		ONE_MINUS_CONSTANT_COLOR(32770),
		ONE_MINUS_DST_ALPHA(773),
		ONE_MINUS_DST_COLOR(775),
		ONE_MINUS_SRC_ALPHA(771),
		ONE_MINUS_SRC_COLOR(769),
		SRC_ALPHA(770),
		SRC_ALPHA_SATURATE(776),
		SRC_COLOR(768),
		ZERO(0);

		public final int field_13529;

		private class_2870(int j) {
			this.field_13529 = j;
		}
	}
}
