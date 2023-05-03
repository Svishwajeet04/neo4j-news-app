package news.bharat.the.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsDto{

    @Null
    private String id;
    @NotNull
    private String headline;

    @NotNull
    private String area;

    @NotNull
    private List<String> imageLinks;

    @NotNull
    private String content;

    @Null
    private String reporterName;
    @Null
    private String reporterId;
}
