package evogpj.evaluation;

/**
 * Created by stevenfine on 8/5/16.
 */
public class EmptyArchiveException extends Exception {

    public EmptyArchiveException() {
        super("Archive is currently empty.");
    }
}
