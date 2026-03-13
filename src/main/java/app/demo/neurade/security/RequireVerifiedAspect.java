package app.demo.neurade.security;

import app.demo.neurade.domain.models.User;
import app.demo.neurade.exception.UserNotVerifiedException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RequireVerifiedAspect {
    @Around("within(@app.demo.neurade.security.RequireVerified *) || @annotation(app.demo.neurade.security.RequireVerified)")
    public Object checkVerified(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("Unauthorized");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        if (user == null || Boolean.FALSE.equals(user.getVerified())) {
            throw new UserNotVerifiedException();
        }
        return joinPoint.proceed();
    }
}
