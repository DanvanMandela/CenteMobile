package com.craft.silicon.centemobile.data.source.constants;


import org.jetbrains.annotations.NotNull;

public class Constants {
    public static class Timeout {
        public static long connection = 60 * 10000;
        public static long read = 60 * 10000;
        public static long write = 60 * 10000;
    }

    public static class BaseUrl {

        @NotNull
        public static final String UAT = "https://uat.craftsilicon.com/ElmaAuthDynamic/";


        @NotNull
        public static final String LIVE = "https://uat.craftsilicon.com/ElmaAuthDynamic/";


    }

    public static class Data {
        @NotNull
        public static final String API_KEY = "8CC9432C-B5AD-471C-A77D-28088C695916";

        @NotNull
        public static final String COUNTRY = "UGANDA";

        public final static String APP_NAME = "CENTEMOBILE";


    }


    public static class RoomDatabase {
        public static final String DATABASE_NAME = "local-db";
    }
}
