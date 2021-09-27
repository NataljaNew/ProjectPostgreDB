import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {


    @Test
    void registerStudentByID() throws SQLException {
        Service service = new Service();
        service.registerStudentByID("1");
        int expected = 1;
        //int result =
    }

    @Test
    void getUniqueID() {
    }

    @Test
    void getValidNumber() {
        int expected = 1;
       // int result = getValidNumber(1);

    }

    @Test
    void checkExam() {
    }

    @Test
    void registerExam() {
    }
}