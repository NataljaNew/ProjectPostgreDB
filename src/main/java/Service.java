import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Service {
    Map<String, Student> mapStudents = new HashMap();
    Scanner sc = new Scanner(System.in);
    List<Answer> answers = new ArrayList<>();
    List<Question> examQuestions = new ArrayList<>();
    Map<Student, List<Answer>> studentAnswerMap = new HashMap<>();
    Exam exam = new Exam();
    Map<String, Exam> examMap = new HashMap<>();
    private int numberOfQuestions = 0;
    List<Result> resultList = new ArrayList<>();
//    String tempLogin = "";

    LocalDateTime time1 = null;
    String time = convertTime(time1);

    PostgreConnection postgreConnection = new PostgreConnection();

    public void menu() throws SQLException {
        File directory = new File("Catalogue");
        if (!directory.exists()) {
            try {
                directory.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File answersFile = new File("Catalogue/answersFile.json");
        if (!answersFile.exists()) {
            try {
                answersFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File studentAnswersFile = new File("Catalogue/studentAnswersFile.json");
        if (!studentAnswersFile.exists()) {
            try {
                studentAnswersFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File studentScourFile = new File("Catalogue/studentScourFile.json");
        if (!studentScourFile.exists()) {
            try {
                studentScourFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        do {
            System.out.println(" __________________________________");
            System.out.println("|    [1]   teacher's account       |");
            System.out.println("|    [2]   student registration    |");
            System.out.println("|    [3]   log in to start " + Enums.EXAM);
            System.out.println("|    [4]   retake " + Enums.EXAM);
            System.out.println("|    [5]   exit                    |");
            System.out.println("|__________________________________|");
            System.out.println("Enter  [1], or [2], [3], [4] or [5] : ");
            String input = sc.next();
            switch (input) {
                case "1" -> {
                    System.out.println(" ___________________________");
                    System.out.println("| [1] create" + Enums.EXAM + "           |");
                    System.out.println("| [2] student's results     |");
                    System.out.println("| [3] go to main menu       |");
                    System.out.println("|___________________________|");
                    String input1 = sc.next();
                    switch (input1) {
                        case "1" -> {
                            String examCode = getUniqueID("examCode", "exams");
                            registerExam(examCode);
                            createQuestions(1);
                            createAnswers(1, examCode);
                            generateAnswerFile(exam, answersFile);

                        }
                        case "2" -> {
                            sort(resultList);
                        }
                        case "3" -> menu();
                        default -> System.out.println("something went wrong, try again.");
                    }
                }
                case "2" -> {
                    System.out.println("*** User Registration ***");
                    registerStudent();
                }
                case "3" -> {
                    System.out.println("*** User Log in ***");
                    takeExam(studentAnswersFile);
                    generateAnswerFile(exam, studentScourFile, resultList);
                }
                case "4" -> {
                    logIN();
                    LocalDateTime newTime = time1.plusDays(2);
                    LocalDateTime timeNow = LocalDateTime.now();
                    if (timeNow.isAfter(newTime)) {
                        takeExam(studentAnswersFile);
                        generateAnswerFile(exam, studentScourFile, resultList);
                    } else {
                        System.out.println("Your can retake your" + Enums.EXAM + " only after " + newTime);
                    }

                }
                case "5" -> System.exit(0);
                default -> System.out.println("something went wrong, try again.");
            }
        } while (true);
    }

    public void registerStudent() throws SQLException {
        String id = getUniqueID("login", "students");
        System.out.println(registerStudentByID(id).toString());
        System.out.println("Number of students registered = " + mapStudents.size());
    }

    public void generateAnswerFile(Exam exam, File name) {
        List<Object> list = new ArrayList<>();
        list.add(exam);
        list.add(answers);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            mapper.writeValue(name, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateAnswerFile(Exam exam, File name, Map<Student, List<Answer>> studentAnswerMap) {
        List<Object> list = new ArrayList<>();
        list.add(exam);
        list.add(studentAnswerMap);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            mapper.writeValue(name, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateAnswerFile(Exam exam, File name, List<Result> resultList) {
        List<Object> list = new ArrayList<>();
        list.add(exam);
        list.add(resultList);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            mapper.writeValue(name, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Student registerStudentByID(String login) throws SQLException {
        System.out.println("Enter your name: ");
        String name = sc.next();
        System.out.println("Enter your surname:");
        String surname = sc.next();
        System.out.println("Enter you classId:");
        String classId = sc.next();
        Student student = new Student(classId, login, name, surname);
        mapStudents.put(login, student);
        try {
            postgreConnection.registerStudent_DB(student, 1);
        } catch (Exception e) {
            System.out.println("something went wrong, try again");
            menu();
        }
        System.out.println("Registration successful");
        return student;
    }

    public String getUniqueID(String column_name, String table_name) throws SQLException {
        String id = "";
        do {
            System.out.println("Enter ID:");
            String loginID = sc.next();
            ResultSet resultSet = postgreConnection.findInfo(loginID, column_name, table_name);
            //checking if this id exists in DB
            if (!resultSet.next()) {
                id = loginID;
            } else {
                System.out.println("This ID already exists, please check ID number and try again");
                id = "";
            }
        } while (id.equals(""));
        return id;
    }

    public int getValidNumber() {
        int idCheck = 0;
        System.out.println("Enter your number:");
        String number = sc.next();
        try {
            idCheck = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            System.out.println("Your number is invalid. Try again.");
            getValidNumber();
        }
        return idCheck;
    }

    public void logIN() throws SQLException {
        String logIDCheck = getLogInId(mapStudents);
        if (mapStudents.get(logIDCheck) != null) {
            System.out.println("Login is successful");
            System.out.println(mapStudents.get(logIDCheck).toString());
        } else {
            System.out.println("Please check your ID or register.");
            menu();
        }
    }

    public <T> String getLogInId(Map<String, T> object) throws SQLException {
        System.out.println("Enter your ID:");
        String id = sc.next();
        ResultSet resultSet = postgreConnection.findInfo(id, "examcode", "exams");
        if (resultSet.next()) {
            System.out.println("Login is successful");
            return id;
        } else {
            System.out.println("Please check your ID or register.");
            menu();
            return null;
        }
    }

    public void startExam(Student student) throws SQLException {
        List<Answer> list = new ArrayList<>();
        System.out.println("Enter examID:");
        String examId = sc.next();
        ResultSet resultSet = postgreConnection.findInfo(examId, "question_text", "questions");
        int i = 0;
        while (i == numberOfQuestions) {
            System.out.println(resultSet.getString("question_text"));
            System.out.println("Press [a], [b], [c], [d], or [e]:");
            String answer = sc.next();
            if (answer.equals("a") || answer.equals("b") || answer.equals("c") || answer.equals("d") || answer.equals("e")) {
                list.add(new Answer(i, answer));
                i++;
            } else {
                System.out.println("Something went wrong, try again.");
                menu();
            }
        }
        studentAnswerMap.put(student, list);
        int score = 0;
        for (int a = 0; a < answers.size(); a++) {
            if (list.get(a).getAnswer_text().equals(answers.get(a).getAnswer_text())) {
                score++;
            }
        }
        BigDecimal result = BigDecimal.valueOf((double) score * 100 / 3).setScale(0, RoundingMode.HALF_UP);
        System.out.println("Your scour is " + score + " out of " + 3 + ". That is = " + result + "%");
        int mark = Integer.parseInt(String.valueOf(result)) / 10;
        int studentScour = (int) Math.round(mark + 0.5);
        resultList.add(new Result(student, studentScour, time));
        postgreConnection.registerResults_DB(student,exam,studentScour,time);
    }

    public void createAnswers(int examId_DB, String examCode) {
        for (int i = 0; i < numberOfQuestions; i++) {
            System.out.println("Enter answer for question nr. " + (i + 1) + ". Enter [a], [b], [c], [d], or [e]");
            String answer = sc.next();
            if (answer.equals("a") || answer.equals("b") || answer.equals("c") || answer.equals("d") || answer.equals("e")) {
                Answer rightAnswer = new Answer(i, answer);
                answers.add(rightAnswer);
                postgreConnection.registerAnswers_DB(rightAnswer, exam, examId_DB, (i + 1));
            } else {
                System.out.println("Something went wrong, try again.");
                i--;
            }
        }
        System.out.println(Enums.EXAM + " file was created successful");
    }

    public void registerExam(String examCode) {
        System.out.println("Enter " + Enums.EXAM + "'s name: ");
        String name = sc.next();
        System.out.println("Enter " + Enums.EXAM + "'s type:");
        String type = sc.next();
        exam = new Exam(examCode, name, type, convertTime(LocalDateTime.now()), numberOfQuestions);
        examMap.put(examCode, exam);
        System.out.println(exam.toString());
        System.out.println(Enums.EXAM + " registered successful");
    }

    public void createQuestions(int examID_DB) {
        Scanner newScanner = new Scanner(System.in);
        System.out.println("Enter number of questions:");
        numberOfQuestions = getValidNumber();
        exam.setNumberOfQuestions(numberOfQuestions);
        postgreConnection.registerExam_DB(exam);
        for (int i = 0; i < numberOfQuestions; i++) {
            System.out.println("Please enter question nr. " + (i + 1) + ":");
            String question = newScanner.nextLine();
            System.out.println("Please enter possible answer [a] : ");
            String answerA = newScanner.nextLine();
            System.out.println("Please enter possible answer [b] : ");
            String answerB = newScanner.nextLine();
            System.out.println("Please enter possible answer [c] : (if you don't need this option, press [x] ");
            String answer1 = newScanner.nextLine();
            String answerC = "";
            if (answer1.equals("x")) {
                answerC = null;
            } else {
                answerC = answer1;
            }
            System.out.println("Please enter possible answer [d] : (if you don't need this option, press [x] ");
            String answer2 = newScanner.nextLine();
            String answerD = "";
            if (answer2.equals("x")) {
                answerD = null;
            } else {
                answerD = answer2;
            }
            System.out.println("Please enter possible answer [e] : (if you don't need this option, press [x] ");
            String answer3 = newScanner.nextLine();
            String answerE = "";
            if (answer3.equals("x")) {
                answerE = null;
            } else {
                answerE = answer3;
            }
            Question question1 = new Question((i + 1), question, answerA, answerB, answerC, answerD, answerE);
            examQuestions.add(question1);
            postgreConnection.registerQuestions_DB(question1, examID_DB);
        }
        System.out.println("Questions registered");
    }

    public void takeExam(File resultFile) throws SQLException {
        String key = getLogInId(mapStudents);
        Student myStudent = mapStudents.get(key);
        startExam(myStudent);
        generateAnswerFile(exam, resultFile, studentAnswerMap);
        time1 = LocalDateTime.now();
    }

    public void sort(List<Result> resultList) {
        resultList.stream()
                .mapToInt(Result::getScour)
                .max();
        System.out.println(resultList.toString());
    }

    public static String convertTime(LocalDateTime time) {
        if (time != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return time.format(formatter);
        } else {
            return null;
        }
    }
}
