package org.bidtime.cache;

import java.util.Set;

import org.bidtime.cache.utils.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

/**
 * jss
 */
public class RedisCacheHosts extends AbstractCache {
  
  private static final Logger log = LoggerFactory.getLogger(RedisCacheHosts.class);

  private JedisPoolConfig poolConfig;

  private Set<HostAndPort> servers;

  public RedisCacheHosts(JedisPoolConfig poolConfig, Set<HostAndPort> servers) {
    this.poolConfig = poolConfig;
    this.servers = servers;
  }

  private JedisCluster getClient() {
    JedisCluster cluster = null;
    try {
      cluster = new JedisCluster(servers, poolConfig);
    } catch (Exception e) {
      log.error("getClient: {}", e.getMessage());
    }
    return cluster;
  }

  @Override
  public void set(String key, int seconds, Object o) throws Exception {
    JedisCluster client = getClient();
    try {
      if (log.isDebugEnabled()) {
        String kk = client.setex(key.getBytes(), seconds, SerializeUtil.serialize(o));
        log.debug("set: {}", kk);
      } else {
        client.setex(key.getBytes(), seconds, SerializeUtil.serialize(o));
      }
    } finally {
      client.close();
    }
  }

  @Override
  public void delete(String key) throws Exception {
    JedisCluster client = getClient();
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
    JedisCluster client = getClient();
    try {
      client.del(key);
      client.setex(key.getBytes(), seconds, SerializeUtil.serialize(o));
    } finally {
      client.close();
    }
  }

  @Override
  public Object get(String key, boolean del) throws Exception {
    JedisCluster client = getClient();
    try {
      byte[] person = client.get(key.getBytes());
      return SerializeUtil.unserialize(person);
    } finally {
      if (del) {
        client.del(key);
      }
      client.close();
    }
  }
  
  @Override
  public String getString(String key, boolean del) throws Exception {
    String o = null;
    JedisCluster client = getClient();
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
    JedisCluster client = getClient();
    try {
      if (log.isDebugEnabled()) {
        String kk = client.setex(key.getBytes(), seconds, s.getBytes());
        log.debug("setString: {}", kk);
      } else {
        client.setex(key.getBytes(), seconds, s.getBytes());
      }
    } finally {
      client.close();
    }
  }
  
//  @Override
//  public String getString(String key) throws Exception {
//    String o = null;
//    JedisCluster client = getClient();
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
//    JedisCluster client = getClient();
//    try {
//      client.expire(key, seconds);
//      String kk = client.set(key, s);
//      //String kk = client.setex(key.getBytes(), seconds, s.getBytes());
//      System.out.println(kk);
//    } finally {
//      client.close();
//    }
//  }

}
