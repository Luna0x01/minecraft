package com.mojang.blaze3d.platform;

import com.google.common.collect.Maps;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Util;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import oshi.SystemInfo;
import oshi.hardware.Processor;

public class GLX {
	public static boolean nvidia;
	public static boolean amd;
	public static int framebuffer;
	public static int renderbuffer;
	public static int colorAttachment;
	public static int depthAttachment;
	public static int completeFramebuffer;
	public static int incompleteFramebufferAttachment;
	public static int incompleteFramebufferAttachmentMiss;
	public static int incompleteFramebufferAttachmentDraw;
	public static int incompleteFramebufferAttachmentRead;
	private static GLX.class_2908 field_13709;
	public static boolean advanced;
	private static boolean shaders;
	private static boolean arbShaderObjects;
	public static int linkStatus;
	public static int compileStatus;
	public static int vertexShader;
	public static int fragmentShader;
	private static boolean arbMultitexture;
	public static int textureUnit;
	public static int lightmapTextureUnit;
	public static int texture;
	private static boolean arbTextureEnvCombine;
	public static int combine;
	public static int interpolate;
	public static int primary;
	public static int constant;
	public static int previous;
	public static int combineRgb;
	public static int source0Rgb;
	public static int source1Rgb;
	public static int source2Rgb;
	public static int operand0Rgb;
	public static int operand1Rgb;
	public static int operand2Rgb;
	public static int combineAlpha;
	public static int source0Alpha;
	public static int source1Alpha;
	public static int source2Alpha;
	public static int operand0Alpha;
	public static int operand1Alpha;
	public static int operand2Alpha;
	private static boolean gl14Supported;
	public static boolean blendFuncSeparateSupported;
	public static boolean gl21Supported;
	public static boolean shadersSupported;
	private static String contextDescription = "";
	private static String processor;
	public static boolean vboSupported;
	public static boolean vboSupportedAmd;
	private static boolean vboShadersSupported;
	public static int arrayBuffer;
	public static int staticDraw;
	private static final Map<Integer, String> field_21150 = Util.make(Maps.newHashMap(), hashMap -> {
		hashMap.put(0, "No error");
		hashMap.put(1280, "Enum parameter is invalid for this function");
		hashMap.put(1281, "Parameter is invalid for this function");
		hashMap.put(1282, "Current state is invalid for this function");
		hashMap.put(1283, "Stack overflow");
		hashMap.put(1284, "Stack underflow");
		hashMap.put(1285, "Out of memory");
		hashMap.put(1286, "Operation on incomplete framebuffer");
		hashMap.put(1286, "Operation on incomplete framebuffer");
	});

