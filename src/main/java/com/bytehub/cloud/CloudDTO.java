package com.bytehub.cloud;

public class CloudDTO {

    private int file_idx;
    private int dept_idx;
    private String filename;

    public int getFile_idx() {
        return file_idx;
    }

    public void setFile_idx(int file_idx) {
        this.file_idx = file_idx;
    }

    public int getDept_idx() {
        return dept_idx;
    }

    public void setDept_idx(int dept_idx) {
        this.dept_idx = dept_idx;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
