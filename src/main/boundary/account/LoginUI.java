package main.boundary.account;

import main.boundary.student.StudentMainPage;
import main.model.user.Coordinator;
import main.model.user.Student;
import main.utils.ui.BoundaryStrings;
import main.boundary.account.getter.DomainGetter;
import main.boundary.account.getter.PasswordGetter;
import main.boundary.account.getter.UserIDGetter;
import main.controller.account.AccountManager;
import main.model.user.User;
import main.model.user.UserType;
import main.utils.exception.model.PasswordIncorrectException;
import main.utils.exception.repository.ModelNotFoundException;
import main.utils.exception.ui.PageBackException;
import main.utils.ui.ChangePage;

import java.util.Scanner;

public class LoginUI {
    public static void login() throws PageBackException {
        ChangePage.changePage();
        UserType domain = DomainGetter.getDomain();
        String userID = UserIDGetter.getUserID();
        if (userID.equals("")) {
            try {
                switch (domain) {
                    case STUDENT:
                        ForgotUserID.forgotUserID();
                        break;
                    default:
                        throw new RuntimeException("Not implemented yet.");
                }
            } catch (PageBackException e) {
                login();
            }
        }
        String password = PasswordGetter.getPassword();
        try {
            User user = AccountManager.login(domain, userID, password);
            switch (domain) {
                case STUDENT:
                    StudentMainPage.studentMainPage(user);
                    break;
                default:
                    throw new RuntimeException("Not implemented yet.");
            }
            return;
        } catch (PasswordIncorrectException e) {
            System.out.println("Password incorrect.");
        } catch (ModelNotFoundException e) {
            System.out.println("User not found.");
        }
        System.out.println("Enter [b] to go back, or any other key to try again.");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        if (choice.equals("b")) {
            throw new PageBackException();
        } else {
            System.out.println("Please try again.");
            login();
        }
    }
}