	public static void createContext() {
		GLCapabilities gLCapabilities = GL.getCapabilities();
		arbMultitexture = gLCapabilities.GL_ARB_multitexture && !gLCapabilities.OpenGL13;
		arbTextureEnvCombine = gLCapabilities.GL_ARB_texture_env_combine && !gLCapabilities.OpenGL13;
		if (arbMultitexture) {
			contextDescription = contextDescription + "Using ARB_multitexture.\n";
			textureUnit = 33984;
			lightmapTextureUnit = 33985;
			texture = 33986;
		} else {
			contextDescription = contextDescription + "Using GL 1.3 multitexturing.\n";
			textureUnit = 33984;
			lightmapTextureUnit = 33985;
			texture = 33986;
		}

		if (arbTextureEnvCombine) {
			contextDescription = contextDescription + "Using ARB_texture_env_combine.\n";
			combine = 34160;
			interpolate = 34165;
			primary = 34167;
			constant = 34166;
			previous = 34168;
			combineRgb = 34161;
			source0Rgb = 34176;
			source1Rgb = 34177;
			source2Rgb = 34178;
			operand0Rgb = 34192;
			operand1Rgb = 34193;
			operand2Rgb = 34194;
			combineAlpha = 34162;
			source0Alpha = 34184;
			source1Alpha = 34185;
			source2Alpha = 34186;
			operand0Alpha = 34200;
			operand1Alpha = 34201;
			operand2Alpha = 34202;
		} else {
			contextDescription = contextDescription + "Using GL 1.3 texture combiners.\n";
			combine = 34160;
			interpolate = 34165;
			primary = 34167;
			constant = 34166;
			previous = 34168;
			combineRgb = 34161;
			source0Rgb = 34176;
			source1Rgb = 34177;
			source2Rgb = 34178;
			operand0Rgb = 34192;
			operand1Rgb = 34193;
			operand2Rgb = 34194;
			combineAlpha = 34162;
			source0Alpha = 34184;
			source1Alpha = 34185;
			source2Alpha = 34186;
			operand0Alpha = 34200;
			operand1Alpha = 34201;
			operand2Alpha = 34202;
		}

		blendFuncSeparateSupported = gLCapabilities.GL_EXT_blend_func_separate && !gLCapabilities.OpenGL14;
		gl14Supported = gLCapabilities.OpenGL14 || gLCapabilities.GL_EXT_blend_func_separate;
		advanced = gl14Supported && (gLCapabilities.GL_ARB_framebuffer_object || gLCapabilities.GL_EXT_framebuffer_object || gLCapabilities.OpenGL30);
		if (advanced) {
			contextDescription = contextDescription + "Using framebuffer objects because ";
			if (gLCapabilities.OpenGL30) {
				contextDescription = contextDescription + "OpenGL 3.0 is supported and separate blending is supported.\n";
				field_13709 = GLX.class_2908.BASE;
				framebuffer = 36160;
				renderbuffer = 36161;
				colorAttachment = 36064;
				depthAttachment = 36096;
				completeFramebuffer = 36053;
				incompleteFramebufferAttachment = 36054;
				incompleteFramebufferAttachmentMiss = 36055;
				incompleteFramebufferAttachmentDraw = 36059;
				incompleteFramebufferAttachmentRead = 36060;
			} else if (gLCapabilities.GL_ARB_framebuffer_object) {
				contextDescription = contextDescription + "ARB_framebuffer_object is supported and separate blending is supported.\n";
				field_13709 = GLX.class_2908.ARB;
				framebuffer = 36160;
				renderbuffer = 36161;
				colorAttachment = 36064;
				depthAttachment = 36096;
				completeFramebuffer = 36053;
				incompleteFramebufferAttachmentMiss = 36055;
				incompleteFramebufferAttachment = 36054;
				incompleteFramebufferAttachmentDraw = 36059;
				incompleteFramebufferAttachmentRead = 36060;
			} else if (gLCapabilities.GL_EXT_framebuffer_object) {
				contextDescription = contextDescription + "EXT_framebuffer_object is supported.\n";
				field_13709 = GLX.class_2908.EXT;
				framebuffer = 36160;
				renderbuffer = 36161;
				colorAttachment = 36064;
				depthAttachment = 36096;
				completeFramebuffer = 36053;
				incompleteFramebufferAttachmentMiss = 36055;
				incompleteFramebufferAttachment = 36054;
				incompleteFramebufferAttachmentDraw = 36059;
				incompleteFramebufferAttachmentRead = 36060;
			}
		} else {
			contextDescription = contextDescription + "Not using framebuffer objects because ";
			contextDescription = contextDescription + "OpenGL 1.4 is " + (gLCapabilities.OpenGL14 ? "" : "not ") + "supported, ";
			contextDescription = contextDescription + "EXT_blend_func_separate is " + (gLCapabilities.GL_EXT_blend_func_separate ? "" : "not ") + "supported, ";
			contextDescription = contextDescription + "OpenGL 3.0 is " + (gLCapabilities.OpenGL30 ? "" : "not ") + "supported, ";
			contextDescription = contextDescription + "ARB_framebuffer_object is " + (gLCapabilities.GL_ARB_framebuffer_object ? "" : "not ") + "supported, and ";
			contextDescription = contextDescription + "EXT_framebuffer_object is " + (gLCapabilities.GL_EXT_framebuffer_object ? "" : "not ") + "supported.\n";
		}

		gl21Supported = gLCapabilities.OpenGL21;
		shaders = gl21Supported || gLCapabilities.GL_ARB_vertex_shader && gLCapabilities.GL_ARB_fragment_shader && gLCapabilities.GL_ARB_shader_objects;
		contextDescription = contextDescription + "Shaders are " + (shaders ? "" : "not ") + "available because ";
		if (shaders) {
			if (gLCapabilities.OpenGL21) {
				contextDescription = contextDescription + "OpenGL 2.1 is supported.\n";
				arbShaderObjects = false;
				linkStatus = 35714;
				compileStatus = 35713;
				vertexShader = 35633;
				fragmentShader = 35632;
			} else {
				contextDescription = contextDescription + "ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are supported.\n";
				arbShaderObjects = true;
				linkStatus = 35714;
				compileStatus = 35713;
				vertexShader = 35633;
				fragmentShader = 35632;
			}
		} else {
			contextDescription = contextDescription + "OpenGL 2.1 is " + (gLCapabilities.OpenGL21 ? "" : "not ") + "supported, ";
			contextDescription = contextDescription + "ARB_shader_objects is " + (gLCapabilities.GL_ARB_shader_objects ? "" : "not ") + "supported, ";
			contextDescription = contextDescription + "ARB_vertex_shader is " + (gLCapabilities.GL_ARB_vertex_shader ? "" : "not ") + "supported, and ";
			contextDescription = contextDescription + "ARB_fragment_shader is " + (gLCapabilities.GL_ARB_fragment_shader ? "" : "not ") + "supported.\n";
		}

		shadersSupported = advanced && shaders;
		String string = GL11.glGetString(7936).toLowerCase(Locale.ROOT);
		nvidia = string.contains("nvidia");
		vboShadersSupported = !gLCapabilities.OpenGL15 && gLCapabilities.GL_ARB_vertex_buffer_object;
		vboSupported = gLCapabilities.OpenGL15 || vboShadersSupported;
		contextDescription = contextDescription + "VBOs are " + (vboSupported ? "" : "not ") + "available because ";
		if (vboSupported) {
			if (vboShadersSupported) {
				contextDescription = contextDescription + "ARB_vertex_buffer_object is supported.\n";
				staticDraw = 35044;
				arrayBuffer = 34962;
			} else {
				contextDescription = contextDescription + "OpenGL 1.5 is supported.\n";
				staticDraw = 35044;
				arrayBuffer = 34962;
			}
		}

		amd = string.contains("ati");
		if (amd) {
			if (vboSupported) {
				vboSupportedAmd = true;
			} else {
				GameOptions.Option.RENDER_DISTANCE.setMaxValue(16.0F);
			}
		}

		try {
			Processor[] processors = new SystemInfo().getHardware().getProcessors();
			processor = String.format("%dx %s", processors.length, processors[0]).replaceAll("\\s+", " ");
		} catch (Throwable var3) {
		}
	}

