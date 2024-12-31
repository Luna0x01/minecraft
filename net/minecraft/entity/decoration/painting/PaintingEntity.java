package net.minecraft.entity.decoration.painting;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class PaintingEntity extends AbstractDecorationEntity {
	public Painting field_3380;

	public PaintingEntity(World world) {
		super(EntityType.PAINTING, world);
	}

	public PaintingEntity(World world, BlockPos blockPos, Direction direction) {
		super(EntityType.PAINTING, world, blockPos);
		List<Painting> list = Lists.newArrayList();
		int i = 0;

		for (Painting painting : Registry.PAINTING) {
			this.field_3380 = painting;
			this.setDirection(direction);
			if (this.isPosValid()) {
				list.add(painting);
				int j = painting.method_15840() * painting.method_15841();
				if (j > i) {
					i = j;
				}
			}
		}

		if (!list.isEmpty()) {
			Iterator<Painting> iterator = list.iterator();

			while (iterator.hasNext()) {
				Painting painting2 = (Painting)iterator.next();
				if (painting2.method_15840() * painting2.method_15841() < i) {
					iterator.remove();
				}
			}

			this.field_3380 = (Painting)list.get(this.random.nextInt(list.size()));
		}

		this.setDirection(direction);
	}

	public PaintingEntity(World world, BlockPos blockPos, Direction direction, Painting painting) {
		this(world, blockPos, direction);
		this.field_3380 = painting;
		this.setDirection(direction);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putString("Motive", Registry.PAINTING.getId(this.field_3380).toString());
		super.writeCustomDataToNbt(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.field_3380 = Registry.PAINTING.get(Identifier.fromString(nbt.getString("Motive")));
		super.readCustomDataFromNbt(nbt);
	}

	@Override
	public int getWidth() {
		return this.field_3380.method_15840();
	}

	@Override
	public int getHeight() {
		return this.field_3380.method_15841();
	}

	@Override
	public void onBreak(@Nullable Entity entity) {
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.playSound(Sounds.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				if (playerEntity.abilities.creativeMode) {
					return;
				}
			}

			this.method_15560(Items.PAINTING);
		}
	}

	@Override
	public void onPlace() {
		this.playSound(Sounds.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
	}

	@Override
	public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
		this.updatePosition(x, y, z);
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		BlockPos blockPos = this.pos.add(x - this.x, y - this.y, z - this.z);
		this.updatePosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
	}
}
