import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Scanner;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exam implements Serializable {
    private String examId;
    private String name;
    private String type;
    private String dateCreated;
    private Integer numberOfQuestions;
}
