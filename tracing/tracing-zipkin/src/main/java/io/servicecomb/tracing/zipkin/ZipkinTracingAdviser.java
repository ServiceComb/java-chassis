package io.servicecomb.tracing.zipkin;

import java.util.function.Supplier;

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;

class ZipkinTracingAdviser {

  private static final String CALL_PATH = "path";

  private final Tracer tracer;

  ZipkinTracingAdviser(Tracer tracer) {
    this.tracer = tracer;
  }

  <T> T invoke(String spanName, String path, Supplier<T> supplier) {
    Span span = createSpan(spanName, path);
    try (SpanInScope spanInScope = tracer.withSpanInScope(span)) {
      return supplier.get();
    } catch (Throwable throwable) {
      span.tag("error", throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
      throw throwable;
    } finally {
      span.finish();
    }
  }

  private Span createSpan(String spanName, String path) {
    Span currentSpan = tracer.currentSpan();
    if (currentSpan != null) {
      return tracer.newChild(currentSpan.context()).name(spanName).tag(CALL_PATH, path).start();
    }

    return tracer.newTrace().name(spanName).tag(CALL_PATH, path).start();
  }
}
