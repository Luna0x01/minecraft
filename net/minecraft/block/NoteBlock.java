package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.NoteBlockBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class NoteBlock extends BlockWithEntity {
	private static final List<Sound> TUNES = Lists.newArrayList(
		new Sound[]{Sounds.BLOCK_NOTE_HARP, Sounds.BLOCK_NOTE_BASEDRUM, Sounds.BLOCK_NOTE_SNARE, Sounds.BLOCK_NOTE_HAT, Sounds.BLOCK_NOTE_BASS}
	);

	public NoteBlock() {
		super(Material.WOOD);
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		boolean bl = world.isReceivingRedstonePower(blockPos);
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof NoteBlockBlockEntity) {
			NoteBlockBlockEntity noteBlockBlockEntity = (NoteBlockBlockEntity)blockEntity;
			if (noteBlockBlockEntity.powered != bl) {
				if (bl) {
					noteBlockBlockEntity.playNote(world, blockPos);
				}

				noteBlockBlockEntity.powered = bl;
			}
		}
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof NoteBlockBlockEntity) {
				NoteBlockBlockEntity noteBlockBlockEntity = (NoteBlockBlockEntity)blockEntity;
				noteBlockBlockEntity.increaseNote();
				noteBlockBlockEntity.playNote(world, blockPos);
				playerEntity.incrementStat(Stats.NOTEBLOCK_TUNED);
			}

			return true;
		}
	}

	@Override
	public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof NoteBlockBlockEntity) {
				((NoteBlockBlockEntity)blockEntity).playNote(world, pos);
				player.incrementStat(Stats.NOTEBLOCK_PLAYED);
			}
		}
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new NoteBlockBlockEntity();
	}

	private Sound method_8839(int i) {
		if (i < 0 || i >= TUNES.size()) {
			i = 0;
		}

		return (Sound)TUNES.get(i);
	}

	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		float f = (float)Math.pow(2.0, (double)(data - 12) / 12.0);
		world.method_11486(null, pos, this.method_8839(type), SoundCategory.BLOCKS, 3.0F, f);
		world.addParticle(ParticleType.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)data / 24.0, 0.0, 0.0);
		return true;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
}
