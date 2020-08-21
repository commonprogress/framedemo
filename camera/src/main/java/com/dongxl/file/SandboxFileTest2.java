package com.dongxl.file;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * https://www.jianshu.com/p/cf7fb9e766e4 android 问答
 *
 * https://blog.csdn.net/zhendong_hu/article/details/104921985
 * 以下内容基于Android 10（Q），即 targetSdkVersion > 28 的应用
 * Android Q不再需要申请文件读写权限，默认可以读写自己沙盒文件和公共媒体文件。所以Q以上不需要再动态申请文件读写权限
 * 因一直的学习方向与Android Camera相关，所以代码示例以存储图像为主
 * apk安装路径为 /data/data/，沙盒路径 /sdcard/Android/data/xxx 不做操作安装的同时不会立即生成
 */
public class SandboxFileTest2 {
    private static final String TAG = SandboxFileTest2.class.getSimpleName();

    /**
     * storage location: sdcard/Android/data/packagename
     * 存储图像至沙盒
     *
     * @param context
     * @param fileName
     * @param image
     * @param environmentType
     * @param dirName
     */
    public static void saveImage2SandBox(Context context, String fileName, byte[] image, String environmentType, String dirName) {
        File standardDirectory;
        String dirPath;
        if (TextUtils.isEmpty(fileName) || 0 == image.length) {
            Log.e(TAG, "saveImage2SandBox: fileName is null or image is null!");
            return;
        }
        if (!TextUtils.isEmpty(environmentType)) {
            standardDirectory = context.getExternalFilesDir(environmentType);
        } else {
            standardDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }

        if (!TextUtils.isEmpty(dirName)) {
            dirPath = standardDirectory + "/" + dirName;
        } else {
            dirPath = String.valueOf(standardDirectory);
        }

        File imageFileDirctory = new File(dirPath);
        if (!imageFileDirctory.exists()) {
            if (!imageFileDirctory.mkdir()) {
                Log.e(TAG, "saveImage2SandBox: mkdir failed! Directory: " + dirPath);
                return;
            }
        }

//        if (queryImageFromSandBox(context, fileName, environmentType, dirName)) {
//            Log.e(TAG, "saveImage2SandBox: The file with the same name already exists！");
//            return;
//        }

        try {
            File imageFile = new File(dirPath + "/" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            fileOutputStream.write(image);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * storage location: sdcard/Android/data/packagename
     * 沙盒中加载图像
     *
     * @param context
     * @param fileName
     * @param environmentType
     * @param dirName
     * @return
     */
    public static byte[] loadImageFromSandBox(Context context, String fileName, String environmentType, String dirName) {
        String type;
        String dirPath;

        if (TextUtils.isEmpty(fileName)) {
            Log.e(TAG, "loadImageFromSandBox: fileName is null");
            return null;
        }

        if (!TextUtils.isEmpty(environmentType)) {
            type = environmentType;
        } else {
            type = Environment.DIRECTORY_PICTURES;
        }

        File standardDirectory = context.getExternalFilesDir(type);
        if (null == standardDirectory) {
            return null;
        }

        if (!TextUtils.isEmpty(dirName)) {
            dirPath = standardDirectory + "/" + dirName;
        } else {
            dirPath = String.valueOf(standardDirectory);
        }

        File direction = new File(dirPath);
        File[] files = direction.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();   // 此处的文件名不带路径
                if (file.isFile() && fileName.equals(name)) {
                    try {
                        InputStream inputStream = new FileInputStream(file);
                        byte[] image = new byte[inputStream.available()];
                        inputStream.read(image);
                        inputStream.close();
                        return image;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }


    /**
     * 存储图像至公共目录
     *
     * @param context
     * @param fileName just file name, not include path
     * @param image
     * @param subDir   sub direction name, not absolute path
     */
    public static void saveImage2Public(Context context, String fileName, byte[] image, String subDir) {
        String subDirection;
        if (!TextUtils.isEmpty(subDir)) {
            if (subDir.endsWith("/")) {
                subDirection = subDir.substring(0, subDir.length() - 1);
            } else {
                subDirection = subDir;
            }
        } else {
            subDirection = "DCIM";
        }

        Cursor cursor = searchImageFromPublic(context, subDir, fileName);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));                     // uri的id，用于获取图片
                Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                if (uri != null) {
                    OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                    if (outputStream != null) {
                        outputStream.write(image);
                        outputStream.flush();
                        outputStream.close();
                    }
                }
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //设置保存参数到ContentValues中
            ContentValues contentValues = new ContentValues();
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            //兼容Android Q和以下版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
                //RELATIVE_PATH是相对路径不是绝对路径
                //关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, subDirection);
                //contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Music/sample");
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
                    outputStream.write(image);
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 公共目录加载图像
     *
     * @param context
     * @param filePath relative path in Q, such as: "DCIM/" or "DCIM/dir_name/"
     *                 absolute path before Q
     * @return
     */
    public static byte[] loadImageFromPublic(Context context, String filePath, String fileName) {
        Cursor cursor = searchImageFromPublic(context, filePath, fileName);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                //循环取出所有查询到的数据
                do {
                    //一张图片的基本信息
                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));                     // uri的id，用于获取图片
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH));   // 图片的相对路径
                    String type = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));       // 图片类型
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));    // 图片名字
                    Log.d(TAG, "loadImageFromPublic: id = " + id);
                    Log.d(TAG, "loadImageFromPublic: name = " + name);
                    //根据图片id获取uri，这里的操作是拼接uri
                    Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
                    //官方代码：
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    if (uri != null) {
                        byte[] image;
                        InputStream inputStream = context.getContentResolver().openInputStream(uri);
                        if (null == inputStream || 0 == inputStream.available()) {
                            return null;
                        }
                        image = new byte[inputStream.available()];
                        inputStream.read(image);
                        inputStream.close();
                        return image;
                    }
                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公共目录删除图像
     *
     * @param context
     * @param filePath relative path in Q, such as: "DCIM/" or "DCIM/dir_name/"
     *                 absolute path before Q
     * @return
     */
    public static void deleteImageFromPublic(Context context, String filePath, String fileName) {
        Cursor cursor = searchImageFromPublic(context, filePath, fileName);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));                     // uri的id，用于获取图片
