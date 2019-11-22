package com.test.backupdemo.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

/**
 * Created by 潘华 on 2017/4/28.
 */

public class CMDUtils {

    private static final String TAG = "CMDUtils";

    /**
     * 执行命令
     *
     * @param command 命令
     * @retrun res
     */
    public static int runCMD(String command) {
        int result;
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command);
            os.flush();
            os.close();

            BufferedReader successResult = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            BufferedReader errorResult = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                Log.e(TAG, "successMsg:" + s);
            }
            while ((s = errorResult.readLine()) != null) {
                Log.e(TAG, "errorMsg:" + s);
            }

            successResult.close();
            errorResult.close();

            result = process.waitFor();
            process.destroy();
            return result;
        } catch (Exception e) {
            Log.e(TAG, "run CMD failed:" + e.toString());
            e.printStackTrace();
        }
        return -1;
    }

}
