package net.minecraft.client.render;

public class VertexFormats {
	public static final VertexFormat BLOCK = new VertexFormat();
	public static final VertexFormat BLOCK_NORMALS = new VertexFormat();
	public static final VertexFormat ENTITY = new VertexFormat();
	public static final VertexFormat PARTICLE = new VertexFormat();
	public static final VertexFormat POSITION = new VertexFormat();
	public static final VertexFormat POSITION_COLOR = new VertexFormat();
	public static final VertexFormat POSITION_TEXTURE = new VertexFormat();
	public static final VertexFormat POSITION_NORMAL = new VertexFormat();
	public static final VertexFormat POSITION_TEXTURE_COLOR = new VertexFormat();
	public static final VertexFormat POSITION_TEXTURE_NORMAL = new VertexFormat();
	public static final VertexFormat POSITION_TEXTURE2_COLOR = new VertexFormat();
	public static final VertexFormat POSITION_TEXTURE_COLOR_NORMAL = new VertexFormat();
	public static final VertexFormatElement POSITION_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.POSITION, 3);
	public static final VertexFormatElement COLOR_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.UNSIGNED_BYTE, VertexFormatElement.Type.COLOR, 4);
	public static final VertexFormatElement TEXTURE_FLOAT_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement TEXTURE_SHORT_ELEMENT = new VertexFormatElement(1, VertexFormatElement.Format.SHORT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement NORMAL_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.BYTE, VertexFormatElement.Type.NORMAL, 3);
	public static final VertexFormatElement PADDING_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.BYTE, VertexFormatElement.Type.PADDING, 1);

	static {
		BLOCK.addElement(POSITION_ELEMENT);
		BLOCK.addElement(COLOR_ELEMENT);
		BLOCK.addElement(TEXTURE_FLOAT_ELEMENT);
		BLOCK.addElement(TEXTURE_SHORT_ELEMENT);
		BLOCK_NORMALS.addElement(POSITION_ELEMENT);
		BLOCK_NORMALS.addElement(COLOR_ELEMENT);
		BLOCK_NORMALS.addElement(TEXTURE_FLOAT_ELEMENT);
		BLOCK_NORMALS.addElement(NORMAL_ELEMENT);
		BLOCK_NORMALS.addElement(PADDING_ELEMENT);
		ENTITY.addElement(POSITION_ELEMENT);
		ENTITY.addElement(TEXTURE_FLOAT_ELEMENT);
		ENTITY.addElement(NORMAL_ELEMENT);
		ENTITY.addElement(PADDING_ELEMENT);
		PARTICLE.addElement(POSITION_ELEMENT);
		PARTICLE.addElement(TEXTURE_FLOAT_ELEMENT);
		PARTICLE.addElement(COLOR_ELEMENT);
		PARTICLE.addElement(TEXTURE_SHORT_ELEMENT);
		POSITION.addElement(POSITION_ELEMENT);
		POSITION_COLOR.addElement(POSITION_ELEMENT);
		POSITION_COLOR.addElement(COLOR_ELEMENT);
		POSITION_TEXTURE.addElement(POSITION_ELEMENT);
		POSITION_TEXTURE.addElement(TEXTURE_FLOAT_ELEMENT);
		POSITION_NORMAL.addElement(POSITION_ELEMENT);
		POSITION_NORMAL.addElement(NORMAL_ELEMENT);
		POSITION_NORMAL.addElement(PADDING_ELEMENT);
		POSITION_TEXTURE_COLOR.addElement(POSITION_ELEMENT);
		POSITION_TEXTURE_COLOR.addElement(TEXTURE_FLOAT_ELEMENT);
		POSITION_TEXTURE_COLOR.addElement(COLOR_ELEMENT);
		POSITION_TEXTURE_NORMAL.addElement(POSITION_ELEMENT);
		POSITION_TEXTURE_NORMAL.addElement(TEXTURE_FLOAT_ELEMENT);
		POSITION_TEXTURE_NORMAL.addElement(NORMAL_ELEMENT);
		POSITION_TEXTURE_NORMAL.addElement(PADDING_ELEMENT);
		POSITION_TEXTURE2_COLOR.addElement(POSITION_ELEMENT);
		POSITION_TEXTURE2_COLOR.addElement(TEXTURE_FLOAT_ELEMENT);
		POSITION_TEXTURE2_COLOR.addElement(TEXTURE_SHORT_ELEMENT);
		POSITION_TEXTURE2_COLOR.addElement(COLOR_ELEMENT);
		POSITION_TEXTURE_COLOR_NORMAL.addElement(POSITION_ELEMENT);
		POSITION_TEXTURE_COLOR_NORMAL.addElement(TEXTURE_FLOAT_ELEMENT);
		POSITION_TEXTURE_COLOR_NORMAL.addElement(COLOR_ELEMENT);
		POSITION_TEXTURE_COLOR_NORMAL.addElement(NORMAL_ELEMENT);
		POSITION_TEXTURE_COLOR_NORMAL.addElement(PADDING_ELEMENT);
	}
}
