package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.class_3133;
import net.minecraft.block.Block;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CowEntity extends AnimalEntity {
	public CowEntity(World world) {
		super(world);
		this.setBounds(0.9F, 1.4F);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, CowEntity.class);
	}

	@Override
	protected void initGoals() {
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new EscapeDangerGoal(this, 2.0));
		this.goals.add(2, new BreedGoal(this, 1.0));
		this.goals.add(3, new TemptGoal(this, 1.25, Items.WHEAT, false));
		this.goals.add(4, new FollowParentGoal(this, 1.25));
		this.goals.add(5, new class_3133(this, 1.0));
		this.goals.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(7, new LookAroundGoal(this));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2F);
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_COW_AMBIENT;
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_COW_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_COW_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound(Sounds.ENTITY_COW_STEP, 0.15F, 1.0F);
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.COW_ENTITIE;
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.BUCKET && !playerEntity.abilities.creativeMode && !this.isBaby()) {
			playerEntity.playSound(Sounds.ENTITY_COW_MILK, 1.0F, 1.0F);
			itemStack.decrement(1);
			if (itemStack.isEmpty()) {
				playerEntity.equipStack(hand, new ItemStack(Items.MILK_BUCKET));
			} else if (!playerEntity.inventory.insertStack(new ItemStack(Items.MILK_BUCKET))) {
				playerEntity.dropItem(new ItemStack(Items.MILK_BUCKET), false);
			}

			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	public CowEntity breed(PassiveEntity passiveEntity) {
		return new CowEntity(this.world);
	}

	@Override
	public float getEyeHeight() {
		return this.isBaby() ? this.height : 1.3F;
	}
}
