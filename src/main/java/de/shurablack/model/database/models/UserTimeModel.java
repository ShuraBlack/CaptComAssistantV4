package de.shurablack.model.database.models;

public class UserTimeModel {

    private String userid;
    private long time_sec;

    public UserTimeModel() {
    }

    public UserTimeModel(String userid, long time_sec) {
        this.userid = userid;
        this.time_sec = time_sec;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public long getTime_sec() {
        return time_sec;
    }

    public void setTime_sec(long time_sec) {
        this.time_sec = time_sec;
    }

    @Override
    public String toString() {
        long seconds = this.time_sec;
        long day = seconds / (24 * 3600);

        seconds = seconds % (24 * 3600);
        long hour = seconds / 3600;

        seconds %= 3600;
        long minutes = seconds / 60 ;

        seconds %= 60;

        return String.format("%s%s%s%s",
                day == 0 ? "" : day + "d ",
                hour == 0 ? "" : hour + "h ",
                minutes == 0 && hour == 0 ? "" : minutes + "m ",
                seconds + "s"
        );
    }
}
