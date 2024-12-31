package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3133;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class SheepEntity extends AnimalEntity {
	private static final TrackedData<Byte> field_14620 = DataTracker.registerData(SheepEntity.class, TrackedDataHandlerRegistry.BYTE);
	private final CraftingInventory field_5369 = new CraftingInventory(new ScreenHandler() {
		@Override
		public boolean canUse(PlayerEntity player) {
			return false;
		}
	}, 2, 1);
	private static final Map<DyeColor, float[]> COLORS = Maps.newEnumMap(DyeColor.class);
	private int eatGrassTimer;
	private EatGrassGoal eatGrassGoal;

	public static float[] getDyedColor(DyeColor color) {
		return (float[])COLORS.get(color);
	}

	public SheepEntity(World world) {
		super(world);
		this.setBounds(0.9F, 1.3F);
		this.field_5369.setInvStack(0, new ItemStack(Items.DYE));
		this.field_5369.setInvStack(1, new ItemStack(Items.DYE));
	}

	@Override
	protected void initGoals() {
		this.eatGrassGoal = new EatGrassGoal(this);
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new EscapeDangerGoal(this, 1.25));
		this.goals.add(2, new BreedGoal(this, 1.0));
		this.goals.add(3, new TemptGoal(this, 1.1, Items.WHEAT, false));
		this.goals.add(4, new FollowParentGoal(this, 1.1));
		this.goals.add(5, this.eatGrassGoal);
		this.goals.add(6, new class_3133(this, 1.0));
		this.goals.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(8, new LookAroundGoal(this));
	}

	@Override
	protected void mobTick() {
		this.eatGrassTimer = this.eatGrassGoal.getTimer();
		super.mobTick();
	}

	@Override
	public void tickMovement() {
		if (this.world.isClient) {
			this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
		}

		super.tickMovement();
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(8.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.23F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14620, (byte)0);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		if (this.isSheared()) {
			return LootTables.SHEEP_ENTITIE;
		} else {
			switch (this.getColor()) {
				case WHITE:
				default:
					return LootTables.SHEEP_WHITE_ENTITIE;
				case ORANGE:
					return LootTables.SHEEP_ORANGE_ENTITIE;
				case MAGENTA:
					return LootTables.SHEEP_MAGENTA_ENTITIE;
				case LIGHT_BLUE:
					return LootTables.SHEEP_LIGHT_BLUE_ENTITIE;
				case YELLOW:
					return LootTables.SHEEP_YELLOW_ENTITIE;
				case LIME:
					return LootTables.SHEEP_LIME_ENTITIE;
				case PINK:
					return LootTables.SHEEP_PINK_ENTITIE;
				case GRAY:
					return LootTables.SHEEP_GRAY_ENTITIE;
				case SILVER:
					return LootTables.SHEEP_SILVER_ENTITIE;
				case CYAN:
					return LootTables.SHEEP_CYAN_ENTITIE;
				case PURPLE:
					return LootTables.SHEEP_PURPLE_ENTITIE;
				case BLUE:
					return LootTables.SHEEP_BLUE_ENTITIE;
				case BROWN:
					return LootTables.SHEEP_BROWN_ENTITIE;
				case GREEN:
					return LootTables.SHEEP_GREEN_ENTITIE;
				case RED:
					return LootTables.SHEEP_RED_ENTITIE;
				case BLACK:
					return LootTables.SHEEP_BLACK_ENTITIE;
			}
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 10) {
			this.eatGrassTimer = 40;
		} else {
			super.handleStatus(status);
		}
	}

	public float method_2864(float f) {
		if (this.eatGrassTimer <= 0) {
			return 0.0F;
		} else if (this.eatGrassTimer >= 4 && this.eatGrassTimer <= 36) {
			return 1.0F;
		} else {
			return this.eatGrassTimer < 4 ? ((float)this.eatGrassTimer - f) / 4.0F : -((float)(this.eatGrassTimer - 40) - f) / 4.0F;
		}
	}

	public float method_2865(float f) {
		if (this.eatGrassTimer > 4 && this.eatGrassTimer <= 36) {
			float g = ((float)(this.eatGrassTimer - 4) - f) / 32.0F;
			return (float) (Math.PI / 5) + 0.21991149F * MathHelper.sin(g * 28.7F);
		} else {
			return this.eatGrassTimer > 0 ? (float) (Math.PI / 5) : this.pitch * (float) (Math.PI / 180.0);
		}
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.SHEARS && !this.isSheared() && !this.isBaby()) {
			if (!this.world.isClient) {
				this.setSheared(true);
				int i = 1 + this.random.nextInt(3);

				for (int j = 0; j < i; j++) {
					ItemEntity itemEntity = this.dropItem(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, this.getColor().getId()), 1.0F);
					itemEntity.velocityY = itemEntity.velocityY + (double)(this.random.nextFloat() * 0.05F);
					itemEntity.velocityX = itemEntity.velocityX + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
					itemEntity.velocityZ = itemEntity.velocityZ + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
				}
			}

			itemStack.damage(1, playerEntity);
			this.playSound(Sounds.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
		}

		return super.interactMob(playerEntity, hand);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, SheepEntity.class);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("Sheared", this.isSheared());
		nbt.putByte("Color", (byte)this.getColor().getId());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setSheared(nbt.getBoolean("Sheared"));
		this.setColor(DyeColor.byId(nbt.getByte("Color")));
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_SHEEP_AMBIENT;
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_SHEEP_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_SHEEP_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound(Sounds.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
	}

	public DyeColor getColor() {
		return DyeColor.byId(this.dataTracker.get(field_14620) & 15);
	}

	public void setColor(DyeColor dyeColor) {
		byte b = this.dataTracker.get(field_14620);
		this.dataTracker.set(field_14620, (byte)(b & 240 | dyeColor.getId() & 15));
	}

	public boolean isSheared() {
		return (this.dataTracker.get(field_14620) & 16) != 0;
	}

	public void setSheared(boolean bl) {
		byte b = this.dataTracker.get(field_14620);
		if (bl) {
			this.dataTracker.set(field_14620, (byte)(b | 16));
		} else {
			this.dataTracker.set(field_14620, (byte)(b & -17));
		}
	}

	public static DyeColor generateDefaultColor(Random random) {
		int i = random.nextInt(100);
		if (i < 5) {
			return DyeColor.BLACK;
		} else if (i < 10) {
			return DyeColor.GRAY;
		} else if (i < 15) {
			return DyeColor.SILVER;
		} else if (i < 18) {
			return DyeColor.BROWN;
		} else {
			return random.nextInt(500) == 0 ? DyeColor.PINK : DyeColor.WHITE;
		}
	}

	public SheepEntity breed(PassiveEntity passiveEntity) {
		SheepEntity sheepEntity = (SheepEntity)passiveEntity;
		SheepEntity sheepEntity2 = new SheepEntity(this.world);
		sheepEntity2.setColor(this.getChildColor(this, sheepEntity));
		return sheepEntity2;
	}

	@Override
	public void onEatingGrass() {
		this.setSheared(false);
		if (this.isBaby()) {
			this.method_6095(60);
		}
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		data = super.initialize(difficulty, data);
		this.setColor(generateDefaultColor(this.world.random));
		return data;
	}

	private DyeColor getChildColor(AnimalEntity animalEntity, AnimalEntity animalEntity2) {
		int i = ((SheepEntity)animalEntity).getColor().getSwappedId();
		int j = ((SheepEntity)animalEntity2).getColor().getSwappedId();
		this.field_5369.getInvStack(0).setDamage(i);
		this.field_5369.getInvStack(1).setDamage(j);
		ItemStack itemStack = RecipeDispatcher.getInstance().matches(this.field_5369, ((SheepEntity)animalEntity).world);
		int k;
		if (itemStack.getItem() == Items.DYE) {
			k = itemStack.getData();
		} else {
			k = this.world.random.nextBoolean() ? i : j;
		}

		return DyeColor.getById(k);
	}

	@Override
	public float getEyeHeight() {
		return 0.95F * this.height;
	}

	static {
		COLORS.put(DyeColor.WHITE, new float[]{1.0F, 1.0F, 1.0F});
		COLORS.put(DyeColor.ORANGE, new float[]{0.85F, 0.5F, 0.2F});
		COLORS.put(DyeColor.MAGENTA, new float[]{0.7F, 0.3F, 0.85F});
		COLORS.put(DyeColor.LIGHT_BLUE, new float[]{0.4F, 0.6F, 0.85F});
		COLORS.put(DyeColor.YELLOW, new float[]{0.9F, 0.9F, 0.2F});
		COLORS.put(DyeColor.LIME, new float[]{0.5F, 0.8F, 0.1F});
		COLORS.put(DyeColor.PINK, new float[]{0.95F, 0.5F, 0.65F});
		COLORS.put(DyeColor.GRAY, new float[]{0.3F, 0.3F, 0.3F});
		COLORS.put(DyeColor.SILVER, new float[]{0.6F, 0.6F, 0.6F});
		COLORS.put(DyeColor.CYAN, new float[]{0.3F, 0.5F, 0.6F});
		COLORS.put(DyeColor.PURPLE, new float[]{0.5F, 0.25F, 0.7F});
		COLORS.put(DyeColor.BLUE, new float[]{0.2F, 0.3F, 0.7F});
		COLORS.put(DyeColor.BROWN, new float[]{0.4F, 0.3F, 0.2F});
		COLORS.put(DyeColor.GREEN, new float[]{0.4F, 0.5F, 0.2F});
		COLORS.put(DyeColor.RED, new float[]{0.6F, 0.2F, 0.2F});
		COLORS.put(DyeColor.BLACK, new float[]{0.1F, 0.1F, 0.1F});
	}
}
