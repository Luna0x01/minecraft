package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_3558;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.DonkeyEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MuleEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.HorseArmorType;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class HorseBaseEntity extends AbstractHorseEntity {
	private static final UUID field_14628 = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
	private static final TrackedData<Integer> field_14631 = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_14633 = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final String[] HORSE_TEXTURES = new String[]{
		"textures/entity/horse/horse_white.png",
		"textures/entity/horse/horse_creamy.png",
		"textures/entity/horse/horse_chestnut.png",
		"textures/entity/horse/horse_brown.png",
		"textures/entity/horse/horse_black.png",
		"textures/entity/horse/horse_gray.png",
		"textures/entity/horse/horse_darkbrown.png"
	};
	private static final String[] field_6879 = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
	private static final String[] HORSE_MARKINGS_TEXTURES = new String[]{
		null,
		"textures/entity/horse/horse_markings_white.png",
		"textures/entity/horse/horse_markings_whitefield.png",
		"textures/entity/horse/horse_markings_whitedots.png",
		"textures/entity/horse/horse_markings_blackdots.png"
	};
	private static final String[] field_6881 = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
	private String field_6895;
	private final String[] field_6896 = new String[3];

	public HorseBaseEntity(World world) {
		super(EntityType.HORSE, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14631, 0);
		this.dataTracker.startTracking(field_14633, HorseArmorType.NONE.method_13134());
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Variant", this.getVariant());
		if (!this.animalInventory.getInvStack(1).isEmpty()) {
			nbt.put("ArmorItem", this.animalInventory.getInvStack(1).toNbt(new NbtCompound()));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setVariant(nbt.getInt("Variant"));
		if (nbt.contains("ArmorItem", 10)) {
			ItemStack itemStack = ItemStack.from(nbt.getCompound("ArmorItem"));
			if (!itemStack.isEmpty() && HorseArmorType.method_13139(itemStack.getItem())) {
				this.animalInventory.setInvStack(1, itemStack);
			}
		}

		this.method_6244();
	}

	public void setVariant(int variant) {
		this.dataTracker.set(field_14631, variant);
		this.method_6245();
	}

	public int getVariant() {
		return this.dataTracker.get(field_14631);
	}

	private void method_6245() {
		this.field_6895 = null;
	}

	private void method_6246() {
		int i = this.getVariant();
		int j = (i & 0xFF) % 7;
		int k = ((i & 0xFF00) >> 8) % 5;
		HorseArmorType horseArmorType = this.method_13131();
		this.field_6896[0] = HORSE_TEXTURES[j];
		this.field_6896[1] = HORSE_MARKINGS_TEXTURES[k];
		this.field_6896[2] = horseArmorType.getEntityTexture();
		this.field_6895 = "horse/" + field_6879[j] + field_6881[k] + horseArmorType.method_13138();
	}

	public String method_6272() {
		if (this.field_6895 == null) {
			this.method_6246();
		}

		return this.field_6895;
	}

	public String[] method_6273() {
		if (this.field_6895 == null) {
			this.method_6246();
		}

		return this.field_6896;
	}

	@Override
	protected void method_6244() {
		super.method_6244();
		this.method_8390(this.animalInventory.getInvStack(1));
	}

	public void method_8390(ItemStack itemStack) {
		HorseArmorType horseArmorType = HorseArmorType.method_13137(itemStack);
		this.dataTracker.set(field_14633, horseArmorType.method_13134());
		this.method_6245();
		if (!this.world.isClient) {
			this.initializeAttribute(EntityAttributes.GENERIC_ARMOR).method_13093(field_14628);
			int i = horseArmorType.getBonus();
			if (i != 0) {
				this.initializeAttribute(EntityAttributes.GENERIC_ARMOR)
					.addModifier(new AttributeModifier(field_14628, "Horse armor bonus", (double)i, 0).setSerialized(false));
			}
		}
	}

	public HorseArmorType method_13131() {
		return HorseArmorType.method_13135(this.dataTracker.get(field_14633));
	}

	@Override
	public void method_13928(Inventory inventory) {
		HorseArmorType horseArmorType = this.method_13131();
		super.method_13928(inventory);
		HorseArmorType horseArmorType2 = this.method_13131();
		if (this.ticksAlive > 20 && horseArmorType != horseArmorType2 && horseArmorType2 != HorseArmorType.NONE) {
			this.playSound(Sounds.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
		}
	}

	@Override
	protected void method_13967(BlockSoundGroup blockSoundGroup) {
		super.method_13967(blockSoundGroup);
		if (this.random.nextInt(10) == 0) {
			this.playSound(Sounds.ENTITY_HORSE_BREATHE, blockSoundGroup.getVolume() * 0.6F, blockSoundGroup.getPitch());
		}
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue((double)this.method_13981());
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(this.method_13983());
		this.initializeAttribute(field_15508).setBaseValue(this.method_13982());
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient && this.dataTracker.isDirty()) {
			this.dataTracker.clearDirty();
			this.method_6245();
		}
	}

	@Override
	protected Sound ambientSound() {
		super.ambientSound();
		return Sounds.ENTITY_HORSE_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		super.deathSound();
		return Sounds.ENTITY_HORSE_DEATH;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		super.getHurtSound(damageSource);
		return Sounds.ENTITY_HORSE_HURT;
	}

	@Override
	protected Sound method_13132() {
		super.method_13132();
		return Sounds.ENTITY_HORSE_ANGRY;
	}

	@Override
	protected Identifier getLootTableId() {
		return LootTables.HORSE_ENTITIE;
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		boolean bl = !itemStack.isEmpty();
		if (bl && itemStack.getItem() instanceof class_3558) {
			return super.interactMob(playerEntity, hand);
		} else {
			if (!this.isBaby()) {
				if (this.method_13990() && playerEntity.isSneaking()) {
					this.method_14000(playerEntity);
					return true;
				}

				if (this.hasPassengers()) {
					return super.interactMob(playerEntity, hand);
				}
			}

			if (bl) {
				if (this.method_13970(playerEntity, itemStack)) {
					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					return true;
				}

				if (itemStack.method_6329(playerEntity, this, hand)) {
					return true;
				}

				if (!this.method_13990()) {
					this.method_13979();
					return true;
				}

				boolean bl2 = HorseArmorType.method_13137(itemStack) != HorseArmorType.NONE;
				boolean bl3 = !this.isBaby() && !this.method_13975() && itemStack.getItem() == Items.SADDLE;
				if (bl2 || bl3) {
					this.method_14000(playerEntity);
					return true;
				}
			}

			if (this.isBaby()) {
				return super.interactMob(playerEntity, hand);
			} else {
				this.method_14003(playerEntity);
				return true;
			}
		}
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (other == this) {
			return false;
		} else {
			return !(other instanceof DonkeyEntity) && !(other instanceof HorseBaseEntity) ? false : this.method_13980() && ((AbstractHorseEntity)other).method_13980();
		}
	}

	@Override
	public PassiveEntity breed(PassiveEntity entity) {
		AbstractHorseEntity abstractHorseEntity;
		if (entity instanceof DonkeyEntity) {
			abstractHorseEntity = new MuleEntity(this.world);
		} else {
			HorseBaseEntity horseBaseEntity = (HorseBaseEntity)entity;
			abstractHorseEntity = new HorseBaseEntity(this.world);
			int i = this.random.nextInt(9);
			int j;
			if (i < 4) {
				j = this.getVariant() & 0xFF;
			} else if (i < 8) {
				j = horseBaseEntity.getVariant() & 0xFF;
			} else {
				j = this.random.nextInt(7);
			}

			int m = this.random.nextInt(5);
			if (m < 2) {
				j |= this.getVariant() & 0xFF00;
			} else if (m < 4) {
				j |= horseBaseEntity.getVariant() & 0xFF00;
			} else {
				j |= this.random.nextInt(5) << 8 & 0xFF00;
			}

			((HorseBaseEntity)abstractHorseEntity).setVariant(j);
		}

		this.method_13968(entity, abstractHorseEntity);
		return abstractHorseEntity;
	}

	@Override
	public boolean method_13984() {
		return true;
	}

	@Override
	public boolean method_14001(ItemStack itemStack) {
		return HorseArmorType.method_13139(itemStack.getItem());
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		entityData = super.initialize(difficulty, entityData, nbt);
		int i;
		if (entityData instanceof HorseBaseEntity.Data) {
			i = ((HorseBaseEntity.Data)entityData).field_6909;
		} else {
			i = this.random.nextInt(7);
			entityData = new HorseBaseEntity.Data(i);
		}

		this.setVariant(i | this.random.nextInt(5) << 8);
		return entityData;
	}

	public static class Data implements EntityData {
		public int field_6909;

		public Data(int i) {
			this.field_6909 = i;
		}
	}
}
