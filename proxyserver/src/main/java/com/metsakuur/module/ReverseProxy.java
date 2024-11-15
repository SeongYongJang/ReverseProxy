// Servlet 을 확장하여 ReserverProxy 클래스를 만들어서 요청을 처리한다.
// 요청에 대한 모든 정보를 가져와서 다시 전달한다.
// 요청에 대한 결과를 모두 담아서 다시 전달한다.

package com.metsakuur.module;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReverseProxy extends HttpServlet {
    private static final long serialVersionUID = 1L;

    String url = null;

    // String url = "https://apple.com";
    @Override
    public void init() {
        String url = getInitParameter("targetUri");
        if (url != null) {
            this.url = url;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {

            if (url == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "url parameter is required");
                return;
            }
            // print original request url
            System.out.println("Request URL: " + request.getRequestURL().toString());
            // print origial request full url with parameters
            System.out.println("Request Full URL: " + request.getRequestURL().toString() + "?"
                    + request.getQueryString());

            String full_original_request_url = request.getRequestURL().toString() + "?" + request.getQueryString();

            // print proxy request url
            // make proxy url with original request url
            // remove address and port number from original request url by regular
            // expression
            String proxyUrl = this.url + full_original_request_url.replaceAll("http://[^/]+", "");

            System.out.println("Proxy URL: " + proxyUrl);

            HttpURLConnection connection = (HttpURLConnection) new URL(proxyUrl).openConnection();
            connection.setRequestMethod("GET");

            @SuppressWarnings("unchecked")
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                connection.setRequestProperty(headerName, request.getHeader(headerName));
            }

            connection.connect();

            response.setStatus(connection.getResponseCode());
            for (Entry<String, java.util.List<String>> header : connection.getHeaderFields().entrySet()) {
                if (header.getKey() != null) {
                    for (String value : header.getValue()) {
                        response.addHeader(header.getKey(), value);
                    }
                }
            }

            InputStream inputStream = connection.getInputStream();
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[4096 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {

            if (url == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "url parameter is required");
                return;
            }
            // print original request url
            System.out.println("Request URL: " + request.getRequestURL().toString());
            // print proxy request url
            // make proxy url with original request url
            // remove address and port number from original request url by regular
            // expression
            String proxyUrl = this.url + request.getRequestURL().toString().replaceAll("http://[^/]+", "");

            System.out.println("Proxy URL: " + proxyUrl);

            HttpURLConnection connection = (HttpURLConnection) new URL(proxyUrl).openConnection();
            connection.setRequestMethod("POST");

            @SuppressWarnings("unchecked")
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                connection.setRequestProperty(headerName, request.getHeader(headerName));
            }

            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            InputStream inputStream = request.getInputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();

            connection.connect();

            response.setStatus(connection.getResponseCode());
            for (Entry<String, java.util.List<String>> header : connection.getHeaderFields().entrySet()) {
                if (header.getKey() != null) {
                    for (String value : header.getValue()) {
                        response.addHeader(header.getKey(), value);
                    }
                }
            }

            InputStream connectionInputStream = connection.getInputStream();
            OutputStream responseOutputStream = response.getOutputStream();
            buffer = new byte[4096 * 1024];
            while ((bytesRead = connectionInputStream.read(buffer)) != -1) {
                responseOutputStream.write(buffer, 0, bytesRead);
            }
            connectionInputStream.close();
            responseOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
