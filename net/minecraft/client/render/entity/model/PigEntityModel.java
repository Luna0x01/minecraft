package net.minecraft.client.render.entity.model;

public class PigEntityModel extends QuadruPedEntityModel {
	public PigEntityModel() {
		this(0.0F);
	}

	public PigEntityModel(float f) {
		super(6, f);
		this.head.setTextureOffset(16, 16).addCuboid(-2.0F, 0.0F, -9.0F, 4, 3, 1, f);
		this.field_1514 = 4.0F;
	}
}
