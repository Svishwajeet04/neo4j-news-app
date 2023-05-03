package news.bharat.the.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Node(value = "ImageLink")
public class ImageLink {
  @Id
  @GeneratedValue(UUIDStringGenerator.class)
  private String id;

  private String link;

  @Relationship(type = "LINKED_TO", direction = Relationship.Direction.OUTGOING)
  private News news;
}
