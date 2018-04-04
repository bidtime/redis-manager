package org.bidtime.cache;

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
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public class ConnectionPools {

	private static final Logger log = LoggerFactory.getLogger(ConnectionPools.class);
	
    private static JedisPoolConfig poolConfig;
    
    private static Set<HostAndPort> servers;
    
    //private GenericKeyedObjectPoolConfig conf;

	public ConnectionPools() {
	}

	private static ConnectionPools instance;
	
	static {
		// 连接创建工厂（用户新建连接）
		poolConfig = new JedisPoolConfig();
		// 连接池配置
	    // 最大连接数
	    poolConfig.setMaxTotal(10);
	    // 最大空闲数
	    poolConfig.setMaxIdle(1);
	    // 最大允许等待时间，如果超过这个时间还未获取到连接，则会报JedisException异常：
	    // Could not get a resource from the pool
	    poolConfig.setMaxWaitMillis(1000);
	    //
		List<CfgPar> list = getProps("redis_client.conf");
		servers = new LinkedHashSet<HostAndPort>();
	    for (CfgPar p : list) {
	    	HostAndPort host = new HostAndPort(p.getServer(), p.getPort());
	    	servers.add(host);
		}
	}

    private static final String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

	private static List<CfgPar> getProps(String props) {
		List<CfgPar> list = null;
		Properties p = new Properties();
		try {
			InputStream in = ConnectionPools.class.getClassLoader().getResourceAsStream(
                    props);
            p.load(in);
            String servers = p.getProperty("server");
            list = toList(servers);
            in.close();
		} catch (Exception e) {
			log.error("getProps: {}", e.getMessage());
		} finally {
			p = null;
		}
		return list;
	}
	
	class CfgPar {
		
		private String server;

		private Integer port;
		
		public CfgPar() {
			
		}
		
		public CfgPar(String servers) {
			String[] srv_port = tokenizeToStringArray(servers,
    				":", true, true);
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
	
	public static List<CfgPar> toList(String srv) throws Exception {
		List<CfgPar> list = new ArrayList<CfgPar>();
		String[] args = tokenizeToStringArray(srv,
				CONFIG_LOCATION_DELIMITERS, true, true);
		for (String s : args) {
			CfgPar p = new ConnectionPools().new CfgPar(s);
			list.add(p);
		}
		return list;
	}
	
    private static String[] tokenizeToStringArray(
            String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

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
    
    public JedisCluster get() {
	    JedisCluster cluster = new JedisCluster(servers, poolConfig);
	    //String name = cluster.get("name");
	    //System.out.println(name);
	    return cluster;
    }
    
//	public JedisPoolConfig get() {
//		// 连接池
//		JedisPoolConfig connectionPool = new ConnectionPool(pooledConnectionFactory, conf);
//		return connectionPool;
//	}
	
//	public void setCfgPar(CfgPar par) {
//		this.par = par;		
//	}

}
