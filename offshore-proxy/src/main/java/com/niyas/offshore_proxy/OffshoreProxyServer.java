package com.niyas.offshore_proxy;

import java.io.*;
import java.net.*;

public class OffshoreProxyServer {
    private static final int PORT = 9090;

    public static void main(String[] args) {
        new OffshoreProxyServer().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Offshore Proxy is running on port " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    // Read request from Ship Proxy
                    String requestLine = in.readLine();
                    if (requestLine == null) continue;

                    String[] parts = requestLine.split(" ", 2);
                    if (parts.length < 2) {
                        out.println("400 Bad Request: Invalid request format");
                        continue;
                    }

                    String method = parts[0];
                    String urlStr = parts[1];

                    // Ensure the URL is valid
                    if (!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) {
                        out.println("400 Bad Request: Invalid URL format");
                        continue;
                    }

                    System.out.println("Processing request: " + method + " " + urlStr);

                    // Forward request to actual website
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(method);

                    // Read response
                    BufferedReader urlIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = urlIn.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                    urlIn.close();

                    // Send response back to Ship Proxy
                    out.println(response.toString());

                } catch (Exception e) {
                    System.err.println("Error processing request: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to start Offshore Proxy Server", e);
        }
    }
}
