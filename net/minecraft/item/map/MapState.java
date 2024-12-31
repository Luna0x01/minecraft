package net.minecraft.item.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3082;
import net.minecraft.class_4066;
import net.minecraft.class_4067;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;

public class MapState extends PersistentState {
	public int xCenter;
	public int zCenter;
	public DimensionType field_19747;
	public boolean trackingPosition;
	public boolean field_15238;
	public byte scale;
	public byte[] colors = new byte[16384];
	public List<MapState.PlayerUpdateTracker> updateTrackers = Lists.newArrayList();
	private final Map<PlayerEntity, MapState.PlayerUpdateTracker> updateTrackersByPlayer = Maps.newHashMap();
	private final Map<String, class_4066> field_19748 = Maps.newHashMap();
	public Map<String, class_3082> icons = Maps.newLinkedHashMap();
	private final Map<String, class_4067> field_19749 = Maps.newHashMap();

	public MapState(String string) {
		super(string);
	}

	public void method_17931(int i, int j, int k, boolean bl, boolean bl2, DimensionType dimensionType) {
		this.scale = (byte)k;
		this.method_9308((double)i, (double)j, this.scale);
		this.field_19747 = dimensionType;
		this.trackingPosition = bl;
		this.field_15238 = bl2;
		this.markDirty();
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
		this.field_19747 = DimensionType.method_17195(nbt.getInt("dimension"));
		this.xCenter = nbt.getInt("xCenter");
		this.zCenter = nbt.getInt("zCenter");
		this.scale = (byte)MathHelper.clamp(nbt.getByte("scale"), 0, 4);
		this.trackingPosition = !nbt.contains("trackingPosition", 1) || nbt.getBoolean("trackingPosition");
		this.field_15238 = nbt.getBoolean("unlimitedTracking");
		this.colors = nbt.getByteArray("colors");
		if (this.colors.length != 16384) {
			this.colors = new byte[16384];
		}

		NbtList nbtList = nbt.getList("banners", 10);

		for (int i = 0; i < nbtList.size(); i++) {
			class_4066 lv = class_4066.method_17917(nbtList.getCompound(i));
			this.field_19748.put(lv.method_17922(), lv);
			this.method_4126(lv.method_17919(), null, lv.method_17922(), (double)lv.method_17915().getX(), (double)lv.method_17915().getZ(), 180.0, lv.method_17920());
		}

		NbtList nbtList2 = nbt.getList("frames", 10);

		for (int j = 0; j < nbtList2.size(); j++) {
			class_4067 lv2 = class_4067.method_17926(nbtList2.getCompound(j));
			this.field_19749.put(lv2.method_17930(), lv2);
			this.method_4126(
				class_3082.class_3083.FRAME,
				null,
				"frame-" + lv2.method_17929(),
				(double)lv2.method_17927().getX(),
				(double)lv2.method_17927().getZ(),
				(double)lv2.method_17928(),
				null
			);
		}
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		nbt.putInt("dimension", this.field_19747.method_17201());
		nbt.putInt("xCenter", this.xCenter);
		nbt.putInt("zCenter", this.zCenter);
		nbt.putByte("scale", this.scale);
		nbt.putByteArray("colors", this.colors);
		nbt.putBoolean("trackingPosition", this.trackingPosition);
		nbt.putBoolean("unlimitedTracking", this.field_15238);
		NbtList nbtList = new NbtList();

		for (class_4066 lv : this.field_19748.values()) {
			nbtList.add((NbtElement)lv.method_17921());
		}

		nbt.put("banners", nbtList);
		NbtList nbtList2 = new NbtList();

		for (class_4067 lv2 : this.field_19749.values()) {
			nbtList2.add((NbtElement)lv2.method_17924());
		}

		nbt.put("frames", nbtList2);
		return nbt;
	}

