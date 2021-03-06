package com.nodomain.freeyourgadget.gadgetbridge.export;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Xml;

import com.nodomain.freeyourgadget.gadgetbridge.activities.HeartRateUtils;
import com.nodomain.freeyourgadget.gadgetbridge.model.ActivityPoint;
import com.nodomain.freeyourgadget.gadgetbridge.model.ActivityTrack;
import com.nodomain.freeyourgadget.gadgetbridge.model.GPSCoordinate;
import com.nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;
import com.nodomain.freeyourgadget.gadgetbridge.util.FileUtils;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class GPXExporter implements ActivityTrackExporter {
    private static final String NS_DEFAULT = "";
    private static final String NS_DEFAULT_URI = "http://www.topografix.com/GPX/1/1";
    private static final String NS_DEFAULT_PREFIX = "";
    private static final String NS_TRACKPOINT_EXTENSION = "gpxtpx";
    private static final String NS_TRACKPOINT_EXTENSION_URI = "http://www.garmin.com/xmlschemas/TrackPointExtension/v1";
    private static final String NS_XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";

    private String creator;
    private boolean includeHeartRate = true;
    private boolean includeHeartRateOfNearestSample = true;

    @NonNull
    @Override
    public String getDefaultFileName(@NonNull ActivityTrack track) {
        return FileUtils.makeValidFileName(track.getName());
    }

    @Override
    public void performExport(ActivityTrack track, File targetFile) throws IOException, GPXTrackEmptyException {
        String encoding = StandardCharsets.UTF_8.name();
        XmlSerializer ser = Xml.newSerializer();
        try {
            ser.setOutput(new FileOutputStream(targetFile), encoding);
            ser.startDocument(encoding, Boolean.TRUE);
            ser.setPrefix("xsi", NS_XSI_URI);
            ser.setPrefix(NS_TRACKPOINT_EXTENSION, NS_TRACKPOINT_EXTENSION_URI);
            ser.setPrefix(NS_DEFAULT_PREFIX, NS_DEFAULT);

            ser.startTag(NS_DEFAULT, "gpx");
            ser.attribute(NS_DEFAULT, "version", "1.1");
            ser.attribute(NS_DEFAULT, "creator", getCreator());
            ser.attribute(NS_XSI_URI, "schemaLocation", NS_DEFAULT_URI + " " + "http://www.topografix.com/GPX/1/1/gpx.xsd");

            exportMetadata(ser, track);
            exportTrack(ser, track);

            ser.endTag(NS_DEFAULT, "gpx");
            ser.endDocument();
        } finally {
            ser.flush();
        }
    }

    private void exportMetadata(XmlSerializer ser, ActivityTrack track) throws IOException {
        ser.startTag(NS_DEFAULT, "metadata");
        ser.startTag(NS_DEFAULT, "name").text(track.getName()).endTag(NS_DEFAULT, "name");

        ser.startTag(NS_DEFAULT, "author");
        ser.startTag(NS_DEFAULT, "name").text(track.getUser().getName()).endTag(NS_DEFAULT, "name");
        ser.endTag(NS_DEFAULT, "author");

        ser.startTag(NS_DEFAULT, "time").text(formatTime(new Date())).endTag(NS_DEFAULT, "time");

        ser.endTag(NS_DEFAULT, "metadata");
    }

    private String formatTime(Date date) {
        return DateTimeUtils.formatIso8601(date);
    }

    private void exportTrack(XmlSerializer ser, ActivityTrack track) throws IOException, GPXTrackEmptyException {
        ser.startTag(NS_DEFAULT, "trk");
        ser.startTag(NS_DEFAULT, "trkseg");

        List<ActivityPoint> trackPoints = track.getTrackPoints();
        String source = getSource(track);
        boolean atLeastOnePointExported = false;
        for (ActivityPoint point : trackPoints) {
            atLeastOnePointExported |= exportTrackPoint(ser, point, source, trackPoints);
        }

        if(!atLeastOnePointExported) {
            throw new GPXTrackEmptyException();
        }

        ser.endTag(NS_DEFAULT, "trkseg");
        ser.endTag(NS_DEFAULT, "trk");
    }

    private String getSource(ActivityTrack track) {
        return track.getDevice().getName();
    }

    private boolean exportTrackPoint(XmlSerializer ser, ActivityPoint point, String source, List<ActivityPoint> trackPoints) throws IOException {
        GPSCoordinate location = point.getLocation();
        if (location == null) {
            return false; // skip invalid points, that just contain hr data, for example
        }
        ser.startTag(NS_DEFAULT, "trkpt");
        ser.attribute(NS_DEFAULT, "lon", formatLocation(location.getLongitude()));
        ser.attribute(NS_DEFAULT, "lat", formatLocation(location.getLatitude()));
        ser.startTag(NS_DEFAULT, "ele").text(formatLocation(location.getAltitude())).endTag(NS_DEFAULT, "ele");
        ser.startTag(NS_DEFAULT, "time").text(formatTime(point.getTime())).endTag(NS_DEFAULT, "time");
        String description = point.getDescription();
        if (description != null) {
            ser.startTag(NS_DEFAULT, "desc").text(description).endTag(NS_DEFAULT, "desc");
        }
        //ser.startTag(NS_DEFAULT, "src").text(source).endTag(NS_DEFAULT, "src");

        exportTrackpointExtensions(ser, point, trackPoints);

        ser.endTag(NS_DEFAULT, "trkpt");

        return true;
    }

    private void exportTrackpointExtensions(XmlSerializer ser, ActivityPoint point, List<ActivityPoint> trackPoints) throws IOException {
        if (!includeHeartRate) {
            return;
        }

        int hr = point.getHeartRate();
        if (!HeartRateUtils.isValidHeartRateValue(hr)) {
            if (!includeHeartRateOfNearestSample) {
                return;
            }

            ActivityPoint closestPointItem = findClosestSensibleActivityPoint(point.getTime(), trackPoints);
            if(closestPointItem == null) {
                return;
            }

            hr = closestPointItem.getHeartRate();
            if (!HeartRateUtils.isValidHeartRateValue(hr)) {
                return;
            }
        }

        ser.startTag(NS_DEFAULT, "extensions");
        ser.setPrefix(NS_TRACKPOINT_EXTENSION, NS_TRACKPOINT_EXTENSION_URI);
        ser.startTag(NS_TRACKPOINT_EXTENSION_URI, "TrackPointExtension");
        ser.startTag(NS_TRACKPOINT_EXTENSION_URI, "hr").text(String.valueOf(hr)).endTag(NS_TRACKPOINT_EXTENSION_URI, "hr");
        ser.endTag(NS_TRACKPOINT_EXTENSION_URI, "TrackPointExtension");
        ser.endTag(NS_DEFAULT, "extensions");
    }

    private @Nullable
    ActivityPoint findClosestSensibleActivityPoint(Date time, List<ActivityPoint> trackPoints) {
        ActivityPoint closestPointItem = null;

        long lowestDifference = 60 * 2 * 1000; // minimum distance is 2min
        for (ActivityPoint pointItem : trackPoints) {
            int hrItem = pointItem.getHeartRate();
            if (HeartRateUtils.isValidHeartRateValue(hrItem)) {
                Date timeItem = pointItem.getTime();
                if (timeItem.after(time) || timeItem.equals(time)) {
                    break; // we assume that the given trackPoints are sorted in time ascending order (oldest first)
                }
                long difference = time.getTime() - timeItem.getTime();
                if (difference < lowestDifference) {
                    lowestDifference = difference;
                    closestPointItem = pointItem;
                }
            }
        }
        return closestPointItem;
    }

    private String formatLocation(double value) {
        return new BigDecimal(value).setScale(GPSCoordinate.GPS_DECIMAL_DEGREES_SCALE, RoundingMode.HALF_UP).toPlainString();
    }

    public String getCreator() {
        return creator; // TODO: move to some kind of BrandingInfo class
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setIncludeHeartRate(boolean includeHeartRate) {
        this.includeHeartRate = includeHeartRate;
    }

    public boolean isIncludeHeartRate() {
        return includeHeartRate;
    }
}
