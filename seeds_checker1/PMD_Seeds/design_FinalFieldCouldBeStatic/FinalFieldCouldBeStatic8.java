import lombok.Builder;
import lombok.Data;

@Data
@Builder
class ExampleClass {

  @Builder.Default private final long exampleField = 0L;
}