	public static boolean areShadersSupported() {
		return shadersSupported;
	}

	public static String getContextDescription() {
		return contextDescription;
	}

	public static int gl20GetProgrami(int program, int param) {
		return arbShaderObjects ? ARBShaderObjects.glGetObjectParameteriARB(program, param) : GL20.glGetProgrami(program, param);
	}

	public static void gl20GetAttachShader(int program, int shader) {
		if (arbShaderObjects) {
			ARBShaderObjects.glAttachObjectARB(program, shader);
		} else {
			GL20.glAttachShader(program, shader);
		}
	}

	public static void gl20DeleteShader(int shader) {
		if (arbShaderObjects) {
			ARBShaderObjects.glDeleteObjectARB(shader);
		} else {
			GL20.glDeleteShader(shader);
		}
	}

	public static int gl20CreateShader(int shader) {
		return arbShaderObjects ? ARBShaderObjects.glCreateShaderObjectARB(shader) : GL20.glCreateShader(shader);
	}

	public static void method_19687(int i, CharSequence charSequence) {
		if (arbShaderObjects) {
			ARBShaderObjects.glShaderSourceARB(i, charSequence);
		} else {
			GL20.glShaderSource(i, charSequence);
		}
	}

	public static void gl20CompileShader(int shader) {
		if (arbShaderObjects) {
			ARBShaderObjects.glCompileShaderARB(shader);
		} else {
			GL20.glCompileShader(shader);
		}
	}

	public static int gl20GetShaderi(int shader, int param) {
		return arbShaderObjects ? ARBShaderObjects.glGetObjectParameteriARB(shader, param) : GL20.glGetShaderi(shader, param);
	}

	public static String gl20GetShaderInfoLog(int shader, int maxLength) {
		return arbShaderObjects ? ARBShaderObjects.glGetInfoLogARB(shader, maxLength) : GL20.glGetShaderInfoLog(shader, maxLength);
	}

	public static String gl20GetProgramInfoLog(int program, int maxLength) {
		return arbShaderObjects ? ARBShaderObjects.glGetInfoLogARB(program, maxLength) : GL20.glGetProgramInfoLog(program, maxLength);
	}

	public static void gl20UseProgram(int program) {
		if (arbShaderObjects) {
			ARBShaderObjects.glUseProgramObjectARB(program);
		} else {
			GL20.glUseProgram(program);
		}
	}

	public static int gl20CreateProgram() {
		return arbShaderObjects ? ARBShaderObjects.glCreateProgramObjectARB() : GL20.glCreateProgram();
	}

	public static void gl20DeleteProgram(int program) {
		if (arbShaderObjects) {
			ARBShaderObjects.glDeleteObjectARB(program);
		} else {
			GL20.glDeleteProgram(program);
		}
	}

	public static void gl20LinkProgram(int program) {
		if (arbShaderObjects) {
			ARBShaderObjects.glLinkProgramARB(program);
		} else {
			GL20.glLinkProgram(program);
		}
	}

	public static int gl20GetUniformLocation(int program, CharSequence name) {
		return arbShaderObjects ? ARBShaderObjects.glGetUniformLocationARB(program, name) : GL20.glGetUniformLocation(program, name);
	}

	public static void gl20Uniform1(int loc, IntBuffer v) {
		if (arbShaderObjects) {
			ARBShaderObjects.glUniform1ivARB(loc, v);
		} else {
			GL20.glUniform1iv(loc, v);
		}
	}

	public static void gl20Uniform1(int loc, int v) {
		if (arbShaderObjects) {
			ARBShaderObjects.glUniform1iARB(loc, v);
		} else {
			GL20.glUniform1i(loc, v);
		}
	}

	public static void gl20Uniform(int loc, FloatBuffer v) {
		if (arbShaderObjects) {
			ARBShaderObjects.glUniform1fvARB(loc, v);
		} else {
			GL20.glUniform1fv(loc, v);
		}
	}

	public static void gl20Uniform2(int loc, IntBuffer v) {
		if (arbShaderObjects) {
			ARBShaderObjects.glUniform2ivARB(loc, v);
		} else {
			GL20.glUniform2iv(loc, v);
		}
	}

	public static void gl20Uniform2(int loc, FloatBuffer v) {
		if (arbShaderObjects) {
			ARBShaderObjects.glUniform2fvARB(loc, v);
		} else {
			GL20.glUniform2fv(loc, v);
		}
	}

	public static void gl20Uniform3(int loc, IntBuffer v) {
		if (arbShaderObjects) {
			ARBShaderObjects.glUniform3ivARB(loc, v);
		} else {
			GL20.glUniform3iv(loc, v);
		}
	}

	public static void gl20Uniform3(int loc, FloatBuffer v) {
		if (arbShaderObjects) {
			ARBShaderObjects.glUniform3fvARB(loc, v);
		} else {
			GL20.glUniform3fv(loc, v);
		}
	}

	public static void gl20Uniform4(int loc, IntBuffer v) {
		if (arbShaderObjects) {
			ARBShaderObjects.glUniform4ivARB(loc, v);
		} else {
			GL20.glUniform4iv(loc, v);
		}
	}

	public static void gl20Uniform4(int loc, FloatBuffer v) {
		if (arbShaderObjects) {
			ARBShaderObjects.glUniform4fvARB(loc, v);
		} else {
			GL20.glUniform4fv(loc, v);
		}
	}

	public static void gl20UniformMatrix2(int uniform, boolean bl, FloatBuffer buf) {
		if (arbShaderObjects) {
			ARBShaderObjects.glUniformMatrix2fvARB(uniform, bl, buf);
		} else {
			GL20.glUniformMatrix2fv(uniform, bl, buf);
		}
	}

	public static void gl20UniformMatrix3(int uniform, boolean bl, FloatBuffer buf) {
		if (arbShaderObjects) {
			ARBShaderObjects.glUniformMatrix3fvARB(uniform, bl, buf);
		} else {
			GL20.glUniformMatrix3fv(uniform, bl, buf);
		}
	}

	public static void gl20UniformMatrix4(int uniform, boolean bl, FloatBuffer buf) {
		if (arbShaderObjects) {
			ARBShaderObjects.glUniformMatrix4fvARB(uniform, bl, buf);
		} else {
			GL20.glUniformMatrix4fv(uniform, bl, buf);
		}
	}

