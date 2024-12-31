package net.minecraft.datafixer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelDataType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataFixerUpper implements DataFixer {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<DataFixType, List<Schema>> schemas = Maps.newHashMap();
	private final Map<DataFixType, List<DataFix>> globalList = Maps.newHashMap();
	private final int dataVersion;

	public DataFixerUpper(int i) {
		this.dataVersion = i;
	}

	public NbtCompound update(DataFixType fixType, NbtCompound tag) {
		int i = tag.contains("DataVersion", 99) ? tag.getInt("DataVersion") : -1;
		return i >= 512 ? tag : this.update(fixType, tag, i);
	}

	@Override
	public NbtCompound update(DataFixType fixType, NbtCompound tag, int dataVersion) {
		if (dataVersion < this.dataVersion) {
			tag = this.applyDataFixes(fixType, tag, dataVersion);
			tag = this.applySchemas(fixType, tag, dataVersion);
		}

		return tag;
	}

	private NbtCompound applyDataFixes(DataFixType fixType, NbtCompound tag, int dataVersion) {
		List<DataFix> list = (List<DataFix>)this.globalList.get(fixType);
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				DataFix dataFix = (DataFix)list.get(i);
				if (dataFix.getVersion() > dataVersion) {
					tag = dataFix.fixData(tag);
				}
			}
		}

		return tag;
	}

	private NbtCompound applySchemas(DataFixType fixType, NbtCompound tag, int dataVersion) {
		List<Schema> list = (List<Schema>)this.schemas.get(fixType);
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				tag = ((Schema)list.get(i)).fixData(this, tag, dataVersion);
			}
		}

		return tag;
	}

	public void addSchema(LevelDataType type, Schema schema) {
		this.addSchema((DataFixType)type, schema);
	}

	public void addSchema(DataFixType fixType, Schema schema) {
		this.getOrSetSchema(this.schemas, fixType).add(schema);
	}

	public void addFixer(DataFixType fixType, DataFix fix) {
		List<DataFix> list = this.getOrSetSchema(this.globalList, fixType);
		int i = fix.getVersion();
		if (i > this.dataVersion) {
			LOGGER.warn("Ignored fix registered for version: {} as the DataVersion of the game is: {}", new Object[]{i, this.dataVersion});
		} else {
			if (!list.isEmpty() && Util.<DataFix>getLast(list).getVersion() > i) {
				for (int j = 0; j < list.size(); j++) {
					if (((DataFix)list.get(j)).getVersion() > i) {
						list.add(j, fix);
						break;
					}
				}
			} else {
				list.add(fix);
			}
		}
	}

	private <V> List<V> getOrSetSchema(Map<DataFixType, List<V>> globalList, DataFixType fixType) {
		List<V> list = (List<V>)globalList.get(fixType);
		if (list == null) {
			list = Lists.newArrayList();
			globalList.put(fixType, list);
		}

		return list;
	}
}
