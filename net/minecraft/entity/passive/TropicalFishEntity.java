package net.minecraft.entity.passive;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class TropicalFishEntity extends SchoolableFishEntity {
	private static final TrackedData<Integer> field_16935 = DataTracker.registerData(TropicalFishEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Identifier[] field_16936 = new Identifier[]{
		new Identifier("textures/entity/fish/tropical_a.png"), new Identifier("textures/entity/fish/tropical_b.png")
	};
	private static final Identifier[] field_16932 = new Identifier[]{
		new Identifier("textures/entity/fish/tropical_a_pattern_1.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_2.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_3.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_4.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_5.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_6.png")
	};
	private static final Identifier[] field_16933 = new Identifier[]{
		new Identifier("textures/entity/fish/tropical_b_pattern_1.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_2.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_3.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_4.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_5.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_6.png")
	};
	public static final int[] field_16931 = new int[]{
		method_15774(TropicalFishEntity.class_3493.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY),
		method_15774(TropicalFishEntity.class_3493.FLOPPER, DyeColor.GRAY, DyeColor.GRAY),
		method_15774(TropicalFishEntity.class_3493.FLOPPER, DyeColor.GRAY, DyeColor.BLUE),
		method_15774(TropicalFishEntity.class_3493.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY),
		method_15774(TropicalFishEntity.class_3493.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY),
		method_15774(TropicalFishEntity.class_3493.KOB, DyeColor.ORANGE, DyeColor.WHITE),
		method_15774(TropicalFishEntity.class_3493.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE),
		method_15774(TropicalFishEntity.class_3493.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW),
		method_15774(TropicalFishEntity.class_3493.CLAYFISH, DyeColor.WHITE, DyeColor.RED),
		method_15774(TropicalFishEntity.class_3493.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW),
		method_15774(TropicalFishEntity.class_3493.GLITTER, DyeColor.WHITE, DyeColor.GRAY),
		method_15774(TropicalFishEntity.class_3493.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE),
		method_15774(TropicalFishEntity.class_3493.DASHER, DyeColor.CYAN, DyeColor.PINK),
		method_15774(TropicalFishEntity.class_3493.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE),
		method_15774(TropicalFishEntity.class_3493.BETTY, DyeColor.RED, DyeColor.WHITE),
		method_15774(TropicalFishEntity.class_3493.SNOOPER, DyeColor.GRAY, DyeColor.RED),
		method_15774(TropicalFishEntity.class_3493.BLOCKFISH, DyeColor.RED, DyeColor.WHITE),
		method_15774(TropicalFishEntity.class_3493.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW),
		method_15774(TropicalFishEntity.class_3493.KOB, DyeColor.RED, DyeColor.WHITE),
		method_15774(TropicalFishEntity.class_3493.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE),
		method_15774(TropicalFishEntity.class_3493.DASHER, DyeColor.CYAN, DyeColor.YELLOW),
		method_15774(TropicalFishEntity.class_3493.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)
	};
	private boolean field_16934 = true;

	private static int method_15774(TropicalFishEntity.class_3493 arg, DyeColor dyeColor, DyeColor dyeColor2) {
		return arg.method_15790() & 0xFF | (arg.method_15792() & 0xFF) << 8 | (dyeColor.getId() & 0xFF) << 16 | (dyeColor2.getId() & 0xFF) << 24;
	}

	public TropicalFishEntity(World world) {
		super(EntityType.TROPICAL_FISH, world);
		this.setBounds(0.5F, 0.4F);
	}

	public static String method_15775(int i) {
		return "entity.minecraft.tropical_fish.predefined." + i;
	}

	public static DyeColor method_15776(int i) {
		return DyeColor.byId(method_15787(i));
	}

	public static DyeColor method_15783(int i) {
		return DyeColor.byId(method_15788(i));
	}

	public static String method_15784(int i) {
		int j = method_15786(i);
		int k = method_15789(i);
		return "entity.minecraft.tropical_fish.type." + TropicalFishEntity.class_3493.method_15791(j, k);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_16935, 0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Variant", this.method_15777());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.method_15785(nbt.getInt("Variant"));
	}

	public void method_15785(int i) {
		this.dataTracker.set(field_16935, i);
	}

	@Override
	public boolean method_15654(int i) {
		return !this.field_16934;
	}

	public int method_15777() {
		return this.dataTracker.get(field_16935);
	}

	@Override
	protected void method_15725(ItemStack itemStack) {
		super.method_15725(itemStack);
		NbtCompound nbtCompound = itemStack.getOrCreateNbt();
		nbtCompound.putInt("BucketVariantTag", this.method_15777());
	}

	@Override
	protected ItemStack method_15726() {
		return new ItemStack(Items.TROPICAL_FISH_BUCKET);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.TROPICAL_FISH_ENTITIE;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_TROPICAL_FISH_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_TROPICAL_FISH_DEATH;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_TROPICAL_FISH_HURT;
	}

	@Override
	protected Sound method_15724() {
		return Sounds.ENTITY_TROPICAL_FISH_FLOP;
	}

	private static int method_15787(int i) {
		return (i & 0xFF0000) >> 16;
	}

	public float[] method_15778() {
		return DyeColor.byId(method_15787(this.method_15777())).getColorComponents();
	}

	private static int method_15788(int i) {
		return (i & 0xFF000000) >> 24;
	}

	public float[] method_15779() {
		return DyeColor.byId(method_15788(this.method_15777())).getColorComponents();
	}

	public static int method_15786(int i) {
		return Math.min(i & 0xFF, 1);
	}

	public int method_15780() {
		return method_15786(this.method_15777());
	}

	private static int method_15789(int i) {
		return Math.min((i & 0xFF00) >> 8, 5);
	}

	public Identifier method_15781() {
		return method_15786(this.method_15777()) == 0 ? field_16932[method_15789(this.method_15777())] : field_16933[method_15789(this.method_15777())];
	}

	public Identifier method_15782() {
		return field_16936[method_15786(this.method_15777())];
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		entityData = super.initialize(difficulty, entityData, nbt);
		if (nbt != null && nbt.contains("BucketVariantTag", 3)) {
			this.method_15785(nbt.getInt("BucketVariantTag"));
			return entityData;
		} else {
			int i;
			int j;
			int k;
			int l;
			if (entityData instanceof TropicalFishEntity.class_3494) {
				TropicalFishEntity.class_3494 lv = (TropicalFishEntity.class_3494)entityData;
				i = lv.field_16953;
				j = lv.field_16954;
				k = lv.field_16955;
				l = lv.field_16956;
			} else if ((double)this.random.nextFloat() < 0.9) {
				int m = field_16931[this.random.nextInt(field_16931.length)];
				i = m & 0xFF;
				j = (m & 0xFF00) >> 8;
				k = (m & 0xFF0000) >> 16;
				l = (m & 0xFF000000) >> 24;
				entityData = new TropicalFishEntity.class_3494(this, i, j, k, l);
			} else {
				this.field_16934 = false;
				i = this.random.nextInt(2);
				j = this.random.nextInt(6);
				k = this.random.nextInt(15);
				l = this.random.nextInt(15);
			}

			this.method_15785(i | j << 8 | k << 16 | l << 24);
			return entityData;
		}
	}

	static enum class_3493 {
		KOB(0, 0),
		SUNSTREAK(0, 1),
		SNOOPER(0, 2),
		DASHER(0, 3),
		BRINELY(0, 4),
		SPOTTY(0, 5),
		FLOPPER(1, 0),
		STRIPEY(1, 1),
		GLITTER(1, 2),
		BLOCKFISH(1, 3),
		BETTY(1, 4),
		CLAYFISH(1, 5);

		private final int field_16949;
		private final int field_16950;
		private static final TropicalFishEntity.class_3493[] field_16951 = values();

		private class_3493(int j, int k) {
			this.field_16949 = j;
			this.field_16950 = k;
		}

		public int method_15790() {
			return this.field_16949;
		}

		public int method_15792() {
			return this.field_16950;
		}

		public static String method_15791(int i, int j) {
			return field_16951[j + 6 * i].method_15793();
		}

		public String method_15793() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}

	static class class_3494 extends SchoolableFishEntity.FishEntityData {
		private final int field_16953;
		private final int field_16954;
		private final int field_16955;
		private final int field_16956;

		private class_3494(TropicalFishEntity tropicalFishEntity, int i, int j, int k, int l) {
			super(tropicalFishEntity);
			this.field_16953 = i;
			this.field_16954 = j;
			this.field_16955 = k;
			this.field_16956 = l;
		}
	}
}
