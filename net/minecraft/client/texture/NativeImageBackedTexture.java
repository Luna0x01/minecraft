package net.minecraft.client.texture;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.class_4277;
import net.minecraft.resource.ResourceManager;

public class NativeImageBackedTexture extends AbstractTexture implements AutoCloseable {
	@Nullable
	private class_4277 field_20979;

	public NativeImageBackedTexture(class_4277 arg) {
		this.field_20979 = arg;
		TextureUtil.prepareImage(this.getGlId(), this.field_20979.method_19458(), this.field_20979.method_19478());
		this.upload();
	}

	public NativeImageBackedTexture(int i, int j, boolean bl) {
		this.field_20979 = new class_4277(i, j, bl);
		TextureUtil.prepareImage(this.getGlId(), this.field_20979.method_19458(), this.field_20979.method_19478());
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
	}

	public void upload() {
		this.method_19530();
		this.field_20979.method_19466(0, 0, 0, false);
	}

	@Nullable
	public class_4277 method_19449() {
		return this.field_20979;
	}

	public void method_19448(class_4277 arg) throws Exception {
		this.field_20979.close();
		this.field_20979 = arg;
	}

	public void close() {
		this.field_20979.close();
		this.clearGlId();
		this.field_20979 = null;
	}
}
