package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.util.List;
import org.lwjgl.opengl.GL11;

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
						GL11.glVertexPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
						GL11.glEnableClientState(32884);
						break;
					case UV:
						GLX.gl13ClientActiveTexture(GLX.textureUnit + l);
						GL11.glTexCoordPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
						GL11.glEnableClientState(32888);
						GLX.gl13ClientActiveTexture(GLX.textureUnit);
						break;
					case COLOR:
						GL11.glColorPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
						GL11.glEnableClientState(32886);
						break;
					case NORMAL:
						GL11.glNormalPointer(k, i, byteBuffer);
						GL11.glEnableClientState(32885);
				}
			}

			GL11.glDrawArrays(builder.getDrawMode(), 0, builder.getVertexCount());
			int m = 0;

			for (int n = list.size(); m < n; m++) {
				VertexFormatElement vertexFormatElement2 = (VertexFormatElement)list.get(m);
				VertexFormatElement.Type type2 = vertexFormatElement2.getType();
				int o = vertexFormatElement2.getIndex();
				switch (type2) {
					case POSITION:
						GL11.glDisableClientState(32884);
						break;
					case UV:
						GLX.gl13ClientActiveTexture(GLX.textureUnit + o);
						GL11.glDisableClientState(32888);
						GLX.gl13ClientActiveTexture(GLX.textureUnit);
						break;
					case COLOR:
						GL11.glDisableClientState(32886);
						GlStateManager.clearColor();
						break;
					case NORMAL:
						GL11.glDisableClientState(32885);
				}
			}
		}

		builder.reset();
	}
}
