package com.example.core.ignite;

import java.io.Closeable;
import java.io.IOException;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author wuwei
 */

public class IgniteCacheManager implements InitializingBean, Closeable {

	private IgniteConfiguration configuration;

	private Ignite ignite;

	private IgniteCache<String, String> productCache;
	private IgniteCache<String, String> lock;

	@Override
	public void afterPropertiesSet() throws Exception {
		ignite = Ignition.start(configuration);

		CacheConfiguration<String, String> basicCacheConfiguration = new CacheConfiguration<>();
		basicCacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
		basicCacheConfiguration.setBackups(1);
		
		CacheConfiguration<String, String> basicCacheConfiguration2 = new CacheConfiguration<>();
		basicCacheConfiguration2.setCacheMode(CacheMode.PARTITIONED);
		basicCacheConfiguration2.setBackups(1);
		basicCacheConfiguration2.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
		
		String cacheName = configuration.getIgniteInstanceName();
		CacheConfiguration<String, String> cacheConfiguration = new CacheConfiguration<>(basicCacheConfiguration);
		cacheConfiguration.setName(cacheName);
		productCache = ignite.getOrCreateCache(cacheConfiguration);
		
		CacheConfiguration<String, String> cacheConfiguration2 = new CacheConfiguration<>(basicCacheConfiguration2);
		cacheConfiguration2.setName(cacheName+"Lock");
		lock = ignite.getOrCreateCache(cacheConfiguration2);
	}

	public IgniteConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(IgniteConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void close() throws IOException {
		if (ignite != null) {
			ignite.close();
		}
	}

	public IgniteCache<String, String> getProductCache() {
		return productCache;
	}

	public IgniteCache<String, String> getLock() {
		return lock;
	}
	
}
