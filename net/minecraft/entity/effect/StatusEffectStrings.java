package net.minecraft.entity.effect;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.util.collection.IntegerStorage;

public class StatusEffectStrings {
	public static final String UNCRAFTABLE = null;
	public static final String SUGAR = "-0+1-2-3&4-4+13";
	public static final String GHAST_TEAR = "+0-1-2-3&4-4+13";
	public static final String POISON = "-0-1+2-3&4-4+13";
	public static final String FERMENTED_SPIDER_EYE = "-0+3-4+13";
	public static final String GLISTERING_MELON = "+0-1+2-3&4-4+13";
	public static final String BLAZE_POWDER = "+0-1-2+3&4-4+13";
	public static final String MAGMA_CREAM = "+0+1-2-3&4-4+13";
	public static final String REDSTONE = "-5+6-7";
	public static final String GLOWSTONE = "+5-6-7";
	public static final String GUNPOWDER = "+14&13-13";
	public static final String GOLDEN_CARROT = "-0+1+2-3+13&4-4";
	public static final String WATER_BREATHING = "+0-1+2+3+13&4-4";
	public static final String RABBIT_FOOT = "+0+1-2+3&4-4+13";
	private static final Map<Integer, String> field_9162 = Maps.newHashMap();
	private static final Map<Integer, String> field_9163 = Maps.newHashMap();
	private static final Map<Integer, Integer> field_9164 = Maps.newHashMap();
	private static final String[] field_4427 = new String[]{
		"potion.prefix.mundane",
		"potion.prefix.uninteresting",
		"potion.prefix.bland",
		"potion.prefix.clear",
		"potion.prefix.milky",
		"potion.prefix.diffuse",
		"potion.prefix.artless",
		"potion.prefix.thin",
		"potion.prefix.awkward",
		"potion.prefix.flat",
		"potion.prefix.bulky",
		"potion.prefix.bungling",
		"potion.prefix.buttered",
		"potion.prefix.smooth",
		"potion.prefix.suave",
		"potion.prefix.debonair",
		"potion.prefix.thick",
		"potion.prefix.elegant",
		"potion.prefix.fancy",
		"potion.prefix.charming",
		"potion.prefix.dashing",
		"potion.prefix.refined",
		"potion.prefix.cordial",
		"potion.prefix.sparkling",
		"potion.prefix.potent",
		"potion.prefix.foul",
		"potion.prefix.odorless",
		"potion.prefix.rank",
		"potion.prefix.harsh",
		"potion.prefix.acrid",
		"potion.prefix.gross",
		"potion.prefix.stinky"
	};

	public static boolean method_3469(int i, int j) {
		return (i & 1 << j) != 0;
	}

	private static int method_3479(int i, int j) {
		return method_3469(i, j) ? 1 : 0;
	}

	private static int method_3480(int i, int j) {
		return method_3469(i, j) ? 0 : 1;
	}

	public static int method_3468(int i) {
		return method_3470(i, 5, 4, 3, 2, 1);
	}

	public static int method_3475(Collection<StatusEffectInstance> collection) {
		int i = 3694022;
		if (collection != null && !collection.isEmpty()) {
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			float j = 0.0F;

			for (StatusEffectInstance statusEffectInstance : collection) {
				if (statusEffectInstance.shouldShowParticles()) {
					int k = StatusEffect.STATUS_EFFECTS[statusEffectInstance.getEffectId()].getColor();

					for (int l = 0; l <= statusEffectInstance.getAmplifier(); l++) {
						f += (float)(k >> 16 & 0xFF) / 255.0F;
						g += (float)(k >> 8 & 0xFF) / 255.0F;
						h += (float)(k >> 0 & 0xFF) / 255.0F;
						j++;
					}
				}
			}

			if (j == 0.0F) {
				return 0;
			} else {
				f = f / j * 255.0F;
				g = g / j * 255.0F;
				h = h / j * 255.0F;
				return (int)f << 16 | (int)g << 8 | (int)h;
			}
		} else {
			return i;
		}
	}

	public static boolean method_4633(Collection<StatusEffectInstance> collection) {
		for (StatusEffectInstance statusEffectInstance : collection) {
			if (!statusEffectInstance.isAmbient()) {
				return false;
			}
		}

		return true;
	}

	public static int getColor(int i, boolean bl) {
		Integer integer = IntegerStorage.get(i);
		if (!bl) {
			if (field_9164.containsKey(integer)) {
				return (Integer)field_9164.get(integer);
			} else {
				int j = method_3475(getPotionEffects(integer, false));
				field_9164.put(integer, j);
				return j;
			}
		} else {
			return method_3475(getPotionEffects(integer, true));
		}
	}

	public static String method_3478(int i) {
		int j = method_3468(i);
		return field_4427[j];
	}

