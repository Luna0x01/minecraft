package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import net.minecraft.class_3562;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HoeItem extends class_3562 {
	private final float field_12298;
	protected static final Map<Block, BlockState> field_17185 = Maps.newHashMap(
		ImmutableMap.of(
			Blocks.GRASS_BLOCK,
			Blocks.FARMLAND.getDefaultState(),
			Blocks.GRASS_PATH,
			Blocks.FARMLAND.getDefaultState(),
			Blocks.DIRT,
			Blocks.FARMLAND.getDefaultState(),
			Blocks.COARSE_DIRT,
			Blocks.DIRT.getDefaultState()
		)
	);

	public HoeItem(IToolMaterial iToolMaterial, float f, Item.Settings settings) {
		super(iToolMaterial, settings);
		this.field_12298 = f;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		if (itemUsageContext.method_16151() != Direction.DOWN && world.getBlockState(blockPos.up()).isAir()) {
			BlockState blockState = (BlockState)field_17185.get(world.getBlockState(blockPos).getBlock());
			if (blockState != null) {
				PlayerEntity playerEntity = itemUsageContext.getPlayer();
				world.playSound(playerEntity, blockPos, Sounds.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (!world.isClient) {
					world.setBlockState(blockPos, blockState, 11);
					if (playerEntity != null) {
						itemUsageContext.getItemStack().damage(1, playerEntity);
					}
				}

				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.PASS;
	}

	@Override
	public boolean onEntityHit(ItemStack stack, LivingEntity entity1, LivingEntity entity2) {
		stack.damage(1, entity2);
		return true;
	}

	@Override
	public Multimap<String, AttributeModifier> method_6326(EquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = super.method_6326(equipmentSlot);
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			multimap.put(EntityAttributes.GENERIC_ATTACK_DAMAGE.getId(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", 0.0, 0));
			multimap.put(EntityAttributes.GENERIC_ATTACK_SPEED.getId(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)this.field_12298, 0));
		}

		return multimap;
	}
}
