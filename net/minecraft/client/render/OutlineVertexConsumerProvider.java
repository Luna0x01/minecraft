package net.minecraft.client.render;

import java.util.Optional;

public class OutlineVertexConsumerProvider implements VertexConsumerProvider {
	private final VertexConsumerProvider.Immediate parent;
	private final VertexConsumerProvider.Immediate plainDrawer = VertexConsumerProvider.immediate(new BufferBuilder(256));
	private int red = 255;
	private int green = 255;
	private int blue = 255;
	private int alpha = 255;

	public OutlineVertexConsumerProvider(VertexConsumerProvider.Immediate parent) {
		this.parent = parent;
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
				return VertexConsumers.union(outlineVertexConsumer, vertexConsumer2);
			} else {
				return vertexConsumer2;
			}
		}
	}

	public void setColor(int red, int green, int blue, int alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
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

		OutlineVertexConsumer(VertexConsumer vertexConsumer, int i, int j, int k, int l) {
			this.delegate = vertexConsumer;
			super.fixedColor(i, j, k, l);
		}

		@Override
		public void fixedColor(int red, int green, int blue, int alpha) {
		}

		@Override
		public void unfixColor() {
		}

		@Override
		public VertexConsumer vertex(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}

		@Override
		public VertexConsumer color(int red, int green, int blue, int alpha) {
			return this;
		}

		@Override
		public VertexConsumer texture(float u, float v) {
			this.u = u;
			this.v = v;
			return this;
		}

		@Override
		public VertexConsumer overlay(int u, int v) {
			return this;
		}

		@Override
		public VertexConsumer light(int u, int v) {
			return this;
		}

		@Override
		public VertexConsumer normal(float x, float y, float z) {
			return this;
		}

		@Override
		public void vertex(
			float x,
			float y,
			float z,
			float red,
			float green,
			float blue,
			float alpha,
			float u,
			float v,
			int overlay,
			int light,
			float normalX,
			float normalY,
			float normalZ
		) {
			this.delegate.vertex((double)x, (double)y, (double)z).color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha).texture(u, v).next();
		}

		@Override
		public void next() {
			this.delegate.vertex(this.x, this.y, this.z).color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha).texture(this.u, this.v).next();
		}
	}
}
