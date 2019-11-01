package com.neptunedreams.webapp;

import org.junit.Test;
import org.junit.Assert;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 11/1/19
 * <p>Time: 10:50 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"HardCodedStringLiteral", "HardcodedFileSeparator"})
public class WebCallTest {
  private static final String PATH = "/base/{alpha}/next/{bravo}";

  @Test(expected = IllegalArgumentException.class)
  public void testBadPathValueCall() {
    WebCall webCall = new WebCall(PATH);
    webCall.setPathValue("charlie", "c");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testRepeatedPathCall() {
    WebCall webCall = new WebCall(PATH);
    webCall.setPathValue("alpha", "a");
    webCall.setPathValue("alpha", "b");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testRepeatedQueryCall() {
    WebCall webCall = new WebCall(PATH);
    webCall.setQueryValue("alpha", "a");
    webCall.setQueryValue("alpha", "b");
  }
  
  @Test(expected = IllegalStateException.class)
  public void testIncompleteCall() {
    WebCall webCall = new WebCall(PATH);
    webCall.setPathValue("alpha", "a");
    webCall.getWebCommand();
  }
  
  
  @Test
  public void testGoodCall() {
    // 
    WebCall webCall = new WebCall(PATH);
    webCall.setPathValue("alpha", "a");
    webCall.setPathValue("bravo", "b b b");
    webCall.setQueryValue("charlie", "c");
    webCall.setQueryValue("delta", "d d d");

    String command = webCall.getWebCommand();
    Assert.assertEquals("/base/a/next/b%20b%20b?charlie=c?delta=d+d+d", command);
  }
}
