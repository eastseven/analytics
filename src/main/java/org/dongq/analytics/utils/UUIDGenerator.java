package org.dongq.analytics.utils;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.StringUtils;

public class UUIDGenerator {

	private static final int CACHE_SIZE = 20;
	
	private static Queue<String> uuidCache = new ConcurrentLinkedQueue<String>();
	
	/**
	 * 生成UUID，每次20个
	 * @return
	 */
	public static String getUUID() {
		if (uuidCache.isEmpty()) {
			generateUUID(CACHE_SIZE);
		}
		return uuidCache.poll();
	}

	/**
	 * 获得指定数目的UUID
	 * 
	 * @param number
	 *            int 需要获得的UUID数量
	 * @return String[] UUID数组
	 */
	private static void generateUUID(int number) {
		for (int i = 0; i < number; i++) {
			String uuid = generateUUID();
			uuidCache.add(uuid);
		}
	}
	
	/**
	 * 获得一个UUID
	 * 
	 * @return String UUID
	 */
	public static String generateUUID() {
		String s = UUID.randomUUID().toString();
		return StringUtils.remove(s, "-").toUpperCase();
	}


	public static void main(String[] args) {
		for (int i = 0; i < 50; i++) {
			String uuid = getUUID();
			System.out.println(uuid + ": " + uuid.length());
		}
	}
}
