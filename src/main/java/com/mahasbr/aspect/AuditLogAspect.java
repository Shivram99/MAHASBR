package com.mahasbr.aspect;

//@Aspect
//@Component
public class AuditLogAspect {
	/*
	 * 
	 * @Autowired private HttpServletRequest request;
	 * 
	 * @Autowired private AuditLogRepository auditLogRepository;
	 * 
	 * @AfterReturning(pointcut =
	 * "execution(* org.springframework.security.authentication.AuthenticationProvider.authenticate(..)) && args(authentication)"
	 * , returning = "result") public void logSuccessfulLogin(Authentication
	 * authentication, Object result) { String username = authentication.getName();
	 * String ipAddress = request.getRemoteAddr(); auditLogRepository.save(new
	 * AuditLog(username, "LOGIN", LocalDateTime.now(), ipAddress)); }
	 * 
	 * 
	 * @AfterThrowing(pointcut =
	 * "execution(* org.springframework.security.authentication.AuthenticationProvider.authenticate(..))"
	 * , throwing = "ex") public void logFailedLogin(JoinPoint joinPoint,
	 * AuthenticationException ex) { String username = (String)
	 * joinPoint.getArgs()[0]; String ipAddress = request.getRemoteAddr();
	 * auditLogRepository.save(new AuditLog(username, "LOGIN_FAILED",
	 * LocalDateTime.now(), ipAddress)); }
	 * 
	 * 
	 * 
	 * @Before("execution(* org.springframework.security.web.authentication.logout.LogoutHandler.logout(..))"
	 * ) public void logSuccessfulLogout() { Authentication authentication =
	 * SecurityContextHolder.getContext().getAuthentication(); String username =
	 * (authentication == null) ? "anonymousUser" : authentication.getName(); String
	 * ipAddress = request.getRemoteAddr(); auditLogRepository.save(new
	 * AuditLog(username, "LOGOUT", LocalDateTime.now(), ipAddress)); }
	 */}
