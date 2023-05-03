package news.bharat.the.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("User")
public class User {

  @Id
  @GeneratedValue(UUIDStringGenerator.class)
  @Null
  private String id;

  @NotNull
  private String name;

  @Property(value = "phone_number")
  @NotNull
  private String phoneNumber;

  @NotNull
  private String password;

  @Builder.Default
  private boolean enabled = true;
}
