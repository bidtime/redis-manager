package org.bidtime.cache;

import java.util.Set;

import org.bidtime.cache.utils.SerializeUtil;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * jss
 */
public class RedisCacheHosts extends AbstractCache {
	
    private JedisPoolConfig poolConfig;
    
    private Set<HostAndPort> servers;
    
    public RedisCacheHosts(JedisPoolConfig poolConfig, Set<HostAndPort> servers) {
    	this.poolConfig = poolConfig;
    	this.servers = servers;
    }

    @Override
	public void set(String key, int seconds, Object o) throws Exception {
		JedisCluster client = getClient();
		try {
			client.setex(key.getBytes(), seconds, SerializeUtil.serialize(o));
		} finally {
			client.close();
		}
	}
	
    @Override
	public void delete(String key) throws Exception {
		JedisCluster client = getClient();
		try {
			client.del(key);
		} finally {
			client.close();
		}
	}
	
    @Override
	public void replace(String key, int seconds, Object o) throws Exception {
		JedisCluster client = getClient();
		try {
			client.del(key);
			client.setex(key.getBytes(), seconds, SerializeUtil.serialize(o));
		} finally {
			client.close();
		}
	}
	
    @Override
	public Object get(String key) throws Exception {
		JedisCluster client = getClient();
		try {
			byte[] person = client.get(key).getBytes();  
			return (Object) SerializeUtil.unserialize(person);
		} finally {
			client.close();
		}
	}
    
	private JedisCluster getClient() {
		JedisCluster cluster = null;
		try {
			cluster = new JedisCluster(servers, poolConfig);
		} catch (Exception e) {
			
		}
		return cluster;
	}

}
