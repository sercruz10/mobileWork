package com.nodomain.freeyourgadget.gadgetbridge.export;

import android.support.annotation.NonNull;

import com.nodomain.freeyourgadget.gadgetbridge.model.ActivityTrack;

import java.io.File;
import java.io.IOException;

public interface ActivityTrackExporter {
    @NonNull
    String getDefaultFileName(@NonNull ActivityTrack track);

    void performExport(ActivityTrack track, File targetFile) throws IOException, GPXTrackEmptyException;

    class GPXTrackEmptyException extends Exception {
    }
}
