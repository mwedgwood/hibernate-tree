package com.github.mwedgwood.service;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.ehcache.EhCacheMessageLogger;
import org.hibernate.cache.ehcache.EhCacheRegionFactory;
import org.hibernate.cache.internal.StandardQueryCache;
import org.hibernate.cache.spi.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class TestRegionFactory extends EhCacheRegionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRegionFactory.class);
    private static final long MIN_CACHE_SIZE = 1048576 * 256;

    @Override
    public void start(Settings settings, Properties properties) throws CacheException {
        super.settings = settings;
        if (manager != null) {
            org.jboss.logging.Logger.getMessageLogger(EhCacheMessageLogger.class, EhCacheRegionFactory.class.getName()).attemptToRestartAlreadyStartedEhCacheProvider();
            return;
        }

        Configuration configuration = new Configuration()
                .name("test-tree")
                .updateCheck(true)
                .monitoring(Configuration.Monitoring.AUTODETECT);

        configuration.setMaxBytesLocalHeap("1G");

        if (configuration.getMaxBytesLocalHeap() < MIN_CACHE_SIZE) {
            throw new CacheException("Specified cache size is below minimum size ( " + MIN_CACHE_SIZE + " bytes) necessary to run the app. Please increase the maxCacheSize config to the appropriate amount");
        }

        configuration.addSizeOfPolicy(new SizeOfPolicyConfiguration()
                .maxDepth(10000)
                .maxDepthExceededBehavior("abort"));

        addCaches(configuration);

        try {
            manager = new CacheManager(configuration);
            LOGGER.info("Starting cache manager with config {}", configuration);
        } catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e);
        }
    }

    private void addCaches(Configuration configuration) {
        configuration.defaultCache(new CacheConfiguration().eternal(false).overflowToOffHeap(false))
                .cache(createBaseCacheConfig("tree-cache", "60%"))
                .cache(createBaseCacheConfig(StandardQueryCache.class.getName(), "20%"))
                .cache(createBaseCacheConfig(UpdateTimestampsCache.class.getName(), "10%"));
    }

    private CacheConfiguration createBaseCacheConfig(String cacheName, String percentHeap) {
        CacheConfiguration baseConfig = new CacheConfiguration();
        baseConfig.setName(cacheName);
        baseConfig.setMaxBytesLocalHeap(percentHeap);
        return baseConfig;
    }

}

