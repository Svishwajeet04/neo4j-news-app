package news.bharat.the.models;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class News {

  @Id
  @GeneratedValue(UUIDStringGenerator.class)
  private String id;

  @NotNull
  private String headline;

  @NotNull
  private String area;

  @NotNull
  private String content;

  @Relationship(type = "REPORTED_BY", direction = Relationship.Direction.OUTGOING)
  private User reporter;

}
