package analysis.search;

import java.util.HashMap;
import java.util.Map;

public class ThreadNumber {
	private static Map<String, Integer> threadNumMap = new HashMap<>();

	public static synchronized int getThreadNum(Class<?> threadObjClass) {
		return getThreadNum(threadObjClass.getName());
	}

	public static synchronized int getThreadNum(String className) {
		Integer num = threadNumMap.get(className);
		num = num == null ? Integer.valueOf(0) : num + 1;
		threadNumMap.put(className, num);
		return num;
	}
}
