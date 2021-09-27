import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PostgreConnection {
    private static final String URL = "postgres.url";
    private static final String USER = "postgres.user";
    private static final String PASS = "postgres.password";
    final AppProperties instance = AppProperties.getInstance();

    public void registerStudent_DB(Student student, int examId_DB) {
        try {
            Connection connection = DriverManager.getConnection(instance.getProperty(URL), instance.getProperty(USER), instance.getProperty(PASS));
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO students (student_name, student_surname, login, exam_id, class_id) VALUES (?, ?, ?,?,?)");
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getSurname());
            preparedStatement.setString(3, student.getStudentLogin());
            preparedStatement.setInt(4, examId_DB);
            preparedStatement.setString(5, student.getClassId());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update_DB_Info(String table_name, String column_ToChange, String column_Where, String newValue, String whereValue) {
        try {
            Connection connection = DriverManager.getConnection(instance.getProperty(URL), instance.getProperty(USER), instance.getProperty(PASS));
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE" + table_name + "SET" + column_ToChange + "=?" + "WHERE" + column_Where + " = ?");
            preparedStatement.setString(1, newValue);
            preparedStatement.setString(2, whereValue);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void registerExam_DB(Exam exam) {
        try {
            Connection connection = DriverManager.getConnection(instance.getProperty(URL), instance.getProperty(USER), instance.getProperty(PASS));
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO exams (examCode, exam_type, exam_name, exam_date, number_of_questions) VALUES (?, ?, ?, ?,?)");
            preparedStatement.setString(1, exam.getExamId());
            preparedStatement.setString(2, exam.getType());
            preparedStatement.setString(3, exam.getName());
            preparedStatement.setString(4, exam.getDateCreated());
            preparedStatement.setInt(5, exam.getNumberOfQuestions());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void registerQuestions_DB(Question question, int examId_DB) {
        try {
            Connection connection = DriverManager.getConnection(instance.getProperty(URL), instance.getProperty(USER), instance.getProperty(PASS));
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO questions (question_text, pa_a, pa_b, pa_c, pa_d, pa_e, exam_id, question_nr) VALUES (?, ?, ?, ?,?,?,?,?)");
            preparedStatement.setString(1, question.getText());
            preparedStatement.setString(2, question.getAnswerA());
            preparedStatement.setString(3, question.getAnswerB());
            preparedStatement.setString(4, question.getAnswerC());
            preparedStatement.setString(5, question.getAnswerD());
            preparedStatement.setString(6, question.getAnswerE());
            preparedStatement.setInt(7, examId_DB);
            preparedStatement.setInt(8, question.getQuestion_nr());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void registerAnswers_DB(Answer answer, Exam exam, int examID_DB, int answer_id_DB) {
        try {
            Connection connection = DriverManager.getConnection(instance.getProperty(URL), instance.getProperty(USER), instance.getProperty(PASS));
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO answers (question_nr, answer_text, examCode, exam_id) VALUES (?, ?, ?, ?)");
            preparedStatement.setInt(1, (answer.getAnswerNr() + 1));
            preparedStatement.setString(2, answer.getAnswer_text());
            preparedStatement.setString(3, exam.getExamId());
            preparedStatement.setInt(4, examID_DB);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void registerResults_DB(Student student, Exam exam, int scour, String time) {
        try {
            Connection connection = DriverManager.getConnection(instance.getProperty(URL), instance.getProperty(USER), instance.getProperty(PASS));
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO results (scour, dateTaken, studentLogin, examCode, exam_id, student_id) VALUES = (?, ?,?,?,?,?)");
            preparedStatement.setInt(1, scour);
            preparedStatement.setString(2, time);
            preparedStatement.setString(3, student.getStudentLogin());
            preparedStatement.setString(4, exam.getExamId());
            preparedStatement.setInt(5, 1);
            preparedStatement.setInt(6, 3);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ResultSet findInfo(String value, String column_name, String table_name) {
        try {
            Connection connection = DriverManager.getConnection(instance.getProperty(URL), instance.getProperty(USER), instance.getProperty(PASS));
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + table_name + " WHERE " + column_name + " = ?");
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

//    public int findStudentIdByLogin(int id){
//        ResultSet resultSet = null;
//        try {
//            Connection connection = DriverManager.getConnection(instance.getProperty(URL), instance.getProperty(USER), instance.getProperty(PASS));
//            Statement statement = connection.createStatement();
//            ResultSet  = statement.executeQuery("SELECT student_id FROM students WHERE login = ?");
//            System.out.println(resultSet);
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        return resultSet;
////    }

}
