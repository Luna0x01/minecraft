package net.minecraft.client.model;

import javax.annotation.Nullable;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.Vec3f;

public final class ModelCuboidData {
	@Nullable
	private final String name;
	private final Vec3f offset;
	private final Vec3f dimensions;
	private final Dilation extraSize;
	private final boolean mirror;
	private final Vector2f textureUV;
	private final Vector2f textureScale;

	protected ModelCuboidData(
		@Nullable String name,
		float textureX,
		float textureY,
		float offsetX,
		float offsetY,
		float offsetZ,
		float sizeX,
		float sizeY,
		float sizeZ,
		Dilation extra,
		boolean mirror,
		float textureScaleX,
		float textureScaleY
	) {
		this.name = name;
		this.textureUV = new Vector2f(textureX, textureY);
		this.offset = new Vec3f(offsetX, offsetY, offsetZ);
		this.dimensions = new Vec3f(sizeX, sizeY, sizeZ);
		this.extraSize = extra;
		this.mirror = mirror;
		this.textureScale = new Vector2f(textureScaleX, textureScaleY);
	}

	public ModelPart.Cuboid createCuboid(int textureWidth, int textureHeight) {
		return new ModelPart.Cuboid(
			(int)this.textureUV.getX(),
			(int)this.textureUV.getY(),
			this.offset.getX(),
			this.offset.getY(),
			this.offset.getZ(),
			this.dimensions.getX(),
			this.dimensions.getY(),
			this.dimensions.getZ(),
			this.extraSize.radiusX,
			this.extraSize.radiusY,
			this.extraSize.radiusZ,
			this.mirror,
			(float)textureWidth * this.textureScale.getX(),
			(float)textureHeight * this.textureScale.getY()
		);
	}
}
