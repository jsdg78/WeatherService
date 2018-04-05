package com.wh.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wh.cache.internal.CachedObject;
import com.wh.cache.internal.MapBackedCache;

public class MapBackedCacheTest {

  private static final Logger LOG = LoggerFactory.getLogger(MapBackedCacheTest.class);

  // Thread-safe cache with default TTL (900s) and default cleanup interval (60s).
  private static final Cache<String, String> cache = new MapBackedCache<String, String>();

  private static final int THREAD_COUNT = 5;
  private static final int ITERATION_COUNT = 20;

  @Before
  public void setUp() {
    // Test clear().
    cache.clear();
    assertEquals(0, cache.size());
  }

  @Test(expected = NullPointerException.class)
  public void testNullKey() {
    cache.put(null, "Zero");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBlankKey() {
    cache.put("", "Zero");
  }

  @Test(expected = NullPointerException.class)
  public void testNullValue() {
    cache.put("0", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBlankValue() {
    cache.put("0", "");
  }

  @Test
  public void testOperations() {
    // Test put().
    cache.put("1", "One");
    assertEquals(1, cache.size());
    cache.put("2", "Two");
    cache.put("3", "Three");
    cache.put("4", "Four");
    assertEquals(4, cache.size());

    // Test containsKey().
    assertTrue(cache.containsKey("1"));
    assertTrue(cache.containsKey("2"));
    assertFalse(cache.containsKey("5"));
    assertFalse(cache.containsKey("6"));

    // Test get().
    assertEquals("One", cache.get("1"));
    assertEquals("Two", cache.get("2"));
    assertEquals("Three", cache.get("3"));
    assertEquals("Four", cache.get("4"));

    // Test remove().
    String value = cache.remove("2");
    assertEquals("Two", value);
    assertEquals(3, cache.size());
    value = cache.remove("3");
    assertEquals("Three", value);
    assertEquals(2, cache.size());
  }

  @Ignore
  @Test
  public void testExpiration() {
    try {
      // Cache with very short TTL (5s) and frequent cleanup interval (1s).
      Cache<String, String> cache = new MapBackedCache<String, String>(5, 1);

      // Test cleanup().
      cache.put("1", "One");
      cache.put("2", "Two");
      TimeUnit.SECONDS.sleep(7);
      cache.put("3", "Three");
      cache.put("4", "Four");

      // "1" and "2" should be expired by now.
      assertFalse(cache.containsKey("1"));
      assertFalse(cache.containsKey("2"));
      // "3" and "4" should still be in there.
      assertTrue(cache.containsKey("3"));
      assertTrue(cache.containsKey("4"));
      assertEquals(2, cache.size());
    } catch (InterruptedException e) {
      fail(ExceptionUtils.getRootCauseMessage(e));
    }
  }

  @Ignore
  @Test(timeout = 30000)
  // Fail if it runs more than 30 seconds.
  public void testThreadSafeCache() {
    long startTime = System.currentTimeMillis();
    for (int i = 1; i <= ITERATION_COUNT; i++) {
      // This should finish all iterations without getting stuck.
      stressTestCache(
          new MapBackedCache<String, Integer>(new ConcurrentHashMap<String, CachedObject<Integer>>(2)),
          THREAD_COUNT, i);
    }
    long endTime = System.currentTimeMillis();
    float elapsedTime = (float) (endTime - startTime) / 1000;
    LOG.info("***** Total elapsed time: {} seconds", elapsedTime);
  }

  @Ignore
  @Test(timeout = 30000)
  // Fail if it runs more than 30 seconds.
  public void testNonThreadSafeCache() {
    long startTime = System.currentTimeMillis();
    for (int i = 1; i <= ITERATION_COUNT; i++) {
      // This could potentially cause an infinite loop at some point before the final iteration
      // causing the CPU utilization to spike drastically. This will happen when two threads both
      // try to put the Nth key-value pair in the map whose current load limit is N-1. Putting the
      // Nth entry would trigger the rehashing of the map. Both threads know this and would try to
      // call the internal transfer() method at the same time. This method transfers entries from
      // old buckets to new buckets during the rehashing process.
      stressTestCache(
          new MapBackedCache<String, Integer>(new HashMap<String, CachedObject<Integer>>(2)),
          THREAD_COUNT, i);
    }
    long endTime = System.currentTimeMillis();
    float elapsedTime = (float) (endTime - startTime) / 1000;
    LOG.info("***** Total elapsed time: {} seconds", elapsedTime);
  }

  public void stressTestCache(Cache<String, Integer> cache, int threadCount, int iteration) {
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    for (int j = 0; j < threadCount; j++) {
      // Create a new worker thread
      // and queue it for execution.
      executor.execute(new Runnable() {
        @Override
        public void run() {
          // Turn off cache logger so as not to clutter up the log.
          Logger logger = LoggerFactory.getLogger(MapBackedCache.class);
          if (logger instanceof ch.qos.logback.classic.Logger) {
            ((ch.qos.logback.classic.Logger) logger).setLevel(ch.qos.logback.classic.Level.OFF);
          }

          // Access the cache 100K times.
          for (int i = 0; i < 100000; i++) {
            // Simulate read and write operations.
            int randomKey = (int) (Math.random() * 100000);
            int randomValue = (int) (Math.random() * 100000);
            cache.get(String.valueOf(randomKey));
            cache.put(String.valueOf(randomValue), randomValue);
          }
        }
      });
    }

    long startTime = System.currentTimeMillis();
    // Execute all tasks in the queue
    // but do not accept any more tasks.
    executor.shutdown();
    while (!executor.isTerminated()) {
      // Wait until all threads are finished.
    }
    long endTime = System.currentTimeMillis();
    float elapsedTime = (float) (endTime - startTime) / 1000;
    LOG.info("[{}] All threads completed in {} seconds", iteration, elapsedTime);
  }
}
