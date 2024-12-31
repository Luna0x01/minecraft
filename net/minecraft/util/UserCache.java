package net.minecraft.util;

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
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.commons.io.IOUtils;

public class UserCache {
	public static final SimpleDateFormat EXPIRATION_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	private static boolean useRemote;
	private final Map<String, UserCache.Entry> byName = Maps.newHashMap();
	private final Map<UUID, UserCache.Entry> byUuid = Maps.newHashMap();
	private final Deque<GameProfile> byAccessTime = Lists.newLinkedList();
	private final GameProfileRepository profileRepository;
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

	public UserCache(GameProfileRepository gameProfileRepository, File file) {
		this.profileRepository = gameProfileRepository;
		this.cacheFile = file;
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeHierarchyAdapter(UserCache.Entry.class, new UserCache.JsonConverter());
		this.gson = gsonBuilder.create();
		this.load();
	}

	private static GameProfile findProfileByName(GameProfileRepository gameProfileRepository, String string) {
		final GameProfile[] gameProfiles = new GameProfile[1];
		ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
			public void onProfileLookupSucceeded(GameProfile gameProfile) {
				gameProfiles[0] = gameProfile;
			}

			public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
				gameProfiles[0] = null;
			}
		};
		gameProfileRepository.findProfilesByNames(new String[]{string}, Agent.MINECRAFT, profileLookupCallback);
		if (!shouldUseRemote() && gameProfiles[0] == null) {
			UUID uUID = PlayerEntity.getUuidFromProfile(new GameProfile(null, string));
			GameProfile gameProfile = new GameProfile(uUID, string);
			profileLookupCallback.onProfileLookupSucceeded(gameProfile);
		}

		return gameProfiles[0];
	}

	public static void setUseRemote(boolean bl) {
		useRemote = bl;
	}

	private static boolean shouldUseRemote() {
		return useRemote;
	}

	public void add(GameProfile gameProfile) {
		this.add(gameProfile, null);
	}

	private void add(GameProfile gameProfile, Date date) {
		UUID uUID = gameProfile.getId();
		if (date == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(2, 1);
			date = calendar.getTime();
		}

		UserCache.Entry entry = new UserCache.Entry(gameProfile, date);
		if (this.byUuid.containsKey(uUID)) {
			UserCache.Entry entry2 = (UserCache.Entry)this.byUuid.get(uUID);
			this.byName.remove(entry2.getProfile().getName().toLowerCase(Locale.ROOT));
			this.byAccessTime.remove(gameProfile);
		}

		this.byName.put(gameProfile.getName().toLowerCase(Locale.ROOT), entry);
		this.byUuid.put(uUID, entry);
		this.byAccessTime.addFirst(gameProfile);
		this.save();
	}

	@Nullable
	public GameProfile findByName(String string) {
		String string2 = string.toLowerCase(Locale.ROOT);
		UserCache.Entry entry = (UserCache.Entry)this.byName.get(string2);
		if (entry != null && new Date().getTime() >= entry.expirationDate.getTime()) {
			this.byUuid.remove(entry.getProfile().getId());
			this.byName.remove(entry.getProfile().getName().toLowerCase(Locale.ROOT));
			this.byAccessTime.remove(entry.getProfile());
			entry = null;
		}

		if (entry != null) {
			GameProfile gameProfile = entry.getProfile();
			this.byAccessTime.remove(gameProfile);
			this.byAccessTime.addFirst(gameProfile);
		} else {
			GameProfile gameProfile2 = findProfileByName(this.profileRepository, string2);
			if (gameProfile2 != null) {
				this.add(gameProfile2);
				entry = (UserCache.Entry)this.byName.get(string2);
			}
		}

		this.save();
		return entry == null ? null : entry.getProfile();
	}

	@Nullable
	public GameProfile getByUuid(UUID uUID) {
		UserCache.Entry entry = (UserCache.Entry)this.byUuid.get(uUID);
		return entry == null ? null : entry.getProfile();
	}

	private UserCache.Entry getEntry(UUID uUID) {
		UserCache.Entry entry = (UserCache.Entry)this.byUuid.get(uUID);
		if (entry != null) {
			GameProfile gameProfile = entry.getProfile();
			this.byAccessTime.remove(gameProfile);
			this.byAccessTime.addFirst(gameProfile);
		}

		return entry;
	}

	public void load() {
		BufferedReader bufferedReader = null;

		try {
			bufferedReader = Files.newReader(this.cacheFile, StandardCharsets.UTF_8);
			List<UserCache.Entry> list = JsonHelper.deserialize(this.gson, bufferedReader, ENTRY_LIST_TYPE);
			this.byName.clear();
			this.byUuid.clear();
			this.byAccessTime.clear();
			if (list != null) {
				for (UserCache.Entry entry : Lists.reverse(list)) {
					if (entry != null) {
						this.add(entry.getProfile(), entry.getExpirationDate());
					}
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
			bufferedWriter = Files.newWriter(this.cacheFile, StandardCharsets.UTF_8);
			bufferedWriter.write(string);
			return;
		} catch (FileNotFoundException var8) {
			return;
		} catch (IOException var9) {
		} finally {
			IOUtils.closeQuietly(bufferedWriter);
		}
	}

	private List<UserCache.Entry> getLastAccessedEntries(int i) {
		List<UserCache.Entry> list = Lists.newArrayList();

		for (GameProfile gameProfile : Lists.newArrayList(Iterators.limit(this.byAccessTime.iterator(), i))) {
			UserCache.Entry entry = this.getEntry(gameProfile.getId());
			if (entry != null) {
				list.add(entry);
			}
		}

		return list;
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

		public JsonElement method_14522(UserCache.Entry entry, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("name", entry.getProfile().getName());
			UUID uUID = entry.getProfile().getId();
			jsonObject.addProperty("uuid", uUID == null ? "" : uUID.toString());
			jsonObject.addProperty("expiresOn", UserCache.EXPIRATION_DATE_FORMAT.format(entry.getExpirationDate()));
			return jsonObject;
		}

		public UserCache.Entry method_14523(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
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
