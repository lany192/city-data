package com.github.lany192;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainGetData {
    public static void main(String[] args) {
        try {
            //2020年11月中华人民共和国县以上行政区划代码网页
            Document doc = Jsoup.connect("http://preview.www.mca.gov.cn/article/sj/xzqh/2020/2020/202101041104.html").maxBodySize(0).get();
            Elements elements = doc.getElementsByClass("xl7024734");
            //省和市
            Elements elementsProAndArea = doc.getElementsByClass("xl7124734");
            List<String> stringListProAndArea = elementsProAndArea.eachText();
            List<String> stringList = elements.eachText();
            List<String> stringName = new ArrayList<>();
            List<String> stringCode = new ArrayList<>();
            stringListProAndArea.addAll(stringList);
            for (int i = 0; i < stringListProAndArea.size(); i++) {
                if (i % 2 == 0) {
                    //地区代码
                    stringCode.add(stringListProAndArea.get(i));
                } else {
                    //地区名字
                    stringName.add(stringListProAndArea.get(i));
                }
            }
            //正常情况 两个 list size 应该 一样
            System.out.println("stringName  size= " + stringName.size() + "   stringCode   size= " + stringCode.size());
            if (stringName.size() != stringCode.size()) {
                throw new RuntimeException("数据错误");
            }
            List<Area> provinceList = processData(stringName, stringCode);
            String path = FileUtils.getProjectDir() + "/2020年11月中华人民共和国县以上行政区划代码" + ".json";
            JSONFormatUtils.jsonWriter(provinceList, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成省份列表数据
     */
    private static List<Area> processData(List<String> stringName, List<String> stringCode) {
        List<Area> provinceList = new ArrayList<>();
        //获取省
        for (int i = 0; i < stringCode.size(); i++) {
            String provinceName = stringName.get(i);
            String provinceCode = stringCode.get(i);
            if (provinceCode.endsWith("0000")) {
                Area province = new Area();
                province.setCode(provinceCode);
                province.setName(provinceName);
                provinceList.add(province);
                List<Area> cities = new ArrayList<>();
                province.setChildren(cities);
            }
        }


        //获取市
        for (Area value : provinceList) {
            String provinceName = value.getName();
            String provinceCode = value.getCode();
            //直辖市 城市和省份名称一样
            if (provinceName.contains("北京") || provinceName.contains("上海") || provinceName.contains("天津") || provinceName.contains("重庆")) {
                Area city = new Area();
                List<Area> areas = new ArrayList<>();
                city.setName(provinceName);
                city.setCode(provinceCode);
                city.setChildren(areas);
                value.getChildren().add(city);
            } else {
                for (int j = 0; j < stringCode.size(); j++) {
                    String cityName = stringName.get(j);
                    String cityCode = stringCode.get(j);
                    if (!cityCode.equals(provinceCode)) {
                        if (cityCode.startsWith(provinceCode.substring(0, 2))) {
                            if (cityCode.endsWith("00")) {
                                Area city = new Area();
                                List<Area> areas = new ArrayList<>();
                                city.setName(cityName);
                                city.setCode(cityCode);
                                city.setChildren(areas);
                                value.getChildren().add(city);
                            }
                        }
                    }
                }
            }
        }


        //获取区县
        for (Area province : provinceList) {
            List<Area> cities = province.getChildren();
            for (Area city : cities) {
                //遍历获取县区
                String cityCode = city.getCode();
                String cityName = city.getName();
                for (int k = 0; k < stringCode.size(); k++) {
                    String areaName = stringName.get(k);
                    String areaCode = stringCode.get(k);
                    if (cityName.contains("北京") || cityName.contains("上海") || cityName.contains("天津") || cityName.contains("重庆")) {
                        if (!province.getCode().equals(areaCode) && areaCode.startsWith(province.getCode().substring(0, 2))) {
                            Area area = new Area();
                            area.setName(areaName);
                            area.setCode(areaCode);
                            city.getChildren().add(area);
                        }
                    } else {
                        if (!areaCode.equals(cityCode) && areaCode.startsWith(cityCode.substring(0, 4))) {
                            Area area = new Area();
                            area.setName(areaName);
                            area.setCode(areaCode);
                            city.getChildren().add(area);
                        }
                    }

                }

            }
        }


        //已经处理的数据移除
        List<String> stringNameList = new ArrayList<>(stringName);
        List<String> stringCodeList = new ArrayList<>(stringCode);
        for (Area province : provinceList) {
            stringNameList.remove(province.getName());
            stringCodeList.remove(province.getCode());
            List<Area> cities = province.getChildren();
            for (Area city : cities) {
                stringNameList.remove(city.getName());
                stringCodeList.remove(city.getCode());
                List<Area> listArea = city.getChildren();
                for (Area area : listArea) {
                    stringNameList.remove(area.getName());
                    stringCodeList.remove(area.getCode());
                }
            }
        }

        //处理石河子 特殊 市，Area Code 不以00结尾
        for (Area province : provinceList) {
            for (int k = 0; k < stringCodeList.size(); k++) {
                if (stringCodeList.get(k).startsWith(province.getCode().substring(0, 2))) {
                    Area city = new Area();
                    List<Area> areas = new ArrayList<>();
                    city.setName(stringNameList.get(k));
                    city.setCode(stringCodeList.get(k));
                    city.setChildren(areas);
                    province.getChildren().add(city);
                }
            }
        }

        return provinceList;
    }
}
