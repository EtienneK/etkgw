package com.etiennek.gw.msg.mail;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.SendGrid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.sendgrid.SendGridProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

@Service
public class SendGridEmailService implements EmailService {
    private WebClient webClient;

    @Autowired
    public SendGridEmailService(SendGrid sg, SendGridProperties sgProps) {
        String baseUrl = "https://" + sg.getHost() + "/" + sg.getVersion();
        //@formatter:off
        WebClient.Builder webClientBuilder = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + sgProps.getApiKey())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        //@formatter:on

        if (sgProps.isProxyConfigured()) {
            HttpClient httpClient = HttpClient.create()
                    // TODO: Remove insecure TLS config below
                    .secure(s -> {
                        try {
                            s.sslContext(SslContextBuilder.forClient()
                                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    // TODO: Remove insecure TLS config above
                    .tcpConfiguration(tcpClient -> tcpClient.proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                            .host(sgProps.getProxy().getHost()).port(sgProps.getProxy().getPort())));
            ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
            webClientBuilder.clientConnector(connector);
        }

        this.webClient = webClientBuilder.build();
    }

    public Mono<Void> send(String from, String subject, String to, String contentType, String body) {
        Content content = new Content(contentType, body);
        Mail mail = new Mail(new Email(from), subject, new Email(to), content);
        return webClient.post().uri("/mail/send").body(BodyInserters.fromObject(mail)).retrieve()
                .bodyToMono(String.class).map(b -> null);
    }

}
