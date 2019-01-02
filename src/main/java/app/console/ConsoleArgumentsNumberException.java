package app.console;

public class ConsoleArgumentsNumberException extends RuntimeException {

    public ConsoleArgumentsNumberException() {
        super("3 or 4 arguments needed: mapping file, package, generated files location, '-stacktrace' (optional)");
    }
}
