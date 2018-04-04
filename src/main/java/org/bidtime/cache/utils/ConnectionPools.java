package org.bidtime.cache.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

public class ConnectionPools {

  private static final Logger log = LoggerFactory.getLogger(ConnectionPools.class);

  private JedisPoolConfig poolConfig;

  private Set<HostAndPort> servers;

  public ConnectionPools() {
  }

  private static ConnectionPools instance;

  static {   
    // 连接创建工厂（用户新建连接）
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    // 连接池配置
    CfgParam cfg = new ConnectionPools().new CfgParam();
    List<HostPort> list = getProps("redis.properties", cfg);
    // 最大连接数
    poolConfig.setMaxTotal(cfg.getMaxTotal());
    // 最大空闲数
    poolConfig.setMaxIdle(cfg.getMaxIdle());
    // 最大允许等待时间，如果超过这个时间还未获取到连接，则会报JedisException异常：
    // Could not get a resource from the pool
    poolConfig.setMaxWaitMillis(cfg.getMaxWait());
    poolConfig.setTestOnBorrow(cfg.getTestOnBorrow());
    poolConfig.setTestOnReturn(cfg.getTestOnReturn());
    // servers
    Set<HostAndPort> servers = new LinkedHashSet<HostAndPort>();
    for (HostPort p : list) {
      HostAndPort host = new HostAndPort(p.getServer(), p.getPort());
      servers.add(host);
    }
    //
    ConnectionPools pools = ConnectionPools.getInstance();
    pools.setPoolConfig(poolConfig);
    pools.setServers(servers);
  }

  private static final String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

  private static List<HostPort> getProps(String props, CfgParam cfg) {
    List<HostPort> list = null;
    Properties p = new Properties();
    try {
      InputStream in = ConnectionPools.class.getClassLoader().getResourceAsStream(props);
      p.load(in);
      String servers = p.getProperty("redis.hosts");
      list = toList(servers);
      //
      cfg.setMaxIdle(Integer.parseInt(p.getProperty("redis.maxIdle")));
      cfg.setMaxWait(Integer.parseInt(p.getProperty("redis.maxWait")));
      cfg.setMaxTotal(Integer.parseInt(p.getProperty("redis.maxTotal")));
      cfg.setTestOnBorrow(Boolean.parseBoolean(p.getProperty("redis.testOnBorrow")));
      cfg.setTestOnReturn(Boolean.parseBoolean(p.getProperty("redis.testOnReturn")));
      in.close();
    } catch (Exception e) {
      log.error("getProps: {}", e.getMessage());
    } finally {
      p = null;
    }
    return list;
  }

  class HostPort {

    private String server;

    private Integer port;

    public HostPort() {

    }

    public HostPort(String servers) {
      String[] srv_port = tokenizeToStringArray(servers, ":", true, true);
      server = srv_port[0];
      port = Integer.parseInt(srv_port[1]);
    }

    public String getServer() {
      return server;
    }

    public void setServer(String server) {
      this.server = server;
    }

    public Integer getPort() {
      return port;
    }

    public void setPort(Integer port) {
      this.port = port;
    }

  }

  class CfgParam {

    private Integer maxIdle;

    private Integer maxWait;

    private Integer maxTotal;

    private Boolean testOnReturn;

    private Boolean testOnBorrow;

    public CfgParam() {

    }

    public Integer getMaxIdle() {
      return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
      this.maxIdle = maxIdle;
    }

    public Integer getMaxWait() {
      return maxWait;
    }

    public void setMaxWait(Integer maxWait) {
      this.maxWait = maxWait;
    }

    public Boolean getTestOnBorrow() {
      return testOnBorrow;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
      this.testOnBorrow = testOnBorrow;
    }

    public Boolean getTestOnReturn() {
      return testOnReturn;
    }

    public void setTestOnReturn(Boolean testOnReturn) {
      this.testOnReturn = testOnReturn;
    }

    public Integer getMaxTotal() {
      return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
      this.maxTotal = maxTotal;
    }

  }

  public static List<HostPort> toList(String srv) throws Exception {
    List<HostPort> list = new ArrayList<HostPort>();
    String[] args = tokenizeToStringArray(srv, CONFIG_LOCATION_DELIMITERS, true, true);
    for (String s : args) {
      HostPort p = new ConnectionPools().new HostPort(s);
      list.add(p);
    }
    return list;
  }

  private static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
      boolean ignoreEmptyTokens) {

    if (str == null) {
      return null;
    }
    StringTokenizer st = new StringTokenizer(str, delimiters);
    List<String> tokens = new ArrayList<String>();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (trimTokens) {
        token = token.trim();
      }
      if (!ignoreEmptyTokens || token.length() > 0) {
        tokens.add(token);
      }
    }
    return toStringArray(tokens);
  }

  private static String[] toStringArray(Collection<String> collection) {
    if (collection == null) {
      return null;
    }
    return collection.toArray(new String[collection.size()]);
  }

  public static ConnectionPools getInstance() {
    if (instance == null) {
      synchronized (ConnectionPools.class) {
        if (instance == null) {
          instance = new ConnectionPools();
        }
      }
    }
    return instance;
  }

  public JedisPoolConfig getPoolConfig() {
    return poolConfig;
  }

  private void setPoolConfig(JedisPoolConfig poolConfig) {
    this.poolConfig = poolConfig;
  }

  public Set<HostAndPort> getServers() {
    return servers;
  }

  private void setServers(Set<HostAndPort> servers) {
    this.servers = servers;
  }

}
