package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public static final String ACCOUNT_TABLE = "Account_Table";
    public static final String COL_ACCOUNT_NO = "Account_No";
    //public static final String COL_ACCOUNT_NO = ACCOUNT_NO;
    public static final String COL_BANK = "Bank";
    public static final String COL_ACCOUNT_HOLDER = "Account_Holder";
    public static final String COL_BALANCE = "Balance";
    public static final String COL_TYPE = "Type";
    public static final String COL_AMOUNT = "Amount";
    public static final String COL_DATE = "Date";
    public static final String COL_TRANSACTION_ID = "Transaction_ID";
    public static final String TRANSACTION_TABLE = "Transaction_Table";

    public DbHelper(@Nullable Context context) {
        super(context, "nimesh.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createAccountTable = "CREATE TABLE " + ACCOUNT_TABLE + "(" + COL_ACCOUNT_NO + " TEXT PRIMARY KEY, " + COL_BANK + " TEXT NOT NULL, " + COL_ACCOUNT_HOLDER + " TEXT NOT NULL, " + COL_BALANCE + " REAL NOT NULL )";
        db.execSQL(createAccountTable);

        String createTransactionTable = "CREATE TABLE " + TRANSACTION_TABLE + " (" + COL_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_ACCOUNT_NO + "TEXT NOT NULL, " + COL_TYPE + " TEXT NOT NULL, " + COL_AMOUNT + " REAL NOT NULL, " + COL_DATE + " TEXT NOT NULL," + "FOREIGN KEY("+COL_ACCOUNT_NO + ")REFERENCES "+ACCOUNT_TABLE+"(" + COL_ACCOUNT_NO +"))";
        db.execSQL(createTransactionTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String dropAccountTable = "DROP TABLE IF EXISTS " + ACCOUNT_TABLE;
        String dropTransactionTable = "DROP TABLE IF EXISTS " + TRANSACTION_TABLE;
        db.execSQL(dropAccountTable);
        db.execSQL(dropTransactionTable);
        onCreate(db);
    }
}
