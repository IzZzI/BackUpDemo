package com.test.backupdemo.util;

import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.util.Log;


import com.test.backupdemo.FullObserver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * author ：zhouzy
 * createTime : 2019-08-15 10:17:39
 * what day : 星期四
 * describe : 全息备份工具类
 */
public class BackupUtils {

    private static String backupBasePath = "/sdcard/test/";

    /**
     * 反射调用BackupManagerService.fullbackup
     * <p>
     * 备份文件（.ab后缀 ex: /sdcard/backup.ab）
     * 需要备份的包名集合
     */
    public static void backup(String mPkgName, String dirName) {
        addILog("backup start");
        backupSdcard(mPkgName, dirName);
        backupApk(mPkgName, dirName);
        addILog("backup end");
    }

    public static void restore(String mPkgName, String dirName) {
        addILog("restore start ");
        restoreSdcard(mPkgName, dirName);
        restoreApk(mPkgName, dirName);
    }

    /**
     * 反射调用BackupManagerService.fullbackup
     * <p>
     * 备份文件（.ab后缀 ex: /sdcard/backup.ab）
     * 需要备份的包名集合
     */
    private static void backupApk(String mPkgName, String dirName) {
        try {
            addILog("backup apk data");
            FileUtils.RecursionDeleteAllFileReName(new File(backupBasePath + mPkgName + "/backup/" + dirName));
            FileUtils.mkdirs(backupBasePath + mPkgName + "/backup/" + dirName);
            String backupFilePath = backupBasePath + mPkgName + "/backup/" + dirName + "/backup.ab";
            File file = new File(backupFilePath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            file = new File(backupFilePath);
            ArrayList pkgList = new ArrayList();
            pkgList.add(mPkgName);
            //原理是反射调用BackupManagerService.fullbackup
            Class<?> serviceManager = Class.forName("android.os.ServiceManager");
            Method getService = serviceManager.getMethod("getService", String.class);
            getService.setAccessible(true);
            IBinder iBinder = (IBinder) getService.invoke(null, "backup");
            Class<?> sub = Class.forName("android.app.backup.IBackupManager$Stub");
            Method asInterface = sub.getDeclaredMethod("asInterface", IBinder.class);
            asInterface.setAccessible(true);
            Object backupManager = asInterface.invoke(null, iBinder);
            if (backupManager == null) {
                addLog("backManager is null");
                return;
            }
            ParcelFileDescriptor parcelFileDescriptor = null;
            try {
                parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
                String[] strArr = new String[pkgList.size()];
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    Method[] methods = backupManager.getClass().getMethods();
                    int length = methods.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        Method method = methods[i];
                        if ("fullBackup".equals(method.getName())) {
                            //需要android.permission.BACKUP权限，hook权限检查
//                           //fullBackup: uid 10065 does not have android.permission.BACKUP.
//                            android.Manifest.permission.BACKUP

                            //fullBackup原方法及参数
                            boolean includeApks = false;
                            //obb文件
                            boolean includeObbs = false;
                            //sdcard文件
                            boolean includeShared = false;
                            //是否所有app
                            boolean doAllApps = false;
                            //是否包括系统
                            boolean includeSystem = false;
                            method.setAccessible(true);
                            method.invoke(backupManager, new Object[]{parcelFileDescriptor,
                                    Boolean.valueOf(includeApks), Boolean.valueOf(includeObbs),
                                    Boolean.valueOf(includeShared), Boolean.valueOf(doAllApps),
                                    Boolean.valueOf(includeSystem), (String[]) pkgList.toArray(strArr)});
                            break;
                        }
                        i++;
                    }
                } else {
                    //5.1方法有所不同，暂未适配
//                    backupManager.fullBackup(parcelFileDescriptor, z, false, false, false, false, false, true, (String[]) arrayList.toArray(strArr));
                }
            } catch (Exception e2) {
                addLog("Unable to invoke backup manager for backup" + e2.getMessage());
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e3) {
                    }
                }
            } finally {
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e4) {
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            addLog("ClassNotFoundException : " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            addLog("NoSuchMethodException : " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            addLog("IllegalAccessException : " + e.getMessage());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            addLog("InvocationTargetException : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 备份sdcard数据
     *
     * @param mPkgName 备份apk包名
     * @param dirName     备份设备dirName
     */
    private static void backupSdcard(String mPkgName, String dirName) {
        addILog("backup sdcard");
        FileUtils.RecursionDeleteAllFileReName(new File(backupBasePath + mPkgName + "/" + dirName));
        FileUtils.mkdirs(backupBasePath + mPkgName + "/" + dirName);
        File sdcard = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        if (sdcard.isDirectory()) {
            File[] childFile = sdcard.listFiles();
            for (File file1 : childFile) {
                if (!file1.exists() || file1.getAbsolutePath().contains("test")) {
                    continue;
                }
                if (file1.getAbsolutePath().contains("Android")) {
                    FileUtils.mkdirs("/sdcard/test/" + mPkgName + "/" + dirName + "/Android/data");
                    CMDUtils.runCMD("cp -r /sdcard/Android/data/" + mPkgName + " /sdcard/test/"
                            + mPkgName + "/" + dirName + "/Android/data");
                    addILog("copy : " + "/Android/data/data/");
                    continue;
                }
                CMDUtils.runCMD("cp -r " + "/sdcard/" + file1.getName() + " /sdcard/test/" + mPkgName + "/" + dirName);
                addILog("copy : " + file1.getName());
            }
        }
    }

    /**
     * @param mpkgName
     * @param dirName
     */
    private static void restoreSdcard(String mpkgName, String dirName) {
        addILog("restore sdcard");
        String baseDirPath = backupBasePath + mpkgName + "/" + dirName;
        File baseDir = new File(baseDirPath);
        if (baseDir != null && baseDir.length() > 0) {
            File[] files = baseDir.listFiles();
            for (File file : files) {
                CMDUtils.runCMD("cp -r " + baseDirPath + "/" + file.getName() + " /sdcard/");
                addILog("restore : " + file.getName());
            }

        } else {
            addLog("sdcard backup file is null");
        }
    }

    /**
     * 确认备份或恢复
     *
     * @param token startConfirmationUi(int token, String action)方法的token
     */
    public static void acknowledgeFullBackupOrRestore(int token) {
        Class<?> serviceManager = null;
        try {
            serviceManager = Class.forName("android.os.ServiceManager");

            Method getService = serviceManager.getMethod("getService", String.class);
            getService.setAccessible(true);
            IBinder iBinder = (IBinder) getService.invoke(null, "backup");
            Class<?> sub = Class.forName("android.app.backup.IBackupManager$Stub");
            Method asInterface = sub.getDeclaredMethod("asInterface", IBinder.class);
            asInterface.setAccessible(true);
            Object backupManager = asInterface.invoke(null, iBinder);
            if (backupManager != null) {
                addLog("backManager is not null");
                Class<?> observer = Class.forName("android.app.backup.IFullBackupRestoreObserver");
                Method acknowledgeFullBackupOrRestore = backupManager.getClass().getMethod(
                        "acknowledgeFullBackupOrRestore", int.class, boolean.class,
                        String.class, String.class, observer);
                addLog("acknowledge is not null");
                acknowledgeFullBackupOrRestore.setAccessible(true);
                acknowledgeFullBackupOrRestore.invoke(backupManager, token, true, "",
                        "", new FullObserver());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 恢复数据
     * <p>
     * 备份文件
     */
    public static void restoreApk(String mPkgName, String dirName) {
        try {
            addILog("restore apk");
            String backupFilePath = backupBasePath + mPkgName + "/backup/" + dirName + "/backup.ab";
            File file = new File(backupFilePath);
            if (!file.exists() || file.length() == 0) {
                addLog("restore error backup.ab is not exits or null");
                return;
            }
            Class<?> serviceManager = Class.forName("android.os.ServiceManager");
            Method getService = serviceManager.getMethod("getService", String.class);
            getService.setAccessible(true);
            IBinder iBinder = (IBinder) getService.invoke(null, "backup");
            Class<?> sub = Class.forName("android.app.backup.IBackupManager$Stub");
            Method asInterface = sub.getDeclaredMethod("asInterface", IBinder.class);
            asInterface.setAccessible(true);
            Object backupManager = asInterface.invoke(null, iBinder);
            if (backupManager == null) {
                addLog("backManager is null");
                return;
            }

            ParcelFileDescriptor parcelFileDescriptor = null;
            try {
                parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
                if (Build.VERSION.SDK_INT <= 19) {
                    Method[] methods = backupManager.getClass().getMethods();
                    int length = methods.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        Method method = methods[i];
                        if ("fullRestore".equals(method.getName())) {
//                           //fullBackup: uid 10065 does not have android.permission.BACKUP.
//                            android.Manifest.permission.BACKUP
//                            public void fullBackup(ParcelFileDescriptor fd, boolean includeApks,
//                                     boolean includeObbs, boolean includeShared,
//                            boolean doAllApps, boolean includeSystem, String[] pkgList

                            method.setAccessible(true);
                            method.invoke(backupManager, new Object[]{parcelFileDescriptor});
                            break;
                        }
                        i++;
                    }
                } else {
//                    backupManager.fullBackup(parcelFileDescriptor, z, false, false, false, false, false, true, (String[]) arrayList.toArray(strArr));
                }
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e) {
                    }
                }
            } catch (Exception e2) {
                Log.e("ContentValues", "Unable to invoke restore manager for restore", e2);
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e3) {
                    }
                }
            } finally {
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e4) {
                    }
                }
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    private static void addLog(String logMsg) {
        Log.e("BackupUtils", logMsg);
    }

    private static void addILog(String logMsg) {
        Log.i("BackupUtils", logMsg);
    }
}
