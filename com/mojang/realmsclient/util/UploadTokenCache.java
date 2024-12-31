package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import java.util.Map;

public class UploadTokenCache {
	private static final Map<Long, String> tokenCache = Maps.newHashMap();

	public static String get(long l) {
		return (String)tokenCache.get(l);
	}

	public static void invalidate(long l) {
		tokenCache.remove(l);
	}

	public static void put(long l, String string) {
		tokenCache.put(l, string);
	}
}
