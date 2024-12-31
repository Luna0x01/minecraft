package net.minecraft.block.entity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.class_3402;
import net.minecraft.class_3741;
import net.minecraft.class_3742;
import net.minecraft.class_3746;
import net.minecraft.datafixer.DataFixerFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockEntityType<T extends BlockEntity> {
	private static final Logger field_18622 = LogManager.getLogger();
	public static final BlockEntityType<FurnaceBlockEntity> FURNACE = method_16787("furnace", BlockEntityType.class_3740.method_16790(FurnaceBlockEntity::new));
	public static final BlockEntityType<ChestBlockEntity> CHEST = method_16787("chest", BlockEntityType.class_3740.method_16790(ChestBlockEntity::new));
	public static final BlockEntityType<class_3746> TRAPPED_CHEST = method_16787("trapped_chest", BlockEntityType.class_3740.method_16790(class_3746::new));
	public static final BlockEntityType<EnderChestBlockEntity> ENDER_CHEST = method_16787(
		"ender_chest", BlockEntityType.class_3740.method_16790(EnderChestBlockEntity::new)
	);
	public static final BlockEntityType<class_3742> JUKEBOX = method_16787("jukebox", BlockEntityType.class_3740.method_16790(class_3742::new));
	public static final BlockEntityType<DispenserBlockEntity> DISPENSER = method_16787(
		"dispenser", BlockEntityType.class_3740.method_16790(DispenserBlockEntity::new)
	);
	public static final BlockEntityType<DropperBlockEntity> DROPPER = method_16787("dropper", BlockEntityType.class_3740.method_16790(DropperBlockEntity::new));
	public static final BlockEntityType<SignBlockEntity> SIGN = method_16787("sign", BlockEntityType.class_3740.method_16790(SignBlockEntity::new));
	public static final BlockEntityType<MobSpawnerBlockEntity> MOB_SPAWNER = method_16787(
		"mob_spawner", BlockEntityType.class_3740.method_16790(MobSpawnerBlockEntity::new)
	);
	public static final BlockEntityType<PistonBlockEntity> PISTON = method_16787("piston", BlockEntityType.class_3740.method_16790(PistonBlockEntity::new));
	public static final BlockEntityType<BrewingStandBlockEntity> BREWING_STAND = method_16787(
		"brewing_stand", BlockEntityType.class_3740.method_16790(BrewingStandBlockEntity::new)
	);
	public static final BlockEntityType<EnchantingTableBlockEntity> ENCHANTING_TABLE = method_16787(
		"enchanting_table", BlockEntityType.class_3740.method_16790(EnchantingTableBlockEntity::new)
	);
	public static final BlockEntityType<EndPortalBlockEntity> END_PORTAL = method_16787(
		"end_portal", BlockEntityType.class_3740.method_16790(EndPortalBlockEntity::new)
	);
	public static final BlockEntityType<BeaconBlockEntity> BEACON = method_16787("beacon", BlockEntityType.class_3740.method_16790(BeaconBlockEntity::new));
	public static final BlockEntityType<SkullBlockEntity> SKULL = method_16787("skull", BlockEntityType.class_3740.method_16790(SkullBlockEntity::new));
	public static final BlockEntityType<DaylightDetectorBlockEntity> DAYLIGHT_DETECTOR = method_16787(
		"daylight_detector", BlockEntityType.class_3740.method_16790(DaylightDetectorBlockEntity::new)
	);
	public static final BlockEntityType<HopperBlockEntity> HOPPER = method_16787("hopper", BlockEntityType.class_3740.method_16790(HopperBlockEntity::new));
	public static final BlockEntityType<ComparatorBlockEntity> COMPARATOR = method_16787(
		"comparator", BlockEntityType.class_3740.method_16790(ComparatorBlockEntity::new)
	);
	public static final BlockEntityType<BannerBlockEntity> BANNER = method_16787("banner", BlockEntityType.class_3740.method_16790(BannerBlockEntity::new));
	public static final BlockEntityType<StructureBlockEntity> STRUCTURE_BLOCK = method_16787(
		"structure_block", BlockEntityType.class_3740.method_16790(StructureBlockEntity::new)
	);
	public static final BlockEntityType<EndGatewayBlockEntity> END_GATEWAY = method_16787(
		"end_gateway", BlockEntityType.class_3740.method_16790(EndGatewayBlockEntity::new)
	);
	public static final BlockEntityType<CommandBlockBlockEntity> COMMAND_BLOCK = method_16787(
		"command_block", BlockEntityType.class_3740.method_16790(CommandBlockBlockEntity::new)
	);
	public static final BlockEntityType<ShulkerBoxBlockEntity> SHULKER_BOX = method_16787(
		"shulker_box", BlockEntityType.class_3740.method_16790(ShulkerBoxBlockEntity::new)
	);
	public static final BlockEntityType<BedBlockEntity> BED = method_16787("bed", BlockEntityType.class_3740.method_16790(BedBlockEntity::new));
	public static final BlockEntityType<class_3741> CONDUIT = method_16787("conduit", BlockEntityType.class_3740.method_16790(class_3741::new));
	private final Supplier<? extends T> field_18595;
	private final Type<?> field_18596;

	@Nullable
	public static Identifier method_16785(BlockEntityType<?> blockEntityType) {
		return Registry.BLOCK_ENTITY_TYPE.getId(blockEntityType);
	}

	public static <T extends BlockEntity> BlockEntityType<T> method_16787(String string, BlockEntityType.class_3740<T> arg) {
		Type<?> type = null;

		try {
			type = DataFixerFactory.method_21531().getSchema(DataFixUtils.makeKey(1631)).getChoiceType(class_3402.field_16591, string);
		} catch (IllegalStateException var4) {
			if (SharedConstants.isDevelopment) {
				throw var4;
			}

			field_18622.warn("No data fixer registered for block entity {}", string);
		}

		BlockEntityType<T> blockEntityType = arg.method_16789(type);
		Registry.BLOCK_ENTITY_TYPE.add(new Identifier(string), blockEntityType);
		return blockEntityType;
	}

	public static void method_16784() {
	}

	public BlockEntityType(Supplier<? extends T> supplier, Type<?> type) {
		this.field_18595 = supplier;
		this.field_18596 = type;
	}

	@Nullable
	public T method_16788() {
		return (T)this.field_18595.get();
	}

	@Nullable
	static BlockEntity method_16786(String string) {
		BlockEntityType<?> blockEntityType = Registry.BLOCK_ENTITY_TYPE.getByIdentifier(new Identifier(string));
		return blockEntityType == null ? null : blockEntityType.method_16788();
	}

	public static final class class_3740<T extends BlockEntity> {
		private final Supplier<? extends T> field_18623;

		private class_3740(Supplier<? extends T> supplier) {
			this.field_18623 = supplier;
		}

		public static <T extends BlockEntity> BlockEntityType.class_3740<T> method_16790(Supplier<? extends T> supplier) {
			return new BlockEntityType.class_3740<>(supplier);
		}

		public BlockEntityType<T> method_16789(Type<?> type) {
			return new BlockEntityType<>(this.field_18623, type);
		}
	}
}
