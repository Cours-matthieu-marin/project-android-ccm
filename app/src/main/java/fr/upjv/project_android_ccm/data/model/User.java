package fr.upjv.project_android_ccm.data.model;

public class User {
    private String pseudo;
    private String friendCode;

    public User(String pseudo, String friendCode) {
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
}
