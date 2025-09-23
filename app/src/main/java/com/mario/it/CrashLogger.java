package com.mario.it;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CrashLogger implements Thread.UncaughtExceptionHandler {

    private final Context context;
    private final Thread.UncaughtExceptionHandler defaultHandler;

    public CrashLogger(Context context) {
        this.context = context.getApplicationContext();
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            // Save crash log to internal storage
            File logFile = new File(context.getFilesDir(), "crash_log.txt");
            FileWriter writer = new FileWriter(logFile, true);
            PrintWriter pw = new PrintWriter(writer);
            pw.println("---- Crash ----");
            e.printStackTrace(pw);
            pw.close();

            Log.e("CrashLogger", "Crash captured: " + e.getMessage(), e);
            Toast.makeText(context, "App crashed! Log saved at: " + logFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Log.e("CrashLogger", "Error writing crash log", ex);
        }

        // Let the default handler continue (system crash dialog)
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(t, e);
        }
    }
}