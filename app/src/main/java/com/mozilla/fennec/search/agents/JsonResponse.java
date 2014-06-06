package com.mozilla.fennec.search.agents;

import org.json.JSONObject;

public class JsonResponse {
  private Boolean success;
  private JSONObject response;
  private String message;

  private JsonResponse() {}
  public static JsonResponse success(JSONObject jsonobj) {
    JsonResponse json = new JsonResponse();
    json.response = jsonobj;
    json.success = true;
    return json;
  }

  public static JsonResponse failure(String errorMessage) {
    JsonResponse json = new JsonResponse();
    json.message = errorMessage;
    json.success = false;
    return json;
  }

  public Boolean success() {
    return success;
  }

  public JSONObject getResponse() {
    return response;
  }

  public String getErrorMessage() {
    return message;
  }
}
