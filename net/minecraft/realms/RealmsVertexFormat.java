package net.minecraft.realms;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

public class RealmsVertexFormat {
	private VertexFormat v;

	public RealmsVertexFormat(VertexFormat vertexFormat) {
		this.v = vertexFormat;
	}

	public VertexFormat getVertexFormat() {
		return this.v;
	}

	public List<RealmsVertexFormatElement> getElements() {
		List<RealmsVertexFormatElement> list = Lists.newArrayList();
		UnmodifiableIterator var2 = this.v.getElements().iterator();

		while (var2.hasNext()) {
			VertexFormatElement vertexFormatElement = (VertexFormatElement)var2.next();
			list.add(new RealmsVertexFormatElement(vertexFormatElement));
		}

		return list;
	}

	public boolean equals(Object object) {
		return this.v.equals(object);
	}

	public int hashCode() {
		return this.v.hashCode();
	}

	public String toString() {
		return this.v.toString();
	}
}
