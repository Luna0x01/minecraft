package net.minecraft.realms;

import net.minecraft.client.render.VertexFormatElement;

public class RealmsVertexFormatElement {
	private final VertexFormatElement vertexFormatElement;

	public RealmsVertexFormatElement(VertexFormatElement vertexFormatElement) {
		this.vertexFormatElement = vertexFormatElement;
	}

	public VertexFormatElement getVertexFormatElement() {
		return this.vertexFormatElement;
	}

	public boolean isPosition() {
		return this.vertexFormatElement.isPosition();
	}

	public int getIndex() {
		return this.vertexFormatElement.getIndex();
	}

	public int getByteSize() {
		return this.vertexFormatElement.getSize();
	}

	public int getCount() {
		return this.vertexFormatElement.getCount();
	}

	public int hashCode() {
		return this.vertexFormatElement.hashCode();
	}

	public boolean equals(Object object) {
		return this.vertexFormatElement.equals(object);
	}

	public String toString() {
		return this.vertexFormatElement.toString();
	}
}
