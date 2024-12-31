package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SwordItem extends Item {
	private final float attackMultiplier;
	private final Item.ToolMaterialType material;

	public SwordItem(Item.ToolMaterialType toolMaterialType) {
		this.material = toolMaterialType;
		this.maxCount = 1;
		this.setMaxDamage(toolMaterialType.getMaxDurability());
		this.setItemGroup(ItemGroup.COMBAT);
		this.attackMultiplier = 3.0F + toolMaterialType.getAttackMultiplier();
	}

	public float getAttackDamage() {
		return this.material.getAttackMultiplier();
	}

	@Override
	public float getBlockBreakingSpeed(ItemStack stack, BlockState state) {
		Block block = state.getBlock();
		if (block == Blocks.COBWEB) {
			return 15.0F;
		} else {
			Material material = state.getMaterial();
			return material != Material.PLANT
					&& material != Material.REPLACEABLE_PLANT
					&& material != Material.SWORD
					&& material != Material.FOLIAGE
					&& material != Material.PUMPKIN
				? 1.0F
				: 1.5F;
		}
	}

	@Override
	public boolean onEntityHit(ItemStack stack, LivingEntity entity1, LivingEntity entity2) {
		stack.damage(1, entity2);
		return true;
	}

	@Override
	public boolean method_3356(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		if ((double)blockState.getHardness(world, blockPos) != 0.0) {
			itemStack.damage(2, livingEntity);
		}

		return true;
	}

	@Override
	public boolean isHandheld() {
		return true;
	}

	@Override
	public boolean method_3346(BlockState blockState) {
		return blockState.getBlock() == Blocks.COBWEB;
	}

	@Override
	public int getEnchantability() {
		return this.material.getEnchantability();
	}

	public String getToolMaterial() {
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
				EntityAttributes.GENERIC_ATTACK_DAMAGE.getId(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", (double)this.attackMultiplier, 0)
			);
			multimap.put(EntityAttributes.GENERIC_ATTACK_SPEED.getId(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4F, 0));
		}

		return multimap;
	}
}
