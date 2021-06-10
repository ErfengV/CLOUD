package cloud.demo.exception;

import org.springframework.security.core.AuthenticationException;


/**
 * @author 帅气的二峰
 */
public class ValidateCodeException extends AuthenticationException {
    private static final long serialVersionUID = -5819955193907953846L;

    public ValidateCodeException(String msg) {
        super(msg);
    }
}
