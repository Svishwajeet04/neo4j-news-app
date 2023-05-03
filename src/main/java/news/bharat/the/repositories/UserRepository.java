package news.bharat.the.repositories;

import news.bharat.the.models.User;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {
  Mono<User> findByPhoneNumber(String s);

    @Query(value = """
      MATCH (ne:News {id : $newsId} ) - [:REPORTED_BY] -> (u:User)
      RETURN distinct u limit 1;
      """)
  Mono<User> findReporterByNewsId(String newsId);
}
