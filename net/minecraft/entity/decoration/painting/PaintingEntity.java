package net.minecraft.entity.decoration.painting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PaintingEntity extends AbstractDecorationEntity {
	public PaintingEntity.PaintingMotive type;

	public PaintingEntity(World world) {
		super(world);
	}

	public PaintingEntity(World world, BlockPos blockPos, Direction direction) {
		super(world, blockPos);
		List<PaintingEntity.PaintingMotive> list = Lists.newArrayList();

		for (PaintingEntity.PaintingMotive paintingMotive : PaintingEntity.PaintingMotive.values()) {
			this.type = paintingMotive;
			this.setDirection(direction);
			if (this.isPosValid()) {
				list.add(paintingMotive);
			}
		}

		if (!list.isEmpty()) {
			this.type = (PaintingEntity.PaintingMotive)list.get(this.random.nextInt(list.size()));
		}

		this.setDirection(direction);
	}

	public PaintingEntity(World world, BlockPos blockPos, Direction direction, String string) {
		this(world, blockPos, direction);

		for (PaintingEntity.PaintingMotive paintingMotive : PaintingEntity.PaintingMotive.values()) {
			if (paintingMotive.name.equals(string)) {
				this.type = paintingMotive;
				break;
			}
		}

		this.setDirection(direction);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putString("Motive", this.type.name);
		super.writeCustomDataToNbt(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		String string = nbt.getString("Motive");

		for (PaintingEntity.PaintingMotive paintingMotive : PaintingEntity.PaintingMotive.values()) {
			if (paintingMotive.name.equals(string)) {
				this.type = paintingMotive;
			}
		}

		if (this.type == null) {
			this.type = PaintingEntity.PaintingMotive.KEBAB;
		}

		super.readCustomDataFromNbt(nbt);
	}

	@Override
	public int getWidth() {
		return this.type.width;
	}

	@Override
	public int getHeight() {
		return this.type.height;
	}

	@Override
	public void onBreak(Entity entity) {
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				if (playerEntity.abilities.creativeMode) {
					return;
				}
			}

			this.dropItem(new ItemStack(Items.PAINTING), 0.0F);
		}
	}

	@Override
	public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
		BlockPos blockPos = this.pos.add(x - this.x, y - this.y, z - this.z);
		this.updatePosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		BlockPos blockPos = this.pos.add(x - this.x, y - this.y, z - this.z);
		this.updatePosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
	}

	public static enum PaintingMotive {
		KEBAB("Kebab", 16, 16, 0, 0),
		AZTEC("Aztec", 16, 16, 16, 0),
		ALBAN("Alban", 16, 16, 32, 0),
		AZTEC2("Aztec2", 16, 16, 48, 0),
		BOMB("Bomb", 16, 16, 64, 0),
		PLANT("Plant", 16, 16, 80, 0),
		WASTELAND("Wasteland", 16, 16, 96, 0),
		POOL("Pool", 32, 16, 0, 32),
		COURBET("Courbet", 32, 16, 32, 32),
		SEA("Sea", 32, 16, 64, 32),
		SUNSET("Sunset", 32, 16, 96, 32),
		CREEBET("Creebet", 32, 16, 128, 32),
		WANDERER("Wanderer", 16, 32, 0, 64),
		GRAHAM("Graham", 16, 32, 16, 64),
		MATCH("Match", 32, 32, 0, 128),
		BUST("Bust", 32, 32, 32, 128),
		STAGE("Stage", 32, 32, 64, 128),
		VOID("Void", 32, 32, 96, 128),
		SKULL_AND_ROSES("SkullAndRoses", 32, 32, 128, 128),
		WITHER("Wither", 32, 32, 160, 128),
		FIGHTERS("Fighters", 64, 32, 0, 96),
		POINTER("Pointer", 64, 64, 0, 192),
		PIGSCENE("Pigscene", 64, 64, 64, 192),
		BURNING_SKULL("BurningSkull", 64, 64, 128, 192),
		SKELETON("Skeleton", 64, 48, 192, 64),
		DONKEY_KONG("DonkeyKong", 64, 48, 192, 112);

		public static final int LENGTH = "SkullAndRoses".length();
		public final String name;
		public final int width;
		public final int height;
		public final int textureX;
		public final int textureY;

		private PaintingMotive(String string2, int j, int k, int l, int m) {
			this.name = string2;
			this.width = j;
			this.height = k;
			this.textureX = l;
			this.textureY = m;
		}
	}
}
