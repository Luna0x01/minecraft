package net.minecraft.client.model;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Random;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;

public class ModelPart {
	private float textureWidth = 64.0F;
	private float textureHeight = 32.0F;
	private int textureOffsetU;
	private int textureOffsetV;
	public float pivotX;
	public float pivotY;
	public float pivotZ;
	public float pitch;
	public float yaw;
	public float roll;
	public boolean mirror;
	public boolean visible = true;
	private final ObjectList<ModelPart.Cuboid> cuboids = new ObjectArrayList();
	private final ObjectList<ModelPart> children = new ObjectArrayList();

	public ModelPart(Model model) {
		model.accept(this);
		this.setTextureSize(model.textureWidth, model.textureHeight);
	}

	public ModelPart(Model model, int i, int j) {
		this(model.textureWidth, model.textureHeight, i, j);
		model.accept(this);
	}

	public ModelPart(int i, int j, int k, int l) {
		this.setTextureSize(i, j);
		this.setTextureOffset(k, l);
	}

	public void copyPositionAndRotation(ModelPart modelPart) {
		this.pitch = modelPart.pitch;
		this.yaw = modelPart.yaw;
		this.roll = modelPart.roll;
		this.pivotX = modelPart.pivotX;
		this.pivotY = modelPart.pivotY;
		this.pivotZ = modelPart.pivotZ;
	}

	public void addChild(ModelPart modelPart) {
		this.children.add(modelPart);
	}

	public ModelPart setTextureOffset(int i, int j) {
		this.textureOffsetU = i;
		this.textureOffsetV = j;
		return this;
	}

