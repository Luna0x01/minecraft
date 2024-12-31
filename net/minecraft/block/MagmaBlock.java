package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MagmaBlock extends Block {
	public MagmaBlock() {
		super(Material.STONE);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
		this.setLightLevel(0.2F);
		this.setTickRandomly(true);
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state, BlockView view, BlockPos pos) {
		return MaterialColor.NETHER;
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		if (!entity.isFireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
			entity.damage(DamageSource.HOT_FLOOR, 1.0F);
		}

		super.onSteppedOn(world, pos, entity);
	}

	@Override
	public int method_11564(BlockState state, BlockView view, BlockPos pos) {
		return 15728880;
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		BlockPos blockPos = pos.up();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == Blocks.WATER || blockState.getBlock() == Blocks.FLOWING_WATER) {
			world.setAir(blockPos);
			world.method_11486(null, pos, Sounds.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
			if (world instanceof ServerWorld) {
				((ServerWorld)world)
					.addParticle(
						ParticleType.SMOKE_LARGE, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.25, (double)blockPos.getZ() + 0.5, 8, 0.5, 0.25, 0.5, 0.0
					);
			}
		}
	}

	@Override
	public boolean method_13315(BlockState blockState, Entity entity) {
		return entity.isFireImmune();
	}
}