	private static int method_3476(boolean bl, boolean bl2, boolean bl3, int i, int j, int k, int l) {
		int m = 0;
		if (bl) {
			m = method_3480(l, j);
		} else if (i != -1) {
			if (i == 0 && method_3481(l) == j) {
				m = 1;
			} else if (i == 1 && method_3481(l) > j) {
				m = 1;
			} else if (i == 2 && method_3481(l) < j) {
				m = 1;
			}
		} else {
			m = method_3479(l, j);
		}

		if (bl2) {
			m *= k;
		}

		if (bl3) {
			m *= -1;
		}

		return m;
	}

	private static int method_3481(int i) {
		int j;
		for (j = 0; i > 0; j++) {
			i &= i - 1;
		}

		return j;
	}

	private static int method_3474(String string, int i, int j, int k) {
		if (i < string.length() && j >= 0 && i < j) {
			int l = string.indexOf(124, i);
			if (l >= 0 && l < j) {
				int m = method_3474(string, i, l - 1, k);
				if (m > 0) {
					return m;
				} else {
					int n = method_3474(string, l + 1, j, k);
					return n > 0 ? n : 0;
				}
			} else {
				int o = string.indexOf(38, i);
				if (o >= 0 && o < j) {
					int p = method_3474(string, i, o - 1, k);
					if (p <= 0) {
						return 0;
					} else {
						int q = method_3474(string, o + 1, j, k);
						if (q <= 0) {
							return 0;
						} else {
							return p > q ? p : q;
						}
					}
				} else {
					boolean bl = false;
					boolean bl2 = false;
					boolean bl3 = false;
					boolean bl4 = false;
					boolean bl5 = false;
					int r = -1;
					int s = 0;
					int t = 0;
					int u = 0;

					for (int v = i; v < j; v++) {
						char c = string.charAt(v);
						if (c >= '0' && c <= '9') {
							if (bl) {
								t = c - '0';
								bl2 = true;
							} else {
								s *= 10;
								s += c - '0';
								bl3 = true;
							}
						} else if (c == '*') {
							bl = true;
						} else if (c == '!') {
							if (bl3) {
								u += method_3476(bl4, bl2, bl5, r, s, t, k);
								bl4 = false;
								bl5 = false;
								bl = false;
								bl2 = false;
								bl3 = false;
								t = 0;
								s = 0;
								r = -1;
							}

							bl4 = true;
						} else if (c == '-') {
							if (bl3) {
								u += method_3476(bl4, bl2, bl5, r, s, t, k);
								bl4 = false;
								bl5 = false;
								bl = false;
								bl2 = false;
								bl3 = false;
								t = 0;
								s = 0;
								r = -1;
							}

							bl5 = true;
						} else if (c != '=' && c != '<' && c != '>') {
							if (c == '+' && bl3) {
								u += method_3476(bl4, bl2, bl5, r, s, t, k);
								bl4 = false;
								bl5 = false;
								bl = false;
								bl2 = false;
								bl3 = false;
								t = 0;
								s = 0;
								r = -1;
							}
						} else {
							if (bl3) {
								u += method_3476(bl4, bl2, bl5, r, s, t, k);
								bl4 = false;
								bl5 = false;
								bl = false;
								bl2 = false;
								bl3 = false;
								t = 0;
								s = 0;
								r = -1;
							}

							if (c == '=') {
								r = 0;
							} else if (c == '<') {
								r = 2;
							} else if (c == '>') {
								r = 1;
							}
						}
					}

					if (bl3) {
						u += method_3476(bl4, bl2, bl5, r, s, t, k);
					}

					return u;
				}
			}
		} else {
			return 0;
		}
	}

	public static List<StatusEffectInstance> getPotionEffects(int i, boolean bl) {
		List<StatusEffectInstance> list = null;

		for (StatusEffect statusEffect : StatusEffect.STATUS_EFFECTS) {
			if (statusEffect != null && (!statusEffect.method_2448() || bl)) {
				String string = (String)field_9162.get(statusEffect.getId());
				if (string != null) {
					int l = method_3474(string, 0, string.length(), i);
					if (l > 0) {
						int m = 0;
						String string2 = (String)field_9163.get(statusEffect.getId());
						if (string2 != null) {
							m = method_3474(string2, 0, string2.length(), i);
							if (m < 0) {
								m = 0;
							}
						}

						if (statusEffect.isInstant()) {
							l = 1;
						} else {
							l = 1200 * (l * 3 + (l - 1) * 2);
							l >>= m;
							l = (int)Math.round((double)l * statusEffect.method_2446());
							if ((i & 16384) != 0) {
								l = (int)Math.round((double)l * 0.75 + 0.5);
							}
						}

						if (list == null) {
							list = Lists.newArrayList();
						}

						StatusEffectInstance statusEffectInstance = new StatusEffectInstance(statusEffect.getId(), l, m);
						if ((i & 16384) != 0) {
							statusEffectInstance.setSplash(true);
						}

						list.add(statusEffectInstance);
					}
				}
			}
		}

		return list;
	}

