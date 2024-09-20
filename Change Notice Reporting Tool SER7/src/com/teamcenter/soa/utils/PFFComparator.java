package com.teamcenter.soa.utils;

import java.util.Comparator;

public class PFFComparator implements Comparator<String> {
	private final String dot = "\\.";

	@Override
	public int compare(String pff1, String pff2) {
		int dotsInPff1 = pff1.split(dot, -1).length - 1;
		int dotsInPff2 = pff2.split(dot, -1).length - 1;
		
		if (dotsInPff1 != dotsInPff2) {
			return dotsInPff1 - dotsInPff2;
		}
		return pff1.compareTo(pff2);
	}
}