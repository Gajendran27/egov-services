package org.egov.tracer.config;

import io.opentracing.noop.NoopTracerFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.egov.tracer.http.RestTemplateLoggingInterceptor;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.apache.http.client.HttpClient;

import java.util.Collections;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"org.egov.tracer"})
@PropertySource("classpath:tracer.properties")
public class TracerConfiguration {

    @Bean
    public TracerProperties tracerProperties(Environment environment) {
        return new TracerProperties(environment);
    }


    @Bean
    public ObjectMapperFactory objectMapperFactory(TracerProperties tracerProperties) {
        return new ObjectMapperFactory(tracerProperties);
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 5000;
        HttpClient httpClient = HttpClientBuilder
            .create()
            .setConnectionManager(new PoolingHttpClientConnectionManager() {{
                setDefaultMaxPerRoute(20);
                setMaxTotal(50);
            }}).build();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
            = new HttpComponentsClientHttpRequestFactory(httpClient);

        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }

    @Bean(name = "logAwareRestTemplate")
    public RestTemplate logAwareRestTemplate(TracerProperties tracerProperties) {
        RestTemplate restTemplate =
            new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        restTemplate.setInterceptors(Collections.singletonList(new RestTemplateLoggingInterceptor()));
        return restTemplate;
    }

    @Bean
    @Profile("monitoring")
    public io.opentracing.Tracer jaegerTracer() {
        return io.jaegertracing.Configuration.fromEnv()
            .getTracer();
    }

    @Bean
    @Profile("!monitoring")
    public io.opentracing.Tracer tracer() {
        return NoopTracerFactory.create();
    }

}


