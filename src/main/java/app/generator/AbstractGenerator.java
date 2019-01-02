package app.generator;

public abstract class AbstractGenerator<E, O> {

    public abstract O generate(E entity);
}
