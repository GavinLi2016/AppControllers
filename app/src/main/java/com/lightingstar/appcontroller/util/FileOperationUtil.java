package com.lightingstar.appcontroller.util;

import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.file.FileIOUtils;
import com.xuexiang.xutil.file.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class FileOperationUtil {
    private static final  String FILE_NAME = "appcontroller.txt";
    public static void saveFile(ArrayList<String> data){
        if (data == null || data.size() == 0 ) return;

        StringBuilder content = new StringBuilder();
        for (String item : data){
            content.append(";"+item);
        }
        String filePath = FileUtils.getDiskDir()+File.separator + FILE_NAME;
        File file = FileUtils.isFileNotExistCreate(filePath);
        if (file != null){
            FileIOUtils.writeFileFromString(file,content.substring(1));
        }
    }

    public static ArrayList<String> readFile(){
        String content="";
        String filePath = FileUtils.getDiskDir()+File.separator + FILE_NAME;
        if (FileUtils.isFileExists(filePath)){
            content = FileIOUtils.readFile2String(filePath);
        }

        if (StringUtils.isEmpty(content)) return new ArrayList<>();

        String[] splitContent = content.split(";");
        ArrayList<String> appInfo = new ArrayList<>();

        for (String s:splitContent){
            appInfo.add(s);
        }

        return appInfo;
    }
}
