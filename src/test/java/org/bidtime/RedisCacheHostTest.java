/*
 * $Id:$
 * Copyright 2017 ecarpo.com All rights reserved.
 */
package org.bidtime;

import java.util.Set;

import org.JUnitTestBase;
import org.bidtime.bean.A;
import org.bidtime.cache.AbstractCache;
import org.bidtime.cache.RedisCacheHost;
import org.bidtime.cache.utils.ConnectionPools;
import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author riverbo
 * @since 2017年11月21日
 */
public class RedisCacheHostTest extends JUnitTestBase {

  @Override
  protected AbstractCache getRedis(ConnectionPools pools) throws Exception {
    JedisPoolConfig cfg = pools.getPoolConfig();
    Set<HostAndPort> servers = pools.getServers();
    AbstractCache redisCache = new RedisCacheHost(cfg, servers);
    return redisCache;
  }

  @Test
  public void test_set() throws Exception {
    A a = super.newA();
    super.set(a);
  }

  @Test
  public void test_get() throws Exception {
    super.get();
  }

  @Test
  public void test_setString() throws Exception {
    String v = "中华人民共和国";
    super.setString(v);
  }

  @Test
  public void test_getString() throws Exception {
    super.getString();
  }

}
