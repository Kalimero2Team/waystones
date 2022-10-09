package com.kalimero2.waystones.api;

public class WayStonesApiHolder {

    private static WayStonesApi api;

    public static boolean setApi(WayStonesApi api){
        if (WayStonesApiHolder.api == null){
            WayStonesApiHolder.api = api;
            return true;
        }
        return false;
    }


    public static WayStonesApi getApi() {
        return api;
    }
}
