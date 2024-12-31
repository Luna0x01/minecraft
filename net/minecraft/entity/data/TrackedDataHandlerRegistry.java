package net.minecraft.entity.data;

import com.google.common.base.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_2929;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;

public class TrackedDataHandlerRegistry {
	private static final class_2929<TrackedDataHandler<?>> field_13831 = new class_2929<>(16);
	public static final TrackedDataHandler<Byte> BYTE = new TrackedDataHandler<Byte>() {
		public void write(PacketByteBuf packetByteBuf, Byte byte_) {
			packetByteBuf.writeByte(byte_);
		}

		public Byte read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readByte();
		}

		@Override
		public TrackedData<Byte> create(int i) {
			return new TrackedData<>(i, this);
		}
	};
	public static final TrackedDataHandler<Integer> INTEGER = new TrackedDataHandler<Integer>() {
		public void write(PacketByteBuf packetByteBuf, Integer integer) {
			packetByteBuf.writeVarInt(integer);
		}

		public Integer read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readVarInt();
		}

		@Override
		public TrackedData<Integer> create(int i) {
			return new TrackedData<>(i, this);
		}
	};
	public static final TrackedDataHandler<Float> FLOAT = new TrackedDataHandler<Float>() {
		public void write(PacketByteBuf packetByteBuf, Float float_) {
			packetByteBuf.writeFloat(float_);
		}

		public Float read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readFloat();
		}

		@Override
		public TrackedData<Float> create(int i) {
			return new TrackedData<>(i, this);
		}
	};
	public static final TrackedDataHandler<String> STRING = new TrackedDataHandler<String>() {
		public void write(PacketByteBuf packetByteBuf, String string) {
			packetByteBuf.writeString(string);
		}

		public String read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readString(32767);
		}

		@Override
		public TrackedData<String> create(int i) {
			return new TrackedData<>(i, this);
		}
	};
	public static final TrackedDataHandler<Text> TEXT_COMPONENT = new TrackedDataHandler<Text>() {
		public void write(PacketByteBuf packetByteBuf, Text text) {
			packetByteBuf.writeText(text);
		}

		public Text read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readText();
		}

		@Override
		public TrackedData<Text> create(int i) {
			return new TrackedData<>(i, this);
		}
	};
	public static final TrackedDataHandler<Optional<ItemStack>> ITEM_STACK = new TrackedDataHandler<Optional<ItemStack>>() {
		public void write(PacketByteBuf packetByteBuf, Optional<ItemStack> optional) {
			packetByteBuf.writeItemStack((ItemStack)optional.orNull());
		}

		public Optional<ItemStack> read(PacketByteBuf packetByteBuf) {
			return Optional.fromNullable(packetByteBuf.readItemStack());
		}

		@Override
		public TrackedData<Optional<ItemStack>> create(int i) {
			return new TrackedData<>(i, this);
		}
	};
	public static final TrackedDataHandler<Optional<BlockState>> OPTIONAL_BLOCK_STATE = new TrackedDataHandler<Optional<BlockState>>() {
		public void write(PacketByteBuf packetByteBuf, Optional<BlockState> optional) {
			if (optional.isPresent()) {
				packetByteBuf.writeVarInt(Block.getByBlockState((BlockState)optional.get()));
			} else {
				packetByteBuf.writeVarInt(0);
			}
		}

		public Optional<BlockState> read(PacketByteBuf packetByteBuf) {
			int i = packetByteBuf.readVarInt();
			return i == 0 ? Optional.absent() : Optional.of(Block.getStateFromRawId(i));
		}

		@Override
		public TrackedData<Optional<BlockState>> create(int i) {
			return new TrackedData<>(i, this);
		}
	};
	public static final TrackedDataHandler<Boolean> BOOLEAN = new TrackedDataHandler<Boolean>() {
		public void write(PacketByteBuf packetByteBuf, Boolean boolean_) {
			packetByteBuf.writeBoolean(boolean_);
		}

		public Boolean read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readBoolean();
		}

		@Override
		public TrackedData<Boolean> create(int i) {
			return new TrackedData<>(i, this);
		}
	};
	public static final TrackedDataHandler<EulerAngle> ROTATION = new TrackedDataHandler<EulerAngle>() {
		public void write(PacketByteBuf packetByteBuf, EulerAngle eulerAngle) {
			packetByteBuf.writeFloat(eulerAngle.getPitch());
			packetByteBuf.writeFloat(eulerAngle.getYaw());
			packetByteBuf.writeFloat(eulerAngle.getRoll());
		}

		public EulerAngle read(PacketByteBuf packetByteBuf) {
			return new EulerAngle(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
		}

		@Override
		public TrackedData<EulerAngle> create(int i) {
			return new TrackedData<>(i, this);
		}
	};
	public static final TrackedDataHandler<BlockPos> BLOCK_POS = new TrackedDataHandler<BlockPos>() {
		public void write(PacketByteBuf packetByteBuf, BlockPos blockPos) {
			packetByteBuf.writeBlockPos(blockPos);
		}

		public BlockPos read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readBlockPos();
		}

		@Override
		public TrackedData<BlockPos> create(int i) {
			return new TrackedData<>(i, this);
		}
	};
	public static final TrackedDataHandler<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new TrackedDataHandler<Optional<BlockPos>>() {
		public void write(PacketByteBuf packetByteBuf, Optional<BlockPos> optional) {
			packetByteBuf.writeBoolean(optional.isPresent());
			if (optional.isPresent()) {
				packetByteBuf.writeBlockPos((BlockPos)optional.get());
			}
		}

		public Optional<BlockPos> read(PacketByteBuf packetByteBuf) {
			return !packetByteBuf.readBoolean() ? Optional.absent() : Optional.of(packetByteBuf.readBlockPos());
		}

		@Override
		public TrackedData<Optional<BlockPos>> create(int i) {
			return new TrackedData<>(i, this);
		}
	};
	public static final TrackedDataHandler<Direction> FACING = new TrackedDataHandler<Direction>() {
		public void write(PacketByteBuf packetByteBuf, Direction direction) {
			packetByteBuf.writeEnumConstant(direction);
		}

		public Direction read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readEnumConstant(Direction.class);
		}

		@Override
		public TrackedData<Direction> create(int i) {
			return new TrackedData<>(i, this);
		}
	};
	public static final TrackedDataHandler<Optional<UUID>> OPTIONAL_UUID = new TrackedDataHandler<Optional<UUID>>() {
		public void write(PacketByteBuf packetByteBuf, Optional<UUID> optional) {
			packetByteBuf.writeBoolean(optional.isPresent());
			if (optional.isPresent()) {
				packetByteBuf.writeUuid((UUID)optional.get());
			}
		}

		public Optional<UUID> read(PacketByteBuf packetByteBuf) {
			return !packetByteBuf.readBoolean() ? Optional.absent() : Optional.of(packetByteBuf.readUuid());
		}

		@Override
		public TrackedData<Optional<UUID>> create(int i) {
			return new TrackedData<>(i, this);
		}
	};

	public static void method_12719(TrackedDataHandler<?> trackedDataHandler) {
		field_13831.method_12864(trackedDataHandler);
	}

	@Nullable
	public static TrackedDataHandler<?> method_12718(int i) {
		return field_13831.getById(i);
	}

	public static int method_12720(TrackedDataHandler<?> trackedDataHandler) {
		return field_13831.getId(trackedDataHandler);
	}

	static {
		method_12719(BYTE);
		method_12719(INTEGER);
		method_12719(FLOAT);
		method_12719(STRING);
		method_12719(TEXT_COMPONENT);
		method_12719(ITEM_STACK);
		method_12719(BOOLEAN);
		method_12719(ROTATION);
		method_12719(BLOCK_POS);
		method_12719(OPTIONAL_BLOCK_POS);
		method_12719(FACING);
		method_12719(OPTIONAL_UUID);
		method_12719(OPTIONAL_BLOCK_STATE);
	}
}
