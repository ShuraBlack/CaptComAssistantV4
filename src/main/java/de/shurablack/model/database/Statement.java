package de.shurablack.model.database;

import static de.shurablack.sql.FluentSqlBuilder.*;

public class Statement {

    /*-- Prepared Statments Ruler ------------------------------------------------------------------------------------*/

    public static String INSERT_RULER(String userid) {
        return create().INSERT("roles", VALUE(STR(userid), "false", NULL)).toString();
    }

    public static String SELECT_RULER(String userid) {
        return create().SELECT("*").FROM("roles").WHERE("userid",EQUAL,STR(userid)).toString();
    }

    public static String UPDATE_RULER(String userid, boolean request, String role) {
        return create().UPDATE("roles").SET("request="+request)
                .WHERE("userid",EQUAL,STR(userid)).APPEND(","+(role == null ? "null" : "'" + role + "'")).toString();
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    /*-- Prepared Statments Time -------------------------------------------------------------------------------------*/

    public static String INSERT_USER_TIME(String userid, long time_sec) {
        return create().INSERT("usertime", VALUE(STR(userid), "" + time_sec)).toString();
    }

    public static String SELECT_USER_TIME(String userid) {
        return create().SELECT("*").FROM("usertime").WHERE(CONDITION("userid",EQUAL,STR(userid))).toString();
    }

    public static String UPDATE_USER_TIME(String userid, long time_sec) {
        return create().UPDATE("usertime").SET("time_sec = time_sec + "+time_sec).WHERE("userid",EQUAL,STR(userid)).toString();
    }

    public static String SELECT_USER_TIME_TOP() {
        return create().SELECT("*").FROM("usertime").ORDER("time_sec",DESC).toString();
    }

}
