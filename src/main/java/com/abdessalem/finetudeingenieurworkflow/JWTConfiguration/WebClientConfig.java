package com.abdessalem.finetudeingenieurworkflow.JWTConfiguration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Configuration
public class WebClientConfig {
    @Value("${github.http.connect-timeout}")
    private int connectTimeout;

    @Value("${github.http.read-timeout}")
    private int readTimeout;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {

        TcpClient tcp = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout * 1_000)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeout))
                                .addHandlerLast(new WriteTimeoutHandler(readTimeout))
                );

        // Augmenter la taille du buffer pour supporter de grandes réponses JSON
        int sizeInBytes = 50 * 1024 * 1024; // Par exemple, 50 Mo

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(sizeInBytes); // Augmenter la taille du buffer à 50 Mo
                })
                .build();

        return builder
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcp)))
                .exchangeStrategies(strategies)  // Ajout de la configuration de buffer
                .build();
    }
}
