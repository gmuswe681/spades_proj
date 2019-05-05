package com.spades.spades;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class SpadesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpadesApplication.class, args);
    }

    @Configuration
    public class HttpsRedirectConf {
        private final static String SEC_USER_CONSTRAINT = "CONFIDENTIAL";
        private final static String REDIRECT_PATTERN = "/*";
        private final static String CONN_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";
        private final static String CONN_SCHEME = "http";


        @Bean
        public TomcatServletWebServerFactory servletContainer() {
            TomcatServletWebServerFactory tomcatServer =
                    new TomcatServletWebServerFactory() {

                        @Override
                        protected void postProcessContext(Context context) {
                            SecurityConstraint sc = new SecurityConstraint();
                            sc.setUserConstraint(SEC_USER_CONSTRAINT);
                            SecurityCollection collection = new SecurityCollection();
                            collection.addPattern(REDIRECT_PATTERN);
                            sc.addCollection(collection);
                            context.addConstraint(sc);
                        }
                    };
            tomcatServer.addAdditionalTomcatConnectors(createHttpConnector());
            return tomcatServer;
        }

        private Connector createHttpConnector() {
            Connector conn =
                    new Connector(CONN_PROTOCOL);
            conn.setScheme(CONN_SCHEME);
            conn.setSecure(false);
            conn.setPort(80);
            conn.setRedirectPort(443);
            return conn;
        }
    }
}
