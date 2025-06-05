package fr.upjv.project_android_ccm.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import fr.upjv.project_android_ccm.data.model.UserLocation;

public class TravelExporter {

    private final List<UserLocation> locations;

    public TravelExporter(List<UserLocation> locations) {
        this.locations = locations;
    }

    public void exportAsGpx(Context context, String fileName) throws IOException {
        StringBuilder gpx = new StringBuilder();
        gpx.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        gpx.append("<gpx version=\"1.1\" creator=\"ProjectAndroid\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n");
        gpx.append("  <trk>\n");
        gpx.append("    <name>Trajet exporté</name>\n");
        gpx.append("    <trkseg>\n");

        for (UserLocation loc : locations) {
            gpx.append("      <trkpt lat=\"")
                    .append(loc.getLatitude())
                    .append("\" lon=\"")
                    .append(loc.getLongitude())
                    .append("\">");
            if (loc.getDate() != null) {
                gpx.append("<time>").append(loc.getDate()).append("</time>");
            }
            gpx.append("</trkpt>\n");
        }

        gpx.append("    </trkseg>\n");
        gpx.append("  </trk>\n");
        gpx.append("</gpx>\n");

        writeToFile(gpx.toString(), context, fileName);
    }

    public void exportAsKml(Context context, String fileName) throws IOException {
        StringBuilder kml = new StringBuilder();
        kml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        kml.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
        kml.append("  <Document>\n");
        kml.append("    <name>Trajet exporté</name>\n");
        kml.append("    <Placemark>\n");
        kml.append("      <LineString>\n");
        kml.append("        <coordinates>\n");

        for (UserLocation loc : locations) {
            kml.append("          ")
                    .append(loc.getLongitude())
                    .append(",")
                    .append(loc.getLatitude())
                    .append(",0\n");
        }

        kml.append("        </coordinates>\n");
        kml.append("      </LineString>\n");
        kml.append("    </Placemark>\n");
        kml.append("  </Document>\n");
        kml.append("</kml>\n");

        writeToFile(kml.toString(), context, fileName);
    }

    private void writeToFile(String content, Context context, String fileName) throws IOException {
        //File file = new File(context.getExternalFilesDir(null), fileName);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }

    public void openKmlInGoogleMaps(Context context, String fileName) {
        File file = new File(context.getExternalFilesDir(null), fileName);
        Uri uri = FileProvider.getUriForFile(context,
                context.getPackageName() + ".provider",
                file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.google-earth.kml+xml");
        intent.setPackage("com.google.android.apps.maps");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(intent);
    }

    public void sendFileByEmail(Context context, String fileName, String emailDestinataire) {
        File file = new File(context.getExternalFilesDir(null), fileName);
        Uri uri = FileProvider.getUriForFile(context,
                context.getPackageName() + ".provider",
                file);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/octet-stream");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailDestinataire});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Trajet exporté");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Voici le fichier de mon voyage.");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(emailIntent, "Envoyer le fichier avec..."));
    }
}
