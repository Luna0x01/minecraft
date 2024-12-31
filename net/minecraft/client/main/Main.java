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
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.util.Session;
import net.minecraft.util.JsonHelper;

public class Main {
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
		OptionSpec<String> optionSpec10 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + MinecraftClient.getTime() % 1000L, new String[0]);
		OptionSpec<String> optionSpec11 = optionParser.accepts("uuid").withRequiredArg();
		OptionSpec<String> optionSpec12 = optionParser.accepts("accessToken").withRequiredArg().required();
		OptionSpec<String> optionSpec13 = optionParser.accepts("version").withRequiredArg().required();
		OptionSpec<Integer> optionSpec14 = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
		OptionSpec<Integer> optionSpec15 = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
		OptionSpec<String> optionSpec16 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
		OptionSpec<String> optionSpec17 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
		OptionSpec<String> optionSpec18 = optionParser.accepts("assetIndex").withRequiredArg();
		OptionSpec<String> optionSpec19 = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
		OptionSpec<String> optionSpec20 = optionParser.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
		OptionSpec<String> optionSpec21 = optionParser.nonOptions();
		OptionSet optionSet = optionParser.parse(args);
		List<String> list = optionSet.valuesOf(optionSpec21);
		if (!list.isEmpty()) {
			System.out.println("Completely ignored arguments: " + list);
		}

		String string = (String)optionSet.valueOf(optionSpec6);
		Proxy proxy = Proxy.NO_PROXY;
		if (string != null) {
			try {
				proxy = new Proxy(Type.SOCKS, new InetSocketAddress(string, (Integer)optionSet.valueOf(optionSpec7)));
			} catch (Exception var48) {
			}
		}

		final String string2 = (String)optionSet.valueOf(optionSpec8);
		final String string3 = (String)optionSet.valueOf(optionSpec9);
		if (!proxy.equals(Proxy.NO_PROXY) && isNotNullOrEmpty(string2) && isNotNullOrEmpty(string3)) {
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(string2, string3.toCharArray());
				}
			});
		}

		int i = (Integer)optionSet.valueOf(optionSpec14);
		int j = (Integer)optionSet.valueOf(optionSpec15);
		boolean bl = optionSet.has("fullscreen");
		boolean bl2 = optionSet.has("checkGlErrors");
		boolean bl3 = optionSet.has("demo");
		String string4 = (String)optionSet.valueOf(optionSpec13);
		Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new Serializer()).create();
		PropertyMap propertyMap = JsonHelper.deserialize(gson, (String)optionSet.valueOf(optionSpec16), PropertyMap.class);
		PropertyMap propertyMap2 = JsonHelper.deserialize(gson, (String)optionSet.valueOf(optionSpec17), PropertyMap.class);
		String string5 = (String)optionSet.valueOf(optionSpec20);
		File file = (File)optionSet.valueOf(optionSpec3);
		File file2 = optionSet.has(optionSpec4) ? (File)optionSet.valueOf(optionSpec4) : new File(file, "assets/");
		File file3 = optionSet.has(optionSpec5) ? (File)optionSet.valueOf(optionSpec5) : new File(file, "resourcepacks/");
		String string6 = optionSet.has(optionSpec11) ? (String)optionSpec11.value(optionSet) : (String)optionSpec10.value(optionSet);
		String string7 = optionSet.has(optionSpec18) ? (String)optionSpec18.value(optionSet) : null;
		String string8 = (String)optionSet.valueOf(optionSpec);
		Integer integer = (Integer)optionSet.valueOf(optionSpec2);
		Session session = new Session((String)optionSpec10.value(optionSet), string6, (String)optionSpec12.value(optionSet), (String)optionSpec19.value(optionSet));
		RunArgs runArgs = new RunArgs(
			new RunArgs.Args(session, propertyMap, propertyMap2, proxy),
			new RunArgs.WindowInformation(i, j, bl, bl2),
			new RunArgs.Directories(file, file3, file2, string7),
			new RunArgs.Game(bl3, string4, string5),
			new RunArgs.AutoConnect(string8, integer)
		);
		Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
			public void run() {
				MinecraftClient.stopServer();
			}
		});
		Thread.currentThread().setName("Client thread");
		new MinecraftClient(runArgs).run();
	}

	private static boolean isNotNullOrEmpty(String s) {
		return s != null && !s.isEmpty();
	}
}
