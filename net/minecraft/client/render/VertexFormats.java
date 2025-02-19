package net.minecraft.client.render;

import com.google.common.collect.ImmutableMap;

public class VertexFormats {
	public static final VertexFormatElement POSITION_ELEMENT = new VertexFormatElement(0, VertexFormatElement.DataType.FLOAT, VertexFormatElement.Type.POSITION, 3);
	public static final VertexFormatElement COLOR_ELEMENT = new VertexFormatElement(0, VertexFormatElement.DataType.UBYTE, VertexFormatElement.Type.COLOR, 4);
	public static final VertexFormatElement TEXTURE_0_ELEMENT = new VertexFormatElement(0, VertexFormatElement.DataType.FLOAT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement OVERLAY_ELEMENT = new VertexFormatElement(1, VertexFormatElement.DataType.SHORT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement LIGHT_ELEMENT = new VertexFormatElement(2, VertexFormatElement.DataType.SHORT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement NORMAL_ELEMENT = new VertexFormatElement(0, VertexFormatElement.DataType.BYTE, VertexFormatElement.Type.NORMAL, 3);
	public static final VertexFormatElement PADDING_ELEMENT = new VertexFormatElement(0, VertexFormatElement.DataType.BYTE, VertexFormatElement.Type.PADDING, 1);
	public static final VertexFormatElement TEXTURE_ELEMENT = TEXTURE_0_ELEMENT;
	public static final VertexFormat BLIT_SCREEN = new VertexFormat(
		ImmutableMap.builder().put("Position", POSITION_ELEMENT).put("UV", TEXTURE_ELEMENT).put("Color", COLOR_ELEMENT).build()
	);
	public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT_NORMAL = new VertexFormat(
		ImmutableMap.builder()
			.put("Position", POSITION_ELEMENT)
			.put("Color", COLOR_ELEMENT)
			.put("UV0", TEXTURE_0_ELEMENT)
			.put("UV2", LIGHT_ELEMENT)
			.put("Normal", NORMAL_ELEMENT)
			.put("Padding", PADDING_ELEMENT)
			.build()
	);
	public static final VertexFormat POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL = new VertexFormat(
		ImmutableMap.builder()
			.put("Position", POSITION_ELEMENT)
			.put("Color", COLOR_ELEMENT)
			.put("UV0", TEXTURE_0_ELEMENT)
			.put("UV1", OVERLAY_ELEMENT)
			.put("UV2", LIGHT_ELEMENT)
			.put("Normal", NORMAL_ELEMENT)
			.put("Padding", PADDING_ELEMENT)
			.build()
	);
	public static final VertexFormat POSITION_TEXTURE_COLOR_LIGHT = new VertexFormat(
		ImmutableMap.builder().put("Position", POSITION_ELEMENT).put("UV0", TEXTURE_0_ELEMENT).put("Color", COLOR_ELEMENT).put("UV2", LIGHT_ELEMENT).build()
	);
	public static final VertexFormat POSITION = new VertexFormat(ImmutableMap.builder().put("Position", POSITION_ELEMENT).build());
	public static final VertexFormat POSITION_COLOR = new VertexFormat(
		ImmutableMap.builder().put("Position", POSITION_ELEMENT).put("Color", COLOR_ELEMENT).build()
	);
	public static final VertexFormat LINES = new VertexFormat(
		ImmutableMap.builder().put("Position", POSITION_ELEMENT).put("Color", COLOR_ELEMENT).put("Normal", NORMAL_ELEMENT).put("Padding", PADDING_ELEMENT).build()
	);
	public static final VertexFormat POSITION_COLOR_LIGHT = new VertexFormat(
		ImmutableMap.builder().put("Position", POSITION_ELEMENT).put("Color", COLOR_ELEMENT).put("UV2", LIGHT_ELEMENT).build()
	);
	public static final VertexFormat POSITION_TEXTURE = new VertexFormat(
		ImmutableMap.builder().put("Position", POSITION_ELEMENT).put("UV0", TEXTURE_0_ELEMENT).build()
	);
	public static final VertexFormat POSITION_COLOR_TEXTURE = new VertexFormat(
		ImmutableMap.builder().put("Position", POSITION_ELEMENT).put("Color", COLOR_ELEMENT).put("UV0", TEXTURE_0_ELEMENT).build()
	);
	public static final VertexFormat POSITION_TEXTURE_COLOR = new VertexFormat(
		ImmutableMap.builder().put("Position", POSITION_ELEMENT).put("UV0", TEXTURE_0_ELEMENT).put("Color", COLOR_ELEMENT).build()
	);
	public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT = new VertexFormat(
		ImmutableMap.builder().put("Position", POSITION_ELEMENT).put("Color", COLOR_ELEMENT).put("UV0", TEXTURE_0_ELEMENT).put("UV2", LIGHT_ELEMENT).build()
	);
	public static final VertexFormat POSITION_TEXTURE_LIGHT_COLOR = new VertexFormat(
		ImmutableMap.builder().put("Position", POSITION_ELEMENT).put("UV0", TEXTURE_0_ELEMENT).put("UV2", LIGHT_ELEMENT).put("Color", COLOR_ELEMENT).build()
	);
	public static final VertexFormat POSITION_TEXTURE_COLOR_NORMAL = new VertexFormat(
		ImmutableMap.builder()
			.put("Position", POSITION_ELEMENT)
			.put("UV0", TEXTURE_0_ELEMENT)
			.put("Color", COLOR_ELEMENT)
			.put("Normal", NORMAL_ELEMENT)
			.put("Padding", PADDING_ELEMENT)
			.build()
	);
}
