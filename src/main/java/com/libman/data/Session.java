package com.libman.data;

// clang-format off
import com.libman.App;

import com.libman.data.model.Account;
import com.libman.data.model.Entity;

import java.sql.*;
// clang-format on

public class Session
{
    private static Session instance;

    public Account account;

    private Session(){};

    public static Session get()
    {
        if (Session.instance == null)
        {
            Session.instance = new Session();
        }
        return Session.instance;
    }

    // Attempt login using a given username and password
    public Boolean login(String username, String password) throws Exception
    {
        String query =
            "SELECT ACCOUNT_ID FROM LIBMAN_ACCOUNTS.ACCOUNTS WHERE USERNAME = ? AND PASSWORD = ? AND ACCOUNT_ID NOT IN(SELECT ACCOUNT_ID FROM LIBMAN_ACCOUNTS.PENDING_ACCOUNTS)";
        PreparedStatement statement = App.database.connection.prepareStatement(query);

        statement.setString(1, username);
        statement.setString(2, password);

        ResultSet results = statement.executeQuery();
        if (!results.next())
        {
            return false;
        }

        this.account = Entity.fromIdentifier(Account.class, results.getInt(1));
        return true;
    }
}
