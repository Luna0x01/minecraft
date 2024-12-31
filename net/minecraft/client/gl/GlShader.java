package net.minecraft.client.gl;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;

public class GlShader {
	private final GlShader.Type type;
	private final String name;
	private int shaderRef;
	private int refCount = 0;

	private GlShader(GlShader.Type type, int i, String string) {
		this.type = type;
		this.shaderRef = i;
		this.name = string;
	}

	public void attachShader(JsonGlProgram program) {
		this.refCount++;
		GLX.gl20GetAttachShader(program.getProgramRef(), this.shaderRef);
	}

	public void deleteShader(JsonGlProgram program) {
		this.refCount--;
		if (this.refCount <= 0) {
			GLX.gl20DeleteShader(this.shaderRef);
			this.type.getLoadedShaders().remove(this.name);
		}
	}

	public String getName() {
		return this.name;
	}

	public static GlShader createShader(ResourceManager manager, GlShader.Type type, String name) throws IOException {
		GlShader glShader = (GlShader)type.getLoadedShaders().get(name);
		if (glShader == null) {
			Identifier identifier = new Identifier("shaders/program/" + name + type.getFileExtension());
			BufferedInputStream bufferedInputStream = new BufferedInputStream(manager.getResource(identifier).getInputStream());
			byte[] bs = readInputStream(bufferedInputStream);
			ByteBuffer byteBuffer = BufferUtils.createByteBuffer(bs.length);
			byteBuffer.put(bs);
			byteBuffer.position(0);
			int i = GLX.gl20CreateShader(type.getGlType());
			GLX.gl20ShaderSource(i, byteBuffer);
			GLX.gl20CompileShader(i);
			if (GLX.gl20GetShaderi(i, GLX.compileStatus) == 0) {
				String string = StringUtils.trim(GLX.gl20GetShaderInfoLog(i, 32768));
				ShaderParseException shaderParseException = new ShaderParseException("Couldn't compile " + type.getName() + " program: " + string);
				shaderParseException.addFaultyFile(identifier.getPath());
				throw shaderParseException;
			}

			glShader = new GlShader(type, i, name);
			type.getLoadedShaders().put(name, glShader);
		}

		return glShader;
	}

	protected static byte[] readInputStream(BufferedInputStream stream) throws IOException {
		byte[] var1;
		try {
			var1 = IOUtils.toByteArray(stream);
		} finally {
			stream.close();
		}

		return var1;
	}

	public static enum Type {
		VERTEX("vertex", ".vsh", GLX.vertexShader),
		FRAGMENT("fragment", ".fsh", GLX.fragmentShader);

		private final String name;
		private final String fileExtension;
		private final int glType;
		private final Map<String, GlShader> loadedShaders = Maps.newHashMap();

		private Type(String string2, String string3, int j) {
			this.name = string2;
			this.fileExtension = string3;
			this.glType = j;
		}

		public String getName() {
			return this.name;
		}

		protected String getFileExtension() {
			return this.fileExtension;
		}

		protected int getGlType() {
			return this.glType;
		}

		protected Map<String, GlShader> getLoadedShaders() {
			return this.loadedShaders;
		}
	}
}
