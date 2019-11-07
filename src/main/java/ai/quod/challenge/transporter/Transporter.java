package ai.quod.challenge.transporter;

public interface Transporter<T> {
  void sendTo(T data);
}

