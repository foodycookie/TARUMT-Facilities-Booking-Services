package entity;

import java.io.Serializable;

public class User implements Serializable, Comparable<User>{
    private int userID;
    private String userName;
    
    public User() {}
    
    public User (int userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "User{" + "userID=" + userID + ", userName=" + userName + '}';
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(userID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        return this.userID == other.userID;
    }
    
    @Override
    public int compareTo(User o) {
        int result = Integer.compare(this.userID, o.userID);
        if (result != 0) {
            return result;
        }
        
        return this.userName.compareToIgnoreCase(o.userName);
    }
    
}
