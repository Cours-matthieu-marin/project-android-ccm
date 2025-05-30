package fr.upjv.project_android_ccm.data.model;


import java.util.List;

public class User {
    private String id;
    private String email;
    private String pseudo;
    private String friendCode;
    private List<String> friendsList;

    public User(String id, String email, String pseudo, String friendCode, List<String> friendsList) {
        this.id = id;
        this.email = email;
        this.pseudo = pseudo;
        this.friendCode = friendCode;
        this.friendsList = friendsList;
    }
    public User() {
    }
    public User(String email, String pseudo, String friendCode) {
        this.email = email;
        this.pseudo = pseudo;
        this.friendCode = friendCode;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getFriendCode() {
        return friendCode;
    }

    public void setFriendCode(String friendCode) {
        this.friendCode = friendCode;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getFriendsList() {
        return friendsList;
    }

    public void addFriendCode(String friendCode){
        this.friendsList.add(friendCode);
    }
}
