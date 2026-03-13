package app.demo.neurade.exception;

public class UserNotVerifiedException extends RuntimeException {
    public UserNotVerifiedException() {
        super("User email is not verified");
    }
}

