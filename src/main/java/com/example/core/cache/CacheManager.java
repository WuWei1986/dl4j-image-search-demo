package com.example.core.cache;

import java.util.Collection;

import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;

/**
 * spring 注解缓存管理
 * 
 * @author wuwei
 *
 */
public class CacheManager extends AbstractTransactionSupportingCacheManager {

	private Collection<? extends Cache> caches;


	/**
	 * Specify the collection of Cache instances to use for this CacheManager.
	 */
	public void setCaches(Collection<? extends Cache> caches) {
		this.caches = caches;
	}

	@Override
	protected Collection<? extends Cache> loadCaches() {
		return this.caches;
	}

	
}
