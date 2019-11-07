package ai.quod.challenge.tranfomer;

public interface Transformer<T,U> {
  public T transform(U data);
}
