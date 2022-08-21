package com.barf.exchangeapi.exchanges;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ApiRequest {

  public static final String REQUEST_GET = "GET";
  public static final String REQUEST_POST = "POST";

  private URL url;

  private final Map<String, String> headers = new HashMap<>();
  private String postData;
  private String requestMethod = ApiRequest.REQUEST_POST;

  @SuppressWarnings("null")
  public String execute() throws IOException {

    HttpsURLConnection connection = null;
    try {
      connection = (HttpsURLConnection) this.url.openConnection();
      connection.setConnectTimeout(10000);
      connection.setReadTimeout(10000);
      connection.setRequestMethod(this.requestMethod);

      // add headers to request
      for (final String header : this.headers.keySet()) {
        connection.addRequestProperty(header, this.headers.get(header));
      }

      // write POST data to request
      if ((this.postData != null) && !this.postData.isEmpty()) {

        connection.setDoOutput(true);

        try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())) {
          out.write(this.postData);
        }
      }
      final InputStream body = connection.getResponseCode() < 400 ? connection.getInputStream() : connection.getErrorStream();

      // execute request and read response
      try (BufferedReader in = new BufferedReader(new InputStreamReader(body))) {

        final StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
          response.append(line);
        }

        return response.toString();
      }
    } finally {
      connection.disconnect();
    }
  }

  public void addHeader(final String key, final String value) {
    this.headers.put(key, value);
  }

  public String setURL(final String url) throws MalformedURLException {
    this.url = new URL(url);
    return this.url.getPath();
  }

  public void setPostData(final String data) {
    this.postData = data;
  }

  public void setRequestMethod(final String method) {
    this.requestMethod = method;
  }
}
