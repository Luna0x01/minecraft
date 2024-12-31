package net.minecraft.client.gl;

public interface GlShader {
	int getProgramRef();

	void markUniformsDirty();

	Program getVertexShader();

	Program getFragmentShader();

	void attachReferencedShaders();
}
