package net.minecraft.item.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
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
	public byte scale;
	public byte[] colors = new byte[16384];
	public List<MapState.PlayerUpdateTracker> updateTrackers = Lists.newArrayList();
	private Map<PlayerEntity, MapState.PlayerUpdateTracker> updateTrackersByPlayer = Maps.newHashMap();
	public Map<String, MapIcon> icons = Maps.newLinkedHashMap();

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
	public void toNbt(NbtCompound nbt) {
		nbt.putByte("dimension", this.dimensionId);
		nbt.putInt("xCenter", this.xCenter);
		nbt.putInt("zCenter", this.zCenter);
		nbt.putByte("scale", this.scale);
		nbt.putShort("width", (short)128);
		nbt.putShort("height", (short)128);
		nbt.putByteArray("colors", this.colors);
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
				if (!stack.isInItemFrame() && playerUpdateTracker2.player.dimension == this.dimensionId) {
					this.method_4126(
						0,
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

		if (stack.isInItemFrame()) {
			ItemFrameEntity itemFrameEntity = stack.getItemFrame();
			BlockPos blockPos = itemFrameEntity.getTilePos();
			this.method_4126(
				1,
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
						nbtCompound.getByte("type"),
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

	private void method_4126(int i, World world, String string, double d, double e, double f) {
		int j = 1 << this.scale;
		float g = (float)(d - (double)this.xCenter) / (float)j;
		float h = (float)(e - (double)this.zCenter) / (float)j;
		byte b = (byte)((int)((double)(g * 2.0F) + 0.5));
		byte c = (byte)((int)((double)(h * 2.0F) + 0.5));
		int k = 63;
		byte l;
		if (g >= (float)(-k) && h >= (float)(-k) && g <= (float)k && h <= (float)k) {
			f += f < 0.0 ? -8.0 : 8.0;
			l = (byte)((int)(f * 16.0 / 360.0));
			if (this.dimensionId < 0) {
				int m = (int)(world.getLevelProperties().getTimeOfDay() / 10L);
				l = (byte)(m * m * 34187121 + m * 121 >> 15 & 15);
			}
		} else {
			if (!(Math.abs(g) < 320.0F) || !(Math.abs(h) < 320.0F)) {
				this.icons.remove(string);
				return;
			}

			i = 6;
			l = 0;
			if (g <= (float)(-k)) {
				b = (byte)((int)((double)(k * 2) + 2.5));
			}

			if (h <= (float)(-k)) {
				c = (byte)((int)((double)(k * 2) + 2.5));
			}

			if (g >= (float)k) {
				b = (byte)(k * 2 + 1);
			}

			if (h >= (float)k) {
				c = (byte)(k * 2 + 1);
			}
		}

		this.icons.put(string, new MapIcon((byte)i, b, c, l));
	}

	public Packet createMapSyncPacket(ItemStack itemStack, World world, PlayerEntity player) {
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
		private int startX = 0;
		private int startZ = 0;
		private int endX = 127;
		private int endZ = 127;
		private int emptyPacketsRequested;
		public int field_4983;

		public PlayerUpdateTracker(PlayerEntity playerEntity) {
			this.player = playerEntity;
		}

		public Packet getPacket(ItemStack stack) {
			if (this.dirty) {
				this.dirty = false;
				return new MapUpdateS2CPacket(
					stack.getData(),
					MapState.this.scale,
					MapState.this.icons.values(),
					MapState.this.colors,
					this.startX,
					this.startZ,
					this.endX + 1 - this.startX,
					this.endZ + 1 - this.startZ
				);
			} else {
				return this.emptyPacketsRequested++ % 5 == 0
					? new MapUpdateS2CPacket(stack.getData(), MapState.this.scale, MapState.this.icons.values(), MapState.this.colors, 0, 0, 0, 0)
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
