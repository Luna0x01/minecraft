package net.minecraft.client.render;

import java.util.Optional;

public class OutlineVertexConsumerProvider implements VertexConsumerProvider {
	private final VertexConsumerProvider.Immediate parent;
	private final VertexConsumerProvider.Immediate plainDrawer = VertexConsumerProvider.immediate(new BufferBuilder(256));
	private int red = 255;
	private int green = 255;
	private int blue = 255;
	private int alpha = 255;

	public OutlineVertexConsumerProvider(VertexConsumerProvider.Immediate immediate) {
		this.parent = immediate;
	}

	@Override
	public VertexConsumer getBuffer(RenderLayer renderLayer) {
		if (renderLayer.isOutline()) {
			VertexConsumer vertexConsumer = this.plainDrawer.getBuffer(renderLayer);
			return new OutlineVertexConsumerProvider.OutlineVertexConsumer(vertexConsumer, this.red, this.green, this.blue, this.alpha);
		} else {
			VertexConsumer vertexConsumer2 = this.parent.getBuffer(renderLayer);
			Optional<RenderLayer> optional = renderLayer.getAffectedOutline();
			if (optional.isPresent()) {
				VertexConsumer vertexConsumer3 = this.plainDrawer.getBuffer((RenderLayer)optional.get());
				OutlineVertexConsumerProvider.OutlineVertexConsumer outlineVertexConsumer = new OutlineVertexConsumerProvider.OutlineVertexConsumer(
					vertexConsumer3, this.red, this.green, this.blue, this.alpha
				);
				return VertexConsumers.dual(outlineVertexConsumer, vertexConsumer2);
			} else {
				return vertexConsumer2;
			}
		}
	}

	public void setColor(int i, int j, int k, int l) {
		this.red = i;
		this.green = j;
		this.blue = k;
		this.alpha = l;
	}

	public void draw() {
		this.plainDrawer.draw();
	}

	static class OutlineVertexConsumer extends FixedColorVertexConsumer {
		private final VertexConsumer delegate;
		private double x;
		private double y;
		private double z;
		private float u;
		private float v;

		private OutlineVertexConsumer(VertexConsumer vertexConsumer, int i, int j, int k, int l) {
			this.delegate = vertexConsumer;
			super.fixedColor(i, j, k, l);
		}

		@Override
		public void fixedColor(int i, int j, int k, int l) {
		}

		@Override
		public VertexConsumer vertex(double d, double e, double f) {
			this.x = d;
			this.y = e;
			this.z = f;
			return this;
		}

		@Override
		public VertexConsumer color(int i, int j, int k, int l) {
			return this;
		}

		@Override
		public VertexConsumer texture(float f, float g) {
			this.u = f;
			this.v = g;
			return this;
		}

		@Override
		public VertexConsumer overlay(int i, int j) {
			return this;
		}

		@Override
		public VertexConsumer light(int i, int j) {
			return this;
		}

		@Override
		public VertexConsumer normal(float f, float g, float h) {
			return this;
		}

		@Override
		public void vertex(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
			this.delegate.vertex((double)f, (double)g, (double)h).color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha).texture(m, n).next();
		}

		@Override
		public void next() {
			this.delegate.vertex(this.x, this.y, this.z).color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha).texture(this.u, this.v).next();
		}
	}
}
