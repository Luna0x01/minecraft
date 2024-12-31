package net.minecraft.util.snooper;

import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.Map.Entry;

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
		}
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
