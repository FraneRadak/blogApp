package com.radak.utils;


import java.util.Optional;

import lombok.Data;


public class StateReminder {
	private static int sort=1;
	private static int filter=0;
	
	public static int getSort() {
		return sort;
	}
	public static void setSort(int sort) {
		StateReminder.sort = sort;
	}
	public static int getFilter() {
		return filter;
	}
	public static void setFilter(int filter) {
		StateReminder.filter = filter;
	}
	public static void setState(Optional<String> sortId,Optional<String> filterId) {
		if (sortId.isPresent()) {
			sort=(Integer.parseInt(sortId.get()));
		}
		if (filterId.isPresent()) {
			filter=(Integer.parseInt(filterId.get()));
		}
	}
	
}
