package net.minecraft.datafixer;

import net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix;
import net.minecraft.datafixer.fix.EntityArmorStandSilentFix;
import net.minecraft.datafixer.fix.EntityEquipmentToArmorAndHandFix;
import net.minecraft.datafixer.fix.EntityHealthFix;
import net.minecraft.datafixer.fix.EntityHorseSaddleFix;
import net.minecraft.datafixer.fix.EntityMinecartIdentifiersFix;
import net.minecraft.datafixer.fix.EntityRedundantChanceTagsFix;
import net.minecraft.datafixer.fix.EntityRidingToPassengerFix;
import net.minecraft.datafixer.fix.EntityStringUuidFix;
import net.minecraft.datafixer.fix.HangingEntityFix;
import net.minecraft.datafixer.fix.ItemIdFix;
import net.minecraft.datafixer.fix.ItemPotionFix;
import net.minecraft.datafixer.fix.ItemSpawnEggFix;
import net.minecraft.datafixer.fix.ItemWrittenBookPagesStrictJsonFix;
import net.minecraft.datafixer.fix.MobSpawnerEntityIdentifiersFix;
import net.minecraft.datafixer.schema.BlockEntitySchema;
import net.minecraft.datafixer.schema.EntityTagSchema;
import net.minecraft.datafixer.schema.ItemListSchema;
import net.minecraft.datafixer.schema.ItemSchema;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
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
	}

	public static DataFixerUpper createDataFixer() {
		DataFixerUpper dataFixerUpper = new DataFixerUpper(184);
		dataFixerUpper.addSchema(LevelDataType.LEVEL, new Schema() {
			@Override
			public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
				if (tag.contains("Player", 10)) {
					tag.put("Player", dataFixer.update(LevelDataType.PLAYER, tag.getCompound("Player"), dataVersion));
				}

				return tag;
			}
		});
		dataFixerUpper.addSchema(LevelDataType.PLAYER, new Schema() {
			@Override
			public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
				DataFixerFactory.updateItemList(dataFixer, tag, dataVersion, "Inventory");
				DataFixerFactory.updateItemList(dataFixer, tag, dataVersion, "EnderItems");
				return tag;
			}
		});
		dataFixerUpper.addSchema(LevelDataType.CHUNK, new Schema() {
			@Override
			public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
				if (tag.contains("Level", 10)) {
					NbtCompound nbtCompound = tag.getCompound("Level");
					if (nbtCompound.contains("Entities", 9)) {
						NbtList nbtList = nbtCompound.getList("Entities", 10);

						for (int i = 0; i < nbtList.size(); i++) {
							nbtList.set(i, dataFixer.update(LevelDataType.ENTITY, (NbtCompound)nbtList.get(i), dataVersion));
						}
					}

					if (nbtCompound.contains("TileEntities", 9)) {
						NbtList nbtList2 = nbtCompound.getList("TileEntities", 10);

						for (int j = 0; j < nbtList2.size(); j++) {
							nbtList2.set(j, dataFixer.update(LevelDataType.BLOCK_ENTITY, (NbtCompound)nbtList2.get(j), dataVersion));
						}
					}
				}

				return tag;
			}
		});
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemSchema("Item", "Item"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemSchema("ThrownPotion", "Potion"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemSchema("ItemFrame", "Item"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemSchema("FireworksRocketEntity", "FireworksItem"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemSchema("TippedArrow", "Item"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("MinecartChest", "Items"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("MinecartHopper", "Items"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Enderman", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("ArmorStand", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Bat", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Blaze", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("CaveSpider", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Chicken", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Cow", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Creeper", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("EnderDragon", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Endermite", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Ghast", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Giant", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Guardian", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("LavaSlime", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Mob", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Monster", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("MushroomCow", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Ozelot", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Pig", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("PigZombie", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Rabbit", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Sheep", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Shulker", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Silverfish", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Skeleton", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Slime", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("SnowMan", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Spider", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Squid", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("VillagerGolem", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Witch", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("WitherBoss", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Wolf", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Zombie", "ArmorItems", "HandItems"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("EntityHorse", "ArmorItems", "HandItems", "Items"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemSchema("EntityHorse", "ArmorItem", "SaddleItem"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new ItemListSchema("Villager", "ArmorItems", "HandItems", "Inventory"));
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new Schema() {
			@Override
			public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
				if ("Villager".equals(tag.getString("id")) && tag.contains("Offers", 10)) {
					NbtCompound nbtCompound = tag.getCompound("Offers");
					if (nbtCompound.contains("Recipes", 9)) {
						NbtList nbtList = nbtCompound.getList("Recipes", 10);

						for (int i = 0; i < nbtList.size(); i++) {
							NbtCompound nbtCompound2 = nbtList.getCompound(i);
							DataFixerFactory.updateItem(dataFixer, nbtCompound2, dataVersion, "buy");
							DataFixerFactory.updateItem(dataFixer, nbtCompound2, dataVersion, "buyB");
							DataFixerFactory.updateItem(dataFixer, nbtCompound2, dataVersion, "sell");
							nbtList.set(i, nbtCompound2);
						}
					}
				}

				return tag;
			}
		});
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new Schema() {
			@Override
			public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
				if ("MinecartSpawner".equals(tag.getString("id"))) {
					tag.putString("id", "MobSpawner");
					dataFixer.update(LevelDataType.BLOCK_ENTITY, tag, dataVersion);
					tag.putString("id", "MinecartSpawner");
				}

				return tag;
			}
		});
		dataFixerUpper.addSchema(LevelDataType.ENTITY, new Schema() {
			@Override
			public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
				if ("MinecartCommandBlock".equals(tag.getString("id"))) {
					tag.putString("id", "Control");
					dataFixer.update(LevelDataType.BLOCK_ENTITY, tag, dataVersion);
					tag.putString("id", "MinecartCommandBlock");
				}

				return tag;
			}
		});
		dataFixerUpper.addSchema(LevelDataType.BLOCK_ENTITY, new ItemListSchema("Furnace", "Items"));
		dataFixerUpper.addSchema(LevelDataType.BLOCK_ENTITY, new ItemListSchema("Chest", "Items"));
		dataFixerUpper.addSchema(LevelDataType.BLOCK_ENTITY, new ItemListSchema("Trap", "Items"));
		dataFixerUpper.addSchema(LevelDataType.BLOCK_ENTITY, new ItemListSchema("Dropper", "Items"));
		dataFixerUpper.addSchema(LevelDataType.BLOCK_ENTITY, new ItemListSchema("Cauldron", "Items"));
		dataFixerUpper.addSchema(LevelDataType.BLOCK_ENTITY, new ItemListSchema("Hopper", "Items"));
		dataFixerUpper.addSchema(LevelDataType.BLOCK_ENTITY, new ItemSchema("RecordPlayer", "RecordItem"));
		dataFixerUpper.addSchema(LevelDataType.BLOCK_ENTITY, new Schema() {
			@Override
			public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
				if ("MobSpawner".equals(tag.getString("id"))) {
					if (tag.contains("SpawnPotentials", 9)) {
						NbtList nbtList = tag.getList("SpawnPotentials", 10);

						for (int i = 0; i < nbtList.size(); i++) {
							NbtCompound nbtCompound = nbtList.getCompound(i);
							nbtCompound.put("Entity", dataFixer.update(LevelDataType.ENTITY, nbtCompound.getCompound("Entity"), dataVersion));
						}
					}

					tag.put("SpawnData", dataFixer.update(LevelDataType.ENTITY, tag.getCompound("SpawnData"), dataVersion));
				}

				return tag;
			}
		});
		dataFixerUpper.addSchema(LevelDataType.ITEM_INSTANCE, new BlockEntitySchema());
		dataFixerUpper.addSchema(LevelDataType.ITEM_INSTANCE, new EntityTagSchema());
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
