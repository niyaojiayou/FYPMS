package main.model.request;

import main.model.request.Request;
import main.model.request.RequestStatus;
import main.model.request.RequestType;

import java.util.Map;

public class StudentChangeTitleRequest implements Request {
    /**
     * The type of the request
     */
    private final RequestType requestType = RequestType.STUDENT_CHANGE_TITLE;
    /**
     * The ID of the request
     */
    private String requestID;
    /**
     * The status of the request
     */
    private RequestStatus requestStatus = RequestStatus.PENDING;
    /**
     * The ID of the student
     */
    private String studentID;
    /**
     * The ID of the supervisor who deals with the request
     */
    private String supervisorID;
    /**
     * The ID of the project
     */
    private String projectID;
    /**
     * The new title of the project
     */
    private String newTitle;

    /**
     * Constructor
     *
     * @param requestID    The ID of the request
     * @param studentID    The ID of the student
     * @param supervisorID The ID of the supervisor
     * @param projectID    The ID of the project
     * @param newTitle     The new title of the project
     */
    public StudentChangeTitleRequest(String requestID, String studentID, String supervisorID, String projectID, String newTitle) {
        this.requestID = requestID;
        this.studentID = studentID;
        this.supervisorID = supervisorID;
        this.projectID = projectID;
        this.newTitle = newTitle;
    }

    /**
     * Constructor
     *
     * @param map The map of the request
     */
    public StudentChangeTitleRequest(Map<String, String> map) {
        fromMap(map);
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    @Override
    public String getSupervisorID() {
        return supervisorID;
    }

    public void setSupervisorID(String supervisorID) {
        this.supervisorID = supervisorID;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getNewTitle() {
        return newTitle;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    /**
     * Get the type of the request.
     *
     * @return the type of the request.
     */
    @Override
    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * Get the ID of the request.
     */
    @Override
    public String getID() {
        return this.requestID;
    }

    /**
     * Get the status of the request.
     *
     * @return the status of the request.
     */
    @Override
    public RequestStatus getStatus() {
        return this.requestStatus;
    }

    /**
     * Set the status of the request.
     *
     * @param status the status of the request.
     */
    @Override
    public void setStatus(RequestStatus status) {
        this.requestStatus = status;
    }

    @Override
    public String getDisplayableString() {
        return String.format("| %-18s | %-27s |\n", "Request ID", requestID) +
                String.format("| %-18s | %-27s |\n", "Request Type", requestType) +
                String.format("| %-18s | %-27s |\n", "Request Status", requestStatus) +
                String.format("| %-18s | %-27s |\n", "Project ID", projectID) +
                String.format("| %-18s | %-27s |\n", "Supervisor ID", supervisorID) +
                String.format("| %-18s | %-27s |\n", "Student ID", studentID) +
                String.format("| %-18s | %-27s |\n", "New Title", newTitle);
    }
}
