package sg.com.hr.salaryms.exception;

public class ObjExistedException extends RuntimeException {

    /*
     * This class is for the custom handling of object found (for duplication) in
     * database
     */

    public ObjExistedException(String message) {
        super(message);
    }

    public ObjExistedException(String message, Throwable cause) {
        super(message, cause);
    }

}
