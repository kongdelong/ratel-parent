package com.ratel.modules.jsth.utils;

import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;

public class ImageProcess {

    public static void main(String[] args) throws IOException {
        File file = new File("/Users/dongchuang/Desktop/070419451029_files/");
        File[] files = file.listFiles();
        for (File fileFrom : files) {
            if (fileFrom.getName().indexOf("png") > 0) {
                File fileTo = new File("/Users/dongchuang/Desktop/070419451029_files/thumb/" + fileFrom.getName());
                Thumbnails.of(fileFrom)
                        .scale(0.4f)
                        .outputQuality(0.8f)
                        .toFile(fileTo);
            }
        }
    }
}
