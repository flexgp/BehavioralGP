package evogpj.evaluation;

/**
 * @author Steven Fine
 */
public class EmptyArchiveException extends Exception {

    public EmptyArchiveException() {
        super("Archive is currently empty.");
    }
}
