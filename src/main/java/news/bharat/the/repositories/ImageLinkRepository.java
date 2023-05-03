package news.bharat.the.repositories;

import news.bharat.the.models.ImageLink;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ImageLinkRepository extends ReactiveCrudRepository<ImageLink, String> {

  @Query("""
      match (im : ImageLink) - [:LINKED_TO] -> (n:News {id : $id})
      return im;
      """)
  Flux<ImageLink> findByNewsId(String id);
}
