package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DbHandler.ACCOUNT_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DbHandler.DATE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DbHandler.AMOUNT;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DbHandler.AMOUNT;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DbHandler.EXPENSE_TYPE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DbHandler.TRANSACTION_TABLE;



public class PersistentTransactionDAO implements TransactionDAO {
    private final DbHandler handler;
    private SQLiteDatabase theDatabase;





    //contructor
    public PersistentTransactionDAO(Context context) {
        handler = new DbHandler(context);

    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        theDatabase = handler.getWritableDatabase();
        DateFormat dateformat = new SimpleDateFormat ("dd-MM-yyyy");
        ContentValues contentValues = new ContentValues();

        //store the details
        contentValues.put(ACCOUNT_NO,accountNo);
        contentValues.put(EXPENSE_TYPE, String.valueOf(expenseType));
        contentValues.put(AMOUNT,amount);
        contentValues.put(DATE , dateformat.format(date));

        //inserting the new row to the transaction table
        theDatabase.insert(TRANSACTION_TABLE , null , contentValues);
        theDatabase.close();

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactionList = new ArrayList<Transaction>();

        theDatabase =handler.getReadableDatabase();

        String query = "select * from "+ TRANSACTION_TABLE ;
        Cursor cursor = theDatabase.rawQuery(query,null);

        while (cursor.moveToNext()){
            int DATE_COLUMN_INDEX = cursor.getColumnIndex(DATE);
            String temp_date = cursor.getString(DATE_COLUMN_INDEX);
            int ACCOUNR_NO_COLUMN_INDEX = cursor.getColumnIndex(ACCOUNT_NO);
            int EXPENSE_TYPE_COLUMN_INDEX = cursor.getColumnIndex(EXPENSE_TYPE);
            int AMOUNT_COLUMN_INDEX = cursor.getColumnIndex(AMOUNT);

            try {
                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(temp_date);
                Transaction Transac =new Transaction(date,cursor.getString(AMOUNT_COLUMN_INDEX),ExpenseType.valueOf(cursor.getString(EXPENSE_TYPE_COLUMN_INDEX)) , cursor.getDouble (ACCOUNR_NO_COLUMN_INDEX));

                //add to transactionList
                transactionList.add(Transac);
            }
            catch ( java.text.ParseException err){
                err.printStackTrace();
            }



        }
        cursor.close();
        return transactionList;

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactionList = new ArrayList<Transaction>();

        theDatabase = handler.getReadableDatabase();

        String query = "select * from "+ TRANSACTION_TABLE;

        Cursor cursor = theDatabase.rawQuery(query,null);

        int row_count = cursor.getCount();

        while (cursor.moveToNext()){
            int DATE_COLUMN_INDEX = cursor.getColumnIndex(DATE);
            String temp_date = cursor.getString(DATE_COLUMN_INDEX);
            int ACCOUNR_NO_COLUMN_INDEX = cursor.getColumnIndex(ACCOUNT_NO);
            int EXPENSE_TYPE_COLUMN_INDEX = cursor.getColumnIndex(EXPENSE_TYPE);
            int AMOUNT_COLUMN_INDEX = cursor.getColumnIndex(AMOUNT);

            try {
                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(temp_date);
                Transaction Transac =new Transaction(date,cursor.getString(AMOUNT_COLUMN_INDEX),ExpenseType.valueOf(cursor.getString(EXPENSE_TYPE_COLUMN_INDEX)) , cursor.getDouble (ACCOUNR_NO_COLUMN_INDEX));

                //add to transactionList
                transactionList.add(Transac);

                if (limit >= row_count){
                    return transactionList;
                }




            }
            catch ( java.text.ParseException err){
                err.printStackTrace();
            }

        }

        return transactionList.subList(row_count- limit,row_count);
    }
}
