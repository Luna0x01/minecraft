package net.minecraft.client.gl;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

public class ShaderEffect {
	private Framebuffer frameBuffer;
	private ResourceManager resourceManager;
	private String name;
	private final List<PostProcessShader> passes = Lists.newArrayList();
	private final Map<String, Framebuffer> targetsByName = Maps.newHashMap();
	private final List<Framebuffer> defaultSizedTargets = Lists.newArrayList();
	private Matrix4f projectionMatrix;
	private int width;
	private int height;
	private float time;
	private float lastTickDelta;

	public ShaderEffect(TextureManager textureManager, ResourceManager resourceManager, Framebuffer framebuffer, Identifier identifier) throws IOException, JsonSyntaxException {
		this.resourceManager = resourceManager;
		this.frameBuffer = framebuffer;
		this.time = 0.0F;
		this.lastTickDelta = 0.0F;
		this.width = framebuffer.viewportWidth;
		this.height = framebuffer.viewportHeight;
		this.name = identifier.toString();
		this.setupProjectionMatrix();
		this.parseEffect(textureManager, identifier);
	}

	public void parseEffect(TextureManager textureManager, Identifier identifier) throws IOException, JsonSyntaxException {
		JsonParser jsonParser = new JsonParser();
		InputStream inputStream = null;

		try {
			Resource resource = this.resourceManager.getResource(identifier);
			inputStream = resource.getInputStream();
			JsonObject jsonObject = jsonParser.parse(IOUtils.toString(inputStream, Charsets.UTF_8)).getAsJsonObject();
			if (JsonHelper.hasArray(jsonObject, "targets")) {
				JsonArray jsonArray = jsonObject.getAsJsonArray("targets");
				int i = 0;

				for (JsonElement jsonElement : jsonArray) {
					try {
						this.parseTarget(jsonElement);
					} catch (Exception var19) {
						ShaderParseException shaderParseException = ShaderParseException.wrap(var19);
						shaderParseException.addFaultyElement("targets[" + i + "]");
						throw shaderParseException;
					}

					i++;
				}
			}

			if (JsonHelper.hasArray(jsonObject, "passes")) {
				JsonArray jsonArray2 = jsonObject.getAsJsonArray("passes");
				int j = 0;

				for (JsonElement jsonElement2 : jsonArray2) {
					try {
						this.parsePass(textureManager, jsonElement2);
					} catch (Exception var18) {
						ShaderParseException shaderParseException2 = ShaderParseException.wrap(var18);
						shaderParseException2.addFaultyElement("passes[" + j + "]");
						throw shaderParseException2;
					}

					j++;
				}
			}
		} catch (Exception var20) {
			ShaderParseException shaderParseException3 = ShaderParseException.wrap(var20);
			shaderParseException3.addFaultyFile(identifier.getPath());
			throw shaderParseException3;
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	private void parseTarget(JsonElement jsonTarget) throws ShaderParseException {
		if (JsonHelper.isString(jsonTarget)) {
			this.addTarget(jsonTarget.getAsString(), this.width, this.height);
		} else {
			JsonObject jsonObject = JsonHelper.asObject(jsonTarget, "target");
			String string = JsonHelper.getString(jsonObject, "name");
			int i = JsonHelper.getInt(jsonObject, "width", this.width);
			int j = JsonHelper.getInt(jsonObject, "height", this.height);
			if (this.targetsByName.containsKey(string)) {
				throw new ShaderParseException(string + " is already defined");
			}

			this.addTarget(string, i, j);
		}
	}

	private void parsePass(TextureManager textureManager, JsonElement jsonPass) throws IOException {
		JsonObject jsonObject = JsonHelper.asObject(jsonPass, "pass");
		String string = JsonHelper.getString(jsonObject, "name");
		String string2 = JsonHelper.getString(jsonObject, "intarget");
		String string3 = JsonHelper.getString(jsonObject, "outtarget");
		Framebuffer framebuffer = this.getTarget(string2);
		Framebuffer framebuffer2 = this.getTarget(string3);
		if (framebuffer == null) {
			throw new ShaderParseException("Input target '" + string2 + "' does not exist");
		} else if (framebuffer2 == null) {
			throw new ShaderParseException("Output target '" + string3 + "' does not exist");
		} else {
			PostProcessShader postProcessShader = this.addPass(string, framebuffer, framebuffer2);
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "auxtargets", null);
			if (jsonArray != null) {
				int i = 0;

				for (JsonElement jsonElement : jsonArray) {
					try {
						JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "auxtarget");
						String string4 = JsonHelper.getString(jsonObject2, "name");
						String string5 = JsonHelper.getString(jsonObject2, "id");
						Framebuffer framebuffer3 = this.getTarget(string5);
						if (framebuffer3 == null) {
							Identifier identifier = new Identifier("textures/effect/" + string5 + ".png");

							try {
								this.resourceManager.getResource(identifier);
							} catch (FileNotFoundException var24) {
								throw new ShaderParseException("Render target or texture '" + string5 + "' does not exist");
							}

							textureManager.bindTexture(identifier);
							Texture texture = textureManager.getTexture(identifier);
							int j = JsonHelper.getInt(jsonObject2, "width");
							int k = JsonHelper.getInt(jsonObject2, "height");
							boolean bl = JsonHelper.getBoolean(jsonObject2, "bilinear");
							if (bl) {
								GL11.glTexParameteri(3553, 10241, 9729);
								GL11.glTexParameteri(3553, 10240, 9729);
							} else {
								GL11.glTexParameteri(3553, 10241, 9728);
								GL11.glTexParameteri(3553, 10240, 9728);
							}

							postProcessShader.addAuxTarget(string4, texture.getGlId(), j, k);
						} else {
							postProcessShader.addAuxTarget(string4, framebuffer3, framebuffer3.textureWidth, framebuffer3.textureHeight);
						}
					} catch (Exception var25) {
						ShaderParseException shaderParseException = ShaderParseException.wrap(var25);
						shaderParseException.addFaultyElement("auxtargets[" + i + "]");
						throw shaderParseException;
					}

					i++;
				}
			}

			JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "uniforms", null);
			if (jsonArray2 != null) {
				int l = 0;

				for (JsonElement jsonElement2 : jsonArray2) {
					try {
						this.parseUniform(jsonElement2);
					} catch (Exception var23) {
						ShaderParseException shaderParseException2 = ShaderParseException.wrap(var23);
						shaderParseException2.addFaultyElement("uniforms[" + l + "]");
						throw shaderParseException2;
					}

					l++;
				}
			}
		}
	}

	private void parseUniform(JsonElement jsonUniform) throws ShaderParseException {
		JsonObject jsonObject = JsonHelper.asObject(jsonUniform, "uniform");
		String string = JsonHelper.getString(jsonObject, "name");
		GlUniform glUniform = ((PostProcessShader)this.passes.get(this.passes.size() - 1)).getProgram().getUniformByName(string);
		if (glUniform == null) {
			throw new ShaderParseException("Uniform '" + string + "' does not exist");
		} else {
			float[] fs = new float[4];
			int i = 0;

			for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "values")) {
				try {
					fs[i] = JsonHelper.asFloat(jsonElement, "value");
				} catch (Exception var12) {
					ShaderParseException shaderParseException = ShaderParseException.wrap(var12);
					shaderParseException.addFaultyElement("values[" + i + "]");
					throw shaderParseException;
				}

				i++;
			}

			switch (i) {
				case 0:
				default:
					break;
				case 1:
					glUniform.set(fs[0]);
					break;
				case 2:
					glUniform.set(fs[0], fs[1]);
					break;
				case 3:
					glUniform.set(fs[0], fs[1], fs[2]);
					break;
				case 4:
					glUniform.set(fs[0], fs[1], fs[2], fs[3]);
			}
		}
	}

	public Framebuffer getSecondaryTarget(String name) {
		return (Framebuffer)this.targetsByName.get(name);
	}

	public void addTarget(String name, int width, int height) {
		Framebuffer framebuffer = new Framebuffer(width, height, true);
		framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		this.targetsByName.put(name, framebuffer);
		if (width == this.width && height == this.height) {
			this.defaultSizedTargets.add(framebuffer);
		}
	}

	public void disable() {
		for (Framebuffer framebuffer : this.targetsByName.values()) {
			framebuffer.delete();
		}

		for (PostProcessShader postProcessShader : this.passes) {
			postProcessShader.close();
		}

		this.passes.clear();
	}

	public PostProcessShader addPass(String programName, Framebuffer source, Framebuffer dest) throws IOException {
		PostProcessShader postProcessShader = new PostProcessShader(this.resourceManager, programName, source, dest);
		this.passes.add(this.passes.size(), postProcessShader);
		return postProcessShader;
	}

	private void setupProjectionMatrix() {
		this.projectionMatrix = new Matrix4f();
		this.projectionMatrix.setIdentity();
		this.projectionMatrix.m00 = 2.0F / (float)this.frameBuffer.textureWidth;
		this.projectionMatrix.m11 = 2.0F / (float)(-this.frameBuffer.textureHeight);
		this.projectionMatrix.m22 = -0.0020001999F;
		this.projectionMatrix.m33 = 1.0F;
		this.projectionMatrix.m03 = -1.0F;
		this.projectionMatrix.m13 = 1.0F;
		this.projectionMatrix.m23 = -1.0001999F;
	}

	public void setupDimensions(int targetsWidth, int targetsHeight) {
		this.width = this.frameBuffer.textureWidth;
		this.height = this.frameBuffer.textureHeight;
		this.setupProjectionMatrix();

		for (PostProcessShader postProcessShader : this.passes) {
			postProcessShader.setProjectionMatrix(this.projectionMatrix);
		}

		for (Framebuffer framebuffer : this.defaultSizedTargets) {
			framebuffer.resize(targetsWidth, targetsHeight);
		}
	}

	public void render(float tickDelta) {
		if (tickDelta < this.lastTickDelta) {
			this.time = this.time + (1.0F - this.lastTickDelta);
			this.time += tickDelta;
		} else {
			this.time = this.time + (tickDelta - this.lastTickDelta);
		}

		this.lastTickDelta = tickDelta;

		while (this.time > 20.0F) {
			this.time -= 20.0F;
		}

		for (PostProcessShader postProcessShader : this.passes) {
			postProcessShader.render(this.time / 20.0F);
		}
	}

	public final String getName() {
		return this.name;
	}

	private Framebuffer getTarget(String name) {
		if (name == null) {
			return null;
		} else {
			return name.equals("minecraft:main") ? this.frameBuffer : (Framebuffer)this.targetsByName.get(name);
		}
	}
}
