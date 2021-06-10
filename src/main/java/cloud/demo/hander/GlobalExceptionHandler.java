package cloud.demo.hander;

import com.ac.comehome.entity.Result;
import com.ac.comehome.entity.ResultError;
import com.ac.comehome.enums.ResultCode;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @program: comehome
 * @description: 全局异常处理器
 * @author: ErFeng_V
 * @create: 2021-05-12 18:50
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /***
     * controller层的错误异常处理
     * @param req
     * @param resultError
     * @return
     */
    @ExceptionHandler(value = ResultError.class)
    @ResponseBody
    public Result controllerException(HttpServletRequest req, ResultError resultError){
        return Result.failure(resultError.getErrorCode(),resultError.getErrorMessage());
    }

    /**
     * 字段值校验错误处理
     * @param exception
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result methodArgumentNotValidException(MethodArgumentNotValidException exception){
        BindingResult result = exception.getBindingResult();
        StringBuffer stringBuffer = new StringBuffer();
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            if (errors != null) {
                errors.forEach(p -> {
                    FieldError fieldError = (FieldError) p;
                    stringBuffer.append(fieldError.getDefaultMessage());
                });
            }
        }
        return Result.failure(ResultCode.COMMON_PARAMS_INVALID,stringBuffer.toString());
    }

    /**
     * 系统异常错误处理
     * @param req
     * @param exception
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result bizException(HttpServletRequest req, Exception exception){
        return Result.failure(ResultCode.SYSTEM_EXCEPTION,exception.getMessage());
    }


}
