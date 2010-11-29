package org.restlet.example.book.restlet.ch08.sec6.sub5;

import org.restlet.Server;
import org.restlet.data.Protocol;

public class CachingServer {
    public static void main(String[] args) throws Exception {
        // Instantiating the HTTP server and listening on port 8082
        new Server(Protocol.HTTP, 8082, TimeStampedServerResource.class)
                .start();
    }

}
