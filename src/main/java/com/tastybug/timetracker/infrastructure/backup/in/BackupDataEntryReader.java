package com.tastybug.timetracker.infrastructure.backup.in;

import android.app.backup.BackupDataInput;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BackupDataEntryReader {

    BackupDataEntryReader() {}

    protected byte[] getPayloadFromBackupData(BackupDataInput data) throws IOException {
        byte[] payload = new byte[data.getDataSize()];
        data.readEntityData(payload, 0, data.getDataSize());
        ByteArrayInputStream baStream = new ByteArrayInputStream(payload);
        baStream.close();

        return payload;
    }

}
