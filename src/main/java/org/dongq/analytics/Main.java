package org.dongq.analytics;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.math.stat.Frequency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

	final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		
		//testGetSequence();
		
		int count = 6;
		boolean letters = false;
		boolean numbers = true;
		
		int size = 10000;
		long[] a = new long[size];
		Set<Long> set = new HashSet<Long>();
		Frequency frequency = new Frequency();
		for(int index = 0; index < size; index++) {
			String randomString = RandomStringUtils.random(count, letters, numbers);
			a[index] = Long.valueOf(randomString);
			frequency.addValue(a[index]);
			set.add(a[index]);
		}
		
		System.out.println(size + " : " + set.size());
		
		for(Long key : set) {
			long _count = frequency.getCount(key);
			if(_count > 1) {
				System.out.println(key + " : " + _count);
			}
		}
		
		for(int index = 0; index < size; index++) {
			String randomString = RandomStringUtils.randomNumeric(count);
			System.out.println(index + ": " + randomString);
		}
	}

	private static void testGetSequence() {
		int[] arr = new int[10];
		arr = getSequence(10);
		for (int i = 0; i < 10; i++) {
			System.out.println(arr[i]);
		}
	}
	
	private static int[] getSequence(int no) {
		int[] sequence = new int[no];
		for (int i = 0; i < no; i++) {
			sequence[i] = i;
		}
		Random random = new Random();
		for (int i = 0; i < no; i++) {
			int p = random.nextInt(no);
			int tmp = sequence[i];
			sequence[i] = sequence[p];
			sequence[p] = tmp;
		}
		random = null;
		return sequence;
	}
}
