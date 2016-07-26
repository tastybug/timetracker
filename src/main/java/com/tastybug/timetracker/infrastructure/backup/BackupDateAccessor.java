package com.tastybug.timetracker.infrastructure.backup;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.google.common.base.Optional;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class BackupDateAccessor {

    private static final String TAG = BackupDateAccessor.class.getSimpleName();

    public BackupDateAccessor() {}

    public Optional<Date> readLastBackupDate(ParcelFileDescriptor state) {
        FileInputStream instream = null;
        try {
            instream = new FileInputStream(state.getFileDescriptor());
            DataInputStream in = new DataInputStream(instream);
            long timestamp = in.readLong();

            return Optional.of(new Date(timestamp));
        } catch (IOException ioe) {
            return Optional.absent();
        } finally {
            try {
                if (instream != null) {
                    instream.close();
                }
            } catch (IOException ioe2) {}
        }
    }

    public void writeBackupDate(ParcelFileDescriptor state) throws IOException {
        writeBackupDate(state, new Date());
    }

    public void writeBackupDate(ParcelFileDescriptor state, Date date) throws IOException {
        FileOutputStream outstream = null;
        try {
            outstream = new FileOutputStream(state.getFileDescriptor());
            DataOutputStream out = new DataOutputStream(outstream);

            out.writeLong(date.getTime());
            out.flush();
        } catch (IOException ioe) {
            Log.e(TAG, "Error while writing backup date: " + ioe.getMessage(), ioe);
        } finally {
            try {
                if (outstream != null) {
                    outstream.close();
                }
            } catch (IOException ioe2) {}
        }
    }
}
