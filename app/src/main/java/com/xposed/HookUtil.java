package com.xposed;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * author ：
 * createTime : 2019-11-19 11:23:17
 * what day : 星期二
 * describe : TODO
 */
public class HookUtil implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        BackupMod.hook(loadPackageParam.classLoader);
    }
}