	public void update(PlayerEntity player, ItemStack stack) {
		if (!this.updateTrackersByPlayer.containsKey(player)) {
			MapState.PlayerUpdateTracker playerUpdateTracker = new MapState.PlayerUpdateTracker(player);
			this.updateTrackersByPlayer.put(player, playerUpdateTracker);
			this.updateTrackers.add(playerUpdateTracker);
		}

		if (!player.inventory.contains(stack)) {
			this.icons.remove(player.method_15540().getString());
		}

		for (int i = 0; i < this.updateTrackers.size(); i++) {
			MapState.PlayerUpdateTracker playerUpdateTracker2 = (MapState.PlayerUpdateTracker)this.updateTrackers.get(i);
			String string = playerUpdateTracker2.player.method_15540().getString();
			if (!playerUpdateTracker2.player.removed && (playerUpdateTracker2.player.inventory.contains(stack) || stack.isInItemFrame())) {
				if (!stack.isInItemFrame() && playerUpdateTracker2.player.field_16696 == this.field_19747 && this.trackingPosition) {
					this.method_4126(
						class_3082.class_3083.PLAYER,
						playerUpdateTracker2.player.world,
						string,
						playerUpdateTracker2.player.x,
						playerUpdateTracker2.player.z,
						(double)playerUpdateTracker2.player.yaw,
						null
					);
				}
			} else {
				this.updateTrackersByPlayer.remove(playerUpdateTracker2.player);
				this.updateTrackers.remove(playerUpdateTracker2);
				this.icons.remove(string);
			}
		}

		if (stack.isInItemFrame() && this.trackingPosition) {
			ItemFrameEntity itemFrameEntity = stack.getItemFrame();
			BlockPos blockPos = itemFrameEntity.getTilePos();
			class_4067 lv = (class_4067)this.field_19749.get(class_4067.method_17925(blockPos));
			if (lv != null && itemFrameEntity.getEntityId() != lv.method_17929() && this.field_19749.containsKey(lv.method_17930())) {
				this.icons.remove("frame-" + lv.method_17929());
			}

			class_4067 lv2 = new class_4067(blockPos, itemFrameEntity.direction.getHorizontal() * 90, itemFrameEntity.getEntityId());
			this.method_4126(
				class_3082.class_3083.FRAME,
				player.world,
				"frame-" + itemFrameEntity.getEntityId(),
				(double)blockPos.getX(),
				(double)blockPos.getZ(),
				(double)(itemFrameEntity.direction.getHorizontal() * 90),
				null
			);
			this.field_19749.put(lv2.method_17930(), lv2);
		}

		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("Decorations", 9)) {
			NbtList nbtList = nbtCompound.getList("Decorations", 10);

			for (int j = 0; j < nbtList.size(); j++) {
				NbtCompound nbtCompound2 = nbtList.getCompound(j);
				if (!this.icons.containsKey(nbtCompound2.getString("id"))) {
					this.method_4126(
						class_3082.class_3083.method_13826(nbtCompound2.getByte("type")),
						player.world,
						nbtCompound2.getString("id"),
						nbtCompound2.getDouble("x"),
						nbtCompound2.getDouble("z"),
						nbtCompound2.getDouble("rot"),
						null
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
			itemStack.addNbt("Decorations", nbtList);
		}

		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putByte("type", arg.method_13825());
		nbtCompound.putString("id", string);
		nbtCompound.putDouble("x", (double)blockPos.getX());
		nbtCompound.putDouble("z", (double)blockPos.getZ());
		nbtCompound.putDouble("rot", 180.0);
		nbtList.add((NbtElement)nbtCompound);
		if (arg.method_13828()) {
			NbtCompound nbtCompound2 = itemStack.getOrCreateNbtCompound("display");
			nbtCompound2.putInt("MapColor", arg.method_13829());
		}
	}

	private void method_4126(class_3082.class_3083 arg, @Nullable IWorld iWorld, String string, double d, double e, double f, @Nullable Text text) {
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
			if (this.field_19747 == DimensionType.THE_NETHER && iWorld != null) {
				int l = (int)(iWorld.method_3588().getTimeOfDay() / 10L);
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

		this.icons.put(string, new class_3082(arg, b, c, k, text));
	}

	@Nullable
	public Packet<?> method_17932(ItemStack itemStack, BlockView blockView, PlayerEntity playerEntity) {
		MapState.PlayerUpdateTracker playerUpdateTracker = (MapState.PlayerUpdateTracker)this.updateTrackersByPlayer.get(playerEntity);
		return playerUpdateTracker == null ? null : playerUpdateTracker.getPacket(itemStack);
	}

	public void markDirty(int x, int z) {
		this.markDirty();

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

	public void method_17934(IWorld iWorld, BlockPos blockPos) {
		float f = (float)blockPos.getX() + 0.5F;
		float g = (float)blockPos.getZ() + 0.5F;
		int i = 1 << this.scale;
		float h = (f - (float)this.xCenter) / (float)i;
		float j = (g - (float)this.zCenter) / (float)i;
		int k = 63;
		boolean bl = false;
		if (h >= -63.0F && j >= -63.0F && h <= 63.0F && j <= 63.0F) {
			class_4066 lv = class_4066.method_17916(iWorld, blockPos);
			if (lv == null) {
				return;
			}

			boolean bl2 = true;
			if (this.field_19748.containsKey(lv.method_17922()) && ((class_4066)this.field_19748.get(lv.method_17922())).equals(lv)) {
				this.field_19748.remove(lv.method_17922());
				this.icons.remove(lv.method_17922());
				bl2 = false;
				bl = true;
			}

			if (bl2) {
				this.field_19748.put(lv.method_17922(), lv);
				this.method_4126(lv.method_17919(), iWorld, lv.method_17922(), (double)f, (double)g, 180.0, lv.method_17920());
				bl = true;
			}

			if (bl) {
				this.markDirty();
			}
		}
	}

	public void method_17933(BlockView blockView, int i, int j) {
		Iterator<class_4066> iterator = this.field_19748.values().iterator();

		while (iterator.hasNext()) {
			class_4066 lv = (class_4066)iterator.next();
			if (lv.method_17915().getX() == i && lv.method_17915().getZ() == j) {
				class_4066 lv2 = class_4066.method_17916(blockView, lv.method_17915());
				if (!lv.equals(lv2)) {
					iterator.remove();
					this.icons.remove(lv.method_17922());
				}
			}
		}
	}

	public void method_17935(BlockPos blockPos, int i) {
		this.icons.remove("frame-" + i);
		this.field_19749.remove(class_4067.method_17925(blockPos));
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
					FilledMapItem.method_16117(stack),
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
						FilledMapItem.method_16117(stack), MapState.this.scale, MapState.this.trackingPosition, MapState.this.icons.values(), MapState.this.colors, 0, 0, 0, 0
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
