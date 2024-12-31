package net.minecraft.client.render.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.util.math.Rotation3;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

public enum ModelRotation implements ModelBakeSettings {
	field_5350(0, 0),
	field_5366(0, 90),
	field_5355(0, 180),
	field_5347(0, 270),
	field_5351(90, 0),
	field_5360(90, 90),
	field_5367(90, 180),
	field_5354(90, 270),
	field_5358(180, 0),
	field_5348(180, 90),
	field_5356(180, 180),
	field_5359(180, 270),
	field_5353(270, 0),
	field_5349(270, 90),
	field_5361(270, 180),
	field_5352(270, 270);

	private static final Map<Integer, ModelRotation> BY_INDEX = (Map<Integer, ModelRotation>)Arrays.stream(values())
		.collect(Collectors.toMap(modelRotation -> modelRotation.index, modelRotation -> modelRotation));
	private final int index;
	private final Quaternion quaternion;
	private final int xRotations;
	private final int yRotations;

	private static int getIndex(int i, int j) {
		return i * 360 + j;
	}

	private ModelRotation(int j, int k) {
		this.index = getIndex(j, k);
		Quaternion quaternion = new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), (float)(-k), true);
		quaternion.hamiltonProduct(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), (float)(-j), true));
		this.quaternion = quaternion;
		this.xRotations = MathHelper.abs(j / 90);
		this.yRotations = MathHelper.abs(k / 90);
	}

	@Override
	public Rotation3 getRotation() {
		return new Rotation3(null, this.quaternion, null, null);
	}

	public static ModelRotation get(int i, int j) {
		return (ModelRotation)BY_INDEX.get(getIndex(MathHelper.floorMod(i, 360), MathHelper.floorMod(j, 360)));
	}
}
