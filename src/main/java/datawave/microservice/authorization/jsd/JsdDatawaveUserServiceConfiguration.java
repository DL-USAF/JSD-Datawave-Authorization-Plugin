package datawave.microservice.authorization.jsd;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import datawave.microservice.cached.CacheInspector;

/**
 * Configuration to supply beans for the {@link JsdDatawaveUserService}. This configuration is only active when the "jsd" profile is selected. This profile is
 * used for retrieving entity information from JSD cache
 */
@Configuration
@EnableCaching
@Profile("jsd")
public class JsdDatawaveUserServiceConfiguration {
    @Bean
    public JsdDatawaveUserService jsdDatawaveUserService(JsdDatawaveUserLookup jsdDatawaveUserLookup, CacheManager cacheManager,
                    @Qualifier("cacheInspectorFactory") Function<CacheManager,CacheInspector> cacheInspectorFactory) {
        return new JsdDatawaveUserService(jsdDatawaveUserLookup, cacheInspectorFactory.apply(cacheManager));
    }
    
    @Bean
    public JsdDatawaveUserLookup jsdDatawaveUserLookup(JsdDULProperties jsdDULProperties) {
        return new JsdDatawaveUserLookup(jsdDULProperties);
    }
    
    @Bean
    public JsdDULProperties jsdDULProperties() {
        return new JsdDULProperties();
    }
}
