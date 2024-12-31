package net.minecraft.village;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class Village {
	private World world;
	private final List<VillageDoor> doors = Lists.newArrayList();
	private BlockPos max = BlockPos.ORIGIN;
	private BlockPos min = BlockPos.ORIGIN;
	private int radius;
	private int stable;
	private int ticks;
	private int populationSize;
	private int mtick;
	private final Map<String, Integer> field_15484 = Maps.newHashMap();
	private final List<Village.Attacker> attackers = Lists.newArrayList();
	private int golems;

	public Village() {
	}

	public Village(World world) {
		this.world = world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
		this.method_2833();
		this.method_2832();
		if (ticks % 20 == 0) {
			this.method_2831();
		}

		if (ticks % 30 == 0) {
			this.method_2830();
		}

		int i = this.populationSize / 10;
		if (this.golems < i && this.doors.size() > 20 && this.world.random.nextInt(7000) == 0) {
			Entity entity = this.method_15717(this.min);
			if (entity != null) {
				this.golems++;
			}
		}
	}

	@Nullable
	private Entity method_15717(BlockPos blockPos) {
		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(this.world.random.nextInt(16) - 8, this.world.random.nextInt(6) - 3, this.world.random.nextInt(16) - 8);
			if (this.method_11052(blockPos2)) {
				IronGolemEntity ironGolemEntity = EntityType.IRON_GOLEM.method_15627(this.world, null, null, null, blockPos2, false, false);
				if (ironGolemEntity != null) {
					if (ironGolemEntity.method_15652(this.world, false) && ironGolemEntity.method_15653(this.world)) {
						this.world.method_3686(ironGolemEntity);
						return ironGolemEntity;
					}

					ironGolemEntity.remove();
				}
			}
		}

		return null;
	}

	private void method_2830() {
		List<IronGolemEntity> list = this.world
			.getEntitiesInBox(
				IronGolemEntity.class,
				new Box(
					(double)(this.min.getX() - this.radius),
					(double)(this.min.getY() - 4),
					(double)(this.min.getZ() - this.radius),
					(double)(this.min.getX() + this.radius),
					(double)(this.min.getY() + 4),
					(double)(this.min.getZ() + this.radius)
				)
			);
		this.golems = list.size();
	}

	private void method_2831() {
		List<VillagerEntity> list = this.world
			.getEntitiesInBox(
				VillagerEntity.class,
				new Box(
					(double)(this.min.getX() - this.radius),
					(double)(this.min.getY() - 4),
					(double)(this.min.getZ() - this.radius),
					(double)(this.min.getX() + this.radius),
					(double)(this.min.getY() + 4),
					(double)(this.min.getZ() + this.radius)
				)
			);
		this.populationSize = list.size();
		if (this.populationSize == 0) {
			this.field_15484.clear();
		}
	}

	public BlockPos getMinPos() {
		return this.min;
	}

	public int getRadius() {
		return this.radius;
	}

	public int getDoorsAmount() {
		return this.doors.size();
	}

	public int method_2824() {
		return this.ticks - this.stable;
	}

	public int getPopulationSize() {
		return this.populationSize;
	}

	public boolean method_11052(BlockPos pos) {
		return this.min.getSquaredDistance(pos) < (double)(this.radius * this.radius);
	}

	public List<VillageDoor> getDoors() {
		return this.doors;
	}

	public VillageDoor method_11055(BlockPos pos) {
		VillageDoor villageDoor = null;
		int i = Integer.MAX_VALUE;

		for (VillageDoor villageDoor2 : this.doors) {
			int j = villageDoor2.method_11043(pos);
			if (j < i) {
				villageDoor = villageDoor2;
				i = j;
			}
		}

		return villageDoor;
	}

	public VillageDoor method_11056(BlockPos pos) {
		VillageDoor villageDoor = null;
		int i = Integer.MAX_VALUE;

		for (VillageDoor villageDoor2 : this.doors) {
			int j = villageDoor2.method_11043(pos);
			if (j > 256) {
				j *= 1000;
			} else {
				j = villageDoor2.method_2811();
			}

			if (j < i) {
				BlockPos blockPos = villageDoor2.getPos1();
				Direction direction = villageDoor2.method_13116();
				if (this.world.getBlockState(blockPos.offset(direction, 1)).canPlaceAtSide(this.world, blockPos.offset(direction, 1), BlockPlacementEnvironment.LAND)
					&& this.world.getBlockState(blockPos.offset(direction, -1)).canPlaceAtSide(this.world, blockPos.offset(direction, -1), BlockPlacementEnvironment.LAND)
					&& this.world
						.getBlockState(blockPos.up().offset(direction, 1))
						.canPlaceAtSide(this.world, blockPos.up().offset(direction, 1), BlockPlacementEnvironment.LAND)
					&& this.world
						.getBlockState(blockPos.up().offset(direction, -1))
						.canPlaceAtSide(this.world, blockPos.up().offset(direction, -1), BlockPlacementEnvironment.LAND)) {
					villageDoor = villageDoor2;
					i = j;
				}
			}
		}

		return villageDoor;
	}

	@Nullable
	public VillageDoor method_11057(BlockPos pos) {
		if (this.min.getSquaredDistance(pos) > (double)(this.radius * this.radius)) {
			return null;
		} else {
			for (VillageDoor villageDoor : this.doors) {
				if (villageDoor.getPos1().getX() == pos.getX() && villageDoor.getPos1().getZ() == pos.getZ() && Math.abs(villageDoor.getPos1().getY() - pos.getY()) <= 1) {
					return villageDoor;
				}
			}

			return null;
		}
	}

	public void method_2817(VillageDoor door) {
		this.doors.add(door);
		this.max = this.max.add(door.getPos1());
		this.method_2834();
		this.stable = door.method_2805();
	}

	public boolean hasNoDoors() {
		return this.doors.isEmpty();
	}

	public void addAttacker(LivingEntity entity) {
		for (Village.Attacker attacker : this.attackers) {
			if (attacker.entity == entity) {
				attacker.ticks = this.ticks;
				return;
			}
		}

		this.attackers.add(new Village.Attacker(entity, this.ticks));
	}

	@Nullable
	public LivingEntity getClosestAttacker(LivingEntity entity) {
		double d = Double.MAX_VALUE;
		Village.Attacker attacker = null;

		for (int i = 0; i < this.attackers.size(); i++) {
			Village.Attacker attacker2 = (Village.Attacker)this.attackers.get(i);
			double e = attacker2.entity.squaredDistanceTo(entity);
			if (!(e > d)) {
				attacker = attacker2;
				d = e;
			}
		}

		return attacker == null ? null : attacker.entity;
	}

	public PlayerEntity method_6229(LivingEntity entity) {
		double d = Double.MAX_VALUE;
		PlayerEntity playerEntity = null;

		for (String string : this.field_15484.keySet()) {
			if (this.method_4510(string)) {
				PlayerEntity playerEntity2 = this.world.getPlayerByName(string);
				if (playerEntity2 != null) {
					double e = playerEntity2.squaredDistanceTo(entity);
					if (!(e > d)) {
						playerEntity = playerEntity2;
						d = e;
					}
				}
			}
		}

		return playerEntity;
	}

	private void method_2832() {
		Iterator<Village.Attacker> iterator = this.attackers.iterator();

		while (iterator.hasNext()) {
			Village.Attacker attacker = (Village.Attacker)iterator.next();
			if (!attacker.entity.isAlive() || Math.abs(this.ticks - attacker.ticks) > 300) {
				iterator.remove();
			}
		}
	}

	private void method_2833() {
		boolean bl = false;
		boolean bl2 = this.world.random.nextInt(50) == 0;
		Iterator<VillageDoor> iterator = this.doors.iterator();

		while (iterator.hasNext()) {
			VillageDoor villageDoor = (VillageDoor)iterator.next();
			if (bl2) {
				villageDoor.method_2809();
			}

			if (!this.method_11058(villageDoor.getPos1()) || Math.abs(this.ticks - villageDoor.method_2805()) > 1200) {
				this.max = this.max.subtract(villageDoor.getPos1());
				bl = true;
				villageDoor.method_11044(true);
				iterator.remove();
			}
		}

		if (bl) {
			this.method_2834();
		}
	}

	private boolean method_11058(BlockPos pos) {
		BlockState blockState = this.world.getBlockState(pos);
		Block block = blockState.getBlock();
		return block instanceof DoorBlock ? blockState.getMaterial() == Material.WOOD : false;
	}

	private void method_2834() {
		int i = this.doors.size();
		if (i == 0) {
			this.min = BlockPos.ORIGIN;
			this.radius = 0;
		} else {
			this.min = new BlockPos(this.max.getX() / i, this.max.getY() / i, this.max.getZ() / i);
			int j = 0;

			for (VillageDoor villageDoor : this.doors) {
				j = Math.max(villageDoor.method_11043(this.min), j);
			}

			this.radius = Math.max(32, (int)Math.sqrt((double)j) + 1);
		}
	}

	public int method_4504(String string) {
		Integer integer = (Integer)this.field_15484.get(string);
		return integer == null ? 0 : integer;
	}

	public int method_4505(String name, int i) {
		int j = this.method_4504(name);
		int k = MathHelper.clamp(j + i, -30, 10);
		this.field_15484.put(name, k);
		return k;
	}

	public boolean method_4510(String string) {
		return this.method_4504(string) <= -15;
	}

	public void fromNbt(NbtCompound nbt) {
		this.populationSize = nbt.getInt("PopSize");
		this.radius = nbt.getInt("Radius");
		this.golems = nbt.getInt("Golems");
		this.stable = nbt.getInt("Stable");
		this.ticks = nbt.getInt("Tick");
		this.mtick = nbt.getInt("MTick");
		this.min = new BlockPos(nbt.getInt("CX"), nbt.getInt("CY"), nbt.getInt("CZ"));
		this.max = new BlockPos(nbt.getInt("ACX"), nbt.getInt("ACY"), nbt.getInt("ACZ"));
		NbtList nbtList = nbt.getList("Doors", 10);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			VillageDoor villageDoor = new VillageDoor(
				new BlockPos(nbtCompound.getInt("X"), nbtCompound.getInt("Y"), nbtCompound.getInt("Z")),
				nbtCompound.getInt("IDX"),
				nbtCompound.getInt("IDZ"),
				nbtCompound.getInt("TS")
			);
			this.doors.add(villageDoor);
		}

		NbtList nbtList2 = nbt.getList("Players", 10);

		for (int j = 0; j < nbtList2.size(); j++) {
			NbtCompound nbtCompound2 = nbtList2.getCompound(j);
			if (nbtCompound2.contains("UUID") && this.world != null && this.world.getServer() != null) {
				UserCache userCache = this.world.getServer().getUserCache();
				GameProfile gameProfile = userCache.getByUuid(UUID.fromString(nbtCompound2.getString("UUID")));
				if (gameProfile != null) {
					this.field_15484.put(gameProfile.getName(), nbtCompound2.getInt("S"));
				}
			} else {
				this.field_15484.put(nbtCompound2.getString("Name"), nbtCompound2.getInt("S"));
			}
		}
	}

	public void toNbt(NbtCompound nbt) {
		nbt.putInt("PopSize", this.populationSize);
		nbt.putInt("Radius", this.radius);
		nbt.putInt("Golems", this.golems);
		nbt.putInt("Stable", this.stable);
		nbt.putInt("Tick", this.ticks);
		nbt.putInt("MTick", this.mtick);
		nbt.putInt("CX", this.min.getX());
		nbt.putInt("CY", this.min.getY());
		nbt.putInt("CZ", this.min.getZ());
		nbt.putInt("ACX", this.max.getX());
		nbt.putInt("ACY", this.max.getY());
		nbt.putInt("ACZ", this.max.getZ());
		NbtList nbtList = new NbtList();

		for (VillageDoor villageDoor : this.doors) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putInt("X", villageDoor.getPos1().getX());
			nbtCompound.putInt("Y", villageDoor.getPos1().getY());
			nbtCompound.putInt("Z", villageDoor.getPos1().getZ());
			nbtCompound.putInt("IDX", villageDoor.getOffsetX2());
			nbtCompound.putInt("IDZ", villageDoor.getOffsetZ2());
			nbtCompound.putInt("TS", villageDoor.method_2805());
			nbtList.add((NbtElement)nbtCompound);
		}

		nbt.put("Doors", nbtList);
		NbtList nbtList2 = new NbtList();

		for (String string : this.field_15484.keySet()) {
			NbtCompound nbtCompound2 = new NbtCompound();
			UserCache userCache = this.world.getServer().getUserCache();

			try {
				GameProfile gameProfile = userCache.findByName(string);
				if (gameProfile != null) {
					nbtCompound2.putString("UUID", gameProfile.getId().toString());
					nbtCompound2.putInt("S", (Integer)this.field_15484.get(string));
					nbtList2.add((NbtElement)nbtCompound2);
				}
			} catch (RuntimeException var9) {
			}
		}

		nbt.put("Players", nbtList2);
	}

	public void method_4511() {
		this.mtick = this.ticks;
	}

	public boolean method_4512() {
		return this.mtick == 0 || this.ticks - this.mtick >= 3600;
	}

	public void method_4507(int i) {
		for (String string : this.field_15484.keySet()) {
			this.method_4505(string, i);
		}
	}

	class Attacker {
		public LivingEntity entity;
		public int ticks;

		Attacker(LivingEntity livingEntity, int i) {
			this.entity = livingEntity;
			this.ticks = i;
		}
	}
}
