package fr.upjv.project_android_ccm.data.model;

public class Travel {
    private String name;
    private String idUser;

    public Travel(String name, String idUser) {
        this.name = name;
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
