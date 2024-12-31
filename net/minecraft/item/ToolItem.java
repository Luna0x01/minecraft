package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToolItem extends Item {
	private Set<Block> effectiveBlocks;
	protected float miningSpeed = 4.0F;
	private float attackDamage;
	protected Item.ToolMaterialType material;

	protected ToolItem(float f, Item.ToolMaterialType toolMaterialType, Set<Block> set) {
		this.material = toolMaterialType;
		this.effectiveBlocks = set;
		this.maxCount = 1;
		this.setMaxDamage(toolMaterialType.getMaxDurability());
		this.miningSpeed = toolMaterialType.getMiningSpeedMultiplier();
		this.attackDamage = f + toolMaterialType.getAttackMultiplier();
		this.setItemGroup(ItemGroup.TOOLS);
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, Block block) {
		return this.effectiveBlocks.contains(block) ? this.miningSpeed : 1.0F;
	}

	@Override
	public boolean onEntityHit(ItemStack stack, LivingEntity entity1, LivingEntity entity2) {
		stack.damage(2, entity2);
		return true;
	}

	@Override
	public boolean onBlockBroken(ItemStack stack, World world, Block block, BlockPos pos, LivingEntity entity) {
		if ((double)block.getStrength(world, pos) != 0.0) {
			stack.damage(1, entity);
		}

		return true;
	}

	@Override
	public boolean isHandheld() {
		return true;
	}

	public Item.ToolMaterialType getMaterial() {
		return this.material;
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
	public Multimap<String, AttributeModifier> getAttributeModifierMap() {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifierMap();
		multimap.put(
			EntityAttributes.GENERIC_ATTACK_DAMAGE.getId(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Tool modifier", (double)this.attackDamage, 0)
		);
		return multimap;
	}
}
