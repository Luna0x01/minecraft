package net.minecraft.network;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PacketByteBuf extends ByteBuf {
	private static final int MAX_VAR_INT_LENGTH = 5;
	private static final int MAX_VAR_LONG_LENGTH = 10;
	private static final int MAX_READ_NBT_SIZE = 2097152;
	private final ByteBuf parent;
	public static final short DEFAULT_MAX_STRING_LENGTH = 32767;
	public static final int MAX_TEXT_LENGTH = 262144;

	public PacketByteBuf(ByteBuf parent) {
		this.parent = parent;
	}

	public static int getVarIntLength(int value) {
		for (int i = 1; i < 5; i++) {
			if ((value & -1 << i * 7) == 0) {
				return i;
			}
		}

		return 5;
	}

	public static int getVarLongLength(long value) {
		for (int i = 1; i < 10; i++) {
			if ((value & -1L << i * 7) == 0L) {
				return i;
			}
		}

		return 10;
	}

	public <T> T decode(Codec<T> codec) {
		NbtCompound nbtCompound = this.readUnlimitedNbt();
		DataResult<T> dataResult = codec.parse(NbtOps.INSTANCE, nbtCompound);
		dataResult.error().ifPresent(partialResult -> {
			throw new EncoderException("Failed to decode: " + partialResult.message() + " " + nbtCompound);
		});
		return (T)dataResult.result().get();
	}

	public <T> void encode(Codec<T> codec, T object) {
		DataResult<NbtElement> dataResult = codec.encodeStart(NbtOps.INSTANCE, object);
		dataResult.error().ifPresent(partialResult -> {
			throw new EncoderException("Failed to encode: " + partialResult.message() + " " + object);
		});
		this.writeNbt((NbtCompound)dataResult.result().get());
	}

	public static <T> IntFunction<T> getMaxValidator(IntFunction<T> applier, int max) {
		return value -> {
			if (value > max) {
				throw new DecoderException("Value " + value + " is larger than limit " + max);
			} else {
				return applier.apply(value);
			}
		};
	}

	public <T, C extends Collection<T>> C readCollection(IntFunction<C> collectionFactory, Function<PacketByteBuf, T> entryParser) {
		int i = this.readVarInt();
		C collection = (C)collectionFactory.apply(i);

		for (int j = 0; j < i; j++) {
			collection.add(entryParser.apply(this));
		}

		return collection;
	}

	public <T> void writeCollection(Collection<T> collection, BiConsumer<PacketByteBuf, T> entrySerializer) {
		this.writeVarInt(collection.size());

		for (T object : collection) {
			entrySerializer.accept(this, object);
		}
	}

	public <T> List<T> readList(Function<PacketByteBuf, T> entryParser) {
		return this.readCollection(Lists::newArrayListWithCapacity, entryParser);
	}

	public IntList readIntList() {
		int i = this.readVarInt();
		IntList intList = new IntArrayList();

		for (int j = 0; j < i; j++) {
			intList.add(this.readVarInt());
		}

		return intList;
	}

	public void writeIntList(IntList list) {
		this.writeVarInt(list.size());
		list.forEach(this::writeVarInt);
	}

	public <K, V, M extends Map<K, V>> M readMap(IntFunction<M> mapFactory, Function<PacketByteBuf, K> keyParser, Function<PacketByteBuf, V> valueParser) {
		int i = this.readVarInt();
		M map = (M)mapFactory.apply(i);

		for (int j = 0; j < i; j++) {
			K object = (K)keyParser.apply(this);
			V object2 = (V)valueParser.apply(this);
			map.put(object, object2);
		}

		return map;
	}

	public <K, V> Map<K, V> readMap(Function<PacketByteBuf, K> keyParser, Function<PacketByteBuf, V> valueParser) {
		return this.readMap(Maps::newHashMapWithExpectedSize, keyParser, valueParser);
	}

	public <K, V> void writeMap(Map<K, V> map, BiConsumer<PacketByteBuf, K> keySerializer, BiConsumer<PacketByteBuf, V> valueSerializer) {
		this.writeVarInt(map.size());
		map.forEach((key, value) -> {
			keySerializer.accept(this, key);
			valueSerializer.accept(this, value);
		});
	}

	public void forEachInCollection(Consumer<PacketByteBuf> consumer) {
		int i = this.readVarInt();

		for (int j = 0; j < i; j++) {
			consumer.accept(this);
		}
	}

	public <T> void writeOptional(Optional<T> value, BiConsumer<PacketByteBuf, T> serializer) {
		if (value.isPresent()) {
			this.writeBoolean(true);
			serializer.accept(this, value.get());
		} else {
			this.writeBoolean(false);
		}
	}

	public <T> Optional<T> readOptional(Function<PacketByteBuf, T> parser) {
		return this.readBoolean() ? Optional.of(parser.apply(this)) : Optional.empty();
	}

	public byte[] readByteArray() {
		return this.readByteArray(this.readableBytes());
	}

	public PacketByteBuf writeByteArray(byte[] array) {
		this.writeVarInt(array.length);
		this.writeBytes(array);
		return this;
	}

	public byte[] readByteArray(int maxSize) {
		int i = this.readVarInt();
		if (i > maxSize) {
			throw new DecoderException("ByteArray with size " + i + " is bigger than allowed " + maxSize);
		} else {
			byte[] bs = new byte[i];
			this.readBytes(bs);
			return bs;
		}
	}

	public PacketByteBuf writeIntArray(int[] array) {
		this.writeVarInt(array.length);

		for (int i : array) {
			this.writeVarInt(i);
		}

		return this;
	}

	public int[] readIntArray() {
		return this.readIntArray(this.readableBytes());
	}

	public int[] readIntArray(int maxSize) {
		int i = this.readVarInt();
		if (i > maxSize) {
			throw new DecoderException("VarIntArray with size " + i + " is bigger than allowed " + maxSize);
		} else {
			int[] is = new int[i];

			for (int j = 0; j < is.length; j++) {
				is[j] = this.readVarInt();
			}

			return is;
		}
	}

	public PacketByteBuf writeLongArray(long[] array) {
		this.writeVarInt(array.length);

		for (long l : array) {
			this.writeLong(l);
		}

		return this;
	}

	public long[] readLongArray() {
		return this.readLongArray(null);
	}

	public long[] readLongArray(@Nullable long[] toArray) {
		return this.readLongArray(toArray, this.readableBytes() / 8);
	}

	public long[] readLongArray(@Nullable long[] toArray, int maxSize) {
		int i = this.readVarInt();
		if (toArray == null || toArray.length != i) {
			if (i > maxSize) {
				throw new DecoderException("LongArray with size " + i + " is bigger than allowed " + maxSize);
			}

			toArray = new long[i];
		}

		for (int j = 0; j < toArray.length; j++) {
			toArray[j] = this.readLong();
		}

		return toArray;
	}

	@VisibleForTesting
	public byte[] getWrittenBytes() {
		int i = this.writerIndex();
		byte[] bs = new byte[i];
		this.getBytes(0, bs);
		return bs;
	}

	public BlockPos readBlockPos() {
		return BlockPos.fromLong(this.readLong());
	}

	public PacketByteBuf writeBlockPos(BlockPos pos) {
		this.writeLong(pos.asLong());
		return this;
	}

	public ChunkPos readChunkPos() {
		return new ChunkPos(this.readLong());
	}

	public PacketByteBuf writeChunkPos(ChunkPos pos) {
		this.writeLong(pos.toLong());
		return this;
	}

	public ChunkSectionPos readChunkSectionPos() {
		return ChunkSectionPos.from(this.readLong());
	}

	public PacketByteBuf writeChunkSectionPos(ChunkSectionPos pos) {
		this.writeLong(pos.asLong());
		return this;
	}

	public Text readText() {
		return Text.Serializer.fromJson(this.readString(262144));
	}

	public PacketByteBuf writeText(Text text) {
		return this.writeString(Text.Serializer.toJson(text), 262144);
	}

	public <T extends Enum<T>> T readEnumConstant(Class<T> enumClass) {
		return (T)enumClass.getEnumConstants()[this.readVarInt()];
	}

	public PacketByteBuf writeEnumConstant(Enum<?> instance) {
		return this.writeVarInt(instance.ordinal());
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

	public PacketByteBuf writeVarInt(int value) {
		while ((value & -128) != 0) {
			this.writeByte(value & 127 | 128);
			value >>>= 7;
		}

		this.writeByte(value);
		return this;
	}

	public PacketByteBuf writeVarLong(long value) {
		while ((value & -128L) != 0L) {
			this.writeByte((int)(value & 127L) | 128);
			value >>>= 7;
		}

		this.writeByte((int)value);
		return this;
	}

	public PacketByteBuf writeNbt(@Nullable NbtCompound compound) {
		if (compound == null) {
			this.writeByte(0);
		} else {
			try {
				NbtIo.write(compound, new ByteBufOutputStream(this));
			} catch (IOException var3) {
				throw new EncoderException(var3);
			}
		}

		return this;
	}

	@Nullable
	public NbtCompound readNbt() {
		return this.readNbt(new NbtTagSizeTracker(2097152L));
	}

	@Nullable
	public NbtCompound readUnlimitedNbt() {
		return this.readNbt(NbtTagSizeTracker.EMPTY);
	}

	@Nullable
	public NbtCompound readNbt(NbtTagSizeTracker sizeTracker) {
		int i = this.readerIndex();
		byte b = this.readByte();
		if (b == 0) {
			return null;
		} else {
			this.readerIndex(i);

			try {
				return NbtIo.read(new ByteBufInputStream(this), sizeTracker);
			} catch (IOException var5) {
				throw new EncoderException(var5);
			}
		}
	}

	public PacketByteBuf writeItemStack(ItemStack stack) {
		if (stack.isEmpty()) {
			this.writeBoolean(false);
		} else {
			this.writeBoolean(true);
			Item item = stack.getItem();
			this.writeVarInt(Item.getRawId(item));
			this.writeByte(stack.getCount());
			NbtCompound nbtCompound = null;
			if (item.isDamageable() || item.shouldSyncTagToClient()) {
				nbtCompound = stack.getTag();
			}

			this.writeNbt(nbtCompound);
		}

		return this;
	}

	public ItemStack readItemStack() {
		if (!this.readBoolean()) {
			return ItemStack.EMPTY;
		} else {
			int i = this.readVarInt();
			int j = this.readByte();
			ItemStack itemStack = new ItemStack(Item.byRawId(i), j);
			itemStack.setTag(this.readNbt());
			return itemStack;
		}
	}

	public String readString() {
		return this.readString(32767);
	}

	public String readString(int maxLength) {
		int i = this.readVarInt();
		if (i > maxLength * 4) {
			throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
		} else if (i < 0) {
			throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
		} else {
			String string = this.toString(this.readerIndex(), i, StandardCharsets.UTF_8);
			this.readerIndex(this.readerIndex() + i);
			if (string.length() > maxLength) {
				throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
			} else {
				return string;
			}
		}
	}

	public PacketByteBuf writeString(String string) {
		return this.writeString(string, 32767);
	}

	public PacketByteBuf writeString(String string, int maxLength) {
		byte[] bs = string.getBytes(StandardCharsets.UTF_8);
		if (bs.length > maxLength) {
			throw new EncoderException("String too big (was " + bs.length + " bytes encoded, max " + maxLength + ")");
		} else {
			this.writeVarInt(bs.length);
			this.writeBytes(bs);
			return this;
		}
	}

	public Identifier readIdentifier() {
		return new Identifier(this.readString(32767));
	}

	public PacketByteBuf writeIdentifier(Identifier id) {
		this.writeString(id.toString());
		return this;
	}

	public Date readDate() {
		return new Date(this.readLong());
	}

	public PacketByteBuf writeDate(Date date) {
		this.writeLong(date.getTime());
		return this;
	}

	public BlockHitResult readBlockHitResult() {
		BlockPos blockPos = this.readBlockPos();
		Direction direction = this.readEnumConstant(Direction.class);
		float f = this.readFloat();
		float g = this.readFloat();
		float h = this.readFloat();
		boolean bl = this.readBoolean();
		return new BlockHitResult(
			new Vec3d((double)blockPos.getX() + (double)f, (double)blockPos.getY() + (double)g, (double)blockPos.getZ() + (double)h), direction, blockPos, bl
		);
	}

	public void writeBlockHitResult(BlockHitResult hitResult) {
		BlockPos blockPos = hitResult.getBlockPos();
		this.writeBlockPos(blockPos);
		this.writeEnumConstant(hitResult.getSide());
		Vec3d vec3d = hitResult.getPos();
		this.writeFloat((float)(vec3d.x - (double)blockPos.getX()));
		this.writeFloat((float)(vec3d.y - (double)blockPos.getY()));
		this.writeFloat((float)(vec3d.z - (double)blockPos.getZ()));
		this.writeBoolean(hitResult.isInsideBlock());
	}

	public BitSet readBitSet() {
		return BitSet.valueOf(this.readLongArray());
	}

	public void writeBitSet(BitSet bitSet) {
		this.writeLongArray(bitSet.toLongArray());
	}

	public int capacity() {
		return this.parent.capacity();
	}

	public ByteBuf capacity(int i) {
		return this.parent.capacity(i);
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

	public boolean isReadOnly() {
		return this.parent.isReadOnly();
	}

	public ByteBuf asReadOnly() {
		return this.parent.asReadOnly();
	}

	public int readerIndex() {
		return this.parent.readerIndex();
	}

	public ByteBuf readerIndex(int i) {
		return this.parent.readerIndex(i);
	}

	public int writerIndex() {
		return this.parent.writerIndex();
	}

	public ByteBuf writerIndex(int i) {
		return this.parent.writerIndex(i);
	}

	public ByteBuf setIndex(int i, int j) {
		return this.parent.setIndex(i, j);
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

	public boolean isReadable(int i) {
		return this.parent.isReadable(i);
	}

	public boolean isWritable() {
		return this.parent.isWritable();
	}

	public boolean isWritable(int i) {
		return this.parent.isWritable(i);
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

	public ByteBuf ensureWritable(int i) {
		return this.parent.ensureWritable(i);
	}

	public int ensureWritable(int i, boolean bl) {
		return this.parent.ensureWritable(i, bl);
	}

	public boolean getBoolean(int i) {
		return this.parent.getBoolean(i);
	}

	public byte getByte(int i) {
		return this.parent.getByte(i);
	}

	public short getUnsignedByte(int i) {
		return this.parent.getUnsignedByte(i);
	}

	public short getShort(int i) {
		return this.parent.getShort(i);
	}

	public short getShortLE(int i) {
		return this.parent.getShortLE(i);
	}

	public int getUnsignedShort(int i) {
		return this.parent.getUnsignedShort(i);
	}

	public int getUnsignedShortLE(int i) {
		return this.parent.getUnsignedShortLE(i);
	}

	public int getMedium(int i) {
		return this.parent.getMedium(i);
	}

	public int getMediumLE(int i) {
		return this.parent.getMediumLE(i);
	}

	public int getUnsignedMedium(int i) {
		return this.parent.getUnsignedMedium(i);
	}

	public int getUnsignedMediumLE(int i) {
		return this.parent.getUnsignedMediumLE(i);
	}

	public int getInt(int i) {
		return this.parent.getInt(i);
	}

	public int getIntLE(int i) {
		return this.parent.getIntLE(i);
	}

	public long getUnsignedInt(int i) {
		return this.parent.getUnsignedInt(i);
	}

	public long getUnsignedIntLE(int i) {
		return this.parent.getUnsignedIntLE(i);
	}

	public long getLong(int i) {
		return this.parent.getLong(i);
	}

	public long getLongLE(int i) {
		return this.parent.getLongLE(i);
	}

	public char getChar(int i) {
		return this.parent.getChar(i);
	}

	public float getFloat(int i) {
		return this.parent.getFloat(i);
	}

	public double getDouble(int i) {
		return this.parent.getDouble(i);
	}

	public ByteBuf getBytes(int i, ByteBuf byteBuf) {
		return this.parent.getBytes(i, byteBuf);
	}

	public ByteBuf getBytes(int i, ByteBuf byteBuf, int j) {
		return this.parent.getBytes(i, byteBuf, j);
	}

	public ByteBuf getBytes(int i, ByteBuf byteBuf, int j, int k) {
		return this.parent.getBytes(i, byteBuf, j, k);
	}

	public ByteBuf getBytes(int i, byte[] bs) {
		return this.parent.getBytes(i, bs);
	}

	public ByteBuf getBytes(int i, byte[] bs, int j, int k) {
		return this.parent.getBytes(i, bs, j, k);
	}

	public ByteBuf getBytes(int i, ByteBuffer byteBuffer) {
		return this.parent.getBytes(i, byteBuffer);
	}

	public ByteBuf getBytes(int i, OutputStream outputStream, int j) throws IOException {
		return this.parent.getBytes(i, outputStream, j);
	}

	public int getBytes(int i, GatheringByteChannel gatheringByteChannel, int j) throws IOException {
		return this.parent.getBytes(i, gatheringByteChannel, j);
	}

	public int getBytes(int i, FileChannel fileChannel, long l, int j) throws IOException {
		return this.parent.getBytes(i, fileChannel, l, j);
	}

	public CharSequence getCharSequence(int i, int j, Charset charset) {
		return this.parent.getCharSequence(i, j, charset);
	}

	public ByteBuf setBoolean(int i, boolean bl) {
		return this.parent.setBoolean(i, bl);
	}

	public ByteBuf setByte(int i, int j) {
		return this.parent.setByte(i, j);
	}

	public ByteBuf setShort(int i, int j) {
		return this.parent.setShort(i, j);
	}

	public ByteBuf setShortLE(int i, int j) {
		return this.parent.setShortLE(i, j);
	}

	public ByteBuf setMedium(int i, int j) {
		return this.parent.setMedium(i, j);
	}

	public ByteBuf setMediumLE(int i, int j) {
		return this.parent.setMediumLE(i, j);
	}

	public ByteBuf setInt(int i, int j) {
		return this.parent.setInt(i, j);
	}

	public ByteBuf setIntLE(int i, int j) {
		return this.parent.setIntLE(i, j);
	}

	public ByteBuf setLong(int i, long l) {
		return this.parent.setLong(i, l);
	}

	public ByteBuf setLongLE(int i, long l) {
		return this.parent.setLongLE(i, l);
	}

	public ByteBuf setChar(int i, int j) {
		return this.parent.setChar(i, j);
	}

	public ByteBuf setFloat(int i, float f) {
		return this.parent.setFloat(i, f);
	}

	public ByteBuf setDouble(int i, double d) {
		return this.parent.setDouble(i, d);
	}

	public ByteBuf setBytes(int i, ByteBuf byteBuf) {
		return this.parent.setBytes(i, byteBuf);
	}

	public ByteBuf setBytes(int i, ByteBuf byteBuf, int j) {
		return this.parent.setBytes(i, byteBuf, j);
	}

	public ByteBuf setBytes(int i, ByteBuf byteBuf, int j, int k) {
		return this.parent.setBytes(i, byteBuf, j, k);
	}

	public ByteBuf setBytes(int i, byte[] bs) {
		return this.parent.setBytes(i, bs);
	}

	public ByteBuf setBytes(int i, byte[] bs, int j, int k) {
		return this.parent.setBytes(i, bs, j, k);
	}

	public ByteBuf setBytes(int i, ByteBuffer byteBuffer) {
		return this.parent.setBytes(i, byteBuffer);
	}

	public int setBytes(int i, InputStream inputStream, int j) throws IOException {
		return this.parent.setBytes(i, inputStream, j);
	}

	public int setBytes(int i, ScatteringByteChannel scatteringByteChannel, int j) throws IOException {
		return this.parent.setBytes(i, scatteringByteChannel, j);
	}

	public int setBytes(int i, FileChannel fileChannel, long l, int j) throws IOException {
		return this.parent.setBytes(i, fileChannel, l, j);
	}

	public ByteBuf setZero(int i, int j) {
		return this.parent.setZero(i, j);
	}

	public int setCharSequence(int i, CharSequence charSequence, Charset charset) {
		return this.parent.setCharSequence(i, charSequence, charset);
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

	public short readShortLE() {
		return this.parent.readShortLE();
	}

	public int readUnsignedShort() {
		return this.parent.readUnsignedShort();
	}

	public int readUnsignedShortLE() {
		return this.parent.readUnsignedShortLE();
	}

	public int readMedium() {
		return this.parent.readMedium();
	}

	public int readMediumLE() {
		return this.parent.readMediumLE();
	}

	public int readUnsignedMedium() {
		return this.parent.readUnsignedMedium();
	}

	public int readUnsignedMediumLE() {
		return this.parent.readUnsignedMediumLE();
	}

	public int readInt() {
		return this.parent.readInt();
	}

	public int readIntLE() {
		return this.parent.readIntLE();
	}

	public long readUnsignedInt() {
		return this.parent.readUnsignedInt();
	}

	public long readUnsignedIntLE() {
		return this.parent.readUnsignedIntLE();
	}

	public long readLong() {
		return this.parent.readLong();
	}

	public long readLongLE() {
		return this.parent.readLongLE();
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

	public ByteBuf readBytes(int i) {
		return this.parent.readBytes(i);
	}

	public ByteBuf readSlice(int i) {
		return this.parent.readSlice(i);
	}

	public ByteBuf readRetainedSlice(int i) {
		return this.parent.readRetainedSlice(i);
	}

	public ByteBuf readBytes(ByteBuf byteBuf) {
		return this.parent.readBytes(byteBuf);
	}

	public ByteBuf readBytes(ByteBuf byteBuf, int i) {
		return this.parent.readBytes(byteBuf, i);
	}

	public ByteBuf readBytes(ByteBuf byteBuf, int i, int j) {
		return this.parent.readBytes(byteBuf, i, j);
	}

	public ByteBuf readBytes(byte[] bs) {
		return this.parent.readBytes(bs);
	}

	public ByteBuf readBytes(byte[] bs, int i, int j) {
		return this.parent.readBytes(bs, i, j);
	}

	public ByteBuf readBytes(ByteBuffer byteBuffer) {
		return this.parent.readBytes(byteBuffer);
	}

	public ByteBuf readBytes(OutputStream outputStream, int i) throws IOException {
		return this.parent.readBytes(outputStream, i);
	}

	public int readBytes(GatheringByteChannel gatheringByteChannel, int i) throws IOException {
		return this.parent.readBytes(gatheringByteChannel, i);
	}

	public CharSequence readCharSequence(int i, Charset charset) {
		return this.parent.readCharSequence(i, charset);
	}

	public int readBytes(FileChannel fileChannel, long l, int i) throws IOException {
		return this.parent.readBytes(fileChannel, l, i);
	}

	public ByteBuf skipBytes(int i) {
		return this.parent.skipBytes(i);
	}

	public ByteBuf writeBoolean(boolean bl) {
		return this.parent.writeBoolean(bl);
	}

	public ByteBuf writeByte(int i) {
		return this.parent.writeByte(i);
	}

	public ByteBuf writeShort(int i) {
		return this.parent.writeShort(i);
	}

	public ByteBuf writeShortLE(int i) {
		return this.parent.writeShortLE(i);
	}

	public ByteBuf writeMedium(int i) {
		return this.parent.writeMedium(i);
	}

	public ByteBuf writeMediumLE(int i) {
		return this.parent.writeMediumLE(i);
	}

	public ByteBuf writeInt(int i) {
		return this.parent.writeInt(i);
	}

	public ByteBuf writeIntLE(int i) {
		return this.parent.writeIntLE(i);
	}

	public ByteBuf writeLong(long l) {
		return this.parent.writeLong(l);
	}

	public ByteBuf writeLongLE(long l) {
		return this.parent.writeLongLE(l);
	}

	public ByteBuf writeChar(int i) {
		return this.parent.writeChar(i);
	}

	public ByteBuf writeFloat(float f) {
		return this.parent.writeFloat(f);
	}

	public ByteBuf writeDouble(double d) {
		return this.parent.writeDouble(d);
	}

	public ByteBuf writeBytes(ByteBuf byteBuf) {
		return this.parent.writeBytes(byteBuf);
	}

	public ByteBuf writeBytes(ByteBuf byteBuf, int i) {
		return this.parent.writeBytes(byteBuf, i);
	}

	public ByteBuf writeBytes(ByteBuf byteBuf, int i, int j) {
		return this.parent.writeBytes(byteBuf, i, j);
	}

	public ByteBuf writeBytes(byte[] bs) {
		return this.parent.writeBytes(bs);
	}

	public ByteBuf writeBytes(byte[] bs, int i, int j) {
		return this.parent.writeBytes(bs, i, j);
	}

	public ByteBuf writeBytes(ByteBuffer byteBuffer) {
		return this.parent.writeBytes(byteBuffer);
	}

	public int writeBytes(InputStream inputStream, int i) throws IOException {
		return this.parent.writeBytes(inputStream, i);
	}

	public int writeBytes(ScatteringByteChannel scatteringByteChannel, int i) throws IOException {
		return this.parent.writeBytes(scatteringByteChannel, i);
	}

	public int writeBytes(FileChannel fileChannel, long l, int i) throws IOException {
		return this.parent.writeBytes(fileChannel, l, i);
	}

	public ByteBuf writeZero(int i) {
		return this.parent.writeZero(i);
	}

	public int writeCharSequence(CharSequence charSequence, Charset charset) {
		return this.parent.writeCharSequence(charSequence, charset);
	}

	public int indexOf(int i, int j, byte b) {
		return this.parent.indexOf(i, j, b);
	}

	public int bytesBefore(byte b) {
		return this.parent.bytesBefore(b);
	}

	public int bytesBefore(int i, byte b) {
		return this.parent.bytesBefore(i, b);
	}

	public int bytesBefore(int i, int j, byte b) {
		return this.parent.bytesBefore(i, j, b);
	}

	public int forEachByte(ByteProcessor byteProcessor) {
		return this.parent.forEachByte(byteProcessor);
	}

	public int forEachByte(int i, int j, ByteProcessor byteProcessor) {
		return this.parent.forEachByte(i, j, byteProcessor);
	}

	public int forEachByteDesc(ByteProcessor byteProcessor) {
		return this.parent.forEachByteDesc(byteProcessor);
	}

	public int forEachByteDesc(int i, int j, ByteProcessor byteProcessor) {
		return this.parent.forEachByteDesc(i, j, byteProcessor);
	}

	public ByteBuf copy() {
		return this.parent.copy();
	}

	public ByteBuf copy(int i, int j) {
		return this.parent.copy(i, j);
	}

	public ByteBuf slice() {
		return this.parent.slice();
	}

	public ByteBuf retainedSlice() {
		return this.parent.retainedSlice();
	}

	public ByteBuf slice(int i, int j) {
		return this.parent.slice(i, j);
	}

	public ByteBuf retainedSlice(int i, int j) {
		return this.parent.retainedSlice(i, j);
	}

	public ByteBuf duplicate() {
		return this.parent.duplicate();
	}

	public ByteBuf retainedDuplicate() {
		return this.parent.retainedDuplicate();
	}

	public int nioBufferCount() {
		return this.parent.nioBufferCount();
	}

	public ByteBuffer nioBuffer() {
		return this.parent.nioBuffer();
	}

	public ByteBuffer nioBuffer(int i, int j) {
		return this.parent.nioBuffer(i, j);
	}

	public ByteBuffer internalNioBuffer(int i, int j) {
		return this.parent.internalNioBuffer(i, j);
	}

	public ByteBuffer[] nioBuffers() {
		return this.parent.nioBuffers();
	}

	public ByteBuffer[] nioBuffers(int i, int j) {
		return this.parent.nioBuffers(i, j);
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

	public String toString(int i, int j, Charset charset) {
		return this.parent.toString(i, j, charset);
	}

	public int hashCode() {
		return this.parent.hashCode();
	}

	public boolean equals(Object o) {
		return this.parent.equals(o);
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

	public ByteBuf touch() {
		return this.parent.touch();
	}

	public ByteBuf touch(Object object) {
		return this.parent.touch(object);
	}

	public int refCnt() {
		return this.parent.refCnt();
	}

	public boolean release() {
		return this.parent.release();
	}

	public boolean release(int i) {
		return this.parent.release(i);
	}
}
