/*
 * $Id:$
 * Copyright 2017 ecarpo.com All rights reserved.
 */
package org.bidtime;

import java.util.Set;

import org.JUnitTestBase;
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
public class RedisCacheHostsTest extends JUnitTestBase {

  @Override
  protected AbstractCache getRedis(ConnectionPools pools) throws Exception {
    JedisPoolConfig cfg = pools.getPoolConfig();
    Set<HostAndPort> servers = pools.getServers();
    AbstractCache redisCache = new RedisCacheHost(cfg, servers);
    return redisCache;
  }

  @Test
  public void test_set() throws Exception {
    String v = "054";
    super.set(v);
  }

  @Test
  public void test_get() throws Exception {
    super.get();
  }

}
