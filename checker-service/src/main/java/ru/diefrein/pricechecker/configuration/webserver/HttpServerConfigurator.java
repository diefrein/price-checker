package ru.diefrein.pricechecker.configuration.webserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.configuration.parameters.WebserverParameterProvider;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

public class HttpServerConfigurator {

    private static final Logger log = LoggerFactory.getLogger(HttpServerConfigurator.class);

    private final String host = WebserverParameterProvider.HOST;
    private final int port = WebserverParameterProvider.PORT;
    private final Map<String, HttpHandler> handlers;

    public HttpServerConfigurator(Map<String, HttpHandler> handlers) {
        this.handlers = handlers;
    }

    public HttpServer initHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getByName(host), port), 0);

        for (var entry : handlers.entrySet()) {
            server.createContext(entry.getKey(), entry.getValue());
        }

        server.start();

        log.info("HTTP Server started on host={} port={}", host, port);
        return server;
    }
}
