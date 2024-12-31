package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.class_3146;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SkeletonEntity extends class_3146 {
	public SkeletonEntity(World world) {
		super(world);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, SkeletonEntity.class);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.SKELETON_ENTITIE;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_SKELETON_AMBIENT;
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_SKELETON_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_SKELETON_DEATH;
	}

	@Override
	Sound method_14060() {
		return Sounds.ENTITY_SKELETON_STEP;
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		if (source.getAttacker() instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)source.getAttacker();
			if (creeperEntity.method_3074() && creeperEntity.shouldDropHead()) {
				creeperEntity.onHeadDropped();
				this.dropItem(new ItemStack(Items.SKULL, 1, 0), 0.0F);
			}
		}
	}

	@Override
	protected AbstractArrowEntity method_14056(float f) {
		ItemStack itemStack = this.getStack(EquipmentSlot.OFFHAND);
		if (itemStack.getItem() == Items.SPECTRAL_ARROW) {
			SpectralArrowEntity spectralArrowEntity = new SpectralArrowEntity(this.world, this);
			spectralArrowEntity.applyEnchantmentEffects(this, f);
			return spectralArrowEntity;
		} else {
			AbstractArrowEntity abstractArrowEntity = super.method_14056(f);
			if (itemStack.getItem() == Items.TIPPED_ARROW && abstractArrowEntity instanceof ArrowEntity) {
				((ArrowEntity)abstractArrowEntity).initFromStack(itemStack);
			}

			return abstractArrowEntity;
		}
	}
}
