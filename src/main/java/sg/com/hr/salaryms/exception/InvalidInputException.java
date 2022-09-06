package sg.com.hr.salaryms.exception;

public class InvalidInputException extends RuntimeException {

    /*
     * This class is for the custom handling of input validation
     */

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

}
