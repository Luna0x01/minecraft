package net.minecraft.item.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapState extends PersistentState {
	private static final Logger field_25019 = LogManager.getLogger();
	private static final int field_31832 = 128;
	private static final int field_31833 = 64;
	public static final int field_31831 = 4;
	public static final int field_33991 = 256;
	public final int centerX;
	public final int centerZ;
	public final RegistryKey<World> dimension;
	private final boolean showIcons;
	private final boolean unlimitedTracking;
	public final byte scale;
	public byte[] colors = new byte[16384];
	public final boolean locked;
	private final List<MapState.PlayerUpdateTracker> updateTrackers = Lists.newArrayList();
	private final Map<PlayerEntity, MapState.PlayerUpdateTracker> updateTrackersByPlayer = Maps.newHashMap();
	private final Map<String, MapBannerMarker> banners = Maps.newHashMap();
	final Map<String, MapIcon> icons = Maps.newLinkedHashMap();
	private final Map<String, MapFrameMarker> frames = Maps.newHashMap();
	private int field_33992;

	private MapState(int centerX, int centerZ, byte scale, boolean showIcons, boolean unlimitedTracking, boolean locked, RegistryKey<World> dimension) {
		this.scale = scale;
		this.centerX = centerX;
		this.centerZ = centerZ;
		this.dimension = dimension;
		this.showIcons = showIcons;
		this.unlimitedTracking = unlimitedTracking;
		this.locked = locked;
		this.markDirty();
	}

	public static MapState of(double centerX, double centerZ, byte scale, boolean showIcons, boolean unlimitedTracking, RegistryKey<World> dimension) {
		int i = 128 * (1 << scale);
		int j = MathHelper.floor((centerX + 64.0) / (double)i);
		int k = MathHelper.floor((centerZ + 64.0) / (double)i);
		int l = j * i + i / 2 - 64;
		int m = k * i + i / 2 - 64;
		return new MapState(l, m, scale, showIcons, unlimitedTracking, false, dimension);
	}

	public static MapState of(byte scale, boolean showIcons, RegistryKey<World> dimension) {
		return new MapState(0, 0, scale, false, false, showIcons, dimension);
	}

	public static MapState fromNbt(NbtCompound nbt) {
		RegistryKey<World> registryKey = (RegistryKey<World>)DimensionType.worldFromDimensionNbt(new Dynamic(NbtOps.INSTANCE, nbt.get("dimension")))
			.resultOrPartial(field_25019::error)
			.orElseThrow(() -> new IllegalArgumentException("Invalid map dimension: " + nbt.get("dimension")));
		int i = nbt.getInt("xCenter");
		int j = nbt.getInt("zCenter");
		byte b = (byte)MathHelper.clamp(nbt.getByte("scale"), 0, 4);
		boolean bl = !nbt.contains("trackingPosition", 1) || nbt.getBoolean("trackingPosition");
		boolean bl2 = nbt.getBoolean("unlimitedTracking");
		boolean bl3 = nbt.getBoolean("locked");
		MapState mapState = new MapState(i, j, b, bl, bl2, bl3, registryKey);
		byte[] bs = nbt.getByteArray("colors");
		if (bs.length == 16384) {
			mapState.colors = bs;
		}

		NbtList nbtList = nbt.getList("banners", 10);

		for (int k = 0; k < nbtList.size(); k++) {
			MapBannerMarker mapBannerMarker = MapBannerMarker.fromNbt(nbtList.getCompound(k));
			mapState.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
			mapState.addIcon(
				mapBannerMarker.getIconType(),
				null,
				mapBannerMarker.getKey(),
				(double)mapBannerMarker.getPos().getX(),
				(double)mapBannerMarker.getPos().getZ(),
				180.0,
				mapBannerMarker.getName()
			);
		}

		NbtList nbtList2 = nbt.getList("frames", 10);

		for (int l = 0; l < nbtList2.size(); l++) {
			MapFrameMarker mapFrameMarker = MapFrameMarker.fromNbt(nbtList2.getCompound(l));
			mapState.frames.put(mapFrameMarker.getKey(), mapFrameMarker);
			mapState.addIcon(
				MapIcon.Type.FRAME,
				null,
				"frame-" + mapFrameMarker.getEntityId(),
				(double)mapFrameMarker.getPos().getX(),
				(double)mapFrameMarker.getPos().getZ(),
				(double)mapFrameMarker.getRotation(),
				null
			);
		}

		return mapState;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		Identifier.CODEC
			.encodeStart(NbtOps.INSTANCE, this.dimension.getValue())
			.resultOrPartial(field_25019::error)
			.ifPresent(nbtElement -> nbt.put("dimension", nbtElement));
		nbt.putInt("xCenter", this.centerX);
		nbt.putInt("zCenter", this.centerZ);
		nbt.putByte("scale", this.scale);
		nbt.putByteArray("colors", this.colors);
		nbt.putBoolean("trackingPosition", this.showIcons);
		nbt.putBoolean("unlimitedTracking", this.unlimitedTracking);
		nbt.putBoolean("locked", this.locked);
		NbtList nbtList = new NbtList();

		for (MapBannerMarker mapBannerMarker : this.banners.values()) {
			nbtList.add(mapBannerMarker.getNbt());
		}

		nbt.put("banners", nbtList);
		NbtList nbtList2 = new NbtList();

		for (MapFrameMarker mapFrameMarker : this.frames.values()) {
			nbtList2.add(mapFrameMarker.toNbt());
		}

		nbt.put("frames", nbtList2);
		return nbt;
	}

	public MapState copy() {
		MapState mapState = new MapState(this.centerX, this.centerZ, this.scale, this.showIcons, this.unlimitedTracking, true, this.dimension);
		mapState.banners.putAll(this.banners);
		mapState.icons.putAll(this.icons);
		mapState.field_33992 = this.field_33992;
		System.arraycopy(this.colors, 0, mapState.colors, 0, this.colors.length);
		mapState.markDirty();
		return mapState;
	}

	public MapState zoomOut(int zoomOutScale) {
		return of(
			(double)this.centerX, (double)this.centerZ, (byte)MathHelper.clamp(this.scale + zoomOutScale, 0, 4), this.showIcons, this.unlimitedTracking, this.dimension
		);
	}

	public void update(PlayerEntity player, ItemStack stack) {
		if (!this.updateTrackersByPlayer.containsKey(player)) {
			MapState.PlayerUpdateTracker playerUpdateTracker = new MapState.PlayerUpdateTracker(player);
			this.updateTrackersByPlayer.put(player, playerUpdateTracker);
			this.updateTrackers.add(playerUpdateTracker);
		}

		if (!player.getInventory().contains(stack)) {
			this.removeIcon(player.getName().getString());
		}

		for (int i = 0; i < this.updateTrackers.size(); i++) {
			MapState.PlayerUpdateTracker playerUpdateTracker2 = (MapState.PlayerUpdateTracker)this.updateTrackers.get(i);
			String string = playerUpdateTracker2.player.getName().getString();
			if (!playerUpdateTracker2.player.isRemoved() && (playerUpdateTracker2.player.getInventory().contains(stack) || stack.isInFrame())) {
				if (!stack.isInFrame() && playerUpdateTracker2.player.world.getRegistryKey() == this.dimension && this.showIcons) {
					this.addIcon(
						MapIcon.Type.PLAYER,
						playerUpdateTracker2.player.world,
						string,
						playerUpdateTracker2.player.getX(),
						playerUpdateTracker2.player.getZ(),
						(double)playerUpdateTracker2.player.getYaw(),
						null
					);
				}
			} else {
				this.updateTrackersByPlayer.remove(playerUpdateTracker2.player);
				this.updateTrackers.remove(playerUpdateTracker2);
				this.removeIcon(string);
			}
		}

		if (stack.isInFrame() && this.showIcons) {
			ItemFrameEntity itemFrameEntity = stack.getFrame();
			BlockPos blockPos = itemFrameEntity.getDecorationBlockPos();
			MapFrameMarker mapFrameMarker = (MapFrameMarker)this.frames.get(MapFrameMarker.getKey(blockPos));
			if (mapFrameMarker != null && itemFrameEntity.getId() != mapFrameMarker.getEntityId() && this.frames.containsKey(mapFrameMarker.getKey())) {
				this.removeIcon("frame-" + mapFrameMarker.getEntityId());
			}

			MapFrameMarker mapFrameMarker2 = new MapFrameMarker(blockPos, itemFrameEntity.getHorizontalFacing().getHorizontal() * 90, itemFrameEntity.getId());
			this.addIcon(
				MapIcon.Type.FRAME,
				player.world,
				"frame-" + itemFrameEntity.getId(),
				(double)blockPos.getX(),
				(double)blockPos.getZ(),
				(double)(itemFrameEntity.getHorizontalFacing().getHorizontal() * 90),
				null
			);
			this.frames.put(mapFrameMarker2.getKey(), mapFrameMarker2);
		}

		NbtCompound nbtCompound = stack.getTag();
		if (nbtCompound != null && nbtCompound.contains("Decorations", 9)) {
			NbtList nbtList = nbtCompound.getList("Decorations", 10);

			for (int j = 0; j < nbtList.size(); j++) {
				NbtCompound nbtCompound2 = nbtList.getCompound(j);
				if (!this.icons.containsKey(nbtCompound2.getString("id"))) {
					this.addIcon(
						MapIcon.Type.byId(nbtCompound2.getByte("type")),
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

	private void removeIcon(String id) {
		MapIcon mapIcon = (MapIcon)this.icons.remove(id);
		if (mapIcon != null && mapIcon.getType().method_37342()) {
			this.field_33992--;
		}

		this.markIconsDirty();
	}

	public static void addDecorationsNbt(ItemStack stack, BlockPos pos, String id, MapIcon.Type type) {
		NbtList nbtList;
		if (stack.hasTag() && stack.getTag().contains("Decorations", 9)) {
			nbtList = stack.getTag().getList("Decorations", 10);
		} else {
			nbtList = new NbtList();
			stack.putSubTag("Decorations", nbtList);
		}

		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putByte("type", type.getId());
		nbtCompound.putString("id", id);
		nbtCompound.putDouble("x", (double)pos.getX());
		nbtCompound.putDouble("z", (double)pos.getZ());
		nbtCompound.putDouble("rot", 180.0);
		nbtList.add(nbtCompound);
		if (type.hasTintColor()) {
			NbtCompound nbtCompound2 = stack.getOrCreateSubTag("display");
			nbtCompound2.putInt("MapColor", type.getTintColor());
		}
	}

	private void addIcon(MapIcon.Type type, @Nullable WorldAccess world, String key, double x, double z, double rotation, @Nullable Text text) {
		int i = 1 << this.scale;
		float f = (float)(x - (double)this.centerX) / (float)i;
		float g = (float)(z - (double)this.centerZ) / (float)i;
		byte b = (byte)((int)((double)(f * 2.0F) + 0.5));
		byte c = (byte)((int)((double)(g * 2.0F) + 0.5));
		int j = 63;
		byte d;
		if (f >= -63.0F && g >= -63.0F && f <= 63.0F && g <= 63.0F) {
			rotation += rotation < 0.0 ? -8.0 : 8.0;
			d = (byte)((int)(rotation * 16.0 / 360.0));
			if (this.dimension == World.NETHER && world != null) {
				int k = (int)(world.getLevelProperties().getTimeOfDay() / 10L);
				d = (byte)(k * k * 34187121 + k * 121 >> 15 & 15);
			}
		} else {
			if (type != MapIcon.Type.PLAYER) {
				this.removeIcon(key);
				return;
			}

			int l = 320;
			if (Math.abs(f) < 320.0F && Math.abs(g) < 320.0F) {
				type = MapIcon.Type.PLAYER_OFF_MAP;
			} else {
				if (!this.unlimitedTracking) {
					this.removeIcon(key);
					return;
				}

				type = MapIcon.Type.PLAYER_OFF_LIMITS;
			}

			d = 0;
			if (f <= -63.0F) {
				b = -128;
			}

			if (g <= -63.0F) {
				c = -128;
			}

			if (f >= 63.0F) {
				b = 127;
			}

			if (g >= 63.0F) {
				c = 127;
			}
		}

		MapIcon mapIcon = new MapIcon(type, b, c, d, text);
		MapIcon mapIcon2 = (MapIcon)this.icons.put(key, mapIcon);
		if (!mapIcon.equals(mapIcon2)) {
			if (mapIcon2 != null && mapIcon2.getType().method_37342()) {
				this.field_33992--;
			}

			if (type.method_37342()) {
				this.field_33992++;
			}

			this.markIconsDirty();
		}
	}

	@Nullable
	public Packet<?> getPlayerMarkerPacket(int id, PlayerEntity player) {
		MapState.PlayerUpdateTracker playerUpdateTracker = (MapState.PlayerUpdateTracker)this.updateTrackersByPlayer.get(player);
		return playerUpdateTracker == null ? null : playerUpdateTracker.getPacket(id);
	}

	private void markDirty(int x, int z) {
		this.markDirty();

		for (MapState.PlayerUpdateTracker playerUpdateTracker : this.updateTrackers) {
			playerUpdateTracker.markDirty(x, z);
		}
	}

	private void markIconsDirty() {
		this.markDirty();
		this.updateTrackers.forEach(MapState.PlayerUpdateTracker::markIconsDirty);
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

	public boolean addBanner(WorldAccess world, BlockPos pos) {
		double d = (double)pos.getX() + 0.5;
		double e = (double)pos.getZ() + 0.5;
		int i = 1 << this.scale;
		double f = (d - (double)this.centerX) / (double)i;
		double g = (e - (double)this.centerZ) / (double)i;
		int j = 63;
		if (f >= -63.0 && g >= -63.0 && f <= 63.0 && g <= 63.0) {
			MapBannerMarker mapBannerMarker = MapBannerMarker.fromWorldBlock(world, pos);
			if (mapBannerMarker == null) {
				return false;
			}

			if (this.banners.remove(mapBannerMarker.getKey(), mapBannerMarker)) {
				this.removeIcon(mapBannerMarker.getKey());
				return true;
			}

			if (!this.method_37343(256)) {
				this.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
				this.addIcon(mapBannerMarker.getIconType(), world, mapBannerMarker.getKey(), d, e, 180.0, mapBannerMarker.getName());
				return true;
			}
		}

		return false;
	}

	public void removeBanner(BlockView world, int x, int z) {
		Iterator<MapBannerMarker> iterator = this.banners.values().iterator();

		while (iterator.hasNext()) {
			MapBannerMarker mapBannerMarker = (MapBannerMarker)iterator.next();
			if (mapBannerMarker.getPos().getX() == x && mapBannerMarker.getPos().getZ() == z) {
				MapBannerMarker mapBannerMarker2 = MapBannerMarker.fromWorldBlock(world, mapBannerMarker.getPos());
				if (!mapBannerMarker.equals(mapBannerMarker2)) {
					iterator.remove();
					this.removeIcon(mapBannerMarker.getKey());
				}
			}
		}
	}

	public Collection<MapBannerMarker> getBanners() {
		return this.banners.values();
	}

	public void removeFrame(BlockPos pos, int id) {
		this.removeIcon("frame-" + id);
		this.frames.remove(MapFrameMarker.getKey(pos));
	}

	public boolean putColor(int x, int z, byte color) {
		byte b = this.colors[x + z * 128];
		if (b != color) {
			this.setColor(x, z, color);
			return true;
		} else {
			return false;
		}
	}

	public void setColor(int x, int z, byte color) {
		this.colors[x + z * 128] = color;
		this.markDirty(x, z);
	}

	public boolean hasMonumentIcon() {
		for (MapIcon mapIcon : this.icons.values()) {
			if (mapIcon.getType() == MapIcon.Type.MANSION || mapIcon.getType() == MapIcon.Type.MONUMENT) {
				return true;
			}
		}

		return false;
	}

	public void replaceIcons(List<MapIcon> icons) {
		this.icons.clear();
		this.field_33992 = 0;

		for (int i = 0; i < icons.size(); i++) {
			MapIcon mapIcon = (MapIcon)icons.get(i);
			this.icons.put("icon-" + i, mapIcon);
			if (mapIcon.getType().method_37342()) {
				this.field_33992++;
			}
		}
	}

	public Iterable<MapIcon> getIcons() {
		return this.icons.values();
	}

	public boolean method_37343(int i) {
		return this.field_33992 >= i;
	}

	public class PlayerUpdateTracker {
		public final PlayerEntity player;
		private boolean dirty = true;
		private int startX;
		private int startZ;
		private int endX = 127;
		private int endZ = 127;
		private boolean iconsDirty = true;
		private int emptyPacketsRequested;
		public int field_131;

		PlayerUpdateTracker(PlayerEntity player) {
			this.player = player;
		}

		private MapState.UpdateData getMapUpdateData() {
			int i = this.startX;
			int j = this.startZ;
			int k = this.endX + 1 - this.startX;
			int l = this.endZ + 1 - this.startZ;
			byte[] bs = new byte[k * l];

			for (int m = 0; m < k; m++) {
				for (int n = 0; n < l; n++) {
					bs[m + n * k] = MapState.this.colors[i + m + (j + n) * 128];
				}
			}

			return new MapState.UpdateData(i, j, k, l, bs);
		}

		@Nullable
		Packet<?> getPacket(int mapId) {
			MapState.UpdateData updateData;
			if (this.dirty) {
				this.dirty = false;
				updateData = this.getMapUpdateData();
			} else {
				updateData = null;
			}

			Collection<MapIcon> collection;
			if (this.iconsDirty && this.emptyPacketsRequested++ % 5 == 0) {
				this.iconsDirty = false;
				collection = MapState.this.icons.values();
			} else {
				collection = null;
			}

			return collection == null && updateData == null ? null : new MapUpdateS2CPacket(mapId, MapState.this.scale, MapState.this.locked, collection, updateData);
		}

		void markDirty(int startX, int startZ) {
			if (this.dirty) {
				this.startX = Math.min(this.startX, startX);
				this.startZ = Math.min(this.startZ, startZ);
				this.endX = Math.max(this.endX, startX);
				this.endZ = Math.max(this.endZ, startZ);
			} else {
				this.dirty = true;
				this.startX = startX;
				this.startZ = startZ;
				this.endX = startX;
				this.endZ = startZ;
			}
		}

		private void markIconsDirty() {
			this.iconsDirty = true;
		}
	}

	public static class UpdateData {
		public final int startX;
		public final int startZ;
		public final int width;
		public final int height;
		public final byte[] colors;

		public UpdateData(int startX, int startZ, int width, int height, byte[] colors) {
			this.startX = startX;
			this.startZ = startZ;
			this.width = width;
			this.height = height;
			this.colors = colors;
		}

		public void setColorsTo(MapState mapState) {
			for (int i = 0; i < this.width; i++) {
				for (int j = 0; j < this.height; j++) {
					mapState.setColor(this.startX + i, this.startZ + j, this.colors[i + j * this.width]);
				}
			}
		}
	}
}
