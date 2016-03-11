package com.aptoide.amethyst.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by neuro on 22-02-2016.
 */
public class ReferrersMap extends ConcurrentHashMap<String, List<Long>> {

	public void add(String packageName, long networkId) {
		if (!containsKey(packageName)) {
			put(packageName, new LinkedList<Long>());
		}

		get(packageName).add(networkId);
	}
}
