package main.controller.request;

import main.controller.project.ProjectManager;
import main.model.project.Project;
import main.model.project.ProjectStatus;
import main.model.request.Request;
import main.model.request.StudentChangeTitleRequest;
import main.model.request.StudentDeregistrationRequest;
import main.model.request.StudentRegistrationRequest;
import main.model.user.Student;
import main.model.user.StudentStatus;
import main.repository.project.ProjectRepository;
import main.repository.request.RequestRepository;
import main.repository.user.StudentRepository;
import main.utils.exception.StudentStatusException;
import main.utils.exception.ModelAlreadyExistsException;
import main.utils.exception.ModelNotFoundException;

import java.util.List;

/**
 * StudentManager class
 */
public class StudentManager {
    /**
     * student request to deregister from a project
     *
     * @param projectID the project ID of the project that the student is going to deregister from
     * @param studentID the student ID of the student that is going to deregister from the project
     * @return The ID of the new reuquest
     * @throws IllegalStateException       if the project is not allocated
     * @throws StudentStatusException      if the student is not registered
     * @throws ModelAlreadyExistsException if the request already exists
     * @throws ModelNotFoundException      if the project or student is not found
     */
    public static String deregisterStudent(String projectID, String studentID) throws IllegalStateException, StudentStatusException, ModelAlreadyExistsException, ModelNotFoundException {
        String requestID = RequestManager.getNewRequestID();
        Project project = ProjectRepository.getInstance().getByID(projectID);
        Student student = StudentRepository.getInstance().getByID(studentID);
        if (project.getStatus() != ProjectStatus.ALLOCATED) {
            throw new IllegalStateException("Project is not allocated");
        }
        if (!project.getStudentID().equals(studentID)) {
            throw new IllegalStateException("Student is not allocated to this project");
        }
        if (student.getStatus() == StudentStatus.UNREGISTERED) {
            throw new StudentStatusException(student.getStatus());
        }
        String supervisorID = project.getSupervisorID();
//        System.err.println("supervisorID = " + supervisorID);
        Request request = new StudentDeregistrationRequest(requestID, studentID, supervisorID, projectID);
        RequestRepository.getInstance().add(request);
        return requestID;
    }

    /**
     * student request to register to a project
     *
     * @param projectID the project ID of the project that the student is going to register to
     * @param studentID the student ID of the student that is going to register to the project
     * @return the ID of the new Request
     * @throws ModelNotFoundException if the project or student is not found
     * @throws StudentStatusException if the student is not unregistered
     * @throws IllegalStateException  if the project is not available
     * @throws ModelAlreadyExistsException if the request already exists
     */
    public static String registerStudent(String projectID, String studentID) throws ModelNotFoundException, StudentStatusException, IllegalStateException, ModelAlreadyExistsException {

        Project project = ProjectRepository.getInstance().getByID(projectID);
        Student student = StudentRepository.getInstance().getByID(studentID);
        if (project.getStatus() != ProjectStatus.AVAILABLE) {
            throw new IllegalStateException("Project is not available");
        }
        if (student.getStatus() == StudentStatus.REGISTERED) {
            throw new StudentStatusException(student.getStatus());
        }
        if (student.getStatus() == StudentStatus.DEREGISTERED) {
            throw new StudentStatusException(student.getStatus());
        }
        String requestID = RequestManager.getNewRequestID();
        String supervisorID = project.getSupervisorID();
        Request request = new StudentRegistrationRequest(requestID, studentID, supervisorID, projectID);
        project.setStatus(ProjectStatus.RESERVED);
        ProjectRepository.getInstance().update(project);
        student.setStatus(StudentStatus.PENDING);
        StudentRepository.getInstance().update(student);
        RequestRepository.getInstance().add(request);
        ProjectManager.updateProjectsStatus();
        return requestID;
    }

    /**
     * student request to change the title of a project
     *
     * @param projectID the project ID of the project that the student is going to change the title of
     * @param newTitle  the new title of the project
     * @param studentID the student ID of the student that is going to change the title of the project
     * @throws ModelAlreadyExistsException if the request already exists
     * @throws ModelNotFoundException      if the project or student is not found
     * @return the ID of the new Request
     */
    public static String changeProjectTitle(String projectID, String newTitle, String studentID) throws ModelAlreadyExistsException, ModelNotFoundException {
        String requestID = RequestManager.getNewRequestID();
        String supervisorID = ProjectRepository.getInstance().getByID(projectID).getSupervisorID();
        Request request = new StudentChangeTitleRequest(requestID, studentID, supervisorID, projectID, newTitle);
        RequestRepository.getInstance().add(request);
        return requestID;
    }

    /**
     * get the student request history
     * @param studentID the student ID
     * @return the list of requests
     */
    public static List<Request> getStudentRequestHistory(String studentID) {
//        System.err.println("StudentRequestManager.getStudentRequestHistory studentID = " + studentID);
        return RequestRepository.getInstance().findByRules(request -> request.getStudentID().equals(studentID));
    }

    /**
     * get the student by ID
     * @param studentID the student ID
     * @return the student
     * @throws ModelNotFoundException if the student is not found
     */
    public static Student getByID(String studentID) throws ModelNotFoundException {
        return StudentRepository.getInstance().getByID(studentID);
    }
}

