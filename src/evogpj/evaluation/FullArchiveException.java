package evogpj.evaluation;

/**
 * @author Steven Fine
 */
public class FullArchiveException extends Exception {

    public FullArchiveException() {
        super("Archive is currently full.");
    }
}
