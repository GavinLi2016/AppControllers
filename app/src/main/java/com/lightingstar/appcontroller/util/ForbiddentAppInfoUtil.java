package com.lightingstar.appcontroller.util;

import java.util.ArrayList;

public class ForbiddentAppInfoUtil {
    private static ArrayList<String> forbiddentPackages = new ArrayList<>();

    public static ArrayList<String> getForbiddentPackages(){
        return forbiddentPackages;
    }

    public static void add(String itemName){
        forbiddentPackages.add(itemName);
    }

    public static void remove(String itemName){
        forbiddentPackages.remove(itemName);
    }

    /**
     * 初始化需要禁止运行的app-查本地数据库
     */
    public static void initForbiddentPackages(){
        ArrayList<String> data =  FileOperationUtil.readFile();
        if (data != null) {
            forbiddentPackages = data;
        }
    }
}
