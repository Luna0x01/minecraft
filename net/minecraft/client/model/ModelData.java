package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;

public class ModelData {
	private ModelPartData data = new ModelPartData(ImmutableList.of(), ModelTransform.NONE);

	public ModelPartData getRoot() {
		return this.data;
	}
}
