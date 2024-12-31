package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

public class RealmsVertexFormat {
	private VertexFormat vertexFormat;

	public RealmsVertexFormat(VertexFormat vertexFormat) {
		this.vertexFormat = vertexFormat;
	}

	public RealmsVertexFormat from(VertexFormat vertexFormat) {
		this.vertexFormat = vertexFormat;
		return this;
	}

	public VertexFormat getVertexFormat() {
		return this.vertexFormat;
	}

	public void clear() {
		this.vertexFormat.clear();
	}

	public int getUvOffset(int uvId) {
		return this.vertexFormat.getUvIndex(uvId);
	}

	public int getElementCount() {
		return this.vertexFormat.getSize();
	}

	public boolean hasColor() {
		return this.vertexFormat.hasColor();
	}

	public boolean hasUv(int uvId) {
		return this.vertexFormat.hasUv(uvId);
	}

	public RealmsVertexFormatElement getElement(int index) {
		return new RealmsVertexFormatElement(this.vertexFormat.get(index));
	}

	public RealmsVertexFormat addElement(RealmsVertexFormatElement element) {
		return this.from(this.vertexFormat.addElement(element.getVertexFormatElement()));
	}

	public int getColorOffset() {
		return this.vertexFormat.getColorIndex();
	}

	public List<RealmsVertexFormatElement> getElements() {
		List<RealmsVertexFormatElement> list = Lists.newArrayList();

		for (VertexFormatElement vertexFormatElement : this.vertexFormat.getElements()) {
			list.add(new RealmsVertexFormatElement(vertexFormatElement));
		}

		return list;
	}

	public boolean hasNormal() {
		return this.vertexFormat.hasNormal();
	}

	public int getVertexSize() {
		return this.vertexFormat.getVertexSize();
	}

	public int getOffset(int elementId) {
		return this.vertexFormat.getIndex(elementId);
	}

	public int getNormalOffset() {
		return this.vertexFormat.getNormalIndex();
	}

	public int getIntegerSize() {
		return this.vertexFormat.getVertexSizeInteger();
	}

	public boolean equals(Object object) {
		return this.vertexFormat.equals(object);
	}

	public int hashCode() {
		return this.vertexFormat.hashCode();
	}

	public String toString() {
		return this.vertexFormat.toString();
	}
}
