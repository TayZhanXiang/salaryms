package sg.com.hr.salaryms.exception;

public class NoSuchObjException extends RuntimeException {

    /*
     * This class is for the custom handling of object not found in database
     */

    public NoSuchObjException(String message) {
        super(message);
    }

    public NoSuchObjException(String message, Throwable cause) {
        super(message, cause);
    }

}
