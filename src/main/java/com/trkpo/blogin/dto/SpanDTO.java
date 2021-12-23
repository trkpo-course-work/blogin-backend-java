package com.trkpo.blogin.dto;

public class SpanDTO {
    private String type;
    private Integer start;
    private Integer end;

    public SpanDTO() {
    }

    public SpanDTO(String type, Integer start, Integer end) {
        this.type = type;
        this.start = start;
        this.end = end;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }
}
