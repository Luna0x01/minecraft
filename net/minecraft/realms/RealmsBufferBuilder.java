package net.minecraft.realms;

import java.nio.ByteBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;

public class RealmsBufferBuilder {
	private BufferBuilder bufferBuilder;

	public RealmsBufferBuilder(BufferBuilder bufferBuilder) {
		this.bufferBuilder = bufferBuilder;
	}

	public RealmsBufferBuilder from(BufferBuilder bufferBuilder) {
		this.bufferBuilder = bufferBuilder;
		return this;
	}

	public void sortQuads(float f, float g, float h) {
		this.bufferBuilder.sortQuads(f, g, h);
	}

	public void fixupQuadColor(int i) {
		this.bufferBuilder.putQuadColor(i);
	}

	public ByteBuffer getBuffer() {
		return this.bufferBuilder.getByteBuffer();
	}

	public void postNormal(float f, float g, float h) {
		this.bufferBuilder.putNormal(f, g, h);
	}

	public int getDrawMode() {
		return this.bufferBuilder.getDrawMode();
	}

	public void offset(double d, double e, double f) {
		this.bufferBuilder.offset(d, e, f);
	}

	public void restoreState(BufferBuilder.DrawArrayParameters drawArrayParameters) {
		this.bufferBuilder.restoreState(drawArrayParameters);
	}

	public void endVertex() {
		this.bufferBuilder.next();
	}

	public RealmsBufferBuilder normal(float f, float g, float h) {
		return this.from(this.bufferBuilder.normal(f, g, h));
	}

	public void end() {
		this.bufferBuilder.end();
	}

	public void begin(int i, VertexFormat vertexFormat) {
		this.bufferBuilder.begin(i, vertexFormat);
	}

	public RealmsBufferBuilder color(int i, int j, int k, int l) {
		return this.from(this.bufferBuilder.color(i, j, k, l));
	}

	public void faceTex2(int i, int j, int k, int l) {
		this.bufferBuilder.faceTexture2(i, j, k, l);
	}

	public void postProcessFacePosition(double d, double e, double f) {
		this.bufferBuilder.postProcessFacePosition(d, e, f);
	}

	public void fixupVertexColor(float f, float g, float h, int i) {
		this.bufferBuilder.putColor(f, g, h, i);
	}

	public RealmsBufferBuilder color(float f, float g, float h, float i) {
		return this.from(this.bufferBuilder.color(f, g, h, i));
	}

	public RealmsVertexFormat getVertexFormat() {
		return new RealmsVertexFormat(this.bufferBuilder.getFormat());
	}

	public void faceTint(float f, float g, float h, int i) {
		this.bufferBuilder.faceTint(f, g, h, i);
	}

	public RealmsBufferBuilder tex2(int i, int j) {
		return this.from(this.bufferBuilder.texture2(i, j));
	}

	public void putBulkData(int[] is) {
		this.bufferBuilder.putArray(is);
	}

	public RealmsBufferBuilder tex(double d, double e) {
		return this.from(this.bufferBuilder.texture(d, e));
	}

	public int getVertexCount() {
		return this.bufferBuilder.getVertexCount();
	}

	public void clear() {
		this.bufferBuilder.reset();
	}

	public RealmsBufferBuilder vertex(double d, double e, double f) {
		return this.from(this.bufferBuilder.vertex(d, e, f));
	}

	public void fixupQuadColor(float f, float g, float h) {
		this.bufferBuilder.putQuadColor(f, g, h);
	}

	public void noColor() {
		this.bufferBuilder.enableTexture();
	}
}