	public static int gl20GetAttribLocation(int loc, CharSequence sequence) {
		return arbShaderObjects ? ARBVertexShader.glGetAttribLocationARB(loc, sequence) : GL20.glGetAttribLocation(loc, sequence);
	}

	public static int gl15GenBuffers() {
		return vboShadersSupported ? ARBVertexBufferObject.glGenBuffersARB() : GL15.glGenBuffers();
	}

	public static void gl15BindBuffer(int i, int j) {
		if (vboShadersSupported) {
			ARBVertexBufferObject.glBindBufferARB(i, j);
		} else {
			GL15.glBindBuffer(i, j);
		}
	}

	public static void gl15BufferData(int i, ByteBuffer buf, int j) {
		if (vboShadersSupported) {
			ARBVertexBufferObject.glBufferDataARB(i, buf, j);
		} else {
			GL15.glBufferData(i, buf, j);
		}
	}

	public static void gl15DeleteBuffers(int i) {
		if (vboShadersSupported) {
			ARBVertexBufferObject.glDeleteBuffersARB(i);
		} else {
			GL15.glDeleteBuffers(i);
		}
	}

	public static boolean supportsVbo() {
		return vboSupported && MinecraftClient.getInstance().options.vbo;
	}

	public static void advancedBindFramebuffer(int i, int j) {
		if (advanced) {
			switch (field_13709) {
				case BASE:
					GL30.glBindFramebuffer(i, j);
					break;
				case ARB:
					ARBFramebufferObject.glBindFramebuffer(i, j);
					break;
				case EXT:
					EXTFramebufferObject.glBindFramebufferEXT(i, j);
			}
		}
	}

	public static void advancedBindRenderBuffer(int i, int j) {
		if (advanced) {
			switch (field_13709) {
				case BASE:
					GL30.glBindRenderbuffer(i, j);
					break;
				case ARB:
					ARBFramebufferObject.glBindRenderbuffer(i, j);
					break;
				case EXT:
					EXTFramebufferObject.glBindRenderbufferEXT(i, j);
			}
		}
	}

	public static void advancedDeleteRenderBuffers(int renderbuffer) {
		if (advanced) {
			switch (field_13709) {
				case BASE:
					GL30.glDeleteRenderbuffers(renderbuffer);
					break;
				case ARB:
					ARBFramebufferObject.glDeleteRenderbuffers(renderbuffer);
					break;
				case EXT:
					EXTFramebufferObject.glDeleteRenderbuffersEXT(renderbuffer);
			}
		}
	}

