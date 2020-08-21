package com.dongxl.file;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * https://blog.csdn.net/yehui928186846/article/details/101706238
 * Android 10  视频
 * //废弃方法
 * //不再用以下代码获取文件根目录了
 * Environment.getExternalStorageDirectory();
 * Environment.getExternalStoragePublicDirectory();
 */
public class SandboxFileTest1 {

    /* 是否可读 可写 */
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* 是否可读 */
    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPrivateAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        //获取沙盒下的文件目录
//沙盒下的图片文件夹
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("LOG_TAG", "Directory not created");
        }
        return file;
    }

    private String signImage = "signImage";

    //沙盒里新建文件夹和新建文件
    //将文件保存到沙盒中
//注意：
//1. 这里的文件操作不再需要申请权限
//2. 沙盒中新建文件夹只能再系统指定的子文件夹中新建
    public void saveSignImageBox(Context context, String fileName, Bitmap bitmap) {
        try {
            //图片沙盒文件夹
            File PICTURES = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFileDirctory = new File(PICTURES + "/" + signImage);
            if (imageFileDirctory.exists()) {
                File imageFile = new File(PICTURES + "/" + signImage + "/" + fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } else if (imageFileDirctory.mkdir()) {//如果该文件夹不存在，则新建
                //new一个文件
                File imageFile = new File(PICTURES + "/" + signImage + "/" + fileName);
                //通过流将图片写入
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (Exception e) {
        }
    }

    //查询沙盒中的指定图片
//先指定哪个沙盒子文件夹，再指定名称
    public Bitmap querySignImageBox(Context context, String environmentType, String fileName) {
        if (TextUtils.isEmpty(fileName)) return null;
        Bitmap bitmap = null;
        try {
            //指定沙盒文件夹
            File picturesFile = context.getExternalFilesDir(environmentType);
            if (picturesFile != null && picturesFile.exists() && picturesFile.isDirectory()) {
                File[] files = picturesFile.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory() && file.getName().equals(signImage)) {
                            File[] signImageFiles = file.listFiles();
                            if (signImageFiles != null) {
                                for (File signImageFile : files) {
                                    String signFileName = signImageFile.getName();
                                    if (signImageFile.isFile() && fileName.equals(signFileName)) {
                                        bitmap = BitmapFactory.decodeFile(signImageFile.getPath());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return bitmap;
    }


    //将文件保存到公共的媒体文件夹
    //这里的filepath不是绝对路径，而是某个媒体文件夹下的子路径，和沙盒子文件夹类似
//这里的filename单纯的指文件名，不包含路径
    public void saveSignImage(Context context, String filePath, String fileName, Bitmap bitmap) {
        try {
            //设置保存参数到ContentValues中
            ContentValues contentValues = new ContentValues();
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            //兼容Android Q和以下版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
                //RELATIVE_PATH是相对路径不是绝对路径
                //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/signImage");
                //contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Music/signImage");
            } else {
                contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
            }
            //设置文件类型
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG");
            //执行insert操作，向系统文件夹中添加文件
            //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (uri != null) {
                //若生成了uri，则表示该文件添加成功
                //使用流将内容写入该uri中即可
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } catch (Exception e) {
        }
    }

    //在公共文件夹下查询图片
//这里的filepath在androidQ中表示相对路径
//在androidQ以下是绝对路径
    public Bitmap querySignImage(Context context, String filePath) {
        try {
            //兼容androidQ和以下版本
            String queryPathKey = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q ? MediaStore.Images.Media.RELATIVE_PATH : MediaStore.Images.Media.DATA;
            //查询的条件语句
            String selection = queryPathKey + "=? ";
            //查询的sql
            //Uri：指向外部存储Uri
            //projection：查询那些结果
            //selection：查询的where条件
            //sortOrder：排序
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID, queryPathKey, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.DISPLAY_NAME},
                    selection,
                    new String[]{filePath},
                    null);

            //是否查询到了
            if (cursor != null && cursor.moveToFirst()) {
                //循环取出所有查询到的数据
                do {
                    //一张图片的基本信息
                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));//uri的id，用于获取图片
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH));//图片的相对路径
                    String type = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));//图片类型
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));//图片名字
                    //根据图片id获取uri，这里的操作是拼接uri
                    Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
                    //官方代码：
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    if (uri != null) {
                        //通过流转化成bitmap对象
                        InputStream inputStream = context.getContentResolver().openInputStream(uri);
                        return BitmapFactory.decodeStream(inputStream);
                    }
                } while (cursor.moveToNext());
            }
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
        }
        return null;
    }
}
