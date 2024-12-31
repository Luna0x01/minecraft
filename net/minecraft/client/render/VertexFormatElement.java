package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.IntConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormatElement {
	private static final Logger LOGGER = LogManager.getLogger();
	private final VertexFormatElement.Format format;
	private final VertexFormatElement.Type type;
	private final int index;
	private final int count;
	private final int size;

	public VertexFormatElement(int i, VertexFormatElement.Format format, VertexFormatElement.Type type, int j) {
		if (this.isValidType(i, type)) {
			this.type = type;
		} else {
			LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
			this.type = VertexFormatElement.Type.field_1636;
		}

		this.format = format;
		this.index = i;
		this.count = j;
		this.size = format.getSize() * this.count;
	}

	private boolean isValidType(int i, VertexFormatElement.Type type) {
		return i == 0 || type == VertexFormatElement.Type.field_1636;
	}

	public final VertexFormatElement.Format getFormat() {
		return this.format;
	}

	public final VertexFormatElement.Type getType() {
		return this.type;
	}

	public final int getCount() {
		return this.count;
	}

	public final int getIndex() {
		return this.index;
	}

	public String toString() {
		return this.count + "," + this.type.getName() + "," + this.format.getName();
	}

	public final int getSize() {
		return this.size;
	}

	public final boolean isPosition() {
		return this.type == VertexFormatElement.Type.field_1633;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			VertexFormatElement vertexFormatElement = (VertexFormatElement)object;
			if (this.count != vertexFormatElement.count) {
				return false;
			} else if (this.index != vertexFormatElement.index) {
				return false;
			} else {
				return this.format != vertexFormatElement.format ? false : this.type == vertexFormatElement.type;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.format.hashCode();
		i = 31 * i + this.type.hashCode();
		i = 31 * i + this.index;
		return 31 * i + this.count;
	}

	public void startDrawing(long l, int i) {
		this.type.startDrawing(this.count, this.format.getGlId(), i, l, this.index);
	}

	public void endDrawing() {
		this.type.endDrawing(this.index);
	}

	public static enum Format {
		field_1623(4, "Float", 5126),
		field_1624(1, "Unsigned Byte", 5121),
		field_1621(1, "Byte", 5120),
		field_1622(2, "Unsigned Short", 5123),
		field_1625(2, "Short", 5122),
		field_1619(4, "Unsigned Int", 5125),
		field_1617(4, "Int", 5124);

		private final int size;
		private final String name;
		private final int glId;

		private Format(int j, String string2, int k) {
			this.size = j;
			this.name = string2;
			this.glId = k;
		}

		public int getSize() {
			return this.size;
		}

		public String getName() {
			return this.name;
		}

		public int getGlId() {
			return this.glId;
		}
	}

	public static enum Type {
		field_1633("Position", (i, j, k, l, m) -> {
			GlStateManager.vertexPointer(i, j, k, l);
			GlStateManager.enableClientState(32884);
		}, i -> GlStateManager.disableClientState(32884)),
		field_1635("Normal", (i, j, k, l, m) -> {
			GlStateManager.normalPointer(j, k, l);
			GlStateManager.enableClientState(32885);
		}, i -> GlStateManager.disableClientState(32885)),
		field_1632("Vertex Color", (i, j, k, l, m) -> {
			GlStateManager.colorPointer(i, j, k, l);
			GlStateManager.enableClientState(32886);
		}, i -> {
			GlStateManager.disableClientState(32886);
			GlStateManager.clearCurrentColor();
		}),
		field_1636("UV", (i, j, k, l, m) -> {
			GlStateManager.clientActiveTexture(33984 + m);
			GlStateManager.texCoordPointer(i, j, k, l);
			GlStateManager.enableClientState(32888);
			GlStateManager.clientActiveTexture(33984);
		}, i -> {
			GlStateManager.clientActiveTexture(33984 + i);
			GlStateManager.disableClientState(32888);
			GlStateManager.clientActiveTexture(33984);
		}),
		field_1629("Padding", (i, j, k, l, m) -> {
		}, i -> {
		}),
		field_20782("Generic", (i, j, k, l, m) -> {
			GlStateManager.enableVertexAttribArray(m);
			GlStateManager.vertexAttribPointer(m, i, j, false, k, l);
		}, GlStateManager::method_22607);

		private final String name;
		private final VertexFormatElement.Type.Starter stater;
		private final IntConsumer finisher;

		private Type(String string2, VertexFormatElement.Type.Starter starter, IntConsumer intConsumer) {
			this.name = string2;
			this.stater = starter;
			this.finisher = intConsumer;
		}

		private void startDrawing(int i, int j, int k, long l, int m) {
			this.stater.setupBufferState(i, j, k, l, m);
		}

		public void endDrawing(int i) {
			this.finisher.accept(i);
		}

		public String getName() {
			return this.name;
		}

		interface Starter {
			void setupBufferState(int i, int j, int k, long l, int m);
		}
	}
}
