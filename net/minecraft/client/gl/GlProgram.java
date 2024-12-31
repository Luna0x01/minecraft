package net.minecraft.client.gl;

public interface GlProgram {
	int getProgramRef();

	void markUniformsDirty();

	GlShader getVertexShader();

	GlShader getFragmentShader();
}
