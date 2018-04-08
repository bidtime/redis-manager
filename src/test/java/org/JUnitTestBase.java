/*
 * $Id:$
 * Copyright 2017 ecarpo.com All rights reserved.
 */
package org;

import org.bidtime.bean.A;
import org.bidtime.cache.AbstractCache;
import org.bidtime.cache.utils.ConnectionPools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author Jades.He
 * @since 2017.05.26
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath*:/META-INF/spring/spring-*.xml")
public class JUnitTestBase {

	//public class JUnitTestBase extends AbstractJUnit4SpringContextTests {
  
  protected static final String KEY1 = "key1";
  protected static final String KEY2 = "key2";
  
  protected AbstractCache getRedis() throws Exception {
    ConnectionPools pools = ConnectionPools.getInstance();
    return getRedis(pools);
  }
  
  protected AbstractCache getRedis(ConnectionPools pools) throws Exception {
    return null;
  }

  public int getId(int nextInt) {
    java.util.Random random = new java.util.Random();// 定义随机类
    int result = random.nextInt(nextInt);// 返回[0,10)集合中的整数，注意不包括10
    return result + 1; // +1后，[0,10)集合变为[1,11)集合，满足要求
  }
  
  public void setString(String v) throws Exception {
    AbstractCache cache = this.getRedis();
    cache.setString(KEY1, 200000, v);
    print(v);
  }
  
  public void getString() throws Exception {
    AbstractCache cache = this.getRedis();
    String v = cache.getString(KEY1, false);
    print(v);
  }
  
  public void set(Object o) throws Exception {
    AbstractCache cache = this.getRedis();
    cache.set(KEY2, 200000, o);
    print(o);
  }
  
  public void get() throws Exception {
    AbstractCache cache = this.getRedis();
    Object o = cache.get(KEY2, false);
    print(o);
  }
  
  protected A newA() {
    A a = new A();
    a.setId(1);
    a.setName("a1");
    a.setSalary(10.0);
    return a;
  }

  /**
   * ＊ QuoteFieldNames———-输出key时是否使用双引号,默认为true ＊
   * WriteMapNullValue——–是否输出值为null的字段,默认为false ＊
   * WriteNullNumberAsZero—-数值字段如果为null,输出为0,而非null ＊
   * WriteNullListAsEmpty—–List字段如果为null,输出为[],而非null ＊
   * WriteNullStringAsEmpty—字符类型字段如果为null,输出为”“,而非null ＊
   * WriteNullBooleanAsFalse–Boolean字段如果为null,输出为false,而非null
   */
  public static void print(Object o) throws Exception {
    System.out.println("");
    System.out.println("");
    System.out.println("####################################");
    if (o != null) {
      String json = JSON.toJSONString(o, SerializerFeature.WriteMapNullValue,
          SerializerFeature.WriteNullListAsEmpty);
      System.out.println("data: ");
      System.out.println("-----------------------");
      System.out.println(json);
    } else {
      System.out.println("data: is null");
    }
    System.out.println("####################################");
    System.out.println("");
    System.out.println("");
  }

}
