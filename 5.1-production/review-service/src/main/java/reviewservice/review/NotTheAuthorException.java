package reviewservice.review;

public class NotTheAuthorException extends RuntimeException {

    public NotTheAuthorException(Long id) {
        super("Review " + id + " was written by someone else");
    }
}
