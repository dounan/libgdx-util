package com.dounanshi.libgdx.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.utils.Json;
import com.dounanshi.libgdx.util.MovingAverage;

public class MovingAverageTest {

  private static final int SIZE = 5;
  private static final double ERR = 0.0001;

  private MovingAverage movingAverage;

  @Before
  public void setUp() {
    movingAverage = new MovingAverage(SIZE);
  }

  @Test
  public void testBasicAverage() {
    testSimpleAverage();
  }

  @Test
  public void testReset() {
    testSimpleAverage();
    movingAverage.reset();
    testSimpleAverage();
  }
  
  @Test
  public void testReadWriteJson() {
    assertEquals(0, movingAverage.avg, ERR);
    movingAverage.sample(4);
    assertEquals(4, movingAverage.avg, ERR);
    movingAverage.sample(5);
    assertEquals(4.5, movingAverage.avg, ERR);
    movingAverage.sample(6);
    assertEquals(5, movingAverage.avg, ERR);
    
    Json json = new Json();
    String jsonStr = json.toJson(movingAverage);
    MovingAverage newMovingAverage = json.fromJson(MovingAverage.class, jsonStr);
    
    newMovingAverage.sample(11);
    assertEquals(6.5, newMovingAverage.avg, ERR);
    newMovingAverage.sample(17);
    assertEquals(8.6, newMovingAverage.avg, ERR);
    newMovingAverage.sample(2);
    assertEquals(8.2, newMovingAverage.avg, ERR);
    newMovingAverage.sample(23);
    assertEquals(11.8, newMovingAverage.avg, ERR);
  }

  private void testSimpleAverage() {
    assertEquals(0, movingAverage.avg, ERR);
    movingAverage.sample(4);
    assertEquals(4, movingAverage.avg, ERR);
    movingAverage.sample(5);
    assertEquals(4.5, movingAverage.avg, ERR);
    movingAverage.sample(6);
    assertEquals(5, movingAverage.avg, ERR);
    movingAverage.sample(11);
    assertEquals(6.5, movingAverage.avg, ERR);
    movingAverage.sample(17);
    assertEquals(8.6, movingAverage.avg, ERR);
    movingAverage.sample(2);
    assertEquals(8.2, movingAverage.avg, ERR);
    movingAverage.sample(23);
    assertEquals(11.8, movingAverage.avg, ERR);
  }
}
