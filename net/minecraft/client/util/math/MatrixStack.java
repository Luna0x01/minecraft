package net.minecraft.client.util.math;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

public class MatrixStack {
	private final Deque<MatrixStack.Entry> stack = Util.make(Queues.newArrayDeque(), arrayDeque -> {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		Matrix3f matrix3f = new Matrix3f();
		matrix3f.loadIdentity();
		arrayDeque.add(new MatrixStack.Entry(matrix4f, matrix3f));
	});

	public void translate(double d, double e, double f) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.modelMatrix.multiply(Matrix4f.translate((float)d, (float)e, (float)f));
	}

	public void scale(float f, float g, float h) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.modelMatrix.multiply(Matrix4f.scale(f, g, h));
		if (f == g && g == h) {
			if (f > 0.0F) {
				return;
			}

			entry.normalMatrix.multiply(-1.0F);
		}

		float i = 1.0F / f;
		float j = 1.0F / g;
		float k = 1.0F / h;
		float l = MathHelper.fastInverseCbrt(i * j * k);
		entry.normalMatrix.multiply(Matrix3f.scale(l * i, l * j, l * k));
	}

	public void multiply(Quaternion quaternion) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.modelMatrix.multiply(quaternion);
		entry.normalMatrix.multiply(quaternion);
	}

	public void push() {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		this.stack.addLast(new MatrixStack.Entry(entry.modelMatrix.copy(), entry.normalMatrix.copy()));
	}

	public void pop() {
		this.stack.removeLast();
	}

	public MatrixStack.Entry peek() {
		return (MatrixStack.Entry)this.stack.getLast();
	}

	public boolean isEmpty() {
		return this.stack.size() == 1;
	}

	public static final class Entry {
		private final Matrix4f modelMatrix;
		private final Matrix3f normalMatrix;

		private Entry(Matrix4f matrix4f, Matrix3f matrix3f) {
			this.modelMatrix = matrix4f;
			this.normalMatrix = matrix3f;
		}

		public Matrix4f getModel() {
			return this.modelMatrix;
		}

		public Matrix3f getNormal() {
			return this.normalMatrix;
		}
	}
}
