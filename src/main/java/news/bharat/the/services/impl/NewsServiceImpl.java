package news.bharat.the.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import news.bharat.the.dtos.NewsDto;
import news.bharat.the.models.ImageLink;
import news.bharat.the.models.News;
import news.bharat.the.models.User;
import news.bharat.the.repositories.ImageLinkRepository;
import news.bharat.the.repositories.NewsRepository;
import news.bharat.the.repositories.UserRepository;
import news.bharat.the.services.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsServiceImpl implements NewsService {

  private final NewsRepository newsRepository;
  private final ImageLinkRepository imageLinkRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public Mono<ResponseEntity<?>> createNews(NewsDto newsDto, String userId) {
    News news = News.builder()
        .area(newsDto.getArea())
        .content(newsDto.getContent())
        .headline(newsDto.getHeadline())
        .build();
    return Mono.just(news)
        .zipWith(Mono.from(userRepository.findById(userId)))
        .flatMap(data -> {
          News news1 = data.getT1();
          news1.setReporter(data.getT2());
          return newsRepository.save(news1);
        }).map(news1 -> {
          String reporterName = news1.getReporter().getName();
          newsDto.getImageLinks().stream().map(imageLink ->
              ImageLink.builder()
                  .news(news1)
                  .link(imageLink)
                  .build()).map(imageLinkRepository::save).forEach(Mono::subscribe);
          return NewsDto.builder()
              .id(news1.getId())
              .reporterId(news1.getReporter().getId())
              .area(news1.getArea())
              .reporterName(reporterName)
              .content(news1.getContent())
              .headline(news1.getHeadline())
              .imageLinks(newsDto.getImageLinks())
              .build();
        })
        .map(ResponseEntity::ok);
  }

  @Override
  public Mono<ResponseEntity<?>> getAllNews(String area, String reporterId) {
    return newsRepository.findByAreaAndReporterId(area, reporterId)
        .flatMap(news -> Mono.just(news)
            .zipWith(imageLinkRepository.findByNewsId(logAndReturn(news.getId())).collectSortedList())
            .map(data -> {
              News news1 = data.getT1();
              List<ImageLink> imageLinkList = data.getT2();
              return logAndReturn(NewsDto.builder()
                  .id(news1.getId())
                  .headline(news1.getHeadline())
                  .content(news1.getContent())
                  .area(news1.getArea())
                  .imageLinks(imageLinkList.stream().map(ImageLink::getLink).collect(Collectors.toList()))
                  .build());
            }))
        .flatMap(newsDto -> Mono.just(newsDto)
            .zipWith(userRepository.findReporterByNewsId(newsDto.getId()))
            .map(data -> {
              User user = data.getT2();
              NewsDto newsDto1 = data.getT1();
              newsDto1.setReporterId(user.getId());
              newsDto1.setReporterName(user.getName());
              return newsDto1;
            }))
        .collectSortedList(Comparator.comparing(NewsDto::getHeadline))
        .map(ResponseEntity::ok);
  }

  private <T> T logAndReturn(T build) {
    log.info( "{} {}" ,build.getClass().getName() , build);
    return build;
  }
}
