package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HoeItem extends Item {
	private final float field_12298;
	protected Item.ToolMaterialType material;

	public HoeItem(Item.ToolMaterialType toolMaterialType) {
		this.material = toolMaterialType;
		this.maxCount = 1;
		this.setMaxDamage(toolMaterialType.getMaxDurability());
		this.setItemGroup(ItemGroup.TOOLS);
		this.field_12298 = toolMaterialType.getAttackMultiplier() + 1.0F;
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		if (!playerEntity.canModify(blockPos.offset(direction), direction, itemStack)) {
			return ActionResult.FAIL;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (direction != Direction.DOWN && world.getBlockState(blockPos.up()).getMaterial() == Material.AIR) {
				if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
					this.method_11372(itemStack, playerEntity, world, blockPos, Blocks.FARMLAND.getDefaultState());
					return ActionResult.SUCCESS;
				}

				if (block == Blocks.DIRT) {
					switch ((DirtBlock.DirtType)blockState.get(DirtBlock.VARIANT)) {
						case DIRT:
							this.method_11372(itemStack, playerEntity, world, blockPos, Blocks.FARMLAND.getDefaultState());
							return ActionResult.SUCCESS;
						case COARSE_DIRT:
							this.method_11372(itemStack, playerEntity, world, blockPos, Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.DIRT));
							return ActionResult.SUCCESS;
					}
				}
			}

			return ActionResult.PASS;
		}
	}

	@Override
	public boolean onEntityHit(ItemStack stack, LivingEntity entity1, LivingEntity entity2) {
		stack.damage(1, entity2);
		return true;
	}

	protected void method_11372(ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, BlockState blockState) {
		world.method_11486(playerEntity, blockPos, Sounds.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
		if (!world.isClient) {
			world.setBlockState(blockPos, blockState, 11);
			itemStack.damage(1, playerEntity);
		}
	}

	@Override
	public boolean isHandheld() {
		return true;
	}

	public String getAsString() {
		return this.material.toString();
	}

	@Override
	public Multimap<String, AttributeModifier> method_6326(EquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = super.method_6326(equipmentSlot);
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			multimap.put(EntityAttributes.GENERIC_ATTACK_DAMAGE.getId(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", 0.0, 0));
			multimap.put(
				EntityAttributes.GENERIC_ATTACK_SPEED.getId(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)(this.field_12298 - 4.0F), 0)
			);
		}

		return multimap;
	}
}
