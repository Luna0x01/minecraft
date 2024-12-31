package net.minecraft.client.render;

public class VertexFormats {
	public static final VertexFormatElement POSITION_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.POSITION, 3);
	public static final VertexFormatElement COLOR_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.UNSIGNED_BYTE, VertexFormatElement.Type.COLOR, 4);
	public static final VertexFormatElement TEXTURE_FLOAT_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement TEXTURE_SHORT_ELEMENT = new VertexFormatElement(1, VertexFormatElement.Format.SHORT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement NORMAL_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.BYTE, VertexFormatElement.Type.NORMAL, 3);
	public static final VertexFormatElement PADDING_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.BYTE, VertexFormatElement.Type.PADDING, 1);
	public static final VertexFormat BLOCK = new VertexFormat()
		.addElement(POSITION_ELEMENT)
		.addElement(COLOR_ELEMENT)
		.addElement(TEXTURE_FLOAT_ELEMENT)
		.addElement(TEXTURE_SHORT_ELEMENT);
	public static final VertexFormat BLOCK_NORMALS = new VertexFormat()
		.addElement(POSITION_ELEMENT)
		.addElement(COLOR_ELEMENT)
		.addElement(TEXTURE_FLOAT_ELEMENT)
		.addElement(NORMAL_ELEMENT)
		.addElement(PADDING_ELEMENT);
	public static final VertexFormat ENTITY = new VertexFormat()
		.addElement(POSITION_ELEMENT)
		.addElement(TEXTURE_FLOAT_ELEMENT)
		.addElement(NORMAL_ELEMENT)
		.addElement(PADDING_ELEMENT);
	public static final VertexFormat PARTICLE = new VertexFormat()
		.addElement(POSITION_ELEMENT)
		.addElement(TEXTURE_FLOAT_ELEMENT)
		.addElement(COLOR_ELEMENT)
		.addElement(TEXTURE_SHORT_ELEMENT);
	public static final VertexFormat POSITION = new VertexFormat().addElement(POSITION_ELEMENT);
	public static final VertexFormat POSITION_COLOR = new VertexFormat().addElement(POSITION_ELEMENT).addElement(COLOR_ELEMENT);
	public static final VertexFormat POSITION_TEXTURE = new VertexFormat().addElement(POSITION_ELEMENT).addElement(TEXTURE_FLOAT_ELEMENT);
	public static final VertexFormat POSITION_NORMAL = new VertexFormat().addElement(POSITION_ELEMENT).addElement(NORMAL_ELEMENT).addElement(PADDING_ELEMENT);
	public static final VertexFormat POSITION_TEXTURE_COLOR = new VertexFormat()
		.addElement(POSITION_ELEMENT)
		.addElement(TEXTURE_FLOAT_ELEMENT)
		.addElement(COLOR_ELEMENT);
	public static final VertexFormat POSITION_TEXTURE_NORMAL = new VertexFormat()
		.addElement(POSITION_ELEMENT)
		.addElement(TEXTURE_FLOAT_ELEMENT)
		.addElement(NORMAL_ELEMENT)
		.addElement(PADDING_ELEMENT);
	public static final VertexFormat POSITION_TEXTURE2_COLOR = new VertexFormat()
		.addElement(POSITION_ELEMENT)
		.addElement(TEXTURE_FLOAT_ELEMENT)
		.addElement(TEXTURE_SHORT_ELEMENT)
		.addElement(COLOR_ELEMENT);
	public static final VertexFormat POSITION_TEXTURE_COLOR_NORMAL = new VertexFormat()
		.addElement(POSITION_ELEMENT)
		.addElement(TEXTURE_FLOAT_ELEMENT)
		.addElement(COLOR_ELEMENT)
		.addElement(NORMAL_ELEMENT)
		.addElement(PADDING_ELEMENT);
}
