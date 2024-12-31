package net.minecraft.util.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<String> path = Lists.newArrayList();
	private final List<Long> timeList = Lists.newArrayList();
	private boolean enabled;
	private String fullLocation = "";
	private final Map<String, Long> profilingLocationTimes = Maps.newHashMap();
	private long field_22250;
	private int field_22251;

	public boolean method_21519() {
		return this.enabled;
	}

	public void method_21521() {
		this.enabled = false;
	}

	public long method_21522() {
		return this.field_22250;
	}

	public int method_21523() {
		return this.field_22251;
	}

	public void method_21520(int i) {
		if (!this.enabled) {
			this.enabled = true;
			this.profilingLocationTimes.clear();
			this.fullLocation = "";
			this.path.clear();
			this.field_22251 = i;
			this.field_22250 = Util.method_20230();
		}
	}

	public void push(String location) {
		if (this.enabled) {
			if (!this.fullLocation.isEmpty()) {
				this.fullLocation = this.fullLocation + ".";
			}

			this.fullLocation = this.fullLocation + location;
			this.path.add(this.fullLocation);
			this.timeList.add(Util.method_20230());
		}
	}

	public void push(Supplier<String> locationSupplier) {
		if (this.enabled) {
			this.push((String)locationSupplier.get());
		}
	}

	public void pop() {
		if (this.enabled && !this.timeList.isEmpty()) {
			long l = Util.method_20230();
			long m = (Long)this.timeList.remove(this.timeList.size() - 1);
			this.path.remove(this.path.size() - 1);
			long n = l - m;
			if (this.profilingLocationTimes.containsKey(this.fullLocation)) {
				this.profilingLocationTimes.put(this.fullLocation, (Long)this.profilingLocationTimes.get(this.fullLocation) + n);
			} else {
				this.profilingLocationTimes.put(this.fullLocation, n);
			}

			if (n > 100000000L) {
				LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", this.fullLocation, (double)n / 1000000.0);
			}

			this.fullLocation = this.path.isEmpty() ? "" : (String)this.path.get(this.path.size() - 1);
		}
	}

	public List<Profiler.Section> getData(String location) {
		String string = location;
		long l = this.profilingLocationTimes.containsKey("root") ? (Long)this.profilingLocationTimes.get("root") : 0L;
		long m = this.profilingLocationTimes.containsKey(location) ? (Long)this.profilingLocationTimes.get(location) : -1L;
		List<Profiler.Section> list = Lists.newArrayList();
		if (!location.isEmpty()) {
			location = location + ".";
		}

		long n = 0L;

		for (String string2 : this.profilingLocationTimes.keySet()) {
			if (string2.length() > location.length() && string2.startsWith(location) && string2.indexOf(".", location.length() + 1) < 0) {
				n += this.profilingLocationTimes.get(string2);
			}
		}

		float f = (float)n;
		if (n < m) {
			n = m;
		}

		if (l < n) {
			l = n;
		}

		for (String string3 : this.profilingLocationTimes.keySet()) {
			if (string3.length() > location.length() && string3.startsWith(location) && string3.indexOf(".", location.length() + 1) < 0) {
				long o = (Long)this.profilingLocationTimes.get(string3);
				double d = (double)o * 100.0 / (double)n;
				double e = (double)o * 100.0 / (double)l;
				String string4 = string3.substring(location.length());
				list.add(new Profiler.Section(string4, d, e));
			}
		}

		for (String string5 : this.profilingLocationTimes.keySet()) {
			this.profilingLocationTimes.put(string5, (Long)this.profilingLocationTimes.get(string5) * 999L / 1000L);
		}

		if ((float)n > f) {
			list.add(new Profiler.Section("unspecified", (double)((float)n - f) * 100.0 / (double)n, (double)((float)n - f) * 100.0 / (double)l));
		}

		Collections.sort(list);
		list.add(0, new Profiler.Section(string, 100.0, (double)n * 100.0 / (double)l));
		return list;
	}

	public void swap(String location) {
		this.pop();
		this.push(location);
	}

	public void swap(Supplier<String> locationSupplier) {
		this.pop();
		this.push(locationSupplier);
	}

	public String getCurrentLocation() {
		return this.path.isEmpty() ? "[UNKNOWN]" : (String)this.path.get(this.path.size() - 1);
	}

	public static final class Section implements Comparable<Profiler.Section> {
		public double relativePercentage;
		public double absolutePercentage;
		public String name;

		public Section(String string, double d, double e) {
			this.name = string;
			this.relativePercentage = d;
			this.absolutePercentage = e;
		}

		public int compareTo(Profiler.Section section) {
			if (section.relativePercentage < this.relativePercentage) {
				return -1;
			} else {
				return section.relativePercentage > this.relativePercentage ? 1 : section.name.compareTo(this.name);
			}
		}

		public int getColor() {
			return (this.name.hashCode() & 11184810) + 4473924;
		}
	}
}
