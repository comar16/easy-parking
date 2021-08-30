package core.easyparking.polimi.configuration.error;

import io.swagger.v3.oas.annotations.Hidden;
import core.easyparking.polimi.service.jwt.JWTService;
import org.apache.catalina.connector.ClientAbortException;
import org.hibernate.AssertionFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.WebUtils;

import java.security.InvalidParameterException;
import java.util.Arrays;

@Hidden
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ErrorHandler {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@ExceptionHandler({
			Exception.class,
			AssertionFailure.class
	})
	public ResponseEntity<Object> exception(Exception ex, WebRequest request) {
		HttpStatus error = HttpStatus.INTERNAL_SERVER_ERROR;
		if (ex instanceof ClientAbortException) {
			return null;
		} else if (ex instanceof AssertionFailure) {
			error = HttpStatus.BAD_REQUEST;
			while (null != ex.getCause()) {
				ex = (Exception) ex.getCause();
			}
		} else if (ex instanceof IllegalArgumentException ||
				ex instanceof AuthenticationException) {
			error = HttpStatus.BAD_REQUEST;
		} else if (ex instanceof JWTService.TokenVerificationException) {
			error = HttpStatus.FORBIDDEN;
		} else if (ex instanceof HttpRequestMethodNotSupportedException ||
				ex instanceof HttpMediaTypeNotSupportedException ||
				ex instanceof HttpMediaTypeNotAcceptableException ||
				ex instanceof ServletRequestBindingException ||
				ex instanceof TypeMismatchException ||
				ex instanceof HttpMessageNotReadableException ||
				ex instanceof HttpMessageNotWritableException ||
				ex instanceof MissingServletRequestPartException ||
				ex instanceof BindException ||
				ex instanceof NoHandlerFoundException ||
				ex instanceof AsyncRequestTimeoutException) {
			return springException(ex);
		}

		if (!(ex instanceof InvalidParameterException
				|| ex instanceof BadCredentialsException
				|| ex instanceof JWTService.TokenVerificationException)) {
			log.error(String.format("ERROR: %s; MESSAGE: %s, USER: %s STACKTRACE: %s",
					ex.getClass().toString(), ex.getMessage(), getUserData(), stackTraceFilter(ex.getStackTrace())));
		} else {
			log.warn(String.format("WARN: %s; MESSAGE: %s, USER: %s STACKTRACE: %s",
					ex.getClass().toString(), ex.getMessage(), getUserData(), stackTraceFilter(ex.getStackTrace())));
		}

		ErrorResponse errorInfo = new ErrorResponse(
				error.value(),
				ex.getMessage()
		);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "json"));

		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(error)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
		}

		return new ResponseEntity<>(errorInfo, headers, error);
	}

	private String stackTraceFilter(StackTraceElement[] stackTraceElements) {
		return Arrays.toString(
				Arrays.stream(stackTraceElements)
						.filter(st -> st.toString().startsWith("core.easyparking.polimi"))
						.toArray()
		);
	}

	private String getUserData() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
				User user = ((User) authentication.getPrincipal());
				return String.format("(authorities: %s, username: %s)",
						Arrays.toString(user.getAuthorities().toArray()), user.getUsername());
			}
			return "not logged";
		} catch (Exception e) {
			return "Auth fail: " + e.getMessage();
		}
	}

	public final ResponseEntity<Object> springException(Exception ex) {
		ErrorResponse errorInfo = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				null
		);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "json"));

		log.warn(String.format("WARN: %s; MESSAGE: %s, USER: %s STACKTRACE: %s",
				ex.getClass().toString(), ex.getMessage(), getUserData(), stackTraceFilter(ex.getStackTrace())));

		return new ResponseEntity<>(errorInfo, headers, HttpStatus.BAD_REQUEST);
	}
}
