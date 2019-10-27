package ai.quod.challenge.transporter;

import java.util.Map;

public interface Transporter {
  void sendTo(Map<String,Object> data);
}

