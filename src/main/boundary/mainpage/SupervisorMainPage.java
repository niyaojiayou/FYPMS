package main.boundary.mainpage;

import main.boundary.account.ChangeAccountPassword;
import main.boundary.account.Logout;
import main.boundary.account.ViewUserProfile;
import main.boundary.modelviewer.RequestViewer;
import main.controller.project.ProjectManager;
import main.controller.request.RequestManager;
import main.controller.request.SupervisorRequestManager;
import main.model.project.Project;
import main.model.request.Request;
import main.model.request.RequestStatus;
import main.model.request.studentrequest.StudentChangeTitleRequest;
import main.model.user.Supervisor;
import main.model.user.User;
import main.model.user.UserType;
import main.repository.project.ProjectRepository;
import main.repository.request.RequestRepository;
import main.repository.user.FacultyRepository;
import main.utils.exception.repository.ModelAlreadyExistsException;
import main.utils.exception.repository.ModelNotFoundException;
import main.utils.exception.ui.PageBackException;
import main.utils.ui.BoundaryStrings;
import main.utils.ui.ChangePage;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class SupervisorMainPage {
    public static void supervisorMainPage(User user) {
        if (user instanceof Supervisor supervisor) {
            ChangePage.changePage();
            System.out.println(BoundaryStrings.separator);
            System.out.println("Welcome to Supervisor Main Page");
            System.out.println("Hello, " + user.getUserName() + "!");
            System.out.println();
            System.out.println("\t1. View my profile");
            System.out.println("\t2. Change my password");
            System.out.println("\t3. Create a project ");
            System.out.println("\t4. Modify title of projects");
            System.out.println("\t5. View all pending student requests");
            System.out.println("\t6. Submit request for transferring");
            System.out.println("\t7. View all incoming/outgoing requests' history and status");
            System.out.println("\t8. Logout");
            System.out.println(BoundaryStrings.separator);

            System.out.println();
            System.out.print("Please enter your choice: ");

            Scanner scanner = new Scanner(System.in);

            int choice = scanner.nextInt();


            try {
                switch (choice) {
                    case 1 -> ViewUserProfile.viewUserProfilePage(supervisor);
                    case 2 -> ChangeAccountPassword.changePassword(UserType.FACULTY, supervisor.getID());
                    case 3 -> supervisorCreateProject(supervisor);
                    case 4 -> supervisorChangeProjectTitle(supervisor);
                    case 5 -> supervisorViewAllPendingRequest(supervisor);
                    case 6 -> supervisorRequestForTransfer(supervisor);
                    //case 7 ->
                    case 8 -> Logout.logout();

                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (PageBackException e) {
                supervisorMainPage(supervisor);
            } catch (ModelAlreadyExistsException | ModelNotFoundException e) {
                throw new RuntimeException(e);
            }

        } else {
            throw new IllegalArgumentException("User is not a supervisor.");
        }
    }

    private static void supervisorCreateProject(Supervisor supervisor) throws ModelAlreadyExistsException, PageBackException {
        System.out.println("Creating a project....");
        System.out.println("Please enter the Project Title");
        String projectTitle = new Scanner(System.in).nextLine();
        ProjectManager.createProject(projectTitle, supervisor.getID());
        System.out.println("Project created successfully!");
        System.out.println("Enter enter to continue");
        new Scanner(System.in).nextLine();
        throw new PageBackException();
    }

    private static void supervisorChangeProjectTitle(Supervisor supervisor) throws ModelAlreadyExistsException, ModelNotFoundException, PageBackException {
        ChangePage.changePage();
        System.out.println("Changing the title of project....");
        System.out.println("Enter the project ID to change");
        String projectID = new Scanner(System.in).next();
        Project p= ProjectRepository.getInstance().getByID(projectID);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (!ProjectManager.containsProjectByID(projectID)){
                System.out.println("Project Not Found! Enter again or Enter b to exit");
            }
            else if (!Objects.equals(p.getSupervisorID(), supervisor.getID())){
                System.out.println("Project created by other supervisor! No access! Enter again or Enter b to exit");
            }
            else break;
            projectID = scanner.next();
            if (projectID.equals("b")) {
                throw new PageBackException();
            }
        }
        System.out.println("Enter the new title");
        String newTitle = new Scanner(System.in).next();
        ProjectManager.changeProjectTitle(projectID, newTitle);
        System.out.println("Project title changed successfully!");
        System.out.println("Enter enter to continue");
        new Scanner(System.in).nextLine();
        throw new PageBackException();

    }

    private static void supervisorRequestForTransfer(Supervisor supervisor) throws ModelNotFoundException, PageBackException {
        ChangePage.changePage();
        System.out.println("Processing to transfer....");
        System.out.println("Enter the project ID to transfer");
        Scanner scanner = new Scanner(System.in);
        String projectID = scanner.next();
        while (!ProjectManager.containsProjectByID(projectID)) {
            System.out.println("Project Not Found! Enter again or Enter b to exit");
            projectID = scanner.next();
            if (projectID.equals("b")) {
                throw new PageBackException();
            }
        }
        System.out.println("Enter the new supervisor transfer to");
        String newSupervisor = scanner.next();
        while (!FacultyRepository.getInstance().contains(newSupervisor)) {
            System.out.println("User Not Found! Enter again or Enter b to exit");
            newSupervisor = scanner.next();
            if (newSupervisor.equals("b")) {
                throw new PageBackException();
            }
        }
        try {
            ProjectManager.transferToNewSupervisor(projectID, newSupervisor);
            System.out.println("Transfer done!");
        } catch (ModelNotFoundException e) {
            throw new RuntimeException(e);
        }
        throw new PageBackException();
    }

    private static String getRequestToProcess(List<Request> requests) throws PageBackException {
        System.out.println("==================================");
        System.out.println("Enter the request ID to process");
        Scanner scanner = new Scanner(System.in);
        String requestID = scanner.next();
        boolean found = false;
        for (Request r : requests) {
            if (r.getID().equals(requestID)) {
                found = true;
                return requestID;
            }
        }
        System.out.println("Request Not Found! Enter again or Enter b to exit");
        requestID = scanner.nextLine();
        if (requestID.equals("b")) {
            throw new PageBackException();
        } else {
            return getRequestToProcess(requests);
        }
    }

    private static void supervisorViewAllPendingRequest(Supervisor supervisor) throws PageBackException {
        ChangePage.changePage();
        System.out.println("Displaying all pending requests by students...");
        List<Request> requests = SupervisorRequestManager.getPendingRequestsBySupervisor(supervisor.getID());
        RequestViewer.viewRequests(requests);
        if (requests.isEmpty()) {
            System.out.println("Enter any key to continue");
            new Scanner(System.in).next();
            throw new PageBackException();
        }
        System.out.println("Enter Y to process the requests OR Enter any other key to exit");
        String c = new Scanner(System.in).next();
        if (!c.equalsIgnoreCase("Y")) {
            throw new PageBackException();
        }
        String requestID = getRequestToProcess(requests);
        Request r1;
        try {
            r1 = RequestRepository.getInstance().getByID(requestID);
        } catch (ModelNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Press Y to confirm to process the following request");
        r1.display();
        String choice = new Scanner(System.in).next();
        if (!choice.equalsIgnoreCase("Y")) {
            System.out.println("Ending request processing, press any key to continue");
            new Scanner(System.in).next();
            throw new PageBackException();
        }
        System.out.println("Enter A to approve / R to reject");
        char process = new Scanner(System.in).next().charAt(0);

        if (r1 instanceof StudentChangeTitleRequest req) {
            if (req.getSupervisorID().equals(supervisor.getID()) && req.getStatus() == RequestStatus.PENDING) {
                if (process == 'A' || process == 'a') {
                    try {
                        ProjectManager.changeProjectTitle(r1.getProjectID(), req.getNewTitle());
                        RequestManager.approveRequest(requestID);
                    } catch (ModelNotFoundException | ModelAlreadyExistsException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Request approved.");
                } else if (process == 'R' || process == 'r') {
                    try {
                        RequestManager.rejectRequest(requestID);
                    } catch (ModelNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Request rejected.");
                }
            } else
                System.out.println("No access to this request or request is not pending. Process unsuccessful.");
        } else {
            System.out.println("Invalid requestID.Process unsuccessful. ");
        }
        System.out.println("Enter any enter to continue");
        new Scanner(System.in).nextLine();
        throw new PageBackException();
    }
}