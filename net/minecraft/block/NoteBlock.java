package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.NoteBlockBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class NoteBlock extends BlockWithEntity {
	private static final List<String> TUNES = Lists.newArrayList(new String[]{"harp", "bd", "snare", "hat", "bassattack"});

	public NoteBlock() {
		super(Material.WOOD);
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		boolean bl = world.isReceivingRedstonePower(pos);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof NoteBlockBlockEntity) {
			NoteBlockBlockEntity noteBlockBlockEntity = (NoteBlockBlockEntity)blockEntity;
			if (noteBlockBlockEntity.powered != bl) {
				if (bl) {
					noteBlockBlockEntity.playNote(world, pos);
				}

				noteBlockBlockEntity.powered = bl;
			}
		}
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof NoteBlockBlockEntity) {
				NoteBlockBlockEntity noteBlockBlockEntity = (NoteBlockBlockEntity)blockEntity;
				noteBlockBlockEntity.increaseNote();
				noteBlockBlockEntity.playNote(world, pos);
				player.incrementStat(Stats.NOTEBLOCK_TUNED);
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

	private String getTuneName(int tune) {
		if (tune < 0 || tune >= TUNES.size()) {
			tune = 0;
		}

		return (String)TUNES.get(tune);
	}

	@Override
	public boolean onEvent(World world, BlockPos pos, BlockState state, int id, int data) {
		float f = (float)Math.pow(2.0, (double)(data - 12) / 12.0);
		world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "note." + this.getTuneName(id), 3.0F, f);
		world.addParticle(ParticleType.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)data / 24.0, 0.0, 0.0);
		return true;
	}

	@Override
	public int getBlockType() {
		return 3;
	}
}
