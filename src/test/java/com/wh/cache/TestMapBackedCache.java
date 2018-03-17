package com.wh.cache;

import java.util.HashMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.wh.cache.internal.CachedObject;
import com.wh.cache.internal.MapBackedCache;

public class TestMapBackedCache {

  // For the purpose of testing basic cache operations:
  // Thread-safe cache with default TTL (900s) and default cleanup interval (60s)
  private static final Cache<String, String> cacheA = new MapBackedCache<String, String>();

  // For the purpose of testing element expiration and cleanup:
  // Thread-safe cache with very short TTL (5s) and frequent cleanup interval (1s)
  private static final Cache<String, String> cacheB = new MapBackedCache<String, String>(5, 1);

  // For the purpose of testing concurrency and thread-safety:
  // Cache backed by a non-synchronized java.util.Map
  private static final Cache<String, String> cacheC =
      new MapBackedCache<String, String>(new HashMap<String, CachedObject<String>>());

  @Before
  public void setUp() {
    cacheA.clear();
    cacheB.clear();
    cacheC.clear();
    // Test clear()
    Assert.assertEquals(0, cacheA.size());
    Assert.assertEquals(0, cacheB.size());
    Assert.assertEquals(0, cacheC.size());
  }

  @Test(expected = NullPointerException.class)
  public void testNullKey() {
    cacheA.put(null, "Zero");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBlankKey() {
    cacheA.put("", "Zero");
  }

  @Test(expected = NullPointerException.class)
  public void testNullValue() {
    cacheA.put("0", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBlankValue() {
    cacheA.put("0", "");
  }

  @Test
  public void testOperations() {
    // Test put()
    cacheA.put("1", "One");
    Assert.assertEquals(1, cacheA.size());
    cacheA.put("2", "Two");
    cacheA.put("3", "Three");
    cacheA.put("4", "Four");
    Assert.assertEquals(4, cacheA.size());

    // Test containsKey()
    Assert.assertTrue(cacheA.containsKey("1"));
    Assert.assertTrue(cacheA.containsKey("2"));
    Assert.assertFalse(cacheA.containsKey("5"));
    Assert.assertFalse(cacheA.containsKey("6"));

    // Test get()
    Assert.assertEquals("One", cacheA.get("1"));
    Assert.assertEquals("Two", cacheA.get("2"));
    Assert.assertEquals("Three", cacheA.get("3"));
    Assert.assertEquals("Four", cacheA.get("4"));

    // Test remove()
    String value = cacheA.remove("2");
    Assert.assertEquals("Two", value);
    Assert.assertEquals(3, cacheA.size());
    value = cacheA.remove("3");
    Assert.assertEquals("Three", value);
    Assert.assertEquals(2, cacheA.size());
  }

  @Test
  public void testExpiration() {
    try {
      // Test cleanup()
      cacheB.put("1", "One");
      Thread.sleep(2000);
      cacheB.put("2", "Two");
      Thread.sleep(2000);
      cacheB.put("3", "Three");
      Thread.sleep(2000);
      cacheB.put("4", "Four");

      // "1" should be expired by now
      Assert.assertFalse(cacheB.containsKey("1"));
      Assert.assertTrue(cacheB.containsKey("2"));
      Assert.assertEquals(3, cacheB.size());

      Thread.sleep(2000);
      cacheB.put("5", "Five");

      // "2" should be expired by now
      Assert.assertFalse(cacheB.containsKey("2"));
      Assert.assertTrue(cacheB.containsKey("3"));
      Assert.assertEquals(3, cacheB.size());

      Thread.sleep(4000);

      // "3" and "4" should be expired by now
      Assert.assertFalse(cacheB.containsKey("3"));
      Assert.assertFalse(cacheB.containsKey("4"));
      Assert.assertEquals(0, cacheB.size());
    } catch (InterruptedException e) {
      Assert.fail(ExceptionUtils.getRootCauseMessage(e));
    }
  }

  @Test
  public void testConcurrency() {
    //
  }
}