	public static void advancedDeleteFrameBuffers(int framebuffer) {
		if (advanced) {
			switch (field_13709) {
				case BASE:
					GL30.glDeleteFramebuffers(framebuffer);
					break;
				case ARB:
					ARBFramebufferObject.glDeleteFramebuffers(framebuffer);
					break;
				case EXT:
					EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffer);
			}
		}
	}

	public static int advancedGenFrameBuffers() {
		if (!advanced) {
			return -1;
		} else {
			switch (field_13709) {
				case BASE:
					return GL30.glGenFramebuffers();
				case ARB:
					return ARBFramebufferObject.glGenFramebuffers();
				case EXT:
					return EXTFramebufferObject.glGenFramebuffersEXT();
				default:
					return -1;
			}
		}
	}

	public static int advancedGenRenderBuffers() {
		if (!advanced) {
			return -1;
		} else {
			switch (field_13709) {
				case BASE:
					return GL30.glGenRenderbuffers();
				case ARB:
					return ARBFramebufferObject.glGenRenderbuffers();
				case EXT:
					return EXTFramebufferObject.glGenRenderbuffersEXT();
				default:
					return -1;
			}
		}
	}

	public static void advancedRenderBufferStorage(int target, int internalFormat, int width, int height) {
		if (advanced) {
			switch (field_13709) {
				case BASE:
					GL30.glRenderbufferStorage(target, internalFormat, width, height);
					break;
				case ARB:
					ARBFramebufferObject.glRenderbufferStorage(target, internalFormat, width, height);
					break;
				case EXT:
					EXTFramebufferObject.glRenderbufferStorageEXT(target, internalFormat, width, height);
			}
		}
	}

	public static void advancedFramebufferRenderbuffer(int target, int attachment, int renderBufferTarget, int renderBuffer) {
		if (advanced) {
			switch (field_13709) {
				case BASE:
					GL30.glFramebufferRenderbuffer(target, attachment, renderBufferTarget, renderBuffer);
					break;
				case ARB:
					ARBFramebufferObject.glFramebufferRenderbuffer(target, attachment, renderBufferTarget, renderBuffer);
					break;
				case EXT:
					EXTFramebufferObject.glFramebufferRenderbufferEXT(target, attachment, renderBufferTarget, renderBuffer);
			}
		}
	}

	public static int advancedCheckFrameBufferStatus(int framebuffer) {
		if (!advanced) {
			return -1;
		} else {
			switch (field_13709) {
				case BASE:
					return GL30.glCheckFramebufferStatus(framebuffer);
				case ARB:
					return ARBFramebufferObject.glCheckFramebufferStatus(framebuffer);
				case EXT:
					return EXTFramebufferObject.glCheckFramebufferStatusEXT(framebuffer);
				default:
					return -1;
			}
		}
	}

	public static void advancedFrameBufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
		if (advanced) {
			switch (field_13709) {
				case BASE:
					GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level);
					break;
				case ARB:
					ARBFramebufferObject.glFramebufferTexture2D(target, attachment, textarget, texture, level);
					break;
				case EXT:
					EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, textarget, texture, level);
			}
		}
	}

	public static void gl13ActiveTexture(int texture) {
		if (arbMultitexture) {
			ARBMultitexture.glActiveTextureARB(texture);
		} else {
			GL13.glActiveTexture(texture);
		}
	}

	public static void gl13ClientActiveTexture(int texture) {
		if (arbMultitexture) {
			ARBMultitexture.glClientActiveTextureARB(texture);
		} else {
			GL13.glClientActiveTexture(texture);
		}
	}

	public static void gl13MultiTexCoord2f(int i, float f1, float f2) {
		if (arbMultitexture) {
			ARBMultitexture.glMultiTexCoord2fARB(i, f1, f2);
		} else {
			GL13.glMultiTexCoord2f(i, f1, f2);
		}
	}

	public static void glBlendFuncSeparate(int r, int g, int b, int a) {
		if (gl14Supported) {
			if (blendFuncSeparateSupported) {
				EXTBlendFuncSeparate.glBlendFuncSeparateEXT(r, g, b, a);
			} else {
				GL14.glBlendFuncSeparate(r, g, b, a);
			}
		} else {
			GL11.glBlendFunc(r, g);
		}
	}

	public static boolean supportsFbo() {
		return advanced && MinecraftClient.getInstance().options.fbo;
	}

	public static String getProcessor() {
		return processor == null ? "<unknown>" : processor;
	}

	public static void method_12554(int i) {
		method_19688(i, true, true, true);
	}

	public static void method_19688(int i, boolean bl, boolean bl2, boolean bl3) {
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GL11.glLineWidth(4.0F);
		bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
		if (bl) {
			bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)i, 0.0, 0.0).color(0, 0, 0, 255).next();
		}

		if (bl2) {
			bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(0.0, (double)i, 0.0).color(0, 0, 0, 255).next();
		}

		if (bl3) {
			bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(0.0, 0.0, (double)i).color(0, 0, 0, 255).next();
		}

		tessellator.draw();
		GL11.glLineWidth(2.0F);
		bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
		if (bl) {
			bufferBuilder.vertex(0.0, 0.0, 0.0).color(255, 0, 0, 255).next();
			bufferBuilder.vertex((double)i, 0.0, 0.0).color(255, 0, 0, 255).next();
		}

		if (bl2) {
			bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 255, 0, 255).next();
			bufferBuilder.vertex(0.0, (double)i, 0.0).color(0, 255, 0, 255).next();
		}

		if (bl3) {
			bufferBuilder.vertex(0.0, 0.0, 0.0).color(127, 127, 255, 255).next();
			bufferBuilder.vertex(0.0, 0.0, (double)i).color(127, 127, 255, 255).next();
		}

		tessellator.draw();
		GL11.glLineWidth(1.0F);
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
	}

	public static String method_19690(int i) {
		return (String)field_21150.get(i);
	}

	static enum class_2908 {
		BASE,
		ARB,
		EXT;
	}
}
