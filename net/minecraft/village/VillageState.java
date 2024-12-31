package net.minecraft.village;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;

public class VillageState extends PersistentState {
	private World world;
	private final List<BlockPos> positions = Lists.newArrayList();
	private final List<VillageDoor> doors = Lists.newArrayList();
	private final List<Village> villages = Lists.newArrayList();
	private int tick;

	public VillageState(String string) {
		super(string);
	}

	public VillageState(World world) {
		super(getId(world.dimension));
		this.world = world;
		this.markDirty();
	}

	public void setWorld(World world) {
		this.world = world;

		for (Village village : this.villages) {
			village.setWorld(world);
		}
	}

	public void method_11061(BlockPos pos) {
		if (this.positions.size() <= 64) {
			if (!this.method_11067(pos)) {
				this.positions.add(pos);
			}
		}
	}

	public void method_2839() {
		this.tick++;

		for (Village village : this.villages) {
			village.setTicks(this.tick);
		}

		this.method_2845();
		this.method_2847();
		this.method_2849();
		if (this.tick % 400 == 0) {
			this.markDirty();
		}
	}

	private void method_2845() {
		Iterator<Village> iterator = this.villages.iterator();

		while (iterator.hasNext()) {
			Village village = (Village)iterator.next();
			if (village.hasNoDoors()) {
				iterator.remove();
				this.markDirty();
			}
		}
	}

	public List<Village> method_2843() {
		return this.villages;
	}

	public Village method_11062(BlockPos pos, int i) {
		Village village = null;
		double d = Float.MAX_VALUE;

		for (Village village2 : this.villages) {
			double e = village2.getMinPos().getSquaredDistance(pos);
			if (!(e >= d)) {
				float f = (float)(i + village2.getRadius());
				if (!(e > (double)(f * f))) {
					village = village2;
					d = e;
				}
			}
		}

		return village;
	}

	private void method_2847() {
		if (!this.positions.isEmpty()) {
			this.method_11064((BlockPos)this.positions.remove(0));
		}
	}

	private void method_2849() {
		for (int i = 0; i < this.doors.size(); i++) {
			VillageDoor villageDoor = (VillageDoor)this.doors.get(i);
			Village village = this.method_11062(villageDoor.getPos1(), 32);
			if (village == null) {
				village = new Village(this.world);
				this.villages.add(village);
				this.markDirty();
			}

			village.method_2817(villageDoor);
		}

		this.doors.clear();
	}

	private void method_11064(BlockPos pos) {
		int i = 16;
		int j = 4;
		int k = 16;

		for (int l = -16; l < 16; l++) {
			for (int m = -4; m < 4; m++) {
				for (int n = -16; n < 16; n++) {
					BlockPos blockPos = pos.add(l, m, n);
					if (this.method_11068(blockPos)) {
						VillageDoor villageDoor = this.method_11065(blockPos);
						if (villageDoor == null) {
							this.method_11066(blockPos);
						} else {
							villageDoor.method_11041(this.tick);
						}
					}
				}
			}
		}
	}

	@Nullable
	private VillageDoor method_11065(BlockPos pos) {
		for (VillageDoor villageDoor : this.doors) {
			if (villageDoor.getPos1().getX() == pos.getX() && villageDoor.getPos1().getZ() == pos.getZ() && Math.abs(villageDoor.getPos1().getY() - pos.getY()) <= 1) {
				return villageDoor;
			}
		}

		for (Village village : this.villages) {
			VillageDoor villageDoor2 = village.method_11057(pos);
			if (villageDoor2 != null) {
				return villageDoor2;
			}
		}

		return null;
	}

	private void method_11066(BlockPos blockPos) {
		Direction direction = DoorBlock.getDirection(this.world, blockPos);
		Direction direction2 = direction.getOpposite();
		int i = this.method_11063(blockPos, direction, 5);
		int j = this.method_11063(blockPos, direction2, i + 1);
		if (i != j) {
			this.doors.add(new VillageDoor(blockPos, i < j ? direction : direction2, this.tick));
		}
	}

	private int method_11063(BlockPos blockPos, Direction direction, int i) {
		int j = 0;

		for (int k = 1; k <= 5; k++) {
			if (this.world.hasDirectSunlight(blockPos.offset(direction, k))) {
				if (++j >= i) {
					return j;
				}
			}
		}

		return j;
	}

	private boolean method_11067(BlockPos pos) {
		for (BlockPos blockPos : this.positions) {
			if (blockPos.equals(pos)) {
				return true;
			}
		}

		return false;
	}

	private boolean method_11068(BlockPos blockPos) {
		BlockState blockState = this.world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		return block instanceof DoorBlock ? blockState.getMaterial() == Material.WOOD : false;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		this.tick = nbt.getInt("Tick");
		NbtList nbtList = nbt.getList("Villages", 10);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			Village village = new Village();
			village.fromNbt(nbtCompound);
			this.villages.add(village);
		}
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		nbt.putInt("Tick", this.tick);
		NbtList nbtList = new NbtList();

		for (Village village : this.villages) {
			NbtCompound nbtCompound = new NbtCompound();
			village.toNbt(nbtCompound);
			nbtList.add(nbtCompound);
		}

		nbt.put("Villages", nbtList);
		return nbt;
	}

	public static String getId(Dimension dimension) {
		return "villages" + dimension.getDimensionType().getSuffix();
	}
}
