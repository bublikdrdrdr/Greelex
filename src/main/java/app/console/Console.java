package app.console;

public class Console {

    //example run: java -jar emf-1.0.jar mapping.txt app.entity.generated ../../src/main/java/app/
    public static void main(String[] args) {
        boolean stacktrace = false;
        try {
            ConsoleHelper consoleHelper = new ConsoleHelper();
            Configuration configuration = consoleHelper.parseConfiguration(args);
            stacktrace = configuration.isStacktrace();
            consoleHelper.process(configuration);
        } catch (Exception e) {
            if (stacktrace) {
                e.printStackTrace();
            } else {
                System.err.println(e.getClass().getName() + " : " + e.getMessage());
            }
        }
    }

}
