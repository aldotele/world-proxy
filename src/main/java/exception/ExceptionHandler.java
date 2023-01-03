package exception;

import io.javalin.Javalin;

import java.util.List;

public class ExceptionHandler {
    private Javalin app;
    private List<Class<? extends Exception>> exceptions;

    @SafeVarargs
    public ExceptionHandler(Javalin app, Class<? extends Exception> ... exceptions) {
        this.app = app;
        this.exceptions = List.of(exceptions);
    }

    public void handleExceptions(int statusCode) {
        for (Class<? extends Exception> ex: exceptions) {
            app.exception(ex, (e, ctx) -> {
                ctx.status(statusCode);
                ctx.json(e.getMessage());
            });
        }
    }
}
