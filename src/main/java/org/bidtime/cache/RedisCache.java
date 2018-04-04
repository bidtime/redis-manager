package org.bidtime.cache;

import org.bidtime.cache.utils.SerializeUtil;

import redis.clients.jedis.JedisCluster;

/**
 * 
 * jss
 */
public class RedisCache extends AbstractCache {
	
	public void set(String key, int seconds, Object o) throws Exception {
		JedisCluster client = ConnectionPools.getInstance().get();
		try {
			client.setex(key.getBytes(), seconds, SerializeUtil.serialize(o));
		} finally {
			client.close();
		}
	}
	
	public void delete(String key) throws Exception {
		JedisCluster client = ConnectionPools.getInstance().get();
		try {
			client.del(key);
		} finally {
			client.close();
		}
	}
	
	public void replace(String key, int seconds, Object o) throws Exception {
		JedisCluster client = ConnectionPools.getInstance().get();
		try {
			client.del(key);
			client.setex(key.getBytes(), seconds, SerializeUtil.serialize(o));
		} finally {
			client.close();
		}
	}
	
	public Object get(String key) throws Exception {
		JedisCluster client = ConnectionPools.getInstance().get();
		try {
			byte[] person = client.get(key).getBytes();  
			return (Object) SerializeUtil.unserialize(person);
		} finally {
			client.close();
		}
	}

}
