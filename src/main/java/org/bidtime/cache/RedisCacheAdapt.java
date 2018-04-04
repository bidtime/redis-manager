package org.bidtime.cache;

import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * jss
 */
public class RedisCacheAdapt extends AbstractCache {
    
    private AbstractCache cache;
    
    public RedisCacheAdapt(JedisPoolConfig poolConfig, Set<HostAndPort> servers) throws Exception {
    	if (servers.isEmpty()) {
    		throw new Exception("no redis hosts");
    	} else if (servers.size() == 1) {
    		cache = new RedisCacheHost(poolConfig, servers.iterator().next());
    	} else {
    		cache = new RedisCacheHosts(poolConfig, servers);
    	}
    }
    
    public RedisCacheAdapt(JedisPoolConfig poolConfig, HostAndPort server) throws Exception {
  		cache = new RedisCacheHost(poolConfig, server);
    }
    
    public RedisCacheAdapt(JedisPool jedisPool) throws Exception {
  		cache = new RedisCacheHost(jedisPool);
    }

    @Override
	public void set(String key, int seconds, Object o) throws Exception {
		cache.set(key, seconds, o);
	}
	
    @Override
	public void delete(String key) throws Exception {
		cache.delete(key);
	}
	
    @Override
	public void replace(String key, int seconds, Object o) throws Exception {
		cache.replace(key, seconds, o);
	}
	
    @Override
	public Object get(String key) throws Exception {
		return cache.get(key);
	}

}
