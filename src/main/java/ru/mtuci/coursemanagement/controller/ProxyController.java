package ru.mtuci.coursemanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.net.InetAddress;
import java.net.URI;

@RestController
public class ProxyController {
    @GetMapping("/api/proxy")
    public String proxy(@RequestParam("targetUrl") String targetUrl) {
        URI uri;
        try {
            uri = new URI(targetUrl);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL");
        }
        String host = uri.getHost();
        if (host == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL");
        }
        try {
            InetAddress addr = InetAddress.getByName(host);
            if (addr.isLoopbackAddress() || addr.isSiteLocalAddress() || addr.isLinkLocalAddress()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Internal addresses are not allowed");
            }
        } catch (java.net.UnknownHostException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown host");
        }
        RestTemplate rt = new RestTemplate();
        return rt.getForObject(targetUrl, String.class);
    }
}
