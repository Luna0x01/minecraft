package net.minecraft.util.snooper;

import com.google.common.collect.Maps;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.server.MinecraftServer;

public class Snooper {
	private final Map<String, Object> systemInfo = Maps.newHashMap();
	private final Map<String, Object> gameInfo = Maps.newHashMap();
	private final String token = UUID.randomUUID().toString();
	private final URL snooperUrl;
	private final Snoopable snooped;
	private final Timer timer = new Timer("Snooper Timer", true);
	private final Object lock = new Object();
	private final long startTime;
	private boolean active;
	private int snooperCount;

	public Snooper(String string, Snoopable snoopable, long l) {
		try {
			this.snooperUrl = new URL("http://snoop.minecraft.net/" + string + "?version=" + 2);
		} catch (MalformedURLException var6) {
			throw new IllegalArgumentException();
		}

		this.snooped = snoopable;
		this.startTime = l;
	}

	public void setActive() {
		if (!this.active) {
			this.active = true;
			this.addJavaInfo();
			this.timer.schedule(new TimerTask() {
				public void run() {
					if (Snooper.this.snooped.isSnooperEnabled()) {
						Map<String, Object> map;
						synchronized (Snooper.this.lock) {
							map = Maps.newHashMap(Snooper.this.gameInfo);
							if (Snooper.this.snooperCount == 0) {
								map.putAll(Snooper.this.systemInfo);
							}

							map.put("snooper_count", Snooper.this.snooperCount++);
							map.put("snooper_token", Snooper.this.token);
						}

						MinecraftServer minecraftServer = Snooper.this.snooped instanceof MinecraftServer ? (MinecraftServer)Snooper.this.snooped : null;
						NetworkUtils.snoop(Snooper.this.snooperUrl, map, true, minecraftServer == null ? null : minecraftServer.getProxy());
					}
				}
			}, 0L, 900000L);
		}
	}

	private void addJavaInfo() {
		this.addJvmArgs();
		this.addGameInfo("snooper_token", this.token);
		this.addSystemInfo("snooper_token", this.token);
		this.addSystemInfo("os_name", System.getProperty("os.name"));
		this.addSystemInfo("os_version", System.getProperty("os.version"));
		this.addSystemInfo("os_architecture", System.getProperty("os.arch"));
		this.addSystemInfo("java_version", System.getProperty("java.version"));
		this.addGameInfo("version", "1.11.2");
		this.snooped.addSnooper(this);
	}

	private void addJvmArgs() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		List<String> list = runtimeMXBean.getInputArguments();
		int i = 0;

		for (String string : list) {
			if (string.startsWith("-X")) {
				this.addGameInfo("jvm_arg[" + i++ + "]", string);
			}
		}

		this.addGameInfo("jvm_args", i);
	}

	public void addCpuInfo() {
		this.addSystemInfo("memory_total", Runtime.getRuntime().totalMemory());
		this.addSystemInfo("memory_max", Runtime.getRuntime().maxMemory());
		this.addSystemInfo("memory_free", Runtime.getRuntime().freeMemory());
		this.addSystemInfo("cpu_cores", Runtime.getRuntime().availableProcessors());
		this.snooped.addSnooperInfo(this);
	}

	public void addGameInfo(String key, Object value) {
		synchronized (this.lock) {
			this.gameInfo.put(key, value);
		}
	}

	public void addSystemInfo(String key, Object value) {
		synchronized (this.lock) {
			this.systemInfo.put(key, value);
		}
	}

	public Map<String, String> getAllInfo() {
		Map<String, String> map = Maps.newLinkedHashMap();
		synchronized (this.lock) {
			this.addCpuInfo();

			for (Entry<String, Object> entry : this.systemInfo.entrySet()) {
				map.put(entry.getKey(), entry.getValue().toString());
			}

			for (Entry<String, Object> entry2 : this.gameInfo.entrySet()) {
				map.put(entry2.getKey(), entry2.getValue().toString());
			}

			return map;
		}
	}

	public boolean isActive() {
		return this.active;
	}

	public void cancel() {
		this.timer.cancel();
	}

	public String getSnooperToken() {
		return this.token;
	}

	public long getStartTime() {
		return this.startTime;
	}
}
