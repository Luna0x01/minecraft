package net.minecraft.client.util;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class Session {
	private final String username;
	private final String uuid;
	private final String accessToken;
	private final Session.AccountType accountType;

	public Session(String string, String string2, String string3, String string4) {
		this.username = string;
		this.uuid = string2;
		this.accessToken = string3;
		this.accountType = Session.AccountType.byName(string4);
	}

	public String getSessionId() {
		return "token:" + this.accessToken + ":" + this.uuid;
	}

	public String getUuid() {
		return this.uuid;
	}

	public String getUsername() {
		return this.username;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public GameProfile getProfile() {
		try {
			UUID uUID = UUIDTypeAdapter.fromString(this.getUuid());
			return new GameProfile(uUID, this.getUsername());
		} catch (IllegalArgumentException var2) {
			return new GameProfile(null, this.getUsername());
		}
	}

	public static enum AccountType {
		LEGACY("legacy"),
		MOJANG("mojang");

		private static final Map<String, Session.AccountType> field_20013 = (Map<String, Session.AccountType>)Arrays.stream(values())
			.collect(Collectors.toMap(accountType -> accountType.field_20014, Function.identity()));
		private final String field_20014;

		private AccountType(String string2) {
			this.field_20014 = string2;
		}

		@Nullable
		public static Session.AccountType byName(String string) {
			return (Session.AccountType)field_20013.get(string.toLowerCase(Locale.ROOT));
		}
	}
}
