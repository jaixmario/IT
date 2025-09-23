package com.mario.it;

import android.content.Context;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CrashLogger implements Thread.UncaughtExceptionHandler {

    private final Context context;
    private final Thread.UncaughtExceptionHandler defaultHandler;

    public CrashLogger(Context ctx) {
        this.context = ctx.getApplicationContext();
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            // Save to external files directory (visible in file manager)
            File logFile = new File(context.getExternalFilesDir(null), "crash_log.txt");
            PrintWriter writer = new PrintWriter(new FileWriter(logFile, true));
            writer.println("=== Crash at " + System.currentTimeMillis() + " ===");
            throwable.printStackTrace(writer);
            writer.println();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Give system default handler a chance (optional)
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, throwable);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }
}