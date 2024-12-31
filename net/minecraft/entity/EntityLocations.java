package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3804;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;

public class EntityLocations {
	private static final Map<EntityType<?>, EntityLocations.class_3463> field_16823 = Maps.newHashMap();

	private static void method_15659(EntityType<?> entityType, EntityLocations.class_3464 arg, class_3804.class_3805 arg2) {
		method_15660(entityType, arg, arg2, null);
	}

	private static void method_15660(EntityType<?> entityType, EntityLocations.class_3464 arg, class_3804.class_3805 arg2, @Nullable Tag<Block> tag) {
		field_16823.put(entityType, new EntityLocations.class_3463(arg2, arg, tag));
	}

	@Nullable
	public static EntityLocations.class_3464 method_15658(EntityType<? extends MobEntity> entityType) {
		EntityLocations.class_3463 lv = (EntityLocations.class_3463)field_16823.get(entityType);
		return lv == null ? null : lv.field_16825;
	}

	public static class_3804.class_3805 method_15662(@Nullable EntityType<? extends MobEntity> entityType) {
		EntityLocations.class_3463 lv = (EntityLocations.class_3463)field_16823.get(entityType);
		return lv == null ? class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES : lv.field_16824;
	}

	public static boolean method_15661(EntityType<? extends MobEntity> entityType, BlockState blockState) {
		EntityLocations.class_3463 lv = (EntityLocations.class_3463)field_16823.get(entityType);
		return lv == null ? false : lv.field_16826 != null && blockState.isIn(lv.field_16826);
	}

	static {
		method_15659(EntityType.COD, EntityLocations.class_3464.IN_WATER, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.DOLPHIN, EntityLocations.class_3464.IN_WATER, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.DROWNED, EntityLocations.class_3464.IN_WATER, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.GUARDIAN, EntityLocations.class_3464.IN_WATER, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.PUFFERFISH, EntityLocations.class_3464.IN_WATER, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.SALMON, EntityLocations.class_3464.IN_WATER, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.SQUID, EntityLocations.class_3464.IN_WATER, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.TROPICAL_FISH, EntityLocations.class_3464.IN_WATER, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15660(EntityType.OCELOT, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING, BlockTags.LEAVES);
		method_15660(EntityType.PARROT, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING, BlockTags.LEAVES);
		method_15660(EntityType.POLAR_BEAR, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES, BlockTags.ICE);
		method_15659(EntityType.BAT, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.BLAZE, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.CAVE_SPIDER, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.CHICKEN, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.COW, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.CREEPER, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.DONKEY, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.ENDERMAN, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.ENDERMITE, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.ENDER_DRAGON, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.GHAST, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.GIANT, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.HORSE, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.HUSK, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.LLAMA, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.MAGMA_CUBE, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.MOOSHROOM, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.MULE, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.PIG, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.RABBIT, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.SHEEP, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.SILVERFISH, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.SKELETON, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.SKELETON_HORSE, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.SLIME, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.SNOW_GOLEM, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.SPIDER, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.STRAY, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.TURTLE, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.VILLAGER, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.IRON_GOLEM, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.WITCH, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.WITHER, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.WITHER_SKELETON, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.WOLF, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.ZOMBIE, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.ZOMBIE_HORSE, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.ZOMBIE_PIGMAN, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
		method_15659(EntityType.ZOMBIE_VILLAGER, EntityLocations.class_3464.ON_GROUND, class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES);
	}

	static class class_3463 {
		private final class_3804.class_3805 field_16824;
		private final EntityLocations.class_3464 field_16825;
		@Nullable
		private final Tag<Block> field_16826;

		public class_3463(class_3804.class_3805 arg, EntityLocations.class_3464 arg2, @Nullable Tag<Block> tag) {
			this.field_16824 = arg;
			this.field_16825 = arg2;
			this.field_16826 = tag;
		}
	}

	public static enum class_3464 {
		ON_GROUND,
		IN_WATER;
	}
}