	private static int method_3471(int i, int j, boolean bl, boolean bl2, boolean bl3) {
		if (bl3) {
			if (!method_3469(i, j)) {
				return 0;
			}
		} else if (bl) {
			i &= ~(1 << j);
		} else if (bl2) {
			if ((i & 1 << j) == 0) {
				i |= 1 << j;
			} else {
				i &= ~(1 << j);
			}
		} else {
			i |= 1 << j;
		}

		return i;
	}

	public static int getStatusEffectData(int i, String string) {
		int j = 0;
		int k = string.length();
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;
		int l = 0;

		for (int m = j; m < k; m++) {
			char c = string.charAt(m);
			if (c >= '0' && c <= '9') {
				l *= 10;
				l += c - '0';
				bl = true;
			} else if (c == '!') {
				if (bl) {
					i = method_3471(i, l, bl3, bl2, bl4);
					bl4 = false;
					bl2 = false;
					bl3 = false;
					bl = false;
					l = 0;
				}

				bl2 = true;
			} else if (c == '-') {
				if (bl) {
					i = method_3471(i, l, bl3, bl2, bl4);
					bl4 = false;
					bl2 = false;
					bl3 = false;
					bl = false;
					l = 0;
				}

				bl3 = true;
			} else if (c == '+') {
				if (bl) {
					i = method_3471(i, l, bl3, bl2, bl4);
					bl4 = false;
					bl2 = false;
					bl3 = false;
					bl = false;
					l = 0;
				}
			} else if (c == '&') {
				if (bl) {
					i = method_3471(i, l, bl3, bl2, bl4);
					bl4 = false;
					bl2 = false;
					bl3 = false;
					bl = false;
					l = 0;
				}

				bl4 = true;
			}
		}

		if (bl) {
			i = method_3471(i, l, bl3, bl2, bl4);
		}

		return i & 32767;
	}

	public static int method_3470(int i, int j, int k, int l, int m, int n) {
		return (method_3469(i, j) ? 16 : 0) | (method_3469(i, k) ? 8 : 0) | (method_3469(i, l) ? 4 : 0) | (method_3469(i, m) ? 2 : 0) | (method_3469(i, n) ? 1 : 0);
	}

	static {
		field_9162.put(StatusEffect.REGENERATION.getId(), "0 & !1 & !2 & !3 & 0+6");
		field_9162.put(StatusEffect.SPEED.getId(), "!0 & 1 & !2 & !3 & 1+6");
		field_9162.put(StatusEffect.FIRE_RESISTANCE.getId(), "0 & 1 & !2 & !3 & 0+6");
		field_9162.put(StatusEffect.INSTANT_HEALTH.getId(), "0 & !1 & 2 & !3");
		field_9162.put(StatusEffect.POISON.getId(), "!0 & !1 & 2 & !3 & 2+6");
		field_9162.put(StatusEffect.WEAKNESS.getId(), "!0 & !1 & !2 & 3 & 3+6");
		field_9162.put(StatusEffect.INSTANT_DAMAGE.getId(), "!0 & !1 & 2 & 3");
		field_9162.put(StatusEffect.SLOWNESS.getId(), "!0 & 1 & !2 & 3 & 3+6");
		field_9162.put(StatusEffect.STRENGTH.getId(), "0 & !1 & !2 & 3 & 3+6");
		field_9162.put(StatusEffect.NIGHTVISION.getId(), "!0 & 1 & 2 & !3 & 2+6");
		field_9162.put(StatusEffect.INVISIBILITY.getId(), "!0 & 1 & 2 & 3 & 2+6");
		field_9162.put(StatusEffect.WATER_BREATHING.getId(), "0 & !1 & 2 & 3 & 2+6");
		field_9162.put(StatusEffect.JUMP_BOOST.getId(), "0 & 1 & !2 & 3 & 3+6");
		field_9163.put(StatusEffect.SPEED.getId(), "5");
		field_9163.put(StatusEffect.HASTE.getId(), "5");
		field_9163.put(StatusEffect.STRENGTH.getId(), "5");
		field_9163.put(StatusEffect.REGENERATION.getId(), "5");
		field_9163.put(StatusEffect.INSTANT_DAMAGE.getId(), "5");
		field_9163.put(StatusEffect.INSTANT_HEALTH.getId(), "5");
		field_9163.put(StatusEffect.RESISTANCE.getId(), "5");
		field_9163.put(StatusEffect.POISON.getId(), "5");
		field_9163.put(StatusEffect.JUMP_BOOST.getId(), "5");
	}
}
