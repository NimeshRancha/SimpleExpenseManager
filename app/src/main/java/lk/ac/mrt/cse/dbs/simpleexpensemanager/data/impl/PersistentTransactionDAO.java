package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private final DbHelper dbHelper;
    private final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us"));

    public PersistentTransactionDAO(DbHelper dbHelper){
        this.dbHelper=dbHelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();


        ContentValues cv = new ContentValues();
        cv.put(dbHelper.COL_DATE, date.toString());
        cv.put(dbHelper.COL_ACCOUNT_NO, accountNo);
        cv.put(dbHelper.COL_TYPE, expenseType.toString());
        cv.put(dbHelper.COL_AMOUNT, amount);

        database.insert(dbHelper.TRANSACTION_TABLE, null, cv);
        database.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> returnList = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String queryString = "SELECT * FROM " + dbHelper.TRANSACTION_TABLE;
        Cursor cursor = database.rawQuery(queryString,null);
        if (cursor.moveToFirst()){
            do {
                String accountNo = cursor.getString(cursor.getColumnIndex(dbHelper.COL_ACCOUNT_NO));
                String type = cursor.getString(cursor.getColumnIndex(dbHelper.COL_TYPE));
                ExpenseType expenseType;
                if (type.equals("EXPENSE")) {
                    expenseType = ExpenseType.EXPENSE;
                } else {
                    expenseType = ExpenseType.INCOME;
                }
                Date date = null;
                try {
                    String dateString = cursor.getString(cursor.getColumnIndex(dbHelper.COL_DATE));
                    date = sdf.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                /*String date = cursor.getString(cursor.getColumnIndex(dbHelper.COL_DATE));
                Date date1 = null;
                try {
                    date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
                double amount = cursor.getDouble(cursor.getColumnIndex(dbHelper.COL_AMOUNT));

                returnList.add(new Transaction(date, accountNo, expenseType, amount));


            }while(cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return returnList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> returnList = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String queryString = "SELECT * FROM " + dbHelper.TRANSACTION_TABLE + " ORDER BY " + dbHelper.COL_TRANSACTION_ID + " DESC LIMIT " + limit;
        Cursor cursor = database.rawQuery(queryString,null);
        if (cursor.moveToFirst()){
            do {
                String accountNo = cursor.getString(cursor.getColumnIndex(dbHelper.COL_ACCOUNT_NO));
                String type = cursor.getString(cursor.getColumnIndex(dbHelper.COL_TYPE));
                ExpenseType expenseType;
                if (type.equals("EXPENSE")) {
                    expenseType = ExpenseType.EXPENSE;
                } else {
                    expenseType = ExpenseType.INCOME;
                }

                Date date = null;
                try {
                    String dateString = cursor.getString(cursor.getColumnIndex(dbHelper.COL_DATE));
                    date = sdf.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                /*String date = cursor.getString(cursor.getColumnIndex(dbHelper.COL_DATE));
                Date date1 = null;
                try {
                    date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
                double amount = cursor.getDouble(cursor.getColumnIndex(dbHelper.COL_AMOUNT));

                returnList.add(new Transaction(date, accountNo, expenseType, amount));


            }while(cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return returnList;


    }
}
