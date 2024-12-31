package net.minecraft.datafixer;

import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.entity.FlowerPotBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix;
import net.minecraft.datafixer.fix.EntityArmorStandSilentFix;
import net.minecraft.datafixer.fix.EntityEquipmentToArmorAndHandFix;
import net.minecraft.datafixer.fix.EntityHealthFix;
import net.minecraft.datafixer.fix.EntityHorseSaddleFix;
import net.minecraft.datafixer.fix.EntityMinecartIdentifiersFix;
import net.minecraft.datafixer.fix.EntityRedundantChanceTagsFix;
import net.minecraft.datafixer.fix.EntityRidingToPassengerFix;
import net.minecraft.datafixer.fix.EntityStringUuidFix;
import net.minecraft.datafixer.fix.EntityZombieVillagerTypeFix;
import net.minecraft.datafixer.fix.HangingEntityFix;
import net.minecraft.datafixer.fix.ItemCookedFishIdFix;
import net.minecraft.datafixer.fix.ItemIdFix;
import net.minecraft.datafixer.fix.ItemPotionFix;
import net.minecraft.datafixer.fix.ItemSpawnEggFix;
import net.minecraft.datafixer.fix.ItemWrittenBookPagesStrictJsonFix;
import net.minecraft.datafixer.fix.MobSpawnerEntityIdentifiersFix;
import net.minecraft.datafixer.fix.OptionsForceVBOFix;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.FireworkRocketEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ShulkerEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.chunk.ThreadedAnvilChunkStorage;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelDataType;

public class DataFixerFactory {
	private static void buildDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addFixer(LevelDataType.ENTITY, new EntityEquipmentToArmorAndHandFix());
		dataFixer.addFixer(LevelDataType.BLOCK_ENTITY, new BlockEntitySignTextStrictJsonFix());
		dataFixer.addFixer(LevelDataType.ITEM_INSTANCE, new ItemIdFix());
		dataFixer.addFixer(LevelDataType.ITEM_INSTANCE, new ItemPotionFix());
		dataFixer.addFixer(LevelDataType.ITEM_INSTANCE, new ItemSpawnEggFix());
		dataFixer.addFixer(LevelDataType.ENTITY, new EntityMinecartIdentifiersFix());
		dataFixer.addFixer(LevelDataType.BLOCK_ENTITY, new MobSpawnerEntityIdentifiersFix());
		dataFixer.addFixer(LevelDataType.ENTITY, new EntityStringUuidFix());
		dataFixer.addFixer(LevelDataType.ENTITY, new EntityHealthFix());
		dataFixer.addFixer(LevelDataType.ENTITY, new EntityHorseSaddleFix());
		dataFixer.addFixer(LevelDataType.ENTITY, new HangingEntityFix());
		dataFixer.addFixer(LevelDataType.ENTITY, new EntityRedundantChanceTagsFix());
		dataFixer.addFixer(LevelDataType.ENTITY, new EntityRidingToPassengerFix());
		dataFixer.addFixer(LevelDataType.ENTITY, new EntityArmorStandSilentFix());
		dataFixer.addFixer(LevelDataType.ITEM_INSTANCE, new ItemWrittenBookPagesStrictJsonFix());
		dataFixer.addFixer(LevelDataType.ITEM_INSTANCE, new ItemCookedFishIdFix());
		dataFixer.addFixer(LevelDataType.ENTITY, new EntityZombieVillagerTypeFix());
		dataFixer.addFixer(LevelDataType.OPTIONS, new OptionsForceVBOFix());
	}

	public static DataFixerUpper createDataFixer() {
		DataFixerUpper dataFixerUpper = new DataFixerUpper(512);
		LevelProperties.registerDataFixes(dataFixerUpper);
		PlayerEntity.registerDataFixes(dataFixerUpper);
		ThreadedAnvilChunkStorage.registerDataFixes(dataFixerUpper);
		ItemStack.registerDataFixes(dataFixerUpper);
		ArmorStandEntity.registerDataFixes(dataFixerUpper);
		AbstractArrowEntity.registerDataFixes(dataFixerUpper);
		BatEntity.registerDataFixes(dataFixerUpper);
		BlazeEntity.registerDataFixes(dataFixerUpper);
		CaveSpiderEntity.registerDataFixes(dataFixerUpper);
		ChickenEntity.registerDataFixes(dataFixerUpper);
		CowEntity.registerDataFixes(dataFixerUpper);
		CreeperEntity.registerDataFixes(dataFixerUpper);
		DragonFireballEntity.registerDataFixes(dataFixerUpper);
		EnderDragonEntity.registerDataFixes(dataFixerUpper);
		EndermanEntity.registerDataFixes(dataFixerUpper);
		EndermiteEntity.registerDataFixes(dataFixerUpper);
		FallingBlockEntity.registerDataFixes(dataFixerUpper);
		FireballEntity.registerDataFixes(dataFixerUpper);
		FireworkRocketEntity.registerDataFixes(dataFixerUpper);
		GhastEntity.registerDataFixes(dataFixerUpper);
		GiantEntity.registerDataFixes(dataFixerUpper);
		GuardianEntity.registerDataFixes(dataFixerUpper);
		HorseBaseEntity.registerDataFixes(dataFixerUpper);
		ItemEntity.registerDataFixes(dataFixerUpper);
		ItemFrameEntity.registerDataFixes(dataFixerUpper);
		MagmaCubeEntity.registerDataFixes(dataFixerUpper);
		ChestMinecartEntity.registerDataFixes(dataFixerUpper);
		CommandBlockMinecartEntity.registerDataFixes(dataFixerUpper);
		FurnaceMinecartEntity.registerDataFixes(dataFixerUpper);
		HopperMinecartEntity.registerDataFixes(dataFixerUpper);
		MinecartEntity.registerDataFixes(dataFixerUpper);
		SpawnerMinecartEntity.registerDataFixes(dataFixerUpper);
		TntMinecartEntity.registerDataFixes(dataFixerUpper);
		MobEntity.method_13495(dataFixerUpper);
		HostileEntity.method_13532(dataFixerUpper);
		MooshroomEntity.registerDataFixes(dataFixerUpper);
		OcelotEntity.registerDataFixes(dataFixerUpper);
		PigEntity.registerDataFixes(dataFixerUpper);
		ZombiePigmanEntity.registerDataFixes(dataFixerUpper);
		RabbitEntity.registerDataFixes(dataFixerUpper);
		SheepEntity.registerDataFixes(dataFixerUpper);
		ShulkerEntity.registerDataFixes(dataFixerUpper);
		SilverfishEntity.registerDataFixes(dataFixerUpper);
		SkeletonEntity.registerDataFixes(dataFixerUpper);
		SlimeEntity.registerDataFixes(dataFixerUpper);
		SmallFireballEntity.registerDataFixes(dataFixerUpper);
		SnowGolemEntity.registerDataFixes(dataFixerUpper);
		SnowballEntity.registerDataFixes(dataFixerUpper);
		SpectralArrowEntity.registerDataFixes(dataFixerUpper);
		SpiderEntity.registerDataFixes(dataFixerUpper);
		SquidEntity.registerDataFixes(dataFixerUpper);
		EggEntity.registerDataFixes(dataFixerUpper);
		EnderPearlEntity.registerDataFixes(dataFixerUpper);
		ExperienceBottleEntity.registerDataFixes(dataFixerUpper);
		PotionEntity.registerDataFixes(dataFixerUpper);
		ArrowEntity.registerDataFixes(dataFixerUpper);
		VillagerEntity.registerDataFixes(dataFixerUpper);
		IronGolemEntity.registerDataFixes(dataFixerUpper);
		WitchEntity.registerDataFixes(dataFixerUpper);
		WitherEntity.registerDataFixes(dataFixerUpper);
		WitherSkullEntity.registerDataFixes(dataFixerUpper);
		WolfEntity.registerDataFixes(dataFixerUpper);
		ZombieEntity.registerDataFixes(dataFixerUpper);
		PistonBlockEntity.registerDataFixes(dataFixerUpper);
		FlowerPotBlockEntity.registerDataFixes(dataFixerUpper);
		FurnaceBlockEntity.registerDataFixes(dataFixerUpper);
		ChestBlockEntity.registerDataFixes(dataFixerUpper);
		DispenserBlockEntity.registerDataFixes(dataFixerUpper);
		DropperBlockEntity.registerDataFixes(dataFixerUpper);
		BrewingStandBlockEntity.registerDataFixes(dataFixerUpper);
		HopperBlockEntity.registerDataFixes(dataFixerUpper);
		JukeboxBlock.registerDataFixes(dataFixerUpper);
		MobSpawnerBlockEntity.registerDataFixes(dataFixerUpper);
		buildDataFixes(dataFixerUpper);
		return dataFixerUpper;
	}

	public static NbtCompound updateItem(DataFixer dataFixer, NbtCompound tag, int dataVersion, String key) {
		if (tag.contains(key, 10)) {
			tag.put(key, dataFixer.update(LevelDataType.ITEM_INSTANCE, tag.getCompound(key), dataVersion));
		}

		return tag;
	}

	public static NbtCompound updateItemList(DataFixer dataFixer, NbtCompound tag, int dataVersion, String key) {
		if (tag.contains(key, 9)) {
			NbtList nbtList = tag.getList(key, 10);

			for (int i = 0; i < nbtList.size(); i++) {
				nbtList.set(i, dataFixer.update(LevelDataType.ITEM_INSTANCE, nbtList.getCompound(i), dataVersion));
			}
		}

		return tag;
	}
}
