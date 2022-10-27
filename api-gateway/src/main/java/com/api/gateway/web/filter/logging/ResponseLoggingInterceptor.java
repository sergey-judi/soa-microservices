package com.api.gateway.web.filter.logging;

import com.api.gateway.util.LoggingUtils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ResponseLoggingInterceptor extends ServerHttpResponseDecorator {

  public ResponseLoggingInterceptor(ServerHttpResponse delegate) {
    super(delegate);
  }

  @Override
  public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
    StringBuilder sb = new StringBuilder();

    Flux<? extends DataBuffer> bodyLogger = Flux.from(body).doOnNext(
        dataBuffer -> {
          try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Channels.newChannel(outputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
            sb.append(outputStream.toString(StandardCharsets.UTF_8));
          } catch (Exception e) {
            log.error(e.getMessage());
          }
        }
    ).doFinally(s -> LoggingUtils.logResponse(getStatusCode(), getHeaders(), sb));

    return super.writeWith(bodyLogger);
  }

}
