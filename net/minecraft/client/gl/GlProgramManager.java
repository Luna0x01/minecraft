package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GLX;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlProgramManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static GlProgramManager instance;

	public static void newInstance() {
		instance = new GlProgramManager();
	}

	public static GlProgramManager getInstance() {
		return instance;
	}

	private GlProgramManager() {
	}

	public void destroyProgram(JsonGlProgram program) {
		program.getFsh().deleteShader(program);
		program.getVsh().deleteShader(program);
		GLX.gl20DeleteProgram(program.getProgramRef());
	}

	public int createProgram() throws ShaderParseException {
		int i = GLX.gl20CreateProgram();
		if (i <= 0) {
			throw new ShaderParseException("Could not create shader program (returned program ID " + i + ")");
		} else {
			return i;
		}
	}

	public void attachProgram(JsonGlProgram program) throws IOException {
		program.getFsh().attachShader(program);
		program.getVsh().attachShader(program);
		GLX.gl20LinkProgram(program.getProgramRef());
		int i = GLX.gl20GetProgrami(program.getProgramRef(), GLX.linkStatus);
		if (i == 0) {
			LOGGER.warn(
				"Error encountered when linking program containing VS {} and FS {}. Log output:", new Object[]{program.getVsh().getName(), program.getFsh().getName()}
			);
			LOGGER.warn(GLX.gl20GetProgramInfoLog(program.getProgramRef(), 32768));
		}
	}
}
