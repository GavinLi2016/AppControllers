package com.lightingstar.appcontroller.util;

import com.xuexiang.xutil.file.FileIOUtils;
import com.xuexiang.xutil.file.FileUtils;

import java.io.File;
import java.util.HashSet;

public class FileOperationUtil {
    private static final  String FILE_NAME = "appcontroller.txt";
    public static void saveFile(HashSet<String> data){
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

    public static HashSet<String> readFile(){
        String content="";
        String filePath = FileUtils.getDiskDir()+File.separator + FILE_NAME;
        if (FileUtils.isFileExists(filePath)){
            content = FileIOUtils.readFile2String(filePath);
        }

        if (content == "") return null;

        String[] splitContent = content.split(";");
        HashSet<String> appInfo = new HashSet<>();

        for (String s:splitContent){
            appInfo.add(s);
        }

        return appInfo;
    }
}
