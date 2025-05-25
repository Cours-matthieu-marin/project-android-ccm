package fr.upjv.project_android_ccm.data.model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<Travel> filterActiveTravels(List<Travel> travels) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        LocalDateTime now = LocalDateTime.now();

        return travels.stream()
                .filter(travel -> {
                    try {
                        LocalDateTime start = LocalDateTime.parse(travel.dateStart, formatter);

                        if (travel.dateEnd == null || travel.dateEnd.isEmpty()) {
                            return !now.isBefore(start); // now >= start
                        } else {
                            LocalDateTime end = LocalDateTime.parse(travel.dateEnd, formatter);
                            return (!now.isBefore(start)) && now.isBefore(end); // start <= now < end
                        }
                    } catch (Exception e) {
                        // Ignorer les entrées mal formatées
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

}

