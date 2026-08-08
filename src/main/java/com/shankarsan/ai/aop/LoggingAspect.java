package com.shankarsan.ai.aop;

import com.shankarsan.ai.exception.ServiceException;
import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

  @Around("execution(public * com.shankarsan.ai..*.*(..))")
  @SuppressWarnings("checkstyle:IllegalThrows")
  public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().toShortString();
    Object[] methodArgs = joinPoint.getArgs();

    log.info("Entering method: {} with arguments: {}", methodName, Arrays.toString(methodArgs));

    long startTime = System.currentTimeMillis();
    Object result;
    try {
      result = joinPoint.proceed();
      long endTime = System.currentTimeMillis();

      Object resultToLog = result;
      if (result instanceof ResponseEntity) {
        resultToLog = ((ResponseEntity<?>) result).getBody();
      }

      log.info(
          "Exiting method: {} with result: {}. Execution time: {}ms",
          methodName,
          resultToLog,
          (endTime - startTime));
      return result;
    } catch (Throwable throwable) {
      log.error("Exception in method: {}. Reason: {}", methodName, throwable.getMessage());
      throw new ServiceException("An error occurred in " + methodName, throwable);
    }
  }
}
