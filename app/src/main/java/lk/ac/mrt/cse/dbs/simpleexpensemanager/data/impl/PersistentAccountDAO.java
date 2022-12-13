package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DbHandler.ACCOUNT_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DbHandler.ACCOUNT_TABLE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DbHandler.BALANCE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DbHandler.BANK_NAME;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DbHandler.HOLDER_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


public class PersistentAccountDAO implements AccountDAO {
    private final DbHandler handler;
    private SQLiteDatabase theDatabase;



    public PersistentAccountDAO(Context context) {
        handler = new DbHandler(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        theDatabase = handler.getReadableDatabase();
        String query = "select " + ACCOUNT_NO +" from " + ACCOUNT_TABLE ;

        Cursor cursor = theDatabase.rawQuery(query, null);

        List<String> AccNos = new ArrayList<String>();

        while (cursor.moveToNext()){
            int index = cursor.getColumnIndexOrThrow(ACCOUNT_NO); //get the index of the account_no
            String accNum = cursor.getString(index);
            AccNos.add(accNum);
        }
        //close the cursor
        cursor.close();
        return AccNos;


    };

    @Override
    public List<Account> getAccountsList() {
        theDatabase = handler.getReadableDatabase();
        String query = "select * from " + ACCOUNT_TABLE ;

        Cursor cursor = theDatabase.rawQuery(query , null);
        List<Account> accountList = new ArrayList<Account>();// to add the accounts


        while ( cursor.moveToNext()){
            int bank_name_index = cursor.getColumnIndex(BANK_NAME);
            int holder_name_index = cursor.getColumnIndex(HOLDER_NAME);
            int balance_index = cursor.getColumnIndex(BALANCE);
            int account_no_index = cursor.getColumnIndex(ACCOUNT_NO);

            //temporary account
            Account acc = new Account(cursor.getString(account_no_index), cursor.getString(bank_name_index),cursor.getString(holder_name_index),cursor.getDouble(balance_index));

            accountList.add(acc);
        }
        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        theDatabase =handler.getReadableDatabase();
        String query = "select * from "+ ACCOUNT_TABLE +" where "+ ACCOUNT_NO +" =? " ;
        String[] condition =  {accountNo};

        Cursor cursor = theDatabase.rawQuery(query , condition );

        //if there are no account related to the given accountNo
        if (cursor == null){
            String exceptionMassage = "Account "+ accountNo +" is invalid.";
            // throw the exception
            throw new InvalidAccountException(exceptionMassage);
        }
        else{
            cursor.moveToFirst();
            int bank_index = cursor.getColumnIndex(BANK_NAME);
            int holder_name_index = cursor.getColumnIndex(HOLDER_NAME);
            int balance_index = cursor.getColumnIndex(BALANCE);

            Account tempAccount = new Account( accountNo, cursor.getString(balance_index) , cursor.getString(holder_name_index) , cursor.getDouble(balance_index));
            return tempAccount;
        }
    }

    @Override
    public void addAccount(Account account) {
        theDatabase = handler.getWritableDatabase();

        //to store data which are to be inserted into the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_NO,account.getAccountNo());
        contentValues.put(BANK_NAME,account.getBankName()) ;
        contentValues.put(HOLDER_NAME,account.getAccountHolderName());
        contentValues.put(BALANCE,account.getBalance());

        //inserting the raw into the the account table
        theDatabase.insert(ACCOUNT_TABLE, null, contentValues);
        theDatabase.close();

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        //deleting the row in the Account table
        theDatabase = handler.getWritableDatabase();
        theDatabase.delete(ACCOUNT_TABLE, ACCOUNT_NO + " =? " ,new String[]{accountNo});
        theDatabase.close();

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        theDatabase = handler.getWritableDatabase();

        //the qurey and the condition(constrain)
        String qurey = "select "+ BALANCE + " from " +ACCOUNT_TABLE  +" where "+ ACCOUNT_NO + " =?";
        String[] constrain =  {accountNo};

        Cursor cursor = theDatabase.rawQuery(qurey,constrain);

        double temp_balance ;
        if (cursor.moveToNext()){
            // if there is a row correspond to that account number
            temp_balance = cursor.getDouble(0);
        }
        else {
            String error = "Account "+ accountNo + " is invalied.";
            throw new InvalidAccountException(error);
        }

        String whereClause = ACCOUNT_NO + " =?";
        ContentValues ChangedContent = new ContentValues();
        switch (expenseType){
            case EXPENSE:
                temp_balance = temp_balance + amount;
                ChangedContent.put(BALANCE, temp_balance);
                break;

            case INCOME:
                temp_balance = temp_balance - amount;
                ChangedContent.put(BALANCE ,temp_balance);
                break;
        }

        String[] condition = {accountNo};
        theDatabase.update(ACCOUNT_TABLE , ChangedContent ,whereClause, condition );

        cursor.close();
        theDatabase.close();





    }
}
