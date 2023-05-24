package com.cyberethik.convocapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ToLongSizeFile extends RuntimeException{
    private long size;

    public ToLongSizeFile(long size) {
        super(String.format("not found with: '%s'", String.valueOf(size)));
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
