package net.minecraft.item.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3082;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class MapState extends PersistentState {
	public int xCenter;
	public int zCenter;
	public byte dimensionId;
	public boolean trackingPosition;
	public boolean field_15238;
	public byte scale;
	public byte[] colors = new byte[16384];
	public List<MapState.PlayerUpdateTracker> updateTrackers = Lists.newArrayList();
	private final Map<PlayerEntity, MapState.PlayerUpdateTracker> updateTrackersByPlayer = Maps.newHashMap();
	public Map<String, class_3082> icons = Maps.newLinkedHashMap();

	public MapState(String string) {
		super(string);
	}

	public void method_9308(double x, double z, int scale) {
		int i = 128 * (1 << scale);
		int j = MathHelper.floor((x + 64.0) / (double)i);
		int k = MathHelper.floor((z + 64.0) / (double)i);
		this.xCenter = j * i + i / 2 - 64;
		this.zCenter = k * i + i / 2 - 64;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		this.dimensionId = nbt.getByte("dimension");
		this.xCenter = nbt.getInt("xCenter");
		this.zCenter = nbt.getInt("zCenter");
		this.scale = nbt.getByte("scale");
		this.scale = (byte)MathHelper.clamp(this.scale, 0, 4);
		if (nbt.contains("trackingPosition", 1)) {
			this.trackingPosition = nbt.getBoolean("trackingPosition");
		} else {
			this.trackingPosition = true;
		}

		this.field_15238 = nbt.getBoolean("unlimitedTracking");
		int i = nbt.getShort("width");
		int j = nbt.getShort("height");
		if (i == 128 && j == 128) {
			this.colors = nbt.getByteArray("colors");
		} else {
			byte[] bs = nbt.getByteArray("colors");
			this.colors = new byte[16384];
			int k = (128 - i) / 2;
			int l = (128 - j) / 2;

			for (int m = 0; m < j; m++) {
				int n = m + l;
				if (n >= 0 || n < 128) {
					for (int o = 0; o < i; o++) {
						int p = o + k;
						if (p >= 0 || p < 128) {
							this.colors[p + n * 128] = bs[o + m * i];
						}
					}
				}
			}
		}
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		nbt.putByte("dimension", this.dimensionId);
		nbt.putInt("xCenter", this.xCenter);
		nbt.putInt("zCenter", this.zCenter);
		nbt.putByte("scale", this.scale);
		nbt.putShort("width", (short)128);
		nbt.putShort("height", (short)128);
		nbt.putByteArray("colors", this.colors);
		nbt.putBoolean("trackingPosition", this.trackingPosition);
		nbt.putBoolean("unlimitedTracking", this.field_15238);
		return nbt;
	}

	public void update(PlayerEntity player, ItemStack stack) {
		if (!this.updateTrackersByPlayer.containsKey(player)) {
			MapState.PlayerUpdateTracker playerUpdateTracker = new MapState.PlayerUpdateTracker(player);
			this.updateTrackersByPlayer.put(player, playerUpdateTracker);
			this.updateTrackers.add(playerUpdateTracker);
		}

		if (!player.inventory.contains(stack)) {
			this.icons.remove(player.getTranslationKey());
		}

		for (int i = 0; i < this.updateTrackers.size(); i++) {
			MapState.PlayerUpdateTracker playerUpdateTracker2 = (MapState.PlayerUpdateTracker)this.updateTrackers.get(i);
			if (!playerUpdateTracker2.player.removed && (playerUpdateTracker2.player.inventory.contains(stack) || stack.isInItemFrame())) {
				if (!stack.isInItemFrame() && playerUpdateTracker2.player.dimension == this.dimensionId && this.trackingPosition) {
					this.method_4126(
						class_3082.class_3083.PLAYER,
						playerUpdateTracker2.player.world,
						playerUpdateTracker2.player.getTranslationKey(),
						playerUpdateTracker2.player.x,
						playerUpdateTracker2.player.z,
						(double)playerUpdateTracker2.player.yaw
					);
				}
			} else {
				this.updateTrackersByPlayer.remove(playerUpdateTracker2.player);
				this.updateTrackers.remove(playerUpdateTracker2);
			}
		}

		if (stack.isInItemFrame() && this.trackingPosition) {
			ItemFrameEntity itemFrameEntity = stack.getItemFrame();
			BlockPos blockPos = itemFrameEntity.getTilePos();
			this.method_4126(
				class_3082.class_3083.FRAME,
				player.world,
				"frame-" + itemFrameEntity.getEntityId(),
				(double)blockPos.getX(),
				(double)blockPos.getZ(),
				(double)(itemFrameEntity.direction.getHorizontal() * 90)
			);
		}

		if (stack.hasNbt() && stack.getNbt().contains("Decorations", 9)) {
			NbtList nbtList = stack.getNbt().getList("Decorations", 10);

			for (int j = 0; j < nbtList.size(); j++) {
				NbtCompound nbtCompound = nbtList.getCompound(j);
				if (!this.icons.containsKey(nbtCompound.getString("id"))) {
					this.method_4126(
						class_3082.class_3083.method_13826(nbtCompound.getByte("type")),
						player.world,
						nbtCompound.getString("id"),
						nbtCompound.getDouble("x"),
						nbtCompound.getDouble("z"),
						nbtCompound.getDouble("rot")
					);
				}
			}
		}
	}

	public static void method_13830(ItemStack itemStack, BlockPos blockPos, String string, class_3082.class_3083 arg) {
		NbtList nbtList;
		if (itemStack.hasNbt() && itemStack.getNbt().contains("Decorations", 9)) {
			nbtList = itemStack.getNbt().getList("Decorations", 10);
		} else {
			nbtList = new NbtList();
			itemStack.putSubNbt("Decorations", nbtList);
		}

		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putByte("type", arg.method_13825());
		nbtCompound.putString("id", string);
		nbtCompound.putDouble("x", (double)blockPos.getX());
		nbtCompound.putDouble("z", (double)blockPos.getZ());
		nbtCompound.putDouble("rot", 180.0);
		nbtList.add(nbtCompound);
		if (arg.method_13828()) {
			NbtCompound nbtCompound2 = itemStack.getOrCreateNbtCompound("display");
			nbtCompound2.putInt("MapColor", arg.method_13829());
		}
	}

	private void method_4126(class_3082.class_3083 arg, World world, String string, double d, double e, double f) {
		int i = 1 << this.scale;
		float g = (float)(d - (double)this.xCenter) / (float)i;
		float h = (float)(e - (double)this.zCenter) / (float)i;
		byte b = (byte)((int)((double)(g * 2.0F) + 0.5));
		byte c = (byte)((int)((double)(h * 2.0F) + 0.5));
		int j = 63;
		byte k;
		if (g >= -63.0F && h >= -63.0F && g <= 63.0F && h <= 63.0F) {
			f += f < 0.0 ? -8.0 : 8.0;
			k = (byte)((int)(f * 16.0 / 360.0));
			if (this.dimensionId < 0) {
				int l = (int)(world.getLevelProperties().getTimeOfDay() / 10L);
				k = (byte)(l * l * 34187121 + l * 121 >> 15 & 15);
			}
		} else {
			if (arg != class_3082.class_3083.PLAYER) {
				this.icons.remove(string);
				return;
			}

			int m = 320;
			if (Math.abs(g) < 320.0F && Math.abs(h) < 320.0F) {
				arg = class_3082.class_3083.PLAYER_OFF_MAP;
			} else {
				if (!this.field_15238) {
					this.icons.remove(string);
					return;
				}

				arg = class_3082.class_3083.PLAYER_OFF_LIMITS;
			}

			k = 0;
			if (g <= -63.0F) {
				b = -128;
			}

			if (h <= -63.0F) {
				c = -128;
			}

			if (g >= 63.0F) {
				b = 127;
			}

			if (h >= 63.0F) {
				c = 127;
			}
		}

		this.icons.put(string, new class_3082(arg, b, c, k));
	}

	@Nullable
	public Packet<?> createMapSyncPacket(ItemStack itemStack, World world, PlayerEntity player) {
		MapState.PlayerUpdateTracker playerUpdateTracker = (MapState.PlayerUpdateTracker)this.updateTrackersByPlayer.get(player);
		return playerUpdateTracker == null ? null : playerUpdateTracker.getPacket(itemStack);
	}

	public void markDirty(int x, int z) {
		super.markDirty();

		for (MapState.PlayerUpdateTracker playerUpdateTracker : this.updateTrackers) {
			playerUpdateTracker.markDirty(x, z);
		}
	}

	public MapState.PlayerUpdateTracker getPlayerSyncData(PlayerEntity player) {
		MapState.PlayerUpdateTracker playerUpdateTracker = (MapState.PlayerUpdateTracker)this.updateTrackersByPlayer.get(player);
		if (playerUpdateTracker == null) {
			playerUpdateTracker = new MapState.PlayerUpdateTracker(player);
			this.updateTrackersByPlayer.put(player, playerUpdateTracker);
			this.updateTrackers.add(playerUpdateTracker);
		}

		return playerUpdateTracker;
	}

	public class PlayerUpdateTracker {
		public final PlayerEntity player;
		private boolean dirty = true;
		private int startX;
		private int startZ;
		private int endX = 127;
		private int endZ = 127;
		private int emptyPacketsRequested;
		public int field_4983;

		public PlayerUpdateTracker(PlayerEntity playerEntity) {
			this.player = playerEntity;
		}

		@Nullable
		public Packet<?> getPacket(ItemStack stack) {
			if (this.dirty) {
				this.dirty = false;
				return new MapUpdateS2CPacket(
					stack.getData(),
					MapState.this.scale,
					MapState.this.trackingPosition,
					MapState.this.icons.values(),
					MapState.this.colors,
					this.startX,
					this.startZ,
					this.endX + 1 - this.startX,
					this.endZ + 1 - this.startZ
				);
			} else {
				return this.emptyPacketsRequested++ % 5 == 0
					? new MapUpdateS2CPacket(
						stack.getData(), MapState.this.scale, MapState.this.trackingPosition, MapState.this.icons.values(), MapState.this.colors, 0, 0, 0, 0
					)
					: null;
			}
		}

		public void markDirty(int x, int z) {
			if (this.dirty) {
				this.startX = Math.min(this.startX, x);
				this.startZ = Math.min(this.startZ, z);
				this.endX = Math.max(this.endX, x);
				this.endZ = Math.max(this.endZ, z);
			} else {
				this.dirty = true;
				this.startX = x;
				this.startZ = z;
				this.endX = x;
				this.endZ = z;
			}
		}
	}
}
