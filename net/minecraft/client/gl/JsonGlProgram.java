package net.minecraft.client.gl;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import net.minecraft.client.texture.Texture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonGlProgram {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DummyGlUniform dummyUniform = new DummyGlUniform();
	private static JsonGlProgram activeProgram = null;
	private static int activeProgramRef = -1;
	private static boolean active = true;
	private final Map<String, Object> samplerBinds = Maps.newHashMap();
	private final List<String> samplerNames = Lists.newArrayList();
	private final List<Integer> samplerShaderLocs = Lists.newArrayList();
	private final List<GlUniform> uniformData = Lists.newArrayList();
	private final List<Integer> uniformLocs = Lists.newArrayList();
	private final Map<String, GlUniform> uniformByName = Maps.newHashMap();
	private final int programRef;
	private final String name;
	private final boolean useCullFace;
	private boolean uniformStateDirty;
	private final GlBlendState blendState;
	private final List<Integer> attribLocs;
	private final List<String> attribNames;
	private final GlShader vertex;
	private final GlShader fragment;

	public JsonGlProgram(ResourceManager resourceManager, String string) throws IOException {
		JsonParser jsonParser = new JsonParser();
		Identifier identifier = new Identifier("shaders/program/" + string + ".json");
		this.name = string;
		Resource resource = null;

		try {
			resource = resourceManager.getResource(identifier);
			JsonObject jsonObject = jsonParser.parse(IOUtils.toString(resource.getInputStream(), Charsets.UTF_8)).getAsJsonObject();
			String string2 = JsonHelper.getString(jsonObject, "vertex");
			String string3 = JsonHelper.getString(jsonObject, "fragment");
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "samplers", null);
			if (jsonArray != null) {
				int i = 0;

				for (JsonElement jsonElement : jsonArray) {
					try {
						this.addSampler(jsonElement);
					} catch (Exception var25) {
						ShaderParseException shaderParseException = ShaderParseException.wrap(var25);
						shaderParseException.addFaultyElement("samplers[" + i + "]");
						throw shaderParseException;
					}

					i++;
				}
			}

			JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "attributes", null);
			if (jsonArray2 != null) {
				int j = 0;
				this.attribLocs = Lists.newArrayListWithCapacity(jsonArray2.size());
				this.attribNames = Lists.newArrayListWithCapacity(jsonArray2.size());

				for (JsonElement jsonElement2 : jsonArray2) {
					try {
						this.attribNames.add(JsonHelper.asString(jsonElement2, "attribute"));
					} catch (Exception var24) {
						ShaderParseException shaderParseException2 = ShaderParseException.wrap(var24);
						shaderParseException2.addFaultyElement("attributes[" + j + "]");
						throw shaderParseException2;
					}

					j++;
				}
			} else {
				this.attribLocs = null;
				this.attribNames = null;
			}

			JsonArray jsonArray3 = JsonHelper.getArray(jsonObject, "uniforms", null);
			if (jsonArray3 != null) {
				int k = 0;

				for (JsonElement jsonElement3 : jsonArray3) {
					try {
						this.addUniform(jsonElement3);
					} catch (Exception var23) {
						ShaderParseException shaderParseException3 = ShaderParseException.wrap(var23);
						shaderParseException3.addFaultyElement("uniforms[" + k + "]");
						throw shaderParseException3;
					}

					k++;
				}
			}

			this.blendState = GlBlendState.deserializeBlendState(JsonHelper.getObject(jsonObject, "blend", null));
			this.useCullFace = JsonHelper.getBoolean(jsonObject, "cull", true);
			this.vertex = GlShader.createShader(resourceManager, GlShader.Type.VERTEX, string2);
			this.fragment = GlShader.createShader(resourceManager, GlShader.Type.FRAGMENT, string3);
			this.programRef = GlProgramManager.getInstance().createProgram();
			GlProgramManager.getInstance().attachProgram(this);
			this.finalizeUniformsAndSamplers();
			if (this.attribNames != null) {
				for (String string4 : this.attribNames) {
					int l = GLX.gl20GetAttribLocation(this.programRef, string4);
					this.attribLocs.add(l);
				}
			}
		} catch (Exception var26) {
			ShaderParseException shaderParseException4 = ShaderParseException.wrap(var26);
			shaderParseException4.addFaultyFile(identifier.getPath());
			throw shaderParseException4;
		} finally {
			IOUtils.closeQuietly(resource);
		}

		this.markUniformsDirty();
	}

	public void close() {
		GlProgramManager.getInstance().destroyProgram(this);
	}

	public void disable() {
		GLX.gl20UseProgram(0);
		activeProgramRef = -1;
		activeProgram = null;
		active = true;

		for (int i = 0; i < this.samplerShaderLocs.size(); i++) {
			if (this.samplerBinds.get(this.samplerNames.get(i)) != null) {
				GlStateManager.activeTexture(GLX.textureUnit + i);
				GlStateManager.bindTexture(0);
			}
		}
	}

	public void enable() {
		this.uniformStateDirty = false;
		activeProgram = this;
		this.blendState.enable();
		if (this.programRef != activeProgramRef) {
			GLX.gl20UseProgram(this.programRef);
			activeProgramRef = this.programRef;
		}

		if (this.useCullFace) {
			GlStateManager.enableCull();
		} else {
			GlStateManager.disableCull();
		}

		for (int i = 0; i < this.samplerShaderLocs.size(); i++) {
			if (this.samplerBinds.get(this.samplerNames.get(i)) != null) {
				GlStateManager.activeTexture(GLX.textureUnit + i);
				GlStateManager.enableTexture();
				Object object = this.samplerBinds.get(this.samplerNames.get(i));
				int j = -1;
				if (object instanceof Framebuffer) {
					j = ((Framebuffer)object).colorAttachment;
				} else if (object instanceof Texture) {
					j = ((Texture)object).getGlId();
				} else if (object instanceof Integer) {
					j = (Integer)object;
				}

				if (j != -1) {
					GlStateManager.bindTexture(j);
					GLX.gl20Uniform1(GLX.gl20GetUniformLocation(this.programRef, (CharSequence)this.samplerNames.get(i)), i);
				}
			}
		}

		for (GlUniform glUniform : this.uniformData) {
			glUniform.upload();
		}
	}

	public void markUniformsDirty() {
		this.uniformStateDirty = true;
	}

	public GlUniform getUniformByName(String name) {
		return this.uniformByName.containsKey(name) ? (GlUniform)this.uniformByName.get(name) : null;
	}

	public GlUniform getUniformByNameOrDummy(String name) {
		return (GlUniform)(this.uniformByName.containsKey(name) ? (GlUniform)this.uniformByName.get(name) : dummyUniform);
	}

	private void finalizeUniformsAndSamplers() {
		int i = 0;

		for (int j = 0; i < this.samplerNames.size(); j++) {
			String string = (String)this.samplerNames.get(i);
			int k = GLX.gl20GetUniformLocation(this.programRef, string);
			if (k == -1) {
				LOGGER.warn("Shader " + this.name + "could not find sampler named " + string + " in the specified shader program.");
				this.samplerBinds.remove(string);
				this.samplerNames.remove(j);
				j--;
			} else {
				this.samplerShaderLocs.add(k);
			}

			i++;
		}

		for (GlUniform glUniform : this.uniformData) {
			String string2 = glUniform.getName();
			int l = GLX.gl20GetUniformLocation(this.programRef, string2);
			if (l == -1) {
				LOGGER.warn("Could not find uniform named " + string2 + " in the specified" + " shader program.");
			} else {
				this.uniformLocs.add(l);
				glUniform.setLoc(l);
				this.uniformByName.put(string2, glUniform);
			}
		}
	}

	private void addSampler(JsonElement jsonElement) throws ShaderParseException {
		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "sampler");
		String string = JsonHelper.getString(jsonObject, "name");
		if (!JsonHelper.hasString(jsonObject, "file")) {
			this.samplerBinds.put(string, null);
			this.samplerNames.add(string);
		} else {
			this.samplerNames.add(string);
		}
	}

	public void bindSampler(String samplerName, Object object) {
		if (this.samplerBinds.containsKey(samplerName)) {
			this.samplerBinds.remove(samplerName);
		}

		this.samplerBinds.put(samplerName, object);
		this.markUniformsDirty();
	}

	private void addUniform(JsonElement jsonElement) throws ShaderParseException {
		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "uniform");
		String string = JsonHelper.getString(jsonObject, "name");
		int i = GlUniform.getTypeIndex(JsonHelper.getString(jsonObject, "type"));
		int j = JsonHelper.getInt(jsonObject, "count");
		float[] fs = new float[Math.max(j, 16)];
		JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
		if (jsonArray.size() != j && jsonArray.size() > 1) {
			throw new ShaderParseException("Invalid amount of values specified (expected " + j + ", found " + jsonArray.size() + ")");
		} else {
			int k = 0;

			for (JsonElement jsonElement2 : jsonArray) {
				try {
					fs[k] = JsonHelper.asFloat(jsonElement2, "value");
				} catch (Exception var13) {
					ShaderParseException shaderParseException = ShaderParseException.wrap(var13);
					shaderParseException.addFaultyElement("values[" + k + "]");
					throw shaderParseException;
				}

				k++;
			}

			if (j > 1 && jsonArray.size() == 1) {
				while (k < j) {
					fs[k] = fs[0];
					k++;
				}
			}

			int l = j > 1 && j <= 4 && i < 8 ? j - 1 : 0;
			GlUniform glUniform = new GlUniform(string, i + l, j, this);
			if (i <= 3) {
				glUniform.set((int)fs[0], (int)fs[1], (int)fs[2], (int)fs[3]);
			} else if (i <= 7) {
				glUniform.setForDataType(fs[0], fs[1], fs[2], fs[3]);
			} else {
				glUniform.set(fs);
			}

			this.uniformData.add(glUniform);
		}
	}

	public GlShader getVsh() {
		return this.vertex;
	}

	public GlShader getFsh() {
		return this.fragment;
	}

	public int getProgramRef() {
		return this.programRef;
	}
}
