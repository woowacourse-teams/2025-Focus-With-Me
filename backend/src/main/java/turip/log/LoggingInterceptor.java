package turip.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME_ATTRIBUTE = "requestStartTime";
    private static final String REQUEST_ID_ATTRIBUTE = "traceId";
    private static final String DURATION_TIME_UNIT = "ms";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String traceId = UUID.randomUUID().toString().substring(0, 8);

        request.setAttribute(REQUEST_ID_ATTRIBUTE, traceId);
        request.setAttribute(REQUEST_START_TIME_ATTRIBUTE, System.currentTimeMillis());

        MDC.put("traceId", traceId);
        MDC.put("remoteAddr", request.getRemoteAddr());
        MDC.put("userAgent", request.getHeader("User-Agent"));
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());

        log.info("API 요청");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;

            MDC.put("httpStatus", String.valueOf(response.getStatus()));
            MDC.put("duration", duration + DURATION_TIME_UNIT);

            if (ex != null) {
                log.error("API 처리 중 예외 발생: {}", ex.getMessage(), ex);

            } else {
                log.info("API 응답");
            }
        } else {
            log.warn("요청 시작 시간을 찾을 수 없음");
        }

        MDC.remove("traceId");
        MDC.remove("remoteAddr");
        MDC.remove("userAgent");
        MDC.remove("method");
        MDC.remove("uri");
        MDC.remove("httpStatus");
        MDC.remove("duration");
    }
}
