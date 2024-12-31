package net.minecraft.util.profiler;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfileResultImpl implements ProfileResult {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ProfileLocationInfo EMPTY_INFO = new ProfileLocationInfo() {
		@Override
		public long getTotalTime() {
			return 0L;
		}

		@Override
		public long getVisitCount() {
			return 0L;
		}

		@Override
		public Object2LongMap<String> getCounts() {
			return Object2LongMaps.emptyMap();
		}
	};
	private static final Splitter SPLITTER = Splitter.on('\u001e');
	private static final Comparator<Entry<String, ProfileResultImpl.CounterInfo>> COMPARATOR = Entry.comparingByValue(
			Comparator.comparingLong(counterInfo -> counterInfo.totalTime)
		)
		.reversed();
	private final Map<String, ? extends ProfileLocationInfo> locationInfos;
	private final long startTime;
	private final int startTick;
	private final long endTime;
	private final int endTick;
	private final int tickDuration;

	public ProfileResultImpl(Map<String, ? extends ProfileLocationInfo> map, long l, int i, long m, int j) {
		this.locationInfos = map;
		this.startTime = l;
		this.startTick = i;
		this.endTime = m;
		this.endTick = j;
		this.tickDuration = j - i;
	}

	private ProfileLocationInfo getInfo(String string) {
		ProfileLocationInfo profileLocationInfo = (ProfileLocationInfo)this.locationInfos.get(string);
		return profileLocationInfo != null ? profileLocationInfo : EMPTY_INFO;
	}

	@Override
	public List<ProfilerTiming> getTimings(String string) {
		String string2 = string;
		ProfileLocationInfo profileLocationInfo = this.getInfo("root");
		long l = profileLocationInfo.getTotalTime();
		ProfileLocationInfo profileLocationInfo2 = this.getInfo(string);
		long m = profileLocationInfo2.getTotalTime();
		long n = profileLocationInfo2.getVisitCount();
		List<ProfilerTiming> list = Lists.newArrayList();
		if (!string.isEmpty()) {
			string = string + '\u001e';
		}

		long o = 0L;

		for (String string3 : this.locationInfos.keySet()) {
			if (isSubpath(string, string3)) {
				o += this.getInfo(string3).getTotalTime();
			}
		}

		float f = (float)o;
		if (o < m) {
			o = m;
		}

		if (l < o) {
			l = o;
		}

		for (String string4 : this.locationInfos.keySet()) {
			if (isSubpath(string, string4)) {
				ProfileLocationInfo profileLocationInfo3 = this.getInfo(string4);
				long p = profileLocationInfo3.getTotalTime();
				double d = (double)p * 100.0 / (double)o;
				double e = (double)p * 100.0 / (double)l;
				String string5 = string4.substring(string.length());
				list.add(new ProfilerTiming(string5, d, e, profileLocationInfo3.getVisitCount()));
			}
		}

		if ((float)o > f) {
			list.add(new ProfilerTiming("unspecified", (double)((float)o - f) * 100.0 / (double)o, (double)((float)o - f) * 100.0 / (double)l, n));
		}

		Collections.sort(list);
		list.add(0, new ProfilerTiming(string2, 100.0, (double)o * 100.0 / (double)l, n));
		return list;
	}

	private static boolean isSubpath(String string, String string2) {
		return string2.length() > string.length() && string2.startsWith(string) && string2.indexOf(30, string.length() + 1) < 0;
	}

	private Map<String, ProfileResultImpl.CounterInfo> setupCounters() {
		Map<String, ProfileResultImpl.CounterInfo> map = Maps.newTreeMap();
		this.locationInfos
			.forEach(
				(string, profileLocationInfo) -> {
					Object2LongMap<String> object2LongMap = profileLocationInfo.getCounts();
					if (!object2LongMap.isEmpty()) {
						List<String> list = SPLITTER.splitToList(string);
						object2LongMap.forEach(
							(stringx, long_) -> ((ProfileResultImpl.CounterInfo)map.computeIfAbsent(stringx, stringxx -> new ProfileResultImpl.CounterInfo()))
									.add(list.iterator(), long_)
						);
					}
				}
			);
		return map;
	}

	@Override
	public long getStartTime() {
		return this.startTime;
	}

	@Override
	public int getStartTick() {
		return this.startTick;
	}

	@Override
	public long getEndTime() {
		return this.endTime;
	}

	@Override
	public int getEndTick() {
		return this.endTick;
	}

	@Override
	public boolean save(File file) {
		file.getParentFile().mkdirs();
		Writer writer = null;

		boolean var4;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			writer.write(this.asString(this.getTimeSpan(), this.getTickSpan()));
			return true;
		} catch (Throwable var8) {
			LOGGER.error("Could not save profiler results to {}", file, var8);
			var4 = false;
		} finally {
			IOUtils.closeQuietly(writer);
		}

		return var4;
	}

	protected String asString(long l, int i) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("---- Minecraft Profiler Results ----\n");
		stringBuilder.append("// ");
		stringBuilder.append(generateWittyComment());
		stringBuilder.append("\n\n");
		stringBuilder.append("Version: ").append(SharedConstants.getGameVersion().getId()).append('\n');
		stringBuilder.append("Time span: ").append(l / 1000000L).append(" ms\n");
		stringBuilder.append("Tick span: ").append(i).append(" ticks\n");
		stringBuilder.append("// This is approximately ")
			.append(String.format(Locale.ROOT, "%.2f", (float)i / ((float)l / 1.0E9F)))
			.append(" ticks per second. It should be ")
			.append(20)
			.append(" ticks per second\n\n");
		stringBuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
		this.appendTiming(0, "root", stringBuilder);
		stringBuilder.append("--- END PROFILE DUMP ---\n\n");
		Map<String, ProfileResultImpl.CounterInfo> map = this.setupCounters();
		if (!map.isEmpty()) {
			stringBuilder.append("--- BEGIN COUNTER DUMP ---\n\n");
			this.appendCounterDump(map, stringBuilder, i);
			stringBuilder.append("--- END COUNTER DUMP ---\n\n");
		}

		return stringBuilder.toString();
	}

	private static StringBuilder indent(StringBuilder stringBuilder, int i) {
		stringBuilder.append(String.format("[%02d] ", i));

		for (int j = 0; j < i; j++) {
			stringBuilder.append("|   ");
		}

		return stringBuilder;
	}

	private void appendTiming(int i, String string, StringBuilder stringBuilder) {
		List<ProfilerTiming> list = this.getTimings(string);
		Object2LongMap<String> object2LongMap = ((ProfileLocationInfo)this.locationInfos.get(string)).getCounts();
		object2LongMap.forEach(
			(stringx, long_) -> indent(stringBuilder, i)
					.append('#')
					.append(stringx)
					.append(' ')
					.append(long_)
					.append('/')
					.append(long_ / (long)this.tickDuration)
					.append('\n')
		);
		if (list.size() >= 3) {
			for (int j = 1; j < list.size(); j++) {
				ProfilerTiming profilerTiming = (ProfilerTiming)list.get(j);
				indent(stringBuilder, i)
					.append(profilerTiming.name)
					.append('(')
					.append(profilerTiming.visitCount)
					.append('/')
					.append(String.format(Locale.ROOT, "%.0f", (float)profilerTiming.visitCount / (float)this.tickDuration))
					.append(')')
					.append(" - ")
					.append(String.format(Locale.ROOT, "%.2f", profilerTiming.parentSectionUsagePercentage))
					.append("%/")
					.append(String.format(Locale.ROOT, "%.2f", profilerTiming.totalUsagePercentage))
					.append("%\n");
				if (!"unspecified".equals(profilerTiming.name)) {
					try {
						this.appendTiming(i + 1, string + '\u001e' + profilerTiming.name, stringBuilder);
					} catch (Exception var9) {
						stringBuilder.append("[[ EXCEPTION ").append(var9).append(" ]]");
					}
				}
			}
		}
	}

	private void appendCounter(int i, String string, ProfileResultImpl.CounterInfo counterInfo, int j, StringBuilder stringBuilder) {
		indent(stringBuilder, i)
			.append(string)
			.append(" total:")
			.append(counterInfo.selfTime)
			.append('/')
			.append(counterInfo.totalTime)
			.append(" average: ")
			.append(counterInfo.selfTime / (long)j)
			.append('/')
			.append(counterInfo.totalTime / (long)j)
			.append('\n');
		counterInfo.subCounters
			.entrySet()
			.stream()
			.sorted(COMPARATOR)
			.forEach(entry -> this.appendCounter(i + 1, (String)entry.getKey(), (ProfileResultImpl.CounterInfo)entry.getValue(), j, stringBuilder));
	}

	private void appendCounterDump(Map<String, ProfileResultImpl.CounterInfo> map, StringBuilder stringBuilder, int i) {
		map.forEach((string, counterInfo) -> {
			stringBuilder.append("-- Counter: ").append(string).append(" --\n");
			this.appendCounter(0, "root", (ProfileResultImpl.CounterInfo)counterInfo.subCounters.get("root"), i, stringBuilder);
			stringBuilder.append("\n\n");
		});
	}

	private static String generateWittyComment() {
		String[] strings = new String[]{
			"Shiny numbers!",
			"Am I not running fast enough? :(",
			"I'm working as hard as I can!",
			"Will I ever be good enough for you? :(",
			"Speedy. Zoooooom!",
			"Hello world",
			"40% better than a crash report.",
			"Now with extra numbers",
			"Now with less numbers",
			"Now with the same numbers",
			"You should add flames to things, it makes them go faster!",
			"Do you feel the need for... optimization?",
			"*cracks redstone whip*",
			"Maybe if you treated it better then it'll have more motivation to work faster! Poor server."
		};

		try {
			return strings[(int)(Util.getMeasuringTimeNano() % (long)strings.length)];
		} catch (Throwable var2) {
			return "Witty comment unavailable :(";
		}
	}

	@Override
	public int getTickSpan() {
		return this.tickDuration;
	}

	static class CounterInfo {
		private long selfTime;
		private long totalTime;
		private final Map<String, ProfileResultImpl.CounterInfo> subCounters = Maps.newHashMap();

		private CounterInfo() {
		}

		public void add(Iterator<String> iterator, long l) {
			this.totalTime += l;
			if (!iterator.hasNext()) {
				this.selfTime += l;
			} else {
				((ProfileResultImpl.CounterInfo)this.subCounters.computeIfAbsent(iterator.next(), string -> new ProfileResultImpl.CounterInfo())).add(iterator, l);
			}
		}
	}
}
