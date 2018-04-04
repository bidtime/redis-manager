package org.bidtime.cache;

import java.util.Set;

import org.bidtime.cache.utils.SerializeUtil;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * jss
 */
public class RedisCacheHost extends AbstractCache {
	
	protected JedisPool jedisPool;
	
	public RedisCacheHost() {
		
	}
    
    public RedisCacheHost(JedisPoolConfig poolConfig, HostAndPort server) {
    	this.jedisPool = new JedisPool(poolConfig, server.getHost(), server.getPort());
    }
    
    public RedisCacheHost(JedisPoolConfig poolConfig, Set<HostAndPort> servers) {
    	HostAndPort server = servers.iterator().next();
    	this.jedisPool = new JedisPool(poolConfig, server.getHost(), server.getPort());
    }
	
	public RedisCacheHost(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
	protected Jedis getClient() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
		} catch(Exception e) {
		}
		return jedis;
	}

	public void set(String key, int seconds, Object o) throws Exception {
		Jedis jedis = getClient();
		try {
			jedis.append(key.getBytes(), SerializeUtil.serialize(o));
		} finally {
			jedis.close();
		}
	}
	
	public void delete(String key) throws Exception {
		Jedis jedis = getClient();
		try {
			jedis.del(key);
		} finally {
			jedis.close();
		}
	}
	
	public void replace(String key, int seconds, Object o) throws Exception {
		Jedis jedis = getClient();
		try {
			jedis.del(key);
			jedis.setex(key.getBytes(), seconds, SerializeUtil.serialize(o));
		} finally {
			jedis.close();
		}
	}
	
	public Object get(String key) throws Exception {
		Jedis jedis = getClient();
		try {
			byte[] person = jedis.get(key).getBytes();  
			return SerializeUtil.unserialize(person);
		} finally {
			jedis.close();
		}
	}
	
	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

}
