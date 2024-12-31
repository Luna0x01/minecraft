package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class SheepEntity extends AnimalEntity {
	private final CraftingInventory field_5369 = new CraftingInventory(new ScreenHandler() {
		@Override
		public boolean canUse(PlayerEntity player) {
			return false;
		}
	}, 2, 1);
	private static final Map<DyeColor, float[]> COLORS = Maps.newEnumMap(DyeColor.class);
	private int eatGrassTimer;
	private EatGrassGoal eatGrassGoal = new EatGrassGoal(this);

	public static float[] getDyedColor(DyeColor color) {
		return (float[])COLORS.get(color);
	}

	public SheepEntity(World world) {
		super(world);
		this.setBounds(0.9F, 1.3F);
		((MobNavigation)this.getNavigation()).method_11027(true);
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new EscapeDangerGoal(this, 1.25));
		this.goals.add(2, new BreedGoal(this, 1.0));
		this.goals.add(3, new TemptGoal(this, 1.1, Items.WHEAT, false));
		this.goals.add(4, new FollowParentGoal(this, 1.1));
		this.goals.add(5, this.eatGrassGoal);
		this.goals.add(6, new WanderAroundGoal(this, 1.0));
		this.goals.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(8, new LookAroundGoal(this));
		this.field_5369.setInvStack(0, new ItemStack(Items.DYE, 1, 0));
		this.field_5369.setInvStack(1, new ItemStack(Items.DYE, 1, 0));
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
		this.dataTracker.track(16, new Byte((byte)0));
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		if (!this.isSheared()) {
			this.dropItem(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, this.getColor().getId()), 0.0F);
		}

		int i = this.random.nextInt(2) + 1 + this.random.nextInt(1 + lootingMultiplier);

		for (int j = 0; j < i; j++) {
			if (this.isOnFire()) {
				this.dropItem(Items.COOKED_MUTTON, 1);
			} else {
				this.dropItem(Items.MUTTON, 1);
			}
		}
	}

	@Override
	protected Item getDefaultDrop() {
		return Item.fromBlock(Blocks.WOOL);
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
			return this.eatGrassTimer > 0 ? (float) (Math.PI / 5) : this.pitch / (180.0F / (float)Math.PI);
		}
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		ItemStack itemStack = playerEntity.inventory.getMainHandStack();
		if (itemStack != null && itemStack.getItem() == Items.SHEARS && !this.isSheared() && !this.isBaby()) {
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
			this.playSound("mob.sheep.shear", 1.0F, 1.0F);
		}

		return super.method_2537(playerEntity);
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
	protected String getAmbientSound() {
		return "mob.sheep.say";
	}

	@Override
	protected String getHurtSound() {
		return "mob.sheep.say";
	}

	@Override
	protected String getDeathSound() {
		return "mob.sheep.say";
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound("mob.sheep.step", 0.15F, 1.0F);
	}

	public DyeColor getColor() {
		return DyeColor.byId(this.dataTracker.getByte(16) & 15);
	}

	public void setColor(DyeColor dyeColor) {
		byte b = this.dataTracker.getByte(16);
		this.dataTracker.setProperty(16, (byte)(b & 240 | dyeColor.getId() & 15));
	}

	public boolean isSheared() {
		return (this.dataTracker.getByte(16) & 16) != 0;
	}

	public void setSheared(boolean bl) {
		byte b = this.dataTracker.getByte(16);
		if (bl) {
			this.dataTracker.setProperty(16, (byte)(b | 16));
		} else {
			this.dataTracker.setProperty(16, (byte)(b & -17));
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

	@Override
	public EntityData initialize(LocalDifficulty difficulty, EntityData data) {
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
		if (itemStack != null && itemStack.getItem() == Items.DYE) {
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