//                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH));   // 图片的相对路径
//                String type = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));       // 图片类型
//                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));    // 图片名字
                context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID + " LIKE ?", new String[]{String.valueOf(id)});
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * 公共目录查询图像
     *
     * @param context
     * @param filePath relative path in Q, such as: "DCIM/" or "DCIM/dir_name/"
     *                 absolute path before Q
     * @return
     */
    private static Cursor searchImageFromPublic(Context context, String filePath, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.e(TAG, "searchImageFromPublic: fileName is null");
            return null;
        }
        if (TextUtils.isEmpty(filePath)) {
            filePath = "DCIM/";
        } else {
            if (!filePath.endsWith("/")) {
                filePath = filePath + "/";
            }
        }

        //兼容androidQ和以下版本
        String queryPathKey = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q ? MediaStore.Images.Media.RELATIVE_PATH : MediaStore.Images.Media.DATA;
        String selection = queryPathKey + "=? and " + MediaStore.Images.Media.DISPLAY_NAME + "=?";
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID, queryPathKey, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.DISPLAY_NAME},
                selection,
                new String[]{filePath, fileName},
                null);

        return cursor;
    }

    /**
     * 读写普通文件，例如txt
     * 存储普通文件至公共目录
     *
     * @param context
     * @param fileName just file name, not include path
     * @param image
     * @param subDir   sub direction name, not absolute path
     */
    public static void saveTxt2Public(Context context, String fileName, String content, String subDir) {
        String subDirection;
        if (!TextUtils.isEmpty(subDir)) {
            if (subDir.endsWith("/")) {
                subDirection = subDir.substring(0, subDir.length() - 1);
            } else {
                subDirection = subDir;
            }
        } else {
            subDirection = "Documents";
        }

        Cursor cursor = searchTxtFromPublic(context, subDir, fileName);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                Uri uri = Uri.withAppendedPath(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), "" + id);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), id);
                if (uri != null) {
                    OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                    if (outputStream != null) {
                        outputStream.write(content.getBytes());
                        outputStream.flush();
                        outputStream.close();
                    }
                }
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, subDirection);
            } else {

            }
            //设置文件类型
            contentValues.put(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_NONE);
            Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), contentValues);
            if (uri != null) {
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    outputStream.write(content.getBytes());
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 公共目录查询普通文件
     *
     * @param context
     * @param filePath relative path in Q, such as: "DCIM/" or "DCIM/dir_name/"
     *                 absolute path before Q
     * @return
     */
    private static Cursor searchTxtFromPublic(Context context, String filePath, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.e(TAG, "searchTxtFromPublic: fileName is null");
            return null;
        }
        if (!filePath.endsWith("/")) {
            filePath = filePath + "/";
        }

        String queryPathKey = MediaStore.Files.FileColumns.RELATIVE_PATH;
        String selection = queryPathKey + "=? and " + MediaStore.Files.FileColumns.DISPLAY_NAME + "=?";
        Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                new String[]{MediaStore.Files.FileColumns._ID, queryPathKey, MediaStore.Files.FileColumns.DISPLAY_NAME},
                selection,
                new String[]{filePath, fileName},
                null);

        return cursor;
    }

    /**
     * storage location: /data/data/packagename
     * 安装路径加载图像
     *
     * @param filePath
     * @return
     */
    public static byte[] loadImageFromSandBox2(String filePath) {
        byte[] image = null;
        try {
            InputStream inputStream = new FileInputStream(filePath);
            image = new byte[inputStream.available()];
            inputStream.read(image);
            inputStream.close();
            return image;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * storage location: /data/data/packagename
     * 存储图像至安装路径
     *
     * @param filePath
     * @param image
     */
    public static void saveImage2SandBox2(String filePath, byte[] image) {
        try {
            File imageFile = new File(filePath);
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            fileOutputStream.write(image);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
