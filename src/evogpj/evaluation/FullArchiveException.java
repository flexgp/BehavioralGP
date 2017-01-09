package evogpj.evaluation;

/**
 * Created by stevenfine on 1/9/17.
 */
public class FullArchiveException extends Exception {

    public FullArchiveException() {
        super("Archive is currently full.");
    }
}
