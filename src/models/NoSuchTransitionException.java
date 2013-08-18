
package models;

/**
 * Exception class which is thrown when a certain transition is not at it's place
 * @author Kai
 */
public class NoSuchTransitionException extends Exception{

    public NoSuchTransitionException(String message) {
        super(message);
    }

    public NoSuchTransitionException() {
    }
    
}
