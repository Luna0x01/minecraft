package net.minecraft.client;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import net.minecraft.resource.Resource;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class class_2901 {
	public final int field_13651;
	public final int field_13652;

	public class_2901(Resource resource) throws IOException {
		MemoryStack memoryStack = MemoryStack.stackPush();
		Throwable var3 = null;

		try (class_2901.class_4281 lv = method_19501(resource.getInputStream())) {
			STBIReadCallback sTBIReadCallback = STBIReadCallback.create(lv::method_19505);
			Throwable var7 = null;

			try {
				STBISkipCallback sTBISkipCallback = STBISkipCallback.create(lv::method_19504);
				Throwable var9 = null;

				try {
					STBIEOFCallback sTBIEOFCallback = STBIEOFCallback.create(lv::method_19503);
					Throwable var11 = null;

					try {
						STBIIOCallbacks sTBIIOCallbacks = STBIIOCallbacks.mallocStack(memoryStack);
						sTBIIOCallbacks.read(sTBIReadCallback);
						sTBIIOCallbacks.skip(sTBISkipCallback);
						sTBIIOCallbacks.eof(sTBIEOFCallback);
						IntBuffer intBuffer = memoryStack.mallocInt(1);
						IntBuffer intBuffer2 = memoryStack.mallocInt(1);
						IntBuffer intBuffer3 = memoryStack.mallocInt(1);
						if (!STBImage.stbi_info_from_callbacks(sTBIIOCallbacks, 0L, intBuffer, intBuffer2, intBuffer3)) {
							throw new IOException("Could not read info from the PNG file " + resource + " " + STBImage.stbi_failure_reason());
						}

						this.field_13651 = intBuffer.get(0);
						this.field_13652 = intBuffer2.get(0);
					} catch (Throwable var121) {
						var11 = var121;
						throw var121;
					} finally {
						if (sTBIEOFCallback != null) {
							if (var11 != null) {
								try {
									sTBIEOFCallback.close();
								} catch (Throwable var120) {
									var11.addSuppressed(var120);
								}
							} else {
								sTBIEOFCallback.close();
							}
						}
					}
				} catch (Throwable var123) {
					var9 = var123;
					throw var123;
				} finally {
					if (sTBISkipCallback != null) {
						if (var9 != null) {
							try {
								sTBISkipCallback.close();
							} catch (Throwable var119) {
								var9.addSuppressed(var119);
							}
						} else {
							sTBISkipCallback.close();
						}
					}
				}
			} catch (Throwable var125) {
				var7 = var125;
				throw var125;
			} finally {
				if (sTBIReadCallback != null) {
					if (var7 != null) {
						try {
							sTBIReadCallback.close();
						} catch (Throwable var118) {
							var7.addSuppressed(var118);
						}
					} else {
						sTBIReadCallback.close();
					}
				}
			}
		} catch (Throwable var129) {
			var3 = var129;
			throw var129;
		} finally {
			if (memoryStack != null) {
				if (var3 != null) {
					try {
						memoryStack.close();
					} catch (Throwable var116) {
						var3.addSuppressed(var116);
					}
				} else {
					memoryStack.close();
				}
			}
		}
	}

	private static class_2901.class_4281 method_19501(InputStream inputStream) {
		return (class_2901.class_4281)(inputStream instanceof FileInputStream
			? new class_2901.class_4283(((FileInputStream)inputStream).getChannel())
			: new class_2901.class_4282(Channels.newChannel(inputStream)));
	}

	abstract static class class_4281 implements AutoCloseable {
		protected boolean field_21020;

		private class_4281() {
		}

		int method_19505(long l, long m, int i) {
			try {
				return this.method_19506(m, i);
			} catch (IOException var7) {
				this.field_21020 = true;
				return 0;
			}
		}

		void method_19504(long l, int i) {
			try {
				this.method_19502(i);
			} catch (IOException var5) {
				this.field_21020 = true;
			}
		}

		int method_19503(long l) {
			return this.field_21020 ? 1 : 0;
		}

		protected abstract int method_19506(long l, int i) throws IOException;

		protected abstract void method_19502(int i) throws IOException;

		public abstract void close() throws IOException;
	}

	static class class_4282 extends class_2901.class_4281 {
		private final ReadableByteChannel field_21021;
		private long field_21022 = MemoryUtil.nmemAlloc(128L);
		private int field_21023 = 128;
		private int field_21024;
		private int field_21025;

		private class_4282(ReadableByteChannel readableByteChannel) {
			this.field_21021 = readableByteChannel;
		}

		private void method_19507(int i) throws IOException {
			ByteBuffer byteBuffer = MemoryUtil.memByteBuffer(this.field_21022, this.field_21023);
			if (i + this.field_21025 > this.field_21023) {
				this.field_21023 = i + this.field_21025;
				byteBuffer = MemoryUtil.memRealloc(byteBuffer, this.field_21023);
				this.field_21022 = MemoryUtil.memAddress(byteBuffer);
			}

			byteBuffer.position(this.field_21024);

			while (i + this.field_21025 > this.field_21024) {
				try {
					int j = this.field_21021.read(byteBuffer);
					if (j == -1) {
						break;
					}
				} finally {
					this.field_21024 = byteBuffer.position();
				}
			}
		}

		@Override
		public int method_19506(long l, int i) throws IOException {
			this.method_19507(i);
			if (i + this.field_21025 > this.field_21024) {
				i = this.field_21024 - this.field_21025;
			}

			MemoryUtil.memCopy(this.field_21022 + (long)this.field_21025, l, (long)i);
			this.field_21025 += i;
			return i;
		}

		@Override
		public void method_19502(int i) throws IOException {
			if (i > 0) {
				this.method_19507(i);
				if (i + this.field_21025 > this.field_21024) {
					throw new EOFException("Can't skip past the EOF.");
				}
			}

			if (this.field_21025 + i < 0) {
				throw new IOException("Can't seek before the beginning: " + (this.field_21025 + i));
			} else {
				this.field_21025 += i;
			}
		}

		@Override
		public void close() throws IOException {
			MemoryUtil.nmemFree(this.field_21022);
			this.field_21021.close();
		}
	}

	static class class_4283 extends class_2901.class_4281 {
		private final SeekableByteChannel field_21026;

		private class_4283(SeekableByteChannel seekableByteChannel) {
			this.field_21026 = seekableByteChannel;
		}

		@Override
		public int method_19506(long l, int i) throws IOException {
			ByteBuffer byteBuffer = MemoryUtil.memByteBuffer(l, i);
			return this.field_21026.read(byteBuffer);
		}

		@Override
		public void method_19502(int i) throws IOException {
			this.field_21026.position(this.field_21026.position() + (long)i);
		}

		@Override
		public int method_19503(long l) {
			return super.method_19503(l) != 0 && this.field_21026.isOpen() ? 1 : 0;
		}

		@Override
		public void close() throws IOException {
			this.field_21026.close();
		}
	}
}
