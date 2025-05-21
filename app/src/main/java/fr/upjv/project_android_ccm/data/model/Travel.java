package fr.upjv.project_android_ccm.data.model;

import java.time.LocalDateTime;
import java.util.Date;

public class Travel {
    private String id;
    private String name;
    private String idUser;
    private String dateStart;
    private String dateEnd;

    public Travel(String name, String idUser,String dateStart, String dateEnd) {
        this.name = name;
        this.idUser = idUser;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }
    public Travel() {}

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

    public String getId() {
        return id;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setId(String id) {
        this.id = id;
    }

}

