package com.test.backupdemo;

import android.app.backup.IFullBackupRestoreObserver;
import android.os.RemoteException;
import android.util.Log;

/**
 * author ：
 * createTime : 2019-08-13 16:34:16
 * what day : 星期二
 * describe : TODO
 */
public class FullObserver extends IFullBackupRestoreObserver.Stub {


    public void onStartBackup() throws RemoteException {
        addLog("备份开始");
    }

    public void onBackupPackage(String name) throws RemoteException {
        addLog("备份名称 ： " + name);
    }

    public void onEndBackup() throws RemoteException {
        addLog("备份结束");
    }

    public void onStartRestore() throws RemoteException {
        addLog("恢复开始");
    }

    public void onRestorePackage(String name) throws RemoteException {
        addLog("恢复包名 ： " + name);
    }

    public void onEndRestore() throws RemoteException {
        addLog("恢复结束");
    }

    public void onTimeout() throws RemoteException {
        addLog("恢复超时");
    }

    private static void addLog(String logMsg) {
        Log.e("BackupMod", logMsg);
        }

}
