package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;



import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private final DbHelper dbHelper;

    public PersistentAccountDAO(DbHelper dbHelper){
        this.dbHelper=dbHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {

        List<String> returnList = new ArrayList<>();

        String queryString = "SELECT " + dbHelper.COL_ACCOUNT_NO + " FROM " + dbHelper.ACCOUNT_TABLE;

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            do{

                String newAccountNo = cursor.getString(cursor.getColumnIndex(dbHelper.COL_ACCOUNT_NO));   //check here
                returnList.add(newAccountNo);
            }while(cursor.moveToNext());
        }
        else{
            System.out.println("No account numbers in the list");
        }
        cursor.close();
        database.close();
        return returnList;
    }

    @Override
    public List<Account> getAccountsList() {

        List<Account> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + dbHelper.ACCOUNT_TABLE;

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            do{

                String account_no = cursor.getString(cursor.getColumnIndex(dbHelper.COL_ACCOUNT_NO));
                String bank  = cursor.getString(cursor.getColumnIndex(dbHelper.COL_BANK));
                String acc_holder  = cursor.getString(cursor.getColumnIndex(dbHelper.COL_ACCOUNT_HOLDER));
                Double balance  = cursor.getDouble(cursor.getColumnIndex(dbHelper.COL_BALANCE));
                Account newAccount = new Account(account_no,bank,acc_holder,balance);
                returnList.add(newAccount);
            }while(cursor.moveToNext());
        }
        else{
            System.out.println("No accounts in the list");
        }
        cursor.close();
        database.close();
        return returnList;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        String queryString = "SELECT * FROM " + dbHelper.ACCOUNT_TABLE + " WHERE " + dbHelper.COL_ACCOUNT_NO + " = " + accountNo;

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(queryString, null);

        if (!cursor.moveToFirst()) {
            String msg = "Invalid account number";
            throw new InvalidAccountException(msg);
        }

        String bank = cursor.getString(cursor.getColumnIndex(dbHelper.COL_BANK));
        String acc_holder = cursor.getString(cursor.getColumnIndex(dbHelper.COL_ACCOUNT_HOLDER));
        Double balance = cursor.getDouble(cursor.getColumnIndex(dbHelper.COL_BALANCE));

        Account account= new Account(accountNo,bank,acc_holder,balance);

        cursor.close();
        database.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();


        cv.put(dbHelper.COL_ACCOUNT_NO, account.getAccountNo());
        cv.put(dbHelper.COL_BANK, account.getBankName());
        cv.put(dbHelper.COL_ACCOUNT_HOLDER, account.getAccountHolderName());
        cv.put(dbHelper.COL_BALANCE, account.getBalance());

        database.insert(dbHelper.ACCOUNT_TABLE, null, cv);
        database.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String[] parameters = {accountNo};
        String queryString = "DELETE FROM " + dbHelper.ACCOUNT_TABLE + " WHERE " + dbHelper.COL_ACCOUNT_NO + "= ?";
        Cursor cursor = database.rawQuery(queryString, parameters);
        database.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String[] parameters = {accountNo};
        String queryString = "SELECT " + dbHelper.COL_BALANCE + " FROM " + dbHelper.ACCOUNT_TABLE + " WHERE " + dbHelper.COL_ACCOUNT_NO + " = ?" ;
        Cursor cursor = database.rawQuery(queryString,parameters);

        if (!cursor.moveToFirst()) {
            String msg = "Invalid account number";
            throw new InvalidAccountException(msg);
        }

        double currentBalance = cursor.getDouble(cursor.getColumnIndex(dbHelper.COL_BALANCE));

        switch (expenseType){
            case EXPENSE:
                currentBalance-=amount;
                break;
            case INCOME:
                currentBalance+=amount;
                break;

        }

        ContentValues cv = new ContentValues();
        cv.put(dbHelper.COL_BALANCE, currentBalance);
        database.update(dbHelper.ACCOUNT_TABLE,cv, dbHelper.COL_ACCOUNT_NO + " = ?" , parameters);
        cursor.close();
        database.close();
    }
}
