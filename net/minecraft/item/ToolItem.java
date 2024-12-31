package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToolItem extends Item {
	private final Set<Block> effectiveBlocks;
	protected float miningSpeed = 4.0F;
	protected float attackDamage;
	protected float field_12294;
	protected Item.ToolMaterialType material;

	protected ToolItem(float f, float g, Item.ToolMaterialType toolMaterialType, Set<Block> set) {
		this.material = toolMaterialType;
		this.effectiveBlocks = set;
		this.maxCount = 1;
		this.setMaxDamage(toolMaterialType.getMaxDurability());
		this.miningSpeed = toolMaterialType.getMiningSpeedMultiplier();
		this.attackDamage = f + toolMaterialType.getAttackMultiplier();
		this.field_12294 = g;
		this.setItemGroup(ItemGroup.TOOLS);
	}

	protected ToolItem(Item.ToolMaterialType toolMaterialType, Set<Block> set) {
		this(0.0F, 0.0F, toolMaterialType, set);
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
		if (!world.isClient && (double)blockState.getHardness(world, blockPos) != 0.0) {
			itemStack.damage(1, livingEntity);
		}

		return true;
	}

	@Override
	public boolean isHandheld() {
		return true;
	}

	@Override
	public int getEnchantability() {
		return this.material.getEnchantability();
	}

	public String getMaterialAsString() {
		return this.material.toString();
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return this.material.getRepairIngredient() == ingredient.getItem() ? true : super.canRepair(stack, ingredient);
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
