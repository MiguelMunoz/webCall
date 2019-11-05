package com.neptunedreams.webapp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Make a call to a web service. This encapsulates the task of replacing path parameters and adding query parameters.
 * To use, instantiate WebCall with the path. Then add path parameters and query parameters. You may then call
 * {@code getWebCommand()} to get the full path to execute.
 * 
 * <pre>
 *     WebCall webCall = new WebCall("/base/{alpha}/next/{bravo}");
 *     webCall.setPathValue("alpha", "a");
 *     webCall.setPathValue("bravo", "b");
 *     webCall.setQueryValue("charlie", "c");
 *     webCall.setQueryValue("delta", "d");
 *     String command webCall.getWebCommand();
 * </pre>
 * 
 * At the end of this block, the {@code command} string should be {@code "/base/a/next/b?charlie=c?delta=d"}. Query
 * parameters will show up in the order which they are added.
 * Failure to declare all path parameters will throw an exception. Duplicating a path or query parameter will also
 * throw an exception.
 * 
 * Somebody may have already created a file to do this, but I haven't found it yet.
 * 
 * I need to add form data support, but I don't need that yet.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 11/1/19
 * <p>Time: 9:04 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings("WeakerAccess")
public class WebCall {
  private final String path;
  private final Map<String, String> pathValues = new HashMap<>();
  private final Map<String, String> queryValues = new LinkedHashMap<>();
  public WebCall(String path) {
    this.path = path;
  }
  
  public void setPathValue(String pathElement, String value) {
    String wrappedElement = wrapElement(pathElement);
    if(!path.contains(wrappedElement)) {
      throw new IllegalArgumentException(String.format("No element %s in path %s", wrappedElement, path));
    }
    if (pathValues.containsKey(pathElement)) {
      throw new IllegalArgumentException(
          String.format("Duplicate path argument: %s:%s can't overwrite %s:%s",
              pathElement, value, pathElement, pathValues.get(pathElement)
          )
      );
    }
    pathValues.put(pathElement, pathEncode(value));
  }

  private String wrapElement(final String pathElement) {
    return String.format("{%s}", pathElement); // NON-NLS
  }

  public void setQueryValue(String queryElement, String value) {
    if (queryValues.containsKey(queryElement)) {
      throw new IllegalArgumentException(
          String.format("Duplicate query argument: %s:%s can't overwrite %s:%s",
              queryElement, value, queryElement, queryValues.get(queryElement)
          )
      );
    }
    queryValues.put(queryElement, queryEncode(value));
  }

  @SuppressWarnings("MagicCharacter")
  public String getWebCommand() {
    String callPath = path;
    for (String pathValue: pathValues.keySet()) {
      String element = wrapElement(pathValue);
      callPath = callPath.replace(element, pathValues.get(pathValue));
    }
    if (callPath.contains("{") || callPath.contains("}")) {
      throw new IllegalStateException(String.format("Incomplete path substitutions. Resulting path: %s", callPath));
    }
    StringBuilder builder = new StringBuilder(callPath);
    char divider = '?';
    for (String queryKey: queryValues.keySet()) {
      builder
          .append(divider)
          .append(queryKey)
          .append('=')
          .append(queryValues.get(queryKey));
      divider = '&';
    }
    return builder.toString();
  }
  
  private static String queryEncode(String value) {
    try {
      return URLEncoder.encode(value, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new AssertionError("Should not happen", e);
    }
  }
  
  private static String pathEncode(String value) {
    // This is safe for the + character. If there was a + in the original value, it will have been encoded as %2B, so 
    // all + characters in the result of queryEncode() were originally spaces.
    return queryEncode(value).replaceAll("\\+", "%20");
  }
}
