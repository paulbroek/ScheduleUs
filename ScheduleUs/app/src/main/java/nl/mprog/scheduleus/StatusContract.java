package nl.mprog.scheduleus;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Paul on 4-6-2015.
 */
public class StatusContract {
    // DB specific constants
    public static final String DB_NAME = "SheduleData.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "events";
    public static final String DEFAULT_SORT = Column.CREATED_AT + " DESC";
    public class Column {
        public static final String ID = BaseColumns._ID;
        public static final String USER = "user";
        public static final String MESSAGE = "message";
        public static final String CREATED_AT = "created_at";
    }
}