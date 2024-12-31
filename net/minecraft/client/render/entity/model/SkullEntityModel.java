package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

public class SkullEntityModel extends Model {
	protected final Cuboid skull;

	public SkullEntityModel() {
		this(0, 35, 64, 64);
	}

	public SkullEntityModel(int i, int j, int k, int l) {
		this.textureWidth = k;
		this.textureHeight = l;
		this.skull = new Cuboid(this, i, j);
		this.skull.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		this.skull.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	public void render(float f, float g, float h, float i, float j, float k) {
		this.skull.yaw = i * (float) (Math.PI / 180.0);
		this.skull.pitch = j * (float) (Math.PI / 180.0);
		this.skull.render(k);
	}
}
