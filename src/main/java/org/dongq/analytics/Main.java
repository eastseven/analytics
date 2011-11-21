package org.dongq.analytics;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

	final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
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
