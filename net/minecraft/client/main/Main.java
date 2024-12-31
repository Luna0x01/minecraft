package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.Optional;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.class_4325;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.util.Session;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
	private static final Logger field_21592 = LogManager.getLogger();

	public static void main(String[] args) {
		OptionParser optionParser = new OptionParser();
		optionParser.allowsUnrecognizedOptions();
		optionParser.accepts("demo");
		optionParser.accepts("fullscreen");
		optionParser.accepts("checkGlErrors");
		OptionSpec<String> optionSpec = optionParser.accepts("server").withRequiredArg();
		OptionSpec<Integer> optionSpec2 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, new Integer[0]);
		OptionSpec<File> optionSpec3 = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
		OptionSpec<File> optionSpec4 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
		OptionSpec<File> optionSpec5 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
		OptionSpec<String> optionSpec6 = optionParser.accepts("proxyHost").withRequiredArg();
		OptionSpec<Integer> optionSpec7 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
		OptionSpec<String> optionSpec8 = optionParser.accepts("proxyUser").withRequiredArg();
		OptionSpec<String> optionSpec9 = optionParser.accepts("proxyPass").withRequiredArg();
		OptionSpec<String> optionSpec10 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + Util.method_20227() % 1000L, new String[0]);
		OptionSpec<String> optionSpec11 = optionParser.accepts("uuid").withRequiredArg();
		OptionSpec<String> optionSpec12 = optionParser.accepts("accessToken").withRequiredArg().required();
		OptionSpec<String> optionSpec13 = optionParser.accepts("version").withRequiredArg().required();
		OptionSpec<Integer> optionSpec14 = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
		OptionSpec<Integer> optionSpec15 = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
		OptionSpec<Integer> optionSpec16 = optionParser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
		OptionSpec<Integer> optionSpec17 = optionParser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
		OptionSpec<String> optionSpec18 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
		OptionSpec<String> optionSpec19 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
		OptionSpec<String> optionSpec20 = optionParser.accepts("assetIndex").withRequiredArg();
		OptionSpec<String> optionSpec21 = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
		OptionSpec<String> optionSpec22 = optionParser.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
		OptionSpec<String> optionSpec23 = optionParser.nonOptions();
		OptionSet optionSet = optionParser.parse(args);
		List<String> list = optionSet.valuesOf(optionSpec23);
		if (!list.isEmpty()) {
			System.out.println("Completely ignored arguments: " + list);
		}

		String string = method_20304(optionSet, optionSpec6);
		Proxy proxy = Proxy.NO_PROXY;
		if (string != null) {
			try {
				proxy = new Proxy(Type.SOCKS, new InetSocketAddress(string, method_20304(optionSet, optionSpec7)));
			} catch (Exception var52) {
			}
		}

		final String string2 = method_20304(optionSet, optionSpec8);
		final String string3 = method_20304(optionSet, optionSpec9);
		if (!proxy.equals(Proxy.NO_PROXY) && isNotNullOrEmpty(string2) && isNotNullOrEmpty(string3)) {
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(string2, string3.toCharArray());
				}
			});
		}

		int i = method_20304(optionSet, optionSpec14);
		int j = method_20304(optionSet, optionSpec15);
		Optional<Integer> optional = Optional.ofNullable(method_20304(optionSet, optionSpec16));
		Optional<Integer> optional2 = Optional.ofNullable(method_20304(optionSet, optionSpec17));
		boolean bl = optionSet.has("fullscreen");
		boolean bl2 = optionSet.has("demo");
		String string4 = method_20304(optionSet, optionSpec13);
		Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new Serializer()).create();
		PropertyMap propertyMap = JsonHelper.deserialize(gson, method_20304(optionSet, optionSpec18), PropertyMap.class);
		PropertyMap propertyMap2 = JsonHelper.deserialize(gson, method_20304(optionSet, optionSpec19), PropertyMap.class);
		String string5 = method_20304(optionSet, optionSpec22);
		File file = method_20304(optionSet, optionSpec3);
		File file2 = optionSet.has(optionSpec4) ? method_20304(optionSet, optionSpec4) : new File(file, "assets/");
		File file3 = optionSet.has(optionSpec5) ? method_20304(optionSet, optionSpec5) : new File(file, "resourcepacks/");
		String string6 = optionSet.has(optionSpec11)
			? (String)optionSpec11.value(optionSet)
			: PlayerEntity.getOfflinePlayerUuid((String)optionSpec10.value(optionSet)).toString();
		String string7 = optionSet.has(optionSpec20) ? (String)optionSpec20.value(optionSet) : null;
		String string8 = method_20304(optionSet, optionSpec);
		Integer integer = method_20304(optionSet, optionSpec2);
		Session session = new Session((String)optionSpec10.value(optionSet), string6, (String)optionSpec12.value(optionSet), (String)optionSpec21.value(optionSet));
		RunArgs runArgs = new RunArgs(
			new RunArgs.Args(session, propertyMap, propertyMap2, proxy),
			new RunArgs.WindowInformation(i, j, optional, optional2, bl),
			new RunArgs.Directories(file, file3, file2, string7),
			new RunArgs.Game(bl2, string4, string5),
			new RunArgs.AutoConnect(string8, integer)
		);
		Thread thread = new Thread("Client Shutdown Thread") {
			public void run() {
				MinecraftClient.stopServer();
			}
		};
		thread.setUncaughtExceptionHandler(new class_4325(field_21592));
		Runtime.getRuntime().addShutdownHook(thread);
		Thread.currentThread().setName("Client thread");
		new MinecraftClient(runArgs).run();
	}

	private static <T> T method_20304(OptionSet optionSet, OptionSpec<T> optionSpec) {
		try {
			return (T)optionSet.valueOf(optionSpec);
		} catch (Throwable var5) {
			if (optionSpec instanceof ArgumentAcceptingOptionSpec) {
				ArgumentAcceptingOptionSpec<T> argumentAcceptingOptionSpec = (ArgumentAcceptingOptionSpec<T>)optionSpec;
				List<T> list = argumentAcceptingOptionSpec.defaultValues();
				if (!list.isEmpty()) {
					return (T)list.get(0);
				}
			}

			throw var5;
		}
	}

	private static boolean isNotNullOrEmpty(String s) {
		return s != null && !s.isEmpty();
	}

	static {
		System.setProperty("java.awt.headless", "true");
	}
}
