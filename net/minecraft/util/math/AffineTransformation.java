package net.minecraft.util.math;

import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import org.apache.commons.lang3.tuple.Triple;

public final class AffineTransformation {
	private final Matrix4f matrix;
	private boolean initialized;
	@Nullable
	private Vec3f translation;
	@Nullable
	private Quaternion rotation2;
	@Nullable
	private Vec3f scale;
	@Nullable
	private Quaternion rotation1;
	private static final AffineTransformation IDENTITY = Util.make(() -> {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		AffineTransformation affineTransformation = new AffineTransformation(matrix4f);
		affineTransformation.getRotation2();
		return affineTransformation;
	});

	public AffineTransformation(@Nullable Matrix4f matrix) {
		if (matrix == null) {
			this.matrix = IDENTITY.matrix;
		} else {
			this.matrix = matrix;
		}
	}

	public AffineTransformation(@Nullable Vec3f translation, @Nullable Quaternion rotation2, @Nullable Vec3f scale, @Nullable Quaternion rotation1) {
		this.matrix = setup(translation, rotation2, scale, rotation1);
		this.translation = translation != null ? translation : new Vec3f();
		this.rotation2 = rotation2 != null ? rotation2 : Quaternion.IDENTITY.copy();
		this.scale = scale != null ? scale : new Vec3f(1.0F, 1.0F, 1.0F);
		this.rotation1 = rotation1 != null ? rotation1 : Quaternion.IDENTITY.copy();
		this.initialized = true;
	}

	public static AffineTransformation identity() {
		return IDENTITY;
	}

	public AffineTransformation multiply(AffineTransformation other) {
		Matrix4f matrix4f = this.getMatrix();
		matrix4f.multiply(other.getMatrix());
		return new AffineTransformation(matrix4f);
	}

	@Nullable
	public AffineTransformation invert() {
		if (this == IDENTITY) {
			return this;
		} else {
			Matrix4f matrix4f = this.getMatrix();
			return matrix4f.invert() ? new AffineTransformation(matrix4f) : null;
		}
	}

	private void init() {
		if (!this.initialized) {
			Pair<Matrix3f, Vec3f> pair = getLinearTransformationAndTranslationFromAffine(this.matrix);
			Triple<Quaternion, Vec3f, Quaternion> triple = ((Matrix3f)pair.getFirst()).decomposeLinearTransformation();
			this.translation = (Vec3f)pair.getSecond();
			this.rotation2 = (Quaternion)triple.getLeft();
			this.scale = (Vec3f)triple.getMiddle();
			this.rotation1 = (Quaternion)triple.getRight();
			this.initialized = true;
		}
	}

	private static Matrix4f setup(@Nullable Vec3f translation, @Nullable Quaternion rotation2, @Nullable Vec3f scale, @Nullable Quaternion rotation1) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		if (rotation2 != null) {
			matrix4f.multiply(new Matrix4f(rotation2));
		}

		if (scale != null) {
			matrix4f.multiply(Matrix4f.scale(scale.getX(), scale.getY(), scale.getZ()));
		}

		if (rotation1 != null) {
			matrix4f.multiply(new Matrix4f(rotation1));
		}

		if (translation != null) {
			matrix4f.a03 = translation.getX();
			matrix4f.a13 = translation.getY();
			matrix4f.a23 = translation.getZ();
		}

		return matrix4f;
	}

	public static Pair<Matrix3f, Vec3f> getLinearTransformationAndTranslationFromAffine(Matrix4f affineTransform) {
		affineTransform.multiply(1.0F / affineTransform.a33);
		Vec3f vec3f = new Vec3f(affineTransform.a03, affineTransform.a13, affineTransform.a23);
		Matrix3f matrix3f = new Matrix3f(affineTransform);
		return Pair.of(matrix3f, vec3f);
	}

	public Matrix4f getMatrix() {
		return this.matrix.copy();
	}

	public Vec3f getTranslation() {
		this.init();
		return this.translation.copy();
	}

	public Quaternion getRotation2() {
		this.init();
		return this.rotation2.copy();
	}

	public Vec3f getScale() {
		this.init();
		return this.scale.copy();
	}

	public Quaternion getRotation1() {
		this.init();
		return this.rotation1.copy();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			AffineTransformation affineTransformation = (AffineTransformation)o;
			return Objects.equals(this.matrix, affineTransformation.matrix);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.matrix});
	}

	public AffineTransformation method_35864(AffineTransformation affineTransformation, float f) {
		Vec3f vec3f = this.getTranslation();
		Quaternion quaternion = this.getRotation2();
		Vec3f vec3f2 = this.getScale();
		Quaternion quaternion2 = this.getRotation1();
		vec3f.lerp(affineTransformation.getTranslation(), f);
		quaternion.method_35822(affineTransformation.getRotation2(), f);
		vec3f2.lerp(affineTransformation.getScale(), f);
		quaternion2.method_35822(affineTransformation.getRotation1(), f);
		return new AffineTransformation(vec3f, quaternion, vec3f2, quaternion2);
	}
}
