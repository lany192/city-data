package com.github.lany192;

import java.util.List;



public class Area {
    /**
     * 地区编码
     */
    private String id;
    /**
     * 地区名称
     */
    private String name;
    /**
     * 该地区下辖地区
     */
    private List<Area> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Area> getChildren() {
        return children;
    }

    public void setChildren(List<Area> children) {
        this.children = children;
    }
}

