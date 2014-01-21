package com.example.ndndroid;

interface NDNBackgroundServiceApi {
 
    String startNDNBackgroundService();
    String addNewConnection(String mac, String prefix);
    String runNdnldcCommand(String command);
    boolean resetServices();
    void stopServices();
}