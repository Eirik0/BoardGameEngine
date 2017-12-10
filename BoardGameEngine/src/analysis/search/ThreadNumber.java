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
		int threadNum = num == null ? 0 : num.intValue() + 1;
		threadNumMap.put(className, Integer.valueOf(threadNum));
		return threadNum;
	}
}
