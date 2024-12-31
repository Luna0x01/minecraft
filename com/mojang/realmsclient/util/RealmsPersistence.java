package com.mojang.realmsclient.util;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import net.minecraft.realms.Realms;
import org.apache.commons.io.FileUtils;

public class RealmsPersistence {
	public static RealmsPersistence.RealmsPersistenceData readFile() {
		File file = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
		Gson gson = new Gson();

		try {
			return (RealmsPersistence.RealmsPersistenceData)gson.fromJson(FileUtils.readFileToString(file), RealmsPersistence.RealmsPersistenceData.class);
		} catch (IOException var3) {
			return new RealmsPersistence.RealmsPersistenceData();
		}
	}

	public static void writeFile(RealmsPersistence.RealmsPersistenceData realmsPersistenceData) {
		File file = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
		Gson gson = new Gson();
		String string = gson.toJson(realmsPersistenceData);

		try {
			FileUtils.writeStringToFile(file, string);
		} catch (IOException var5) {
		}
	}

	public static class RealmsPersistenceData {
		public String newsLink;
		public boolean hasUnreadNews = false;

		private RealmsPersistenceData() {
		}
	}
}