	public ModelPart addCuboid(String string, float f, float g, float h, int i, int j, int k, float l, int m, int n) {
		this.setTextureOffset(m, n);
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, (float)i, (float)j, (float)k, l, l, l, this.mirror, false);
		return this;
	}

	public ModelPart addCuboid(float f, float g, float h, float i, float j, float k) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0F, 0.0F, 0.0F, this.mirror, false);
		return this;
	}

	public ModelPart addCuboid(float f, float g, float h, float i, float j, float k, boolean bl) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0F, 0.0F, 0.0F, bl, false);
		return this;
	}

	public void addCuboid(float f, float g, float h, float i, float j, float k, float l) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, l, l, this.mirror, false);
	}

	public void addCuboid(float f, float g, float h, float i, float j, float k, float l, float m, float n) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, m, n, this.mirror, false);
	}

	public void addCuboid(float f, float g, float h, float i, float j, float k, float l, boolean bl) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, l, l, bl, false);
	}

	private void addCuboid(int i, int j, float f, float g, float h, float k, float l, float m, float n, float o, float p, boolean bl, boolean bl2) {
		this.cuboids.add(new ModelPart.Cuboid(i, j, f, g, h, k, l, m, n, o, p, bl, this.textureWidth, this.textureHeight));
	}

	public void setPivot(float f, float g, float h) {
		this.pivotX = f;
		this.pivotY = g;
		this.pivotZ = h;
	}

	public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j) {
		this.render(matrixStack, vertexConsumer, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
		if (this.visible) {
			if (!this.cuboids.isEmpty() || !this.children.isEmpty()) {
				matrixStack.push();
				this.rotate(matrixStack);
				this.renderCuboids(matrixStack.peek(), vertexConsumer, i, j, f, g, h, k);
				ObjectListIterator var9 = this.children.iterator();

				while (var9.hasNext()) {
					ModelPart modelPart = (ModelPart)var9.next();
					modelPart.render(matrixStack, vertexConsumer, i, j, f, g, h, k);
				}

				matrixStack.pop();
			}
		}
	}

	public void rotate(MatrixStack matrixStack) {
		matrixStack.translate((double)(this.pivotX / 16.0F), (double)(this.pivotY / 16.0F), (double)(this.pivotZ / 16.0F));
		if (this.roll != 0.0F) {
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(this.roll));
		}

		if (this.yaw != 0.0F) {
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(this.yaw));
		}

		if (this.pitch != 0.0F) {
			matrixStack.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(this.pitch));
		}
	}

	private void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
		Matrix4f matrix4f = entry.getModel();
		Matrix3f matrix3f = entry.getNormal();
		ObjectListIterator var11 = this.cuboids.iterator();

		while (var11.hasNext()) {
			ModelPart.Cuboid cuboid = (ModelPart.Cuboid)var11.next();

			for (ModelPart.Quad quad : cuboid.sides) {
				Vector3f vector3f = quad.direction.copy();
				vector3f.transform(matrix3f);
				float l = vector3f.getX();
				float m = vector3f.getY();
				float n = vector3f.getZ();

				for (int o = 0; o < 4; o++) {
					ModelPart.Vertex vertex = quad.vertices[o];
					float p = vertex.pos.getX() / 16.0F;
					float q = vertex.pos.getY() / 16.0F;
					float r = vertex.pos.getZ() / 16.0F;
					Vector4f vector4f = new Vector4f(p, q, r, 1.0F);
					vector4f.transform(matrix4f);
					vertexConsumer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), f, g, h, k, vertex.u, vertex.v, j, i, l, m, n);
				}
			}
		}
	}

	public ModelPart setTextureSize(int i, int j) {
		this.textureWidth = (float)i;
		this.textureHeight = (float)j;
		return this;
	}

	public ModelPart.Cuboid getRandomCuboid(Random random) {
		return (ModelPart.Cuboid)this.cuboids.get(random.nextInt(this.cuboids.size()));
	}

	public static class Cuboid {
		private final ModelPart.Quad[] sides;
		public final float minX;
		public final float minY;
		public final float minZ;
		public final float maxX;
		public final float maxY;
		public final float maxZ;

		public Cuboid(int i, int j, float f, float g, float h, float k, float l, float m, float n, float o, float p, boolean bl, float q, float r) {
			this.minX = f;
			this.minY = g;
			this.minZ = h;
			this.maxX = f + k;
			this.maxY = g + l;
			this.maxZ = h + m;
			this.sides = new ModelPart.Quad[6];
			float s = f + k;
			float t = g + l;
			float u = h + m;
			f -= n;
			g -= o;
			h -= p;
			s += n;
			t += o;
			u += p;
			if (bl) {
				float v = s;
				s = f;
				f = v;
			}

			ModelPart.Vertex vertex = new ModelPart.Vertex(f, g, h, 0.0F, 0.0F);
			ModelPart.Vertex vertex2 = new ModelPart.Vertex(s, g, h, 0.0F, 8.0F);
			ModelPart.Vertex vertex3 = new ModelPart.Vertex(s, t, h, 8.0F, 8.0F);
			ModelPart.Vertex vertex4 = new ModelPart.Vertex(f, t, h, 8.0F, 0.0F);
			ModelPart.Vertex vertex5 = new ModelPart.Vertex(f, g, u, 0.0F, 0.0F);
			ModelPart.Vertex vertex6 = new ModelPart.Vertex(s, g, u, 0.0F, 8.0F);
			ModelPart.Vertex vertex7 = new ModelPart.Vertex(s, t, u, 8.0F, 8.0F);
			ModelPart.Vertex vertex8 = new ModelPart.Vertex(f, t, u, 8.0F, 0.0F);
			float w = (float)i;
			float x = (float)i + m;
			float y = (float)i + m + k;
			float z = (float)i + m + k + k;
			float aa = (float)i + m + k + m;
			float ab = (float)i + m + k + m + k;
			float ac = (float)j;
			float ad = (float)j + m;
			float ae = (float)j + m + l;
			this.sides[2] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex6, vertex5, vertex, vertex2}, x, ac, y, ad, q, r, bl, Direction.field_11033);
			this.sides[3] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex3, vertex4, vertex8, vertex7}, y, ad, z, ac, q, r, bl, Direction.field_11036);
			this.sides[1] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex, vertex5, vertex8, vertex4}, w, ad, x, ae, q, r, bl, Direction.field_11039);
			this.sides[4] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex2, vertex, vertex4, vertex3}, x, ad, y, ae, q, r, bl, Direction.field_11043);
			this.sides[0] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex6, vertex2, vertex3, vertex7}, y, ad, aa, ae, q, r, bl, Direction.field_11034);
			this.sides[5] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex5, vertex6, vertex7, vertex8}, aa, ad, ab, ae, q, r, bl, Direction.field_11035);
		}
	}

	static class Quad {
		public final ModelPart.Vertex[] vertices;
		public final Vector3f direction;

		public Quad(ModelPart.Vertex[] vertexs, float f, float g, float h, float i, float j, float k, boolean bl, Direction direction) {
			this.vertices = vertexs;
			float l = 0.0F / j;
			float m = 0.0F / k;
			vertexs[0] = vertexs[0].remap(h / j - l, g / k + m);
			vertexs[1] = vertexs[1].remap(f / j + l, g / k + m);
			vertexs[2] = vertexs[2].remap(f / j + l, i / k - m);
			vertexs[3] = vertexs[3].remap(h / j - l, i / k - m);
			if (bl) {
				int n = vertexs.length;

				for (int o = 0; o < n / 2; o++) {
					ModelPart.Vertex vertex = vertexs[o];
					vertexs[o] = vertexs[n - 1 - o];
					vertexs[n - 1 - o] = vertex;
				}
			}

			this.direction = direction.getUnitVector();
			if (bl) {
				this.direction.multiplyComponentwise(-1.0F, 1.0F, 1.0F);
			}
		}
	}

	static class Vertex {
		public final Vector3f pos;
		public final float u;
		public final float v;

		public Vertex(float f, float g, float h, float i, float j) {
			this(new Vector3f(f, g, h), i, j);
		}

		public ModelPart.Vertex remap(float f, float g) {
			return new ModelPart.Vertex(this.pos, f, g);
		}

		public Vertex(Vector3f vector3f, float f, float g) {
			this.pos = vector3f;
			this.u = f;
			this.v = g;
		}
	}
}
