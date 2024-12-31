package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class class_2975 implements WorldEventListener {
	private final List<EntityNavigation> field_14609 = Lists.newArrayList();

	@Override
	public void method_11493(World world, BlockPos position, BlockState blockState, BlockState blockState2, int i) {
		if (this.method_13115(world, position, blockState, blockState2)) {
			int j = 0;

			for (int k = this.field_14609.size(); j < k; j++) {
				EntityNavigation entityNavigation = (EntityNavigation)this.field_14609.get(j);
				if (entityNavigation != null && !entityNavigation.method_13111()) {
					PathMinHeap pathMinHeap = entityNavigation.method_13113();
					if (pathMinHeap != null && !pathMinHeap.method_11930() && pathMinHeap.method_11936() != 0) {
						PathNode pathNode = entityNavigation.field_14599.method_11934();
						double d = position.squaredDistanceTo(
							((double)pathNode.posX + entityNavigation.mob.x) / 2.0,
							((double)pathNode.posY + entityNavigation.mob.y) / 2.0,
							((double)pathNode.posZ + entityNavigation.mob.z) / 2.0
						);
						int l = (pathMinHeap.method_11936() - pathMinHeap.method_11937()) * (pathMinHeap.method_11936() - pathMinHeap.method_11937());
						if (d < (double)l) {
							entityNavigation.method_13112();
						}
					}
				}
			}
		}
	}

	protected boolean method_13115(World world, BlockPos blockPos, BlockState blockState, BlockState blockState2) {
		Box box = blockState.getCollisionBox(world, blockPos);
		Box box2 = blockState2.getCollisionBox(world, blockPos);
		return box != box2 && (box == null || !box.equals(box2));
	}

	@Override
	public void onLightUpdate(BlockPos pos) {
	}

	@Override
	public void onRenderRegionUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
	}

	@Override
	public void method_3747(@Nullable PlayerEntity playerEntity, Sound sound, SoundCategory soundCategory, double d, double e, double f, float g, float h) {
	}

	@Override
	public void addParticle(int id, boolean bl, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... args) {
	}

	@Override
	public void onEntitySpawned(Entity entity) {
		if (entity instanceof MobEntity) {
			this.field_14609.add(((MobEntity)entity).getNavigation());
		}
	}

	@Override
	public void onEntityRemoved(Entity entity) {
		if (entity instanceof MobEntity) {
			this.field_14609.remove(((MobEntity)entity).getNavigation());
		}
	}

	@Override
	public void method_8572(Sound sound, BlockPos blockPos) {
	}

	@Override
	public void processGlobalEvent(int eventId, BlockPos pos, int j) {
	}

	@Override
	public void processWorldEvent(PlayerEntity player, int eventId, BlockPos pos, int data) {
	}

	@Override
	public void setBlockBreakInfo(int entityId, BlockPos pos, int progress) {
	}
}
