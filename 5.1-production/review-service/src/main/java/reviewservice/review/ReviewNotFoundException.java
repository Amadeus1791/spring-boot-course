package reviewservice.review;

public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(Long id) {
        super("Review " + id + " does not exist");
    }
}
