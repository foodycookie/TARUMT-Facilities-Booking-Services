package entity;

import java.io.Serializable;

/**
 *
 * @author TAY TIAN YOU
 */


public class User implements Comparable<User>, Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String studentId;
    private String studentName;

    public User() {
    }

    public User(String userId, String studentId, String studentName) {
        this.userId = userId;
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public String getUserId() {
        return userId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public int compareTo(User other) {
        return this.userId.compareTo(other.userId);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;

        User other = (User) obj;
        return this.userId != null && this.userId.equalsIgnoreCase(other.userId);
    }

    @Override
    public String toString() {
        return String.format("User ID: %s, Student ID:  %s, Name: %s", userId, studentId, studentName);
    }
}

