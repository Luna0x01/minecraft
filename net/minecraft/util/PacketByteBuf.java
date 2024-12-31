package net.minecraft.util;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufProcessor;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class PacketByteBuf extends ByteBuf {
	private final ByteBuf parent;

	public PacketByteBuf(ByteBuf byteBuf) {
		this.parent = byteBuf;
	}

	public static int getVarIntSizeBytes(int size) {
		for (int i = 1; i < 5; i++) {
			if ((size & -1 << i * 7) == 0) {
				return i;
			}
		}

		return 5;
	}

	public PacketByteBuf writeByteArray(byte[] bs) {
		this.writeVarInt(bs.length);
		this.writeBytes(bs);
		return this;
	}

	public byte[] readByteArray() {
		return this.readByteArray(this.readableBytes());
	}

	public byte[] readByteArray(int size) {
		int i = this.readVarInt();
		if (i > size) {
			throw new DecoderException("ByteArray with size " + i + " is bigger than allowed " + size);
		} else {
			byte[] bs = new byte[i];
			this.readBytes(bs);
			return bs;
		}
	}

	public PacketByteBuf writeIntArray(int[] intArray) {
		this.writeVarInt(intArray.length);

		for (int i = 0; i < intArray.length; i++) {
			this.writeVarInt(intArray[i]);
		}

		return this;
	}

	public int[] readIntArray() {
		return this.readIntArray(this.readableBytes());
	}

	public int[] readIntArray(int size) {
		int i = this.readVarInt();
		if (i > size) {
			throw new DecoderException("VarIntArray with size " + i + " is bigger than allowed " + size);
		} else {
			int[] is = new int[i];

			for (int j = 0; j < is.length; j++) {
				is[j] = this.readVarInt();
			}

			return is;
		}
	}

	public PacketByteBuf writeLongArray(long[] ls) {
		this.writeVarInt(ls.length);

		for (int i = 0; i < ls.length; i++) {
			this.writeLong(ls[i]);
		}

		return this;
	}

	public long[] readLongArray(@Nullable long[] dest) {
		return this.readLongArray(dest, this.readableBytes() / 8);
	}

	public long[] readLongArray(@Nullable long[] dest, int size) {
		int i = this.readVarInt();
		if (dest == null || dest.length != i) {
			if (i > size) {
				throw new DecoderException("LongArray with size " + i + " is bigger than allowed " + size);
			}

			dest = new long[i];
		}

		for (int j = 0; j < dest.length; j++) {
			dest[j] = this.readLong();
		}

		return dest;
	}

	public BlockPos readBlockPos() {
		return BlockPos.fromLong(this.readLong());
	}

	public PacketByteBuf writeBlockPos(BlockPos pos) {
		this.writeLong(pos.asLong());
		return this;
	}

	public Text readText() {
		return Text.Serializer.deserializeText(this.readString(32767));
	}

	public PacketByteBuf writeText(Text text) {
		return this.writeString(Text.Serializer.serialize(text));
	}

	public <T extends Enum<T>> T readEnumConstant(Class<T> instance) {
		return (T)instance.getEnumConstants()[this.readVarInt()];
	}

	public PacketByteBuf writeEnumConstant(Enum<?> constant) {
		return this.writeVarInt(constant.ordinal());
	}

	public int readVarInt() {
		int i = 0;
		int j = 0;

		byte b;
		do {
			b = this.readByte();
			i |= (b & 127) << j++ * 7;
			if (j > 5) {
				throw new RuntimeException("VarInt too big");
			}
		} while ((b & 128) == 128);

		return i;
	}

	public long readVarLong() {
		long l = 0L;
		int i = 0;

		byte b;
		do {
			b = this.readByte();
			l |= (long)(b & 127) << i++ * 7;
			if (i > 10) {
				throw new RuntimeException("VarLong too big");
			}
		} while ((b & 128) == 128);

		return l;
	}

	public PacketByteBuf writeUuid(UUID uuid) {
		this.writeLong(uuid.getMostSignificantBits());
		this.writeLong(uuid.getLeastSignificantBits());
		return this;
	}

	public UUID readUuid() {
		return new UUID(this.readLong(), this.readLong());
	}

	public PacketByteBuf writeVarInt(int integer) {
		while ((integer & -128) != 0) {
			this.writeByte(integer & 127 | 128);
			integer >>>= 7;
		}

		this.writeByte(integer);
		return this;
	}

	public PacketByteBuf method_10608(long l) {
		while ((l & -128L) != 0L) {
			this.writeByte((int)(l & 127L) | 128);
			l >>>= 7;
		}

		this.writeByte((int)l);
		return this;
	}

	public PacketByteBuf writeNbtCompound(@Nullable NbtCompound nbt) {
		if (nbt == null) {
			this.writeByte(0);
		} else {
			try {
				NbtIo.write(nbt, new ByteBufOutputStream(this));
			} catch (IOException var3) {
				throw new EncoderException(var3);
			}
		}

		return this;
	}

	@Nullable
	public NbtCompound readNbtCompound() {
		int i = this.readerIndex();
		byte b = this.readByte();
		if (b == 0) {
			return null;
		} else {
			this.readerIndex(i);

			try {
				return NbtIo.read(new ByteBufInputStream(this), new PositionTracker(2097152L));
			} catch (IOException var4) {
				throw new EncoderException(var4);
			}
		}
	}

	public PacketByteBuf writeItemStack(@Nullable ItemStack stack) {
		if (stack == null) {
			this.writeShort(-1);
		} else {
			this.writeShort(Item.getRawId(stack.getItem()));
			this.writeByte(stack.count);
			this.writeShort(stack.getData());
			NbtCompound nbtCompound = null;
			if (stack.getItem().isDamageable() || stack.getItem().shouldSyncNbtToClient()) {
				nbtCompound = stack.getNbt();
			}

			this.writeNbtCompound(nbtCompound);
		}

		return this;
	}

	@Nullable
	public ItemStack readItemStack() {
		ItemStack itemStack = null;
		int i = this.readShort();
		if (i >= 0) {
			int j = this.readByte();
			int k = this.readShort();
			itemStack = new ItemStack(Item.byRawId(i), j, k);
			itemStack.setNbt(this.readNbtCompound());
		}

		return itemStack;
	}

	public String readString(int maxLength) {
		int i = this.readVarInt();
		if (i > maxLength * 4) {
			throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
		} else if (i < 0) {
			throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
		} else {
			String string = new String(this.readBytes(i).array(), Charsets.UTF_8);
			if (string.length() > maxLength) {
				throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
			} else {
				return string;
			}
		}
	}

	public PacketByteBuf writeString(String string) {
		byte[] bs = string.getBytes(Charsets.UTF_8);
		if (bs.length > 32767) {
			throw new EncoderException("String too big (was " + string.length() + " bytes encoded, max " + 32767 + ")");
		} else {
			this.writeVarInt(bs.length);
			this.writeBytes(bs);
			return this;
		}
	}

	public int capacity() {
		return this.parent.capacity();
	}

	public ByteBuf capacity(int capacity) {
		return this.parent.capacity(capacity);
	}

	public int maxCapacity() {
		return this.parent.maxCapacity();
	}

	public ByteBufAllocator alloc() {
		return this.parent.alloc();
	}

	public ByteOrder order() {
		return this.parent.order();
	}

	public ByteBuf order(ByteOrder byteOrder) {
		return this.parent.order(byteOrder);
	}

	public ByteBuf unwrap() {
		return this.parent.unwrap();
	}

	public boolean isDirect() {
		return this.parent.isDirect();
	}

	public int readerIndex() {
		return this.parent.readerIndex();
	}

	public ByteBuf readerIndex(int index) {
		return this.parent.readerIndex(index);
	}

	public int writerIndex() {
		return this.parent.writerIndex();
	}

	public ByteBuf writerIndex(int index) {
		return this.parent.writerIndex(index);
	}

	public ByteBuf setIndex(int readerIndex, int writerIndex) {
		return this.parent.setIndex(readerIndex, writerIndex);
	}

	public int readableBytes() {
		return this.parent.readableBytes();
	}

	public int writableBytes() {
		return this.parent.writableBytes();
	}

	public int maxWritableBytes() {
		return this.parent.maxWritableBytes();
	}

	public boolean isReadable() {
		return this.parent.isReadable();
	}

	public boolean isReadable(int size) {
		return this.parent.isReadable(size);
	}

	public boolean isWritable() {
		return this.parent.isWritable();
	}

	public boolean isWritable(int size) {
		return this.parent.isWritable(size);
	}

	public ByteBuf clear() {
		return this.parent.clear();
	}

	public ByteBuf markReaderIndex() {
		return this.parent.markReaderIndex();
	}

	public ByteBuf resetReaderIndex() {
		return this.parent.resetReaderIndex();
	}

	public ByteBuf markWriterIndex() {
		return this.parent.markWriterIndex();
	}

	public ByteBuf resetWriterIndex() {
		return this.parent.resetWriterIndex();
	}

	public ByteBuf discardReadBytes() {
		return this.parent.discardReadBytes();
	}

	public ByteBuf discardSomeReadBytes() {
		return this.parent.discardSomeReadBytes();
	}

	public ByteBuf ensureWritable(int minBytes) {
		return this.parent.ensureWritable(minBytes);
	}

	public int ensureWritable(int minBytes, boolean force) {
		return this.parent.ensureWritable(minBytes, force);
	}

	public boolean getBoolean(int index) {
		return this.parent.getBoolean(index);
	}

	public byte getByte(int index) {
		return this.parent.getByte(index);
	}

	public short getUnsignedByte(int index) {
		return this.parent.getUnsignedByte(index);
	}

	public short getShort(int index) {
		return this.parent.getShort(index);
	}

	public int getUnsignedShort(int index) {
		return this.parent.getUnsignedShort(index);
	}

	public int getMedium(int index) {
		return this.parent.getMedium(index);
	}

	public int getUnsignedMedium(int index) {
		return this.parent.getUnsignedMedium(index);
	}

	public int getInt(int index) {
		return this.parent.getInt(index);
	}

	public long getUnsignedInt(int index) {
		return this.parent.getUnsignedInt(index);
	}

	public long getLong(int index) {
		return this.parent.getLong(index);
	}

	public char getChar(int index) {
		return this.parent.getChar(index);
	}

	public float getFloat(int index) {
		return this.parent.getFloat(index);
	}

	public double getDouble(int index) {
		return this.parent.getDouble(index);
	}

	public ByteBuf getBytes(int index, ByteBuf buf) {
		return this.parent.getBytes(index, buf);
	}

	public ByteBuf getBytes(int index, ByteBuf buf, int length) {
		return this.parent.getBytes(index, buf, length);
	}

	public ByteBuf getBytes(int index, ByteBuf buf, int outputIndex, int length) {
		return this.parent.getBytes(index, buf, outputIndex, length);
	}

	public ByteBuf getBytes(int index, byte[] bytes) {
		return this.parent.getBytes(index, bytes);
	}

	public ByteBuf getBytes(int index, byte[] bytes, int outputIndex, int length) {
		return this.parent.getBytes(index, bytes, outputIndex, length);
	}

	public ByteBuf getBytes(int index, ByteBuffer buf) {
		return this.parent.getBytes(index, buf);
	}

	public ByteBuf getBytes(int index, OutputStream stream, int length) throws IOException {
		return this.parent.getBytes(index, stream, length);
	}

	public int getBytes(int index, GatheringByteChannel channel, int length) throws IOException {
		return this.parent.getBytes(index, channel, length);
	}

	public ByteBuf setBoolean(int index, boolean value) {
		return this.parent.setBoolean(index, value);
	}

	public ByteBuf setByte(int index, int value) {
		return this.parent.setByte(index, value);
	}

	public ByteBuf setShort(int index, int value) {
		return this.parent.setShort(index, value);
	}

	public ByteBuf setMedium(int index, int value) {
		return this.parent.setMedium(index, value);
	}

	public ByteBuf setInt(int index, int value) {
		return this.parent.setInt(index, value);
	}

	public ByteBuf setLong(int index, long value) {
		return this.parent.setLong(index, value);
	}

	public ByteBuf setChar(int index, int value) {
		return this.parent.setChar(index, value);
	}

	public ByteBuf setFloat(int index, float value) {
		return this.parent.setFloat(index, value);
	}

	public ByteBuf setDouble(int index, double value) {
		return this.parent.setDouble(index, value);
	}

	public ByteBuf setBytes(int index, ByteBuf buf) {
		return this.parent.setBytes(index, buf);
	}

	public ByteBuf setBytes(int index, ByteBuf buf, int length) {
		return this.parent.setBytes(index, buf, length);
	}

	public ByteBuf setBytes(int index, ByteBuf buf, int sourceIndex, int length) {
		return this.parent.setBytes(index, buf, sourceIndex, length);
	}

	public ByteBuf setBytes(int index, byte[] bytes) {
		return this.parent.setBytes(index, bytes);
	}

	public ByteBuf setBytes(int index, byte[] bytes, int sourceIndex, int length) {
		return this.parent.setBytes(index, bytes, sourceIndex, length);
	}

	public ByteBuf setBytes(int index, ByteBuffer buf) {
		return this.parent.setBytes(index, buf);
	}

	public int setBytes(int index, InputStream stream, int length) throws IOException {
		return this.parent.setBytes(index, stream, length);
	}

	public int setBytes(int index, ScatteringByteChannel channel, int length) throws IOException {
		return this.parent.setBytes(index, channel, length);
	}

	public ByteBuf setZero(int index, int length) {
		return this.parent.setZero(index, length);
	}

	public boolean readBoolean() {
		return this.parent.readBoolean();
	}

	public byte readByte() {
		return this.parent.readByte();
	}

	public short readUnsignedByte() {
		return this.parent.readUnsignedByte();
	}

	public short readShort() {
		return this.parent.readShort();
	}

	public int readUnsignedShort() {
		return this.parent.readUnsignedShort();
	}

	public int readMedium() {
		return this.parent.readMedium();
	}

	public int readUnsignedMedium() {
		return this.parent.readUnsignedMedium();
	}

	public int readInt() {
		return this.parent.readInt();
	}

	public long readUnsignedInt() {
		return this.parent.readUnsignedInt();
	}

	public long readLong() {
		return this.parent.readLong();
	}

	public char readChar() {
		return this.parent.readChar();
	}

	public float readFloat() {
		return this.parent.readFloat();
	}

	public double readDouble() {
		return this.parent.readDouble();
	}

	public ByteBuf readBytes(int length) {
		return this.parent.readBytes(length);
	}

	public ByteBuf readSlice(int length) {
		return this.parent.readSlice(length);
	}

	public ByteBuf readBytes(ByteBuf buf) {
		return this.parent.readBytes(buf);
	}

	public ByteBuf readBytes(ByteBuf buf, int length) {
		return this.parent.readBytes(buf, length);
	}

	public ByteBuf readBytes(ByteBuf buf, int outputIndex, int length) {
		return this.parent.readBytes(buf, outputIndex, length);
	}

	public ByteBuf readBytes(byte[] bytes) {
		return this.parent.readBytes(bytes);
	}

	public ByteBuf readBytes(byte[] bytes, int outputIndex, int length) {
		return this.parent.readBytes(bytes, outputIndex, length);
	}

	public ByteBuf readBytes(ByteBuffer buf) {
		return this.parent.readBytes(buf);
	}

	public ByteBuf readBytes(OutputStream stream, int length) throws IOException {
		return this.parent.readBytes(stream, length);
	}

	public int readBytes(GatheringByteChannel cannel, int length) throws IOException {
		return this.parent.readBytes(cannel, length);
	}

	public ByteBuf skipBytes(int length) {
		return this.parent.skipBytes(length);
	}

	public ByteBuf writeBoolean(boolean value) {
		return this.parent.writeBoolean(value);
	}

	public ByteBuf writeByte(int value) {
		return this.parent.writeByte(value);
	}

	public ByteBuf writeShort(int value) {
		return this.parent.writeShort(value);
	}

	public ByteBuf writeMedium(int value) {
		return this.parent.writeMedium(value);
	}

	public ByteBuf writeInt(int value) {
		return this.parent.writeInt(value);
	}

	public ByteBuf writeLong(long value) {
		return this.parent.writeLong(value);
	}

	public ByteBuf writeChar(int value) {
		return this.parent.writeChar(value);
	}

	public ByteBuf writeFloat(float value) {
		return this.parent.writeFloat(value);
	}

	public ByteBuf writeDouble(double value) {
		return this.parent.writeDouble(value);
	}

	public ByteBuf writeBytes(ByteBuf buf) {
		return this.parent.writeBytes(buf);
	}

	public ByteBuf writeBytes(ByteBuf buf, int length) {
		return this.parent.writeBytes(buf, length);
	}

	public ByteBuf writeBytes(ByteBuf buf, int sourceIndex, int length) {
		return this.parent.writeBytes(buf, sourceIndex, length);
	}

	public ByteBuf writeBytes(byte[] bytes) {
		return this.parent.writeBytes(bytes);
	}

	public ByteBuf writeBytes(byte[] bytes, int sourceIndex, int length) {
		return this.parent.writeBytes(bytes, sourceIndex, length);
	}

	public ByteBuf writeBytes(ByteBuffer buf) {
		return this.parent.writeBytes(buf);
	}

	public int writeBytes(InputStream stream, int length) throws IOException {
		return this.parent.writeBytes(stream, length);
	}

	public int writeBytes(ScatteringByteChannel channel, int length) throws IOException {
		return this.parent.writeBytes(channel, length);
	}

	public ByteBuf writeZero(int length) {
		return this.parent.writeZero(length);
	}

	public int indexOf(int start, int end, byte value) {
		return this.parent.indexOf(start, end, value);
	}

	public int bytesBefore(byte value) {
		return this.parent.bytesBefore(value);
	}

	public int bytesBefore(int index, byte value) {
		return this.parent.bytesBefore(index, value);
	}

	public int bytesBefore(int index, int length, byte value) {
		return this.parent.bytesBefore(index, length, value);
	}

	public int forEachByte(ByteBufProcessor byteBufProcessor) {
		return this.parent.forEachByte(byteBufProcessor);
	}

	public int forEachByte(int index, int length, ByteBufProcessor byteBufProcessor) {
		return this.parent.forEachByte(index, length, byteBufProcessor);
	}

	public int forEachByteDesc(ByteBufProcessor byteBufProcessor) {
		return this.parent.forEachByteDesc(byteBufProcessor);
	}

	public int forEachByteDesc(int index, int length, ByteBufProcessor byteBufProcessor) {
		return this.parent.forEachByteDesc(index, length, byteBufProcessor);
	}

	public ByteBuf copy() {
		return this.parent.copy();
	}

	public ByteBuf copy(int index, int length) {
		return this.parent.copy(index, length);
	}

	public ByteBuf slice() {
		return this.parent.slice();
	}

	public ByteBuf slice(int index, int length) {
		return this.parent.slice(index, length);
	}

	public ByteBuf duplicate() {
		return this.parent.duplicate();
	}

	public int nioBufferCount() {
		return this.parent.nioBufferCount();
	}

	public ByteBuffer nioBuffer() {
		return this.parent.nioBuffer();
	}

	public ByteBuffer nioBuffer(int index, int length) {
		return this.parent.nioBuffer(index, length);
	}

	public ByteBuffer internalNioBuffer(int index, int length) {
		return this.parent.internalNioBuffer(index, length);
	}

	public ByteBuffer[] nioBuffers() {
		return this.parent.nioBuffers();
	}

	public ByteBuffer[] nioBuffers(int index, int length) {
		return this.parent.nioBuffers(index, length);
	}

	public boolean hasArray() {
		return this.parent.hasArray();
	}

	public byte[] array() {
		return this.parent.array();
	}

	public int arrayOffset() {
		return this.parent.arrayOffset();
	}

	public boolean hasMemoryAddress() {
		return this.parent.hasMemoryAddress();
	}

	public long memoryAddress() {
		return this.parent.memoryAddress();
	}

	public String toString(Charset charset) {
		return this.parent.toString(charset);
	}

	public String toString(int index, int length, Charset charset) {
		return this.parent.toString(index, length, charset);
	}

	public int hashCode() {
		return this.parent.hashCode();
	}

	public boolean equals(Object object) {
		return this.parent.equals(object);
	}

	public int compareTo(ByteBuf byteBuf) {
		return this.parent.compareTo(byteBuf);
	}

	public String toString() {
		return this.parent.toString();
	}

	public ByteBuf retain(int i) {
		return this.parent.retain(i);
	}

	public ByteBuf retain() {
		return this.parent.retain();
	}

	public int refCnt() {
		return this.parent.refCnt();
	}

	public boolean release() {
		return this.parent.release();
	}

	public boolean release(int decrement) {
		return this.parent.release(decrement);
	}
}
