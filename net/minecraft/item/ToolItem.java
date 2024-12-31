package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.class_3562;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToolItem extends class_3562 {
	private final Set<Block> effectiveBlocks;
	protected float miningSpeed;
	protected float attackDamage;
	protected float field_12294;

	protected ToolItem(float f, float g, IToolMaterial iToolMaterial, Set<Block> set, Item.Settings settings) {
		super(iToolMaterial, settings);
		this.effectiveBlocks = set;
		this.miningSpeed = iToolMaterial.getBlockBreakSpeed();
		this.attackDamage = f + iToolMaterial.getAttackDamage();
		this.field_12294 = g;
	}

	@Override
	public float getBlockBreakingSpeed(ItemStack stack, BlockState state) {
		return this.effectiveBlocks.contains(state.getBlock()) ? this.miningSpeed : 1.0F;
	}

	@Override
	public boolean onEntityHit(ItemStack stack, LivingEntity entity1, LivingEntity entity2) {
		stack.damage(2, entity2);
		return true;
	}

	@Override
	public boolean method_3356(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		if (!world.isClient && blockState.getHardness(world, blockPos) != 0.0F) {
			itemStack.damage(1, livingEntity);
		}

		return true;
	}

	@Override
	public Multimap<String, AttributeModifier> method_6326(EquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = super.method_6326(equipmentSlot);
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			multimap.put(
				EntityAttributes.GENERIC_ATTACK_DAMAGE.getId(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Tool modifier", (double)this.attackDamage, 0)
			);
			multimap.put(EntityAttributes.GENERIC_ATTACK_SPEED.getId(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)this.field_12294, 0));
		}

		return multimap;
	}
}
