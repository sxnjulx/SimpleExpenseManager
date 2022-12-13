package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DbHandler extends SQLiteOpenHelper {
    private static final String db_name = "200323V.sqlite";
    private static final int version =1;

    //table name
    public static final String ACCOUNT_TABLE = "Account_Table";
    public static final String TRANSACTION_TABLE ="Transaction_Table";



    //Account table column names
    public static final String BANK_NAME = "Bank_Name";
    public static final String HOLDER_NAME = "Account_Hoder_Name";
    public static final String BALANCE = "Balance";
    public static final String ACCOUNT_NO = "Account_Number"; // used in Transaction table also

    //Transaction table column names
    public static final String ID = "ID";
    public static final String EXPENSE_TYPE = "Expense_Type";
    public static final String DATE = "Date";
    public static final String AMOUNT = "Amount";


    public DbHandler( Context context) {
        super(context,db_name , null ,version );
    }

    @Override
    public void onCreate(SQLiteDatabase TheDatabase) {
        // query to create the Account Table
        TheDatabase.execSQL("create table " + ACCOUNT_TABLE + " ( " +
                ACCOUNT_NO + " text primary key, "+
                BANK_NAME +" text not null, " +
                HOLDER_NAME + " text not null,"+
                BALANCE + " real not null )"
                );
        // query to create the Transaction Table
        TheDatabase.execSQL("create table " + TRANSACTION_TABLE + "( " +
                ID + " integer primary key autoincrement, " +
                ACCOUNT_NO + " text , foreign key ("+ ACCOUNT_NO + " ) references "+ ACCOUNT_TABLE + " ( " + ACCOUNT_NO + " ) "+
                EXPENSE_TYPE + " text not null, "+
                AMOUNT + " real not null, "+
                DATE + " real not null )");


    }

    @Override
    public void onUpgrade(SQLiteDatabase TheDatabase, int oldVersion, int newVersion) {
        //Drop older account table if exist
        String Drop_Account_Table_Query ="DROP TABLE IF EXISTS "+ ACCOUNT_TABLE ;
        TheDatabase.execSQL(Drop_Account_Table_Query);

        // Drop older transaction table if exist
        String Drop_Transaction_Table_Query ="DROP TABLE IF EXISTS "+ TRANSACTION_TABLE;

        // create new account and transaction tables
        onCreate(TheDatabase);


    }
}
