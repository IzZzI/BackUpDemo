// IFullBackupRestoreObserver.aidl
package android.app.backup;

// Declare any non-default types here with import statements

interface IFullBackupRestoreObserver {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
   void onStartBackup();
   void onEndBackup();
   void onStartRestore();
   void onRestorePackage(String name);
   void onEndRestore();
   void onTimeout();
}
