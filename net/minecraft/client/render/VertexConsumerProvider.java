package net.minecraft.client.render;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public interface VertexConsumerProvider {
	static VertexConsumerProvider.Immediate immediate(BufferBuilder bufferBuilder) {
		return immediate(ImmutableMap.of(), bufferBuilder);
	}

	static VertexConsumerProvider.Immediate immediate(Map<RenderLayer, BufferBuilder> map, BufferBuilder bufferBuilder) {
		return new VertexConsumerProvider.Immediate(bufferBuilder, map);
	}

	VertexConsumer getBuffer(RenderLayer renderLayer);

	public static class Immediate implements VertexConsumerProvider {
		protected final BufferBuilder fallbackBuffer;
		protected final Map<RenderLayer, BufferBuilder> layerBuffers;
		protected Optional<RenderLayer> currentLayer = Optional.empty();
		protected final Set<BufferBuilder> activeConsumers = Sets.newHashSet();

		protected Immediate(BufferBuilder bufferBuilder, Map<RenderLayer, BufferBuilder> map) {
			this.fallbackBuffer = bufferBuilder;
			this.layerBuffers = map;
		}

		@Override
		public VertexConsumer getBuffer(RenderLayer renderLayer) {
			Optional<RenderLayer> optional = renderLayer.asOptional();
			BufferBuilder bufferBuilder = this.getBufferInternal(renderLayer);
			if (!Objects.equals(this.currentLayer, optional)) {
				if (this.currentLayer.isPresent()) {
					RenderLayer renderLayer2 = (RenderLayer)this.currentLayer.get();
					if (!this.layerBuffers.containsKey(renderLayer2)) {
						this.draw(renderLayer2);
					}
				}

				if (this.activeConsumers.add(bufferBuilder)) {
					bufferBuilder.begin(renderLayer.getDrawMode(), renderLayer.getVertexFormat());
				}

				this.currentLayer = optional;
			}

			return bufferBuilder;
		}

		private BufferBuilder getBufferInternal(RenderLayer renderLayer) {
			return (BufferBuilder)this.layerBuffers.getOrDefault(renderLayer, this.fallbackBuffer);
		}

		public void draw() {
			this.currentLayer.ifPresent(renderLayerx -> {
				VertexConsumer vertexConsumer = this.getBuffer(renderLayerx);
				if (vertexConsumer == this.fallbackBuffer) {
					this.draw(renderLayerx);
				}
			});

			for (RenderLayer renderLayer : this.layerBuffers.keySet()) {
				this.draw(renderLayer);
			}
		}

		public void draw(RenderLayer renderLayer) {
			BufferBuilder bufferBuilder = this.getBufferInternal(renderLayer);
			boolean bl = Objects.equals(this.currentLayer, renderLayer.asOptional());
			if (bl || bufferBuilder != this.fallbackBuffer) {
				if (this.activeConsumers.remove(bufferBuilder)) {
					renderLayer.draw(bufferBuilder, 0, 0, 0);
					if (bl) {
						this.currentLayer = Optional.empty();
					}
				}
			}
		}
	}
}
