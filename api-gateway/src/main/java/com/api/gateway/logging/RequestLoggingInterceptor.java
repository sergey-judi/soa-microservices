package com.api.gateway.logging;

import com.api.gateway.util.LoggingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RequestLoggingInterceptor extends ServerHttpRequestDecorator {

  public RequestLoggingInterceptor(ServerHttpRequest delegate) {
    super(delegate);
  }

  @Override
  public Flux<DataBuffer> getBody() {
    StringBuilder sb = new StringBuilder();
    return super.getBody()
        .map(
            dataBuffer -> {
              try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                Channels.newChannel(outputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                sb.append(outputStream.toString(StandardCharsets.UTF_8));
              } catch (Exception e) {
                log.error(e.getMessage());
              }
              return dataBuffer;
            }
        ).doFinally(signal -> LoggingUtils.logRequest(getURI(), getMethod(), getHeaders(), sb));
  }

}
