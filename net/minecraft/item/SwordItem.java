package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.class_3562;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SwordItem extends class_3562 {
	private final float attackMultiplier;
	private final float field_17385;

	public SwordItem(IToolMaterial iToolMaterial, int i, float f, Item.Settings settings) {
		super(iToolMaterial, settings);
		this.field_17385 = f;
		this.attackMultiplier = (float)i + iToolMaterial.getAttackDamage();
	}

	public float method_16130() {
		return this.attackMultiplier;
	}

	@Override
	public boolean beforeBlockBreak(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		return !player.isCreative();
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
					&& !state.isIn(BlockTags.LEAVES)
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
		if (blockState.getHardness(world, blockPos) != 0.0F) {
			itemStack.damage(2, livingEntity);
		}

		return true;
	}

	@Override
	public boolean method_3346(BlockState blockState) {
		return blockState.getBlock() == Blocks.COBWEB;
	}

	@Override
	public Multimap<String, AttributeModifier> method_6326(EquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = super.method_6326(equipmentSlot);
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			multimap.put(
				EntityAttributes.GENERIC_ATTACK_DAMAGE.getId(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", (double)this.attackMultiplier, 0)
			);
			multimap.put(EntityAttributes.GENERIC_ATTACK_SPEED.getId(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)this.field_17385, 0));
		}

		return multimap;
	}
}
