package datawave.microservice.authorization.postgres;

import static datawave.microservice.authorization.jsd.JsdDatawaveUserService.CACHE_NAME;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;

import datawave.microservice.cached.CacheInspector;
import datawave.security.authorization.CachedDatawaveUserService;
import datawave.security.authorization.DatawaveUser;
import datawave.security.authorization.DatawaveUserInfo;
import datawave.security.authorization.SubjectIssuerDNPair;

/** A version of the {@link CachedDatawaveUserService} that returns results from the JSD Cache */
@CacheConfig(cacheNames = CACHE_NAME)
public class PostgresDatawaveUserService implements CachedDatawaveUserService {
    public static final String CACHE_NAME = "datawaveUsers";
    
    private final PostgresDatawaveUserLookup jsdDatawaveUserLookup;
    private final CacheInspector cacheInspector;
    
    public PostgresDatawaveUserService(PostgresDatawaveUserLookup jsdDatawaveUserLookup, CacheInspector cacheInspector) {
        this.jsdDatawaveUserLookup = jsdDatawaveUserLookup;
        this.cacheInspector = cacheInspector;
    }
    
    @Override
    public Collection<DatawaveUser> lookup(Collection<SubjectIssuerDNPair> dns) {
    	if (dns == null) {
    		return Collections.emptyList();
    	}
        return dns.stream().map(jsdDatawaveUserLookup::lookupUser).collect(Collectors.toList());
    }
    
    @Override
    public Collection<DatawaveUser> reload(Collection<SubjectIssuerDNPair> dns) {
    	if (dns == null) {
    		return Collections.emptyList();
    	}
    	return dns.stream().map(jsdDatawaveUserLookup::reloadUser).collect(Collectors.toList());
    }
    
    @Override
    public DatawaveUser list(String name) {
    	return cacheInspector.list(CACHE_NAME, DatawaveUser.class, name.toLowerCase());
    }
    
    @Override
    public Collection<? extends DatawaveUserInfo> listAll() {
    	Collection<? extends DatawaveUser> users = cacheInspector.listAll(CACHE_NAME, DatawaveUser.class);
    	if (users == null) {
    		return Collections.emptyList();
    	}
    	return users.stream().map(DatawaveUserInfo::new).collect(Collectors.toList());
    }
    
    @Override
    public Collection<? extends DatawaveUserInfo> listMatching(String substring) {
    	Collection<? extends DatawaveUser> users = cacheInspector.listMatching(CACHE_NAME, DatawaveUser.class, substring.toLowerCase());
    	if (users == null) {
    		return Collections.emptyList();
    	}
        return users.stream().map(DatawaveUserInfo::new).collect(Collectors.toList());
    }
    
    @Override
    @CacheEvict
    public String evict(String name) {
        return "Evicted " + name;
    }
    
    @Override
    public String evictMatching(String substring) {
        int numEvicted = cacheInspector.evictMatching(CACHE_NAME, DatawaveUser.class, substring.toLowerCase());
        return "Evicted " + numEvicted + " entries from the cache.";
    }
    
    @Override
    @CacheEvict(allEntries = true)
    public String evictAll() {
        return "All entries evicted";
    }
}
