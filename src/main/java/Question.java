import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question implements Serializable {
    private int question_nr;
    private String text;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String answerE;
}
