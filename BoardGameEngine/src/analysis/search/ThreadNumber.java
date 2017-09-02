package analysis.search;

import java.util.HashMap;
import java.util.Map;

public class ThreadNumber {
	private static Map<Class<?>, Integer> threadNumMap = new HashMap<>();

	public static synchronized int getThreadNum(Class<?> threadObjClass) {
		Integer num = threadNumMap.get(threadObjClass);
		num = num == null ? Integer.valueOf(0) : num + 1;
		threadNumMap.put(threadObjClass, num);
		return num;
	}
}
