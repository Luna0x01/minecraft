package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.util.List;

public class BufferRenderer {
	public void draw(BufferBuilder builder) {
		if (builder.getVertexCount() > 0) {
			VertexFormat vertexFormat = builder.getFormat();
			int i = vertexFormat.getVertexSize();
			ByteBuffer byteBuffer = builder.getByteBuffer();
			List<VertexFormatElement> list = vertexFormat.getElements();

			for (int j = 0; j < list.size(); j++) {
				VertexFormatElement vertexFormatElement = (VertexFormatElement)list.get(j);
				VertexFormatElement.Type type = vertexFormatElement.getType();
				int k = vertexFormatElement.getFormat().getGlId();
				int l = vertexFormatElement.getIndex();
				byteBuffer.position(vertexFormat.getIndex(j));
				switch (type) {
					case POSITION:
						GlStateManager.method_12296(vertexFormatElement.getCount(), k, i, byteBuffer);
						GlStateManager.method_12317(32884);
						break;
					case UV:
						GLX.gl13ClientActiveTexture(GLX.textureUnit + l);
						GlStateManager.method_12279(vertexFormatElement.getCount(), k, i, byteBuffer);
						GlStateManager.method_12317(32888);
						GLX.gl13ClientActiveTexture(GLX.textureUnit);
						break;
					case COLOR:
						GlStateManager.method_12303(vertexFormatElement.getCount(), k, i, byteBuffer);
						GlStateManager.method_12317(32886);
						break;
					case NORMAL:
						GlStateManager.method_12280(k, i, byteBuffer);
						GlStateManager.method_12317(32885);
				}
			}

			GlStateManager.method_12313(builder.getDrawMode(), 0, builder.getVertexCount());
			int m = 0;

			for (int n = list.size(); m < n; m++) {
				VertexFormatElement vertexFormatElement2 = (VertexFormatElement)list.get(m);
				VertexFormatElement.Type type2 = vertexFormatElement2.getType();
				int o = vertexFormatElement2.getIndex();
				switch (type2) {
					case POSITION:
						GlStateManager.method_12316(32884);
						break;
					case UV:
						GLX.gl13ClientActiveTexture(GLX.textureUnit + o);
						GlStateManager.method_12316(32888);
						GLX.gl13ClientActiveTexture(GLX.textureUnit);
						break;
					case COLOR:
						GlStateManager.method_12316(32886);
						GlStateManager.clearColor();
						break;
					case NORMAL:
						GlStateManager.method_12316(32885);
				}
			}
		}

		builder.reset();
	}
}
