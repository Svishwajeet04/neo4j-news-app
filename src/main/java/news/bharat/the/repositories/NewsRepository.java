package news.bharat.the.repositories;

import news.bharat.the.models.News;
import news.bharat.the.models.User;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface NewsRepository extends ReactiveCrudRepository<News, String>  {

  @Query(value = """
      match (n:News)
      where ($area is null or n.area = $area)
      return n limit 10;
      """)
  Flux<News> findByAreaAndReporterId(String area, String userId);

}
