package com.example.xposedunshell;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class XposedUnshell implements IXposedHookLoadPackage {

    private static final String TAG = XposedUnshell.class.getSimpleName();

    private int dexIndex = 0;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final String packageName = getTargetPackageName();
        if (!lpparam.packageName.equals(packageName))
            return;

        findAndHookConstructor("com.android.dex.Dex", lpparam.classLoader, ByteBuffer.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object dexObject = param.thisObject;
                ByteBuffer data = (ByteBuffer) getObjectField(dexObject, "data");
                Log.i(TAG, "Dex.data:" + data.toString());

                String filePath = "/data/data/" + packageName + "/";
                write(filePath + dexIndex, data);
                dexIndex = dexIndex + 1;
            }
        });
    }

    private String getTargetPackageName() {
        String configFilePath = "/data/local/tmp/unshellConfig.txt";
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(configFilePath);
            byte[] temp = new byte[1024];
            int len;
            while((len = fileInputStream.read(temp)) > 0) {
                stringBuilder.append(new String(temp, 0, len));
            }
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString().trim();
    }

    private void write(String fileName, ByteBuffer byteBuffer) {
        FileOutputStream fileOutputStream = null;
        FileChannel fileChannel = null;
        try {
            fileOutputStream = new FileOutputStream(fileName);
            fileChannel = fileOutputStream.getChannel();
            fileChannel.write(byteBuffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert fileChannel != null;
            fileChannel.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
