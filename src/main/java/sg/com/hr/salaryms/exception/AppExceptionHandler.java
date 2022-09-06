package sg.com.hr.salaryms.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import sg.com.hr.salaryms.dto.ResponseResultDTO;
import sg.com.hr.salaryms.utility.CommonString;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    /*
     * This class allows the handling of exceptions across different web controllers
     * The @ExceptionHandler will be mapping the top/ front exception accordingly
     */

    private static Logger log = LoggerFactory.getLogger(AppExceptionHandler.class);

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ResponseResultDTO> handleNullPointerException(NullPointerException exception,
            WebRequest request) {
        log.error("HandleNullPointerException : ", exception);
        ResponseResultDTO responseResult = new ResponseResultDTO(CommonString.ERROR_LOGIC);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseResult);
    }

    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    protected ResponseEntity<ResponseResultDTO> handleArrayIndexOutOfBoundsException(
            ArrayIndexOutOfBoundsException exception,
            WebRequest request) {
        log.error("HandleArrayIndexOutOfBoundsException : ", exception);
        ResponseResultDTO responseResult = new ResponseResultDTO(CommonString.ERROR_LOGIC);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseResult);
    }

    @ExceptionHandler(InvalidInputException.class)
    protected ResponseEntity<ResponseResultDTO> handleInvalidInputException(InvalidInputException exception,
            WebRequest request) {
        ResponseResultDTO responseResult = new ResponseResultDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseResult);
    }

    @ExceptionHandler(NoSuchObjException.class)
    protected ResponseEntity<ResponseResultDTO> handleNoSuchObjException(NoSuchObjException exception,
            WebRequest request) {
        ResponseResultDTO responseResult = new ResponseResultDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseResult);
    }

    @ExceptionHandler(ObjExistedException.class)
    protected ResponseEntity<ResponseResultDTO> handleObjExistedException(ObjExistedException exception,
            WebRequest request) {
        ResponseResultDTO responseResult = new ResponseResultDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseResult);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        ResponseResultDTO responseResult = new ResponseResultDTO(CommonString.ERROR_INVALID_FORMAT);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseResult);
    }

}
