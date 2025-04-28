package com.niyas.ship_proxy.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping ("/")
public class ShipProxyController
{
	private static final String OFFSHORE_PROXY_HOST = "offshore-proxy"; // Must be a valid hostname or IP
	private static final int OFFSHORE_PROXY_PORT = 9090; // Offshore Proxy's port
	private final Lock lock = new ReentrantLock(); // Ensure sequential processing

	@RequestMapping (value = "/**", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<String> handleRequest(@RequestHeader HttpHeaders headers,
			@RequestBody (required = false) String body, HttpMethod method, HttpServletRequest request)
	{
		String host = headers.getFirst("Host");  // Get target hostname
		if (host == null)
		{
			return ResponseEntity.badRequest().body("Missing Host header");
		}
		// Construct the full URL
		String url = "http://" + host + request.getRequestURI();
		String queryString = request.getQueryString();
		if (queryString != null)
		{
			url += "?" + queryString;
		}

		lock.lock(); // Ensure requests are handled one by one
		try (Socket socket = new Socket())
		{
			socket.connect(new InetSocketAddress(OFFSHORE_PROXY_HOST, OFFSHORE_PROXY_PORT),
					5000); // Set connection timeout
			try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
			{
				// Send request to offshore proxy
				out.println(method.name() + " " + url);
				headers.forEach((key, values) -> values.forEach(value -> out.println(key + ": " + value)));
				out.println(); // End headers
				if (body != null)
				{
					out.println(body);
				}
				out.flush();

				// Read response from offshore proxy
				StringBuilder response = new StringBuilder();
				String line;
				while ((line = in.readLine()) != null)
				{
					response.append(line).append("\n");
				}
				return ResponseEntity.ok(response.toString());
			}
			catch (IOException e)
			{
				return ResponseEntity.internalServerError()
						.body("Error communicating with offshore proxy: " + e.getMessage());
			}
		}
		catch (IOException e)
		{
			return ResponseEntity.internalServerError().body("Failed to connect to offshore proxy: " + e.getMessage());
		}
		finally
		{
			lock.unlock();
		}
	}

}
