package fr.upjv.project_android_ccm.data.model;

import java.time.LocalDateTime;
import java.util.Date;

public class Travel {
    private String id;
    private String name;
    private String idUser;
    private LocalDateTime dateStart;
    private LocalDateTime dateEnd;

    public Travel(String name, String idUser, LocalDateTime dateStart, LocalDateTime dateEnd) {
        this.name = name;
        this.idUser = idUser;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
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

    public String getId() {
        return id;
    }

    public LocalDateTime getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDateTime dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDateTime getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDateTime dateEnd) {
        this.dateEnd = dateEnd;
    }
}
