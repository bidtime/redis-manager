package org.bidtime.cache;

import java.util.Set;

import org.bidtime.cache.utils.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * jss
 */
public class RedisCacheHost extends AbstractCache {
  
  private static final Logger log = LoggerFactory.getLogger(RedisCacheHost.class);
  
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
    } catch (Exception e) {
      log.error("getClient: {}", e.getMessage());
    }
    return client;
  }

  @Override
  public void set(String key, int seconds, Object o) throws Exception {
    Jedis client = getClient();
    try {
      if (log.isDebugEnabled()) {
        String kk = client.setex(key.getBytes(), seconds, SerializeUtil.serialize(o));
        log.debug("set: {}", kk);
      } else {
      }
    } finally {
      client.close();
    }
  }

  @Override
  public void delete(String key) throws Exception {
    Jedis client = getClient();
    try {
      if (log.isDebugEnabled()) {
        Long l = client.del(key);
        log.debug("del: {}", l);
      } else {
        client.del(key);
      }
    } finally {
      client.close();
    }
  }

  @Override
  public void replace(String key, int seconds, Object o) throws Exception {
    Jedis client = getClient();
    try {
      client.del(key);
      client.setex(key.getBytes(), seconds, SerializeUtil.serialize(o));
    } finally {
      client.close();
    }
  }

  @Override
  public Object get(String key, boolean del) throws Exception {
    Object o = null;
    Jedis client = getClient();
    try {
      byte[] bytes = client.get(key.getBytes());
      o = SerializeUtil.unserialize(bytes);
    } finally {
      if (del) {
        client.del(key);
      }
      client.close();
    }
    return o;
  }
  
  @Override
  public String getString(String key, boolean del) throws Exception {
    String o = null;
    Jedis client = getClient();
    try {
      byte[] bytes = client.get(key.getBytes());
      o = new String(bytes);
    } finally {
      if (del) {
        client.del(key);
      }
      client.close();
    }
    return o;
  }
  
  @Override
  public void setString(String key, int seconds, String s) throws Exception {
    Jedis client = getClient();
    try {
      if (log.isDebugEnabled()) {
        String kk = client.setex(key.getBytes(), seconds, s.getBytes());
        log.debug("set: {}", kk);
      } else {
        client.setex(key.getBytes(), seconds, s.getBytes());
      }
    } finally {
      client.close();
    }
  }

//  @Override
//  public String getString(String key, boolean del) throws Exception {
//    String o = null;
//    Jedis client = getClient();
//    try {
//      o = client.get(key);
//    } finally {
//      client.close();
//    }
//    return o;
//  }
//  
//  @Override
//  public void setString(String key, int seconds, String s) throws Exception {
//    Jedis client = getClient();
//    try {
//      client.expire(key, seconds);
//      String kk = null;
//      if (log.isDebugEnabled()) {
//        kk = client.set(key, s);
//        log.debug("setString: {}", kk);
//      } else {
//        client.set(key, s);
//      }
//    } finally {
//      client.close();
//    }
//  }

  public JedisPool getJedisPool() {
    return jedisPool;
  }

  public void setJedisPool(JedisPool jedisPool) {
    this.jedisPool = jedisPool;
  }

}
