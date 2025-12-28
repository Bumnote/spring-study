package com.laboratory.security.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

  /* [1] 포인트컷 정의 */
  @Pointcut(
      "execution(* com.laboratory.security.service.OrderService.createOrder(..))"
  )
  private void executionCreateProduct() {
  }

  /* [2] 어드바이스 정의 */
  @Around("executionCreateProduct()")
  public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
    System.out.println("begin create order");
    Object result = joinPoint.proceed();
    System.out.println("end create order");
    return result;
  }

}