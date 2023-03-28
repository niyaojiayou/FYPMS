package test.ui.student.details;

import main.boundary.account.LoginUI;
import main.boundary.student.details.ChangeStudentPassword;
import main.controller.account.AccountManager;
import main.model.user.Student;
import main.model.user.UserType;
import main.repository.user.CoordinatorRepository;
import main.repository.user.StudentRepository;
import main.utils.exception.repository.ModelAlreadyExistsException;
import main.utils.exception.ui.PageBackException;

public class ChangeStudentPasswordTest {
    public static void main(String[] args) throws ModelAlreadyExistsException, PageBackException {
        StudentRepository.getInstance().clear();
        AccountManager.register(UserType.STUDENT, "D220006", "Nicole", "D220006@e.ntu.edu.sg");
        try {
            ChangeStudentPassword.changeStudentPassword("D220006");
        } catch (PageBackException e) {
            System.out.println("Page back.");
        }
        LoginUI.login();
    }
}