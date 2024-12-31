package net.minecraft.client.util;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class PngFile {
	public final int width;
	public final int height;

	public PngFile(String string, InputStream inputStream) throws IOException {
		MemoryStack memoryStack = MemoryStack.stackPush();
		Throwable var4 = null;

		try (PngFile.Reader reader = createReader(inputStream)) {
			STBIReadCallback sTBIReadCallback = STBIReadCallback.create(reader::read);
			Throwable var8 = null;

			try {
				STBISkipCallback sTBISkipCallback = STBISkipCallback.create(reader::skip);
				Throwable var10 = null;

				try {
					STBIEOFCallback sTBIEOFCallback = STBIEOFCallback.create(reader::eof);
					Throwable var12 = null;

					try {
						STBIIOCallbacks sTBIIOCallbacks = STBIIOCallbacks.mallocStack(memoryStack);
						sTBIIOCallbacks.read(sTBIReadCallback);
						sTBIIOCallbacks.skip(sTBISkipCallback);
						sTBIIOCallbacks.eof(sTBIEOFCallback);
						IntBuffer intBuffer = memoryStack.mallocInt(1);
						IntBuffer intBuffer2 = memoryStack.mallocInt(1);
						IntBuffer intBuffer3 = memoryStack.mallocInt(1);
						if (!STBImage.stbi_info_from_callbacks(sTBIIOCallbacks, 0L, intBuffer, intBuffer2, intBuffer3)) {
							throw new IOException("Could not read info from the PNG file " + string + " " + STBImage.stbi_failure_reason());
						}

						this.width = intBuffer.get(0);
						this.height = intBuffer2.get(0);
					} catch (Throwable var122) {
						var12 = var122;
						throw var122;
					} finally {
						if (sTBIEOFCallback != null) {
							if (var12 != null) {
								try {
									sTBIEOFCallback.close();
								} catch (Throwable var121) {
									var12.addSuppressed(var121);
								}
							} else {
								sTBIEOFCallback.close();
							}
						}
					}
				} catch (Throwable var124) {
					var10 = var124;
					throw var124;
				} finally {
					if (sTBISkipCallback != null) {
						if (var10 != null) {
							try {
								sTBISkipCallback.close();
							} catch (Throwable var120) {
								var10.addSuppressed(var120);
							}
						} else {
							sTBISkipCallback.close();
						}
					}
				}
			} catch (Throwable var126) {
				var8 = var126;
				throw var126;
			} finally {
				if (sTBIReadCallback != null) {
					if (var8 != null) {
						try {
							sTBIReadCallback.close();
						} catch (Throwable var119) {
							var8.addSuppressed(var119);
						}
					} else {
						sTBIReadCallback.close();
					}
				}
			}
		} catch (Throwable var130) {
			var4 = var130;
			throw var130;
		} finally {
			if (memoryStack != null) {
				if (var4 != null) {
					try {
						memoryStack.close();
					} catch (Throwable var117) {
						var4.addSuppressed(var117);
					}
				} else {
					memoryStack.close();
				}
			}
		}
	}

	private static PngFile.Reader createReader(InputStream inputStream) {
		return (PngFile.Reader)(inputStream instanceof FileInputStream
			? new PngFile.SeekableChannelReader(((FileInputStream)inputStream).getChannel())
			: new PngFile.ChannelReader(Channels.newChannel(inputStream)));
	}

	static class ChannelReader extends PngFile.Reader {
		private final ReadableByteChannel channel;
		private long buffer = MemoryUtil.nmemAlloc(128L);
		private int bufferSize = 128;
		private int bufferPosition;
		private int readPosition;

		private ChannelReader(ReadableByteChannel readableByteChannel) {
			this.channel = readableByteChannel;
		}

		private void readToBuffer(int i) throws IOException {
			ByteBuffer byteBuffer = MemoryUtil.memByteBuffer(this.buffer, this.bufferSize);
			if (i + this.readPosition > this.bufferSize) {
				this.bufferSize = i + this.readPosition;
				byteBuffer = MemoryUtil.memRealloc(byteBuffer, this.bufferSize);
				this.buffer = MemoryUtil.memAddress(byteBuffer);
			}

			byteBuffer.position(this.bufferPosition);

			while (i + this.readPosition > this.bufferPosition) {
				try {
					int j = this.channel.read(byteBuffer);
					if (j == -1) {
						break;
					}
				} finally {
					this.bufferPosition = byteBuffer.position();
				}
			}
		}

		@Override
		public int read(long l, int i) throws IOException {
			this.readToBuffer(i);
			if (i + this.readPosition > this.bufferPosition) {
				i = this.bufferPosition - this.readPosition;
			}

			MemoryUtil.memCopy(this.buffer + (long)this.readPosition, l, (long)i);
			this.readPosition += i;
			return i;
		}

		@Override
		public void skip(int i) throws IOException {
			if (i > 0) {
				this.readToBuffer(i);
				if (i + this.readPosition > this.bufferPosition) {
					throw new EOFException("Can't skip past the EOF.");
				}
			}

			if (this.readPosition + i < 0) {
				throw new IOException("Can't seek before the beginning: " + (this.readPosition + i));
			} else {
				this.readPosition += i;
			}
		}

		@Override
		public void close() throws IOException {
			MemoryUtil.nmemFree(this.buffer);
			this.channel.close();
		}
	}

	abstract static class Reader implements AutoCloseable {
		protected boolean errored;

		private Reader() {
		}

		int read(long l, long m, int i) {
			try {
				return this.read(m, i);
			} catch (IOException var7) {
				this.errored = true;
				return 0;
			}
		}

		void skip(long l, int i) {
			try {
				this.skip(i);
			} catch (IOException var5) {
				this.errored = true;
			}
		}

		int eof(long l) {
			return this.errored ? 1 : 0;
		}

		protected abstract int read(long l, int i) throws IOException;

		protected abstract void skip(int i) throws IOException;

		public abstract void close() throws IOException;
	}

	static class SeekableChannelReader extends PngFile.Reader {
		private final SeekableByteChannel channel;

		private SeekableChannelReader(SeekableByteChannel seekableByteChannel) {
			this.channel = seekableByteChannel;
		}

		@Override
		public int read(long l, int i) throws IOException {
			ByteBuffer byteBuffer = MemoryUtil.memByteBuffer(l, i);
			return this.channel.read(byteBuffer);
		}

		@Override
		public void skip(int i) throws IOException {
			this.channel.position(this.channel.position() + (long)i);
		}

		@Override
		public int eof(long l) {
			return super.eof(l) != 0 && this.channel.isOpen() ? 1 : 0;
		}

		@Override
		public void close() throws IOException {
			this.channel.close();
		}
	}
}
