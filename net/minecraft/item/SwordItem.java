package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SwordItem extends Item {
	private float attackMultiplier;
	private final Item.ToolMaterialType material;

	public SwordItem(Item.ToolMaterialType toolMaterialType) {
		this.material = toolMaterialType;
		this.maxCount = 1;
		this.setMaxDamage(toolMaterialType.getMaxDurability());
		this.setItemGroup(ItemGroup.COMBAT);
		this.attackMultiplier = 4.0F + toolMaterialType.getAttackMultiplier();
	}

	public float getAttackDamage() {
		return this.material.getAttackMultiplier();
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, Block block) {
		if (block == Blocks.COBWEB) {
			return 15.0F;
		} else {
			Material material = block.getMaterial();
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
	public boolean onBlockBroken(ItemStack stack, World world, Block block, BlockPos pos, LivingEntity entity) {
		if ((double)block.getStrength(world, pos) != 0.0) {
			stack.damage(2, entity);
		}

		return true;
	}

	@Override
	public boolean isHandheld() {
		return true;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 72000;
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		player.setUseItem(stack, this.getMaxUseTime(stack));
		return stack;
	}

	@Override
	public boolean isEffectiveOn(Block block) {
		return block == Blocks.COBWEB;
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
	public Multimap<String, AttributeModifier> getAttributeModifierMap() {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifierMap();
		multimap.put(
			EntityAttributes.GENERIC_ATTACK_DAMAGE.getId(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", (double)this.attackMultiplier, 0)
		);
		return multimap;
	}
}
