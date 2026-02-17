package com.E_Bird_Telecome.E_Bird_React_Backend.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "AddNewRequest")
public class AddNewRequest {

    @Id
    private String id;
    private String requestId;
    private String receivedVia;
    private LocalDate receivedDate;
    private String receivedTime;
    private String mainCategory;
    private String source;
    private String organization;
    private String requestInBrief;
    private String complaintType;
    private String group;
    private String designation;
    private String assignTo;
    private String assignToName;
    private String assignedBy;
    private String remarks;
    private String status = "Pending";
    private List<Attachment> attachments = new ArrayList<>();

    private String forwardedBy;
    private List<ForwardingHistory> forwardingHistory = new ArrayList<>();
    private String lastForwardedDate;

    public AddNewRequest() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getReceivedVia() {
        return receivedVia;
    }

    public void setReceivedVia(String receivedVia) {
        this.receivedVia = receivedVia;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(String receivedTime) {
        this.receivedTime = receivedTime;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRequestInBrief() {
        return requestInBrief;
    }

    public void setRequestInBrief(String requestInBrief) {
        this.requestInBrief = requestInBrief;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    public String getAssignToName() {
        return assignToName;
    }

    public void setAssignToName(String assignToName) {
        this.assignToName = assignToName;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getForwardedBy() {
        return forwardedBy;
    }

    public void setForwardedBy(String forwardedBy) {
        this.forwardedBy = forwardedBy;
    }

    public List<ForwardingHistory> getForwardingHistory() {
        return forwardingHistory;
    }

    public void setForwardingHistory(List<ForwardingHistory> forwardingHistory) {
        this.forwardingHistory = forwardingHistory;
    }

    public String getLastForwardedDate() {
        return lastForwardedDate;
    }

    public void setLastForwardedDate(String lastForwardedDate) {
        this.lastForwardedDate = lastForwardedDate;
    }


    public static class Attachment {
        private String cout;  // Base64 encoded file data
        private String title;
        private String size;
        private String attachName;

        public Attachment() {
        }

        public Attachment(String cout, String title, String size, String attachName) {
            this.cout = cout;
            this.title = title;
            this.size = size;
            this.attachName = attachName;
        }

        public String getCout() {
            return cout;
        }

        public void setCout(String cout) {
            this.cout = cout;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getAttachName() {
            return attachName;
        }

        public void setAttachName(String attachName) {
            this.attachName = attachName;
        }

        @Override
        public String toString() {
            return "Attachment{" +
                    "title='" + title + '\'' +
                    ", size='" + size + '\'' +
                    ", attachName='" + attachName + '\'' +
                    ", hasCout=" + (cout != null && !cout.isEmpty()) +
                    '}';
        }
    }

    // Inner class for ForwardingHistory
    public static class ForwardingHistory {
        private String timestamp;
        private String forwardedBy;
        private String forwardedFrom;
        private String forwardedTo;
        private String remarks;

        public ForwardingHistory() {
        }

        public ForwardingHistory(String timestamp, String forwardedBy, String forwardedFrom, String forwardedTo, String remarks) {
            this.timestamp = timestamp;
            this.forwardedBy = forwardedBy;
            this.forwardedFrom = forwardedFrom;
            this.forwardedTo = forwardedTo;
            this.remarks = remarks;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getForwardedBy() {
            return forwardedBy;
        }

        public void setForwardedBy(String forwardedBy) {
            this.forwardedBy = forwardedBy;
        }

        public String getForwardedFrom() {
            return forwardedFrom;
        }

        public void setForwardedFrom(String forwardedFrom) {
            this.forwardedFrom = forwardedFrom;
        }

        public String getForwardedTo() {
            return forwardedTo;
        }

        public void setForwardedTo(String forwardedTo) {
            this.forwardedTo = forwardedTo;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        @Override
        public String toString() {
            return "ForwardingHistory{" +
                    "timestamp='" + timestamp + '\'' +
                    ", forwardedBy='" + forwardedBy + '\'' +
                    ", forwardedFrom='" + forwardedFrom + '\'' +
                    ", forwardedTo='" + forwardedTo + '\'' +
                    ", remarks='" + remarks + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AddNewRequest{" +
                "id='" + id + '\'' +
                ", requestId='" + requestId + '\'' +
                ", status='" + status + '\'' +
                ", assignedBy='" + assignedBy + '\'' +
                ", assignTo='" + assignTo + '\'' +
                ", assignToName='" + assignToName + '\'' +
                ", forwardedBy='" + forwardedBy + '\'' +
                ", attachmentsCount=" + (attachments != null ? attachments.size() : 0) +
                '}';
    }
}