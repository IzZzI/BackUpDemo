package com.xposed;

import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;

import com.test.backupdemo.util.BackupUtils;

import org.xmlpull.v1.XmlPullParser;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;


/**
 * author ：zhouzy
 * createTime : 2019-08-09 17:25:54
 * what day : 星期五
 * describe : 备份
 */
public class BackupMod {

    public static void hook(final ClassLoader classLoader) {

        try {
            final Class<?> parserPackage = XposedHelpers.findClass("android.content.pm.PackageParser$Package", classLoader);
            XposedHelpers.findAndHookMethod("android.content.pm.PackageParser", classLoader, "parseApplication",
                    parserPackage, Resources.class, XmlPullParser.class, AttributeSet.class, int.class,
                    String[].class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            ApplicationInfo applicationInfo = (ApplicationInfo) XposedHelpers.getObjectField(param.args[0], "applicationInfo");
                            String str2 = applicationInfo.packageName;
                            applicationInfo.flags |= ApplicationInfo.FLAG_ALLOW_BACKUP;
                            applicationInfo.flags |= ApplicationInfo.FLAG_RESTORE_ANY_VERSION;
                            addLog("包名 ： " + str2 + "  flags : " + applicationInfo.flags);
                        }
                    });


            XposedHelpers.findAndHookMethod("android.app.ContextImpl", classLoader, "enforceCallingPermission",
                    String.class, String.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                            if ("android.permission.BACKUP".equals(methodHookParam.args[0])) {
                                addLog("enforceCallingPermission  replace : " + methodHookParam.args[1]);
                                return null;
                            } else {
                                addLog("enforceCallingPermission  : " + methodHookParam.args[0] + "  - " + methodHookParam.args[1]);
                                return XposedBridge.invokeOriginalMethod(methodHookParam.method, methodHookParam.thisObject,
                                        methodHookParam.args);
                            }
                        }
                    });


            XposedHelpers.findAndHookMethod("com.android.server.BackupManagerService", classLoader,
                    "startConfirmationUi", int.class, String.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                            addLog("action : " + methodHookParam.args[1] + "  token : " + methodHookParam.args[0]);
                            BackupUtils.acknowledgeFullBackupOrRestore((Integer) methodHookParam.args[0]);
                            return true;
                        }
                    });

        } catch (Exception e) {
            addLog("ex = " + e.getMessage());
        }
    }


    private static void addLog(String logMsg) {
        Log.e("BackupMod", logMsg);
    }
}
