package net.minecraft.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;

public class UserCache {
	public static final SimpleDateFormat EXPIRATION_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	private final Map<String, UserCache.Entry> byName = Maps.newHashMap();
	private final Map<UUID, UserCache.Entry> byUuid = Maps.newHashMap();
	private final LinkedList<GameProfile> profiles = Lists.newLinkedList();
	private final MinecraftServer server;
	protected final Gson gson;
	private final File cacheFile;
	private static final ParameterizedType ENTRY_LIST_TYPE = new ParameterizedType() {
		public Type[] getActualTypeArguments() {
			return new Type[]{UserCache.Entry.class};
		}

		public Type getRawType() {
			return List.class;
		}

		public Type getOwnerType() {
			return null;
		}
	};

	public UserCache(MinecraftServer minecraftServer, File file) {
		this.server = minecraftServer;
		this.cacheFile = file;
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeHierarchyAdapter(UserCache.Entry.class, new UserCache.JsonConverter());
		this.gson = gsonBuilder.create();
		this.load();
	}

	private static GameProfile method_8188(MinecraftServer minecraftServer, String string) {
		final GameProfile[] gameProfiles = new GameProfile[1];
		ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
			public void onProfileLookupSucceeded(GameProfile gameProfile) {
				gameProfiles[0] = gameProfile;
			}

			public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
				gameProfiles[0] = null;
			}
		};
		minecraftServer.getGameProfileRepo().findProfilesByNames(new String[]{string}, Agent.MINECRAFT, profileLookupCallback);
		if (!minecraftServer.isOnlineMode() && gameProfiles[0] == null) {
			UUID uUID = PlayerEntity.getUuidFromProfile(new GameProfile(null, string));
			GameProfile gameProfile = new GameProfile(uUID, string);
			profileLookupCallback.onProfileLookupSucceeded(gameProfile);
		}

		return gameProfiles[0];
	}

	public void add(GameProfile profile) {
		this.add(profile, null);
	}

	private void add(GameProfile profile, Date expirationDate) {
		UUID uUID = profile.getId();
		if (expirationDate == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(2, 1);
			expirationDate = calendar.getTime();
		}

		String string = profile.getName().toLowerCase(Locale.ROOT);
		UserCache.Entry entry = new UserCache.Entry(profile, expirationDate);
		if (this.byUuid.containsKey(uUID)) {
			UserCache.Entry entry2 = (UserCache.Entry)this.byUuid.get(uUID);
			this.byName.remove(entry2.getProfile().getName().toLowerCase(Locale.ROOT));
			this.profiles.remove(profile);
		}

		this.byName.put(profile.getName().toLowerCase(Locale.ROOT), entry);
		this.byUuid.put(uUID, entry);
		this.profiles.addFirst(profile);
		this.save();
	}

	public GameProfile findByName(String name) {
		String string = name.toLowerCase(Locale.ROOT);
		UserCache.Entry entry = (UserCache.Entry)this.byName.get(string);
		if (entry != null && new Date().getTime() >= entry.expirationDate.getTime()) {
			this.byUuid.remove(entry.getProfile().getId());
			this.byName.remove(entry.getProfile().getName().toLowerCase(Locale.ROOT));
			this.profiles.remove(entry.getProfile());
			entry = null;
		}

		if (entry != null) {
			GameProfile gameProfile = entry.getProfile();
			this.profiles.remove(gameProfile);
			this.profiles.addFirst(gameProfile);
		} else {
			GameProfile gameProfile2 = method_8188(this.server, string);
			if (gameProfile2 != null) {
				this.add(gameProfile2);
				entry = (UserCache.Entry)this.byName.get(string);
			}
		}

		this.save();
		return entry == null ? null : entry.getProfile();
	}

	public String[] getNames() {
		List<String> list = Lists.newArrayList(this.byName.keySet());
		return (String[])list.toArray(new String[list.size()]);
	}

	public GameProfile getByUuid(UUID uuid) {
		UserCache.Entry entry = (UserCache.Entry)this.byUuid.get(uuid);
		return entry == null ? null : entry.getProfile();
	}

	private UserCache.Entry getEntry(UUID uuid) {
		UserCache.Entry entry = (UserCache.Entry)this.byUuid.get(uuid);
		if (entry != null) {
			GameProfile gameProfile = entry.getProfile();
			this.profiles.remove(gameProfile);
			this.profiles.addFirst(gameProfile);
		}

		return entry;
	}

	public void load() {
		BufferedReader bufferedReader = null;

		try {
			bufferedReader = Files.newReader(this.cacheFile, Charsets.UTF_8);
			List<UserCache.Entry> list = (List<UserCache.Entry>)this.gson.fromJson(bufferedReader, ENTRY_LIST_TYPE);
			this.byName.clear();
			this.byUuid.clear();
			this.profiles.clear();

			for (UserCache.Entry entry : Lists.reverse(list)) {
				if (entry != null) {
					this.add(entry.getProfile(), entry.getExpirationDate());
				}
			}
		} catch (FileNotFoundException var9) {
		} catch (JsonParseException var10) {
		} finally {
			IOUtils.closeQuietly(bufferedReader);
		}
	}

	public void save() {
		String string = this.gson.toJson(this.getLastAccessedEntries(1000));
		BufferedWriter bufferedWriter = null;

		try {
			bufferedWriter = Files.newWriter(this.cacheFile, Charsets.UTF_8);
			bufferedWriter.write(string);
			return;
		} catch (FileNotFoundException var8) {
			return;
		} catch (IOException var9) {
		} finally {
			IOUtils.closeQuietly(bufferedWriter);
		}
	}

	private List<UserCache.Entry> getLastAccessedEntries(int limit) {
		ArrayList<UserCache.Entry> arrayList = Lists.newArrayList();

		for (GameProfile gameProfile : Lists.newArrayList(Iterators.limit(this.profiles.iterator(), limit))) {
			UserCache.Entry entry = this.getEntry(gameProfile.getId());
			if (entry != null) {
				arrayList.add(entry);
			}
		}

		return arrayList;
	}

	class Entry {
		private final GameProfile profile;
		private final Date expirationDate;

		private Entry(GameProfile gameProfile, Date date) {
			this.profile = gameProfile;
			this.expirationDate = date;
		}

		public GameProfile getProfile() {
			return this.profile;
		}

		public Date getExpirationDate() {
			return this.expirationDate;
		}
	}

	class JsonConverter implements JsonDeserializer<UserCache.Entry>, JsonSerializer<UserCache.Entry> {
		private JsonConverter() {
		}

		public JsonElement serialize(UserCache.Entry entry, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("name", entry.getProfile().getName());
			UUID uUID = entry.getProfile().getId();
			jsonObject.addProperty("uuid", uUID == null ? "" : uUID.toString());
			jsonObject.addProperty("expiresOn", UserCache.EXPIRATION_DATE_FORMAT.format(entry.getExpirationDate()));
			return jsonObject;
		}

		public UserCache.Entry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				JsonElement jsonElement2 = jsonObject.get("name");
				JsonElement jsonElement3 = jsonObject.get("uuid");
				JsonElement jsonElement4 = jsonObject.get("expiresOn");
				if (jsonElement2 != null && jsonElement3 != null) {
					String string = jsonElement3.getAsString();
					String string2 = jsonElement2.getAsString();
					Date date = null;
					if (jsonElement4 != null) {
						try {
							date = UserCache.EXPIRATION_DATE_FORMAT.parse(jsonElement4.getAsString());
						} catch (ParseException var14) {
							date = null;
						}
					}

					if (string2 != null && string != null) {
						UUID uUID;
						try {
							uUID = UUID.fromString(string);
						} catch (Throwable var13) {
							return null;
						}

						return UserCache.this.new Entry(new GameProfile(uUID, string2), date);
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}
}
