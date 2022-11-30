package com.compass.ux.entity;

import java.util.List;

import dji.sdk.media.MediaFile;

public class MyGallyData {
    private MediaFile mediaFile;
    private boolean isChecked=false;

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
