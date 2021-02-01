package com.github.lany192;

import java.util.List;



public class Area {
    /**
     * 地区编码
     */
    private int id;
    /**
     * 地区名称
     */
    private String name;
    /**
     * 该地区下辖地区
     */
    private List<Area> subarea;

    public String getId() {
        return String.valueOf(id);
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Area> getSubarea() {
        return subarea;
    }

    public void setSubarea(List<Area> subarea) {
        this.subarea = subarea;
    }
}

