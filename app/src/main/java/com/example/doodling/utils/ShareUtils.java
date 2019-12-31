package com.example.doodling.utils;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.doodling.androidutils.AppUtils;
import com.example.doodling.androidutils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ShareUtils {
    public static final String PACKAGE_WECHAT = "com.tencent.mm";
    public static final String PACKAGE_BAIDU_DISK = "com.baidu.netdisk";


    /**
     * 分享单张图片到微信好友
     *
     * @param context context
     * @param picFile 要分享的图片文件
     */
    public static void sharePictureToWechatFriend(Context context, File picFile) {
        if (AppUtils.isInstallApp(context, PACKAGE_WECHAT)) {
            Intent intent = new Intent();
            ComponentName cop = new ComponentName(PACKAGE_WECHAT, "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(cop);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            if (picFile != null) {
                if (picFile.isFile() && picFile.exists()) {
                    Uri uri = Uri.fromFile(picFile);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                }
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(intent, "sharePictureToWechatFriend"));
        } else {
            ToastUtils.showLongToast("请先安装微信客户端");
        }
    }
    /**
     * 分享单张图片到微信好友
     *
     * @param context context
     * @param picFile 要分享的图片文件
     */
    public static void sharePictureToBaiduDisk(Context context, File picFile) {
        if (AppUtils.isInstallApp(context, PACKAGE_BAIDU_DISK)) {
            Intent intent = new Intent();
            ComponentName cop = new ComponentName(PACKAGE_BAIDU_DISK, "com.baidu.netdisk.ui.EnterShareFileActivity");
            intent.setComponent(cop);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            if (picFile != null) {
                if (picFile.isFile() && picFile.exists()) {
                    Uri uri = Uri.fromFile(picFile);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                }
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(intent, "savePictureToBaiduNetDisk"));
        } else {
            ToastUtils.showLongToast("请先安装百度网盘");
        }
    }

    /**
     * 分享多张图片到朋友圈
     *
     * @param context context
     * @param files   图片集合
     */
    public static void shareMultiplePictureToTimeLine(Context context, List<File> files) {
        if (AppUtils.isInstallApp(context, PACKAGE_WECHAT)) {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName(PACKAGE_WECHAT, "sharePictureToWechatFriend");
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");

            ArrayList<Uri> imageUris = new ArrayList<>();
            for (File f : files) {
                imageUris.add(Uri.fromFile(f));
            }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            intent.putExtra("Kdescription", "shareMultiplePictureToTimeLine");
            context.startActivity(intent);
        } else {
            ToastUtils.showLongToast("请先安装微信客户端");
        }
    }
}