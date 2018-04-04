package org.bidtime.cache;

import java.util.Set;

import org.bidtime.cache.utils.SerializeUtil;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
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
    Jedis client = null;
    try {
      client = jedisPool.getResource();
    } catch (Exception e) {}
    return client;
  }

  public void set(String key, int seconds, Object o) throws Exception {
    Jedis client = getClient();
    try {
      String kk = client.setex(key.getBytes(), seconds, SerializeUtil.serialize(o));
      //String kk = client.set(key.getBytes(), SerializeUtil.serialize(o));
      System.out.println(kk);
    } finally {
      client.close();
    }
  }

  public void delete(String key) throws Exception {
    Jedis client = getClient();
    try {
      client.del(key);
    } finally {
      client.close();
    }
  }

  public void replace(String key, int seconds, Object o) throws Exception {
    Jedis client = getClient();
    try {
      client.del(key);
      client.setex(key.getBytes(), seconds, SerializeUtil.serialize(o));
    } finally {
      client.close();
    }
  }

  public Object get(String key) throws Exception {
    Object o = null;
    Jedis client = getClient();
    try {
      byte[] bytes = client.get(key.getBytes());
      System.out.println(bytes);
      o = SerializeUtil.unserialize(bytes);
    } finally {
      client.close();
    }
    return o;
  }
  
  public String getString(String key) throws Exception {
    String o = null;
    Jedis client = getClient();
    try {
      o = client.get(key);
    } finally {
      client.close();
    }
    return o;
  }
  
  public void setString(String key, int seconds, String s) throws Exception {
    Jedis client = getClient();
    try {
      client.expire(key, seconds);
      String kk = client.set(key, s);
      //String kk = client.setex(key.getBytes(), seconds, s.getBytes());
      System.out.println(kk);
    } finally {
      client.close();
    }
  }

  public JedisPool getJedisPool() {
    return jedisPool;
  }

  public void setJedisPool(JedisPool jedisPool) {
    this.jedisPool = jedisPool;
  }

}
