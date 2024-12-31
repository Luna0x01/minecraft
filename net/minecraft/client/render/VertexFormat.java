package net.minecraft.client.render;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormat {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<VertexFormatElement> elements = Lists.newArrayList();
	private final List<Integer> positions = Lists.newArrayList();
	private int size;
	private int colorIndex = -1;
	private final List<Integer> uvIndices = Lists.newArrayList();
	private int normalIndex = -1;

	public VertexFormat(VertexFormat vertexFormat) {
		this();

		for (int i = 0; i < vertexFormat.getSize(); i++) {
			this.addElement(vertexFormat.get(i));
		}

		this.size = vertexFormat.getVertexSize();
	}

	public VertexFormat() {
	}

	public void clear() {
		this.elements.clear();
		this.positions.clear();
		this.colorIndex = -1;
		this.uvIndices.clear();
		this.normalIndex = -1;
		this.size = 0;
	}

	public VertexFormat addElement(VertexFormatElement element) {
		if (element.isPosition() && this.hasPosition()) {
			LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
			return this;
		} else {
			this.elements.add(element);
			this.positions.add(this.size);
			switch (element.getType()) {
				case NORMAL:
					this.normalIndex = this.size;
					break;
				case COLOR:
					this.colorIndex = this.size;
					break;
				case UV:
					this.uvIndices.add(element.getIndex(), this.size);
			}

			this.size = this.size + element.getSize();
			return this;
		}
	}

	public boolean hasNormal() {
		return this.normalIndex >= 0;
	}

	public int getNormalIndex() {
		return this.normalIndex;
	}

	public boolean hasColor() {
		return this.colorIndex >= 0;
	}

	public int getColorIndex() {
		return this.colorIndex;
	}

	public boolean hasUv(int uvId) {
		return this.uvIndices.size() - 1 >= uvId;
	}

	public int getUvIndex(int uvId) {
		return (Integer)this.uvIndices.get(uvId);
	}

	public String toString() {
		String string = "format: " + this.elements.size() + " elements: ";

		for (int i = 0; i < this.elements.size(); i++) {
			string = string + ((VertexFormatElement)this.elements.get(i)).toString();
			if (i != this.elements.size() - 1) {
				string = string + " ";
			}
		}

		return string;
	}

	private boolean hasPosition() {
		int i = 0;

		for (int j = this.elements.size(); i < j; i++) {
			VertexFormatElement vertexFormatElement = (VertexFormatElement)this.elements.get(i);
			if (vertexFormatElement.isPosition()) {
				return true;
			}
		}

		return false;
	}

	public int getVertexSizeInteger() {
		return this.getVertexSize() / 4;
	}

	public int getVertexSize() {
		return this.size;
	}

	public List<VertexFormatElement> getElements() {
		return this.elements;
	}

	public int getSize() {
		return this.elements.size();
	}

	public VertexFormatElement get(int i) {
		return (VertexFormatElement)this.elements.get(i);
	}

	public int getIndex(int elementId) {
		return (Integer)this.positions.get(elementId);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			VertexFormat vertexFormat = (VertexFormat)obj;
			if (this.size != vertexFormat.size) {
				return false;
			} else {
				return !this.elements.equals(vertexFormat.elements) ? false : this.positions.equals(vertexFormat.positions);
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.elements.hashCode();
		i = 31 * i + this.positions.hashCode();
		return 31 * i + this.size;
	}
}
