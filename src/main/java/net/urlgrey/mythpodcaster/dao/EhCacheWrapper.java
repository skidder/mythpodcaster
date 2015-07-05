/*
 * EhCacheWrapper.java
 * 
 * Created: 2010-12-08
 * 
 * Copyright (C) 2010 Scott Kidder
 * 
 * This file is part of MythPodcaster.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package net.urlgrey.mythpodcaster.dao;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * @author scottkidder
 * 
 */
public class EhCacheWrapper<K, V> implements CacheWrapper<K, V> {
	private final String cacheName;
	private final CacheManager cacheManager;

	public EhCacheWrapper(final String cacheName,
			final CacheManager cacheManager) {
		this.cacheName = cacheName;
		this.cacheManager = cacheManager;
	}

	public void put(final K key, final V value) {
		getCache().put(key, value);
	}

	@SuppressWarnings("unchecked")
	public V get(final K key) {
		return (V) getCache().get(key);
	}

	public Cache getCache() {
		return cacheManager.getCache(cacheName);
	}
}
