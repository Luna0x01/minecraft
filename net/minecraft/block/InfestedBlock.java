package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfestedBlock extends Block {
	public static final EnumProperty<InfestedBlock.Variants> VARIANT = EnumProperty.of("variant", InfestedBlock.Variants.class);

	public InfestedBlock() {
		super(Material.CLAY);
		this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, InfestedBlock.Variants.STONE));
		this.setStrength(0.0F);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	public static boolean isInfestable(BlockState state) {
		Block block = state.getBlock();
		return state == Blocks.STONE.getDefaultState().with(StoneBlock.VARIANT, StoneBlock.StoneType.STONE)
			|| block == Blocks.COBBLESTONE
			|| block == Blocks.STONE_BRICKS;
	}

	@Override
	protected ItemStack createStackFromBlock(BlockState state) {
		switch ((InfestedBlock.Variants)state.get(VARIANT)) {
			case COBBLESTONE:
				return new ItemStack(Blocks.COBBLESTONE);
			case STONE_BRICK:
				return new ItemStack(Blocks.STONE_BRICKS);
			case MOSSY_STONE_BRICK:
				return new ItemStack(Blocks.STONE_BRICKS, 1, StoneBrickBlock.Type.MOSSY.byId());
			case CRACKED_STONE_BRICK:
				return new ItemStack(Blocks.STONE_BRICKS, 1, StoneBrickBlock.Type.CRACKED.byId());
			case CHISELED_STONE_BRICK:
				return new ItemStack(Blocks.STONE_BRICKS, 1, StoneBrickBlock.Type.CHISELED.byId());
			default:
				return new ItemStack(Blocks.STONE);
		}
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		if (!world.isClient && world.getGameRules().getBoolean("doTileDrops")) {
			SilverfishEntity silverfishEntity = new SilverfishEntity(world);
			silverfishEntity.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, 0.0F, 0.0F);
			world.spawnEntity(silverfishEntity);
			silverfishEntity.playSpawnEffects();
		}
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this, 1, blockState.getBlock().getData(blockState));
	}

	@Override
	public void method_13700(Item item, ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		for (InfestedBlock.Variants variants : InfestedBlock.Variants.values()) {
			defaultedList.add(new ItemStack(item, 1, variants.getId()));
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(VARIANT, InfestedBlock.Variants.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((InfestedBlock.Variants)state.get(VARIANT)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, VARIANT);
	}

	public static enum Variants implements StringIdentifiable {
		STONE(0, "stone") {
			@Override
			public BlockState getBlockState() {
				return Blocks.STONE.getDefaultState().with(StoneBlock.VARIANT, StoneBlock.StoneType.STONE);
			}
		},
		COBBLESTONE(1, "cobblestone", "cobble") {
			@Override
			public BlockState getBlockState() {
				return Blocks.COBBLESTONE.getDefaultState();
			}
		},
		STONE_BRICK(2, "stone_brick", "brick") {
			@Override
			public BlockState getBlockState() {
				return Blocks.STONE_BRICKS.getDefaultState().with(StoneBrickBlock.VARIANT, StoneBrickBlock.Type.DEFAULT);
			}
		},
		MOSSY_STONE_BRICK(3, "mossy_brick", "mossybrick") {
			@Override
			public BlockState getBlockState() {
				return Blocks.STONE_BRICKS.getDefaultState().with(StoneBrickBlock.VARIANT, StoneBrickBlock.Type.MOSSY);
			}
		},
		CRACKED_STONE_BRICK(4, "cracked_brick", "crackedbrick") {
			@Override
			public BlockState getBlockState() {
				return Blocks.STONE_BRICKS.getDefaultState().with(StoneBrickBlock.VARIANT, StoneBrickBlock.Type.CRACKED);
			}
		},
		CHISELED_STONE_BRICK(5, "chiseled_brick", "chiseledbrick") {
			@Override
			public BlockState getBlockState() {
				return Blocks.STONE_BRICKS.getDefaultState().with(StoneBrickBlock.VARIANT, StoneBrickBlock.Type.CHISELED);
			}
		};

		private static final InfestedBlock.Variants[] VARIANTS = new InfestedBlock.Variants[values().length];
		private final int id;
		private final String name;
		private final String translationKey;

		private Variants(int j, String string2) {
			this(j, string2, string2);
		}

		private Variants(int j, String string2, String string3) {
			this.id = j;
			this.name = string2;
			this.translationKey = string3;
		}

		public int getId() {
			return this.id;
		}

		public String toString() {
			return this.name;
		}

		public static InfestedBlock.Variants getById(int id) {
			if (id < 0 || id >= VARIANTS.length) {
				id = 0;
			}

			return VARIANTS[id];
		}

		@Override
		public String asString() {
			return this.name;
		}

		public String getTranslationKey() {
			return this.translationKey;
		}

		public abstract BlockState getBlockState();

		public static InfestedBlock.Variants getByBlockState(BlockState state) {
			for (InfestedBlock.Variants variants : values()) {
				if (state == variants.getBlockState()) {
					return variants;
				}
			}

			return STONE;
		}

		static {
			for (InfestedBlock.Variants variants : values()) {
				VARIANTS[variants.getId()] = variants;
			}
		}
	}
}
