package reviewservice.review;

// A rule over two fields: 1 or 2 stars need a text.
public class TextRequiredException extends RuntimeException {

    public TextRequiredException(int stars) {
        super("A review with " + stars + " star(s) needs a text");
    }
}
