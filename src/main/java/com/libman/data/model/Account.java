package com.libman.data.model;

import com.libman.App;
import java.sql.*;
import java.util.*;
import javafx.util.Pair;

public class Account extends Entity
{
    public AccountType type;

    public String firstname;
    public String username;
    public String password;

    public Account(Integer identifier) throws Exception
    {
        this.identifier = new Pair<String, Integer>("ACCOUNT_ID", identifier);
        this.type = Entity.createBlank(AccountType.class);
    }

    @Override protected String getTableName()
    {
        return "LIBMAN_ACCOUNTS.ACCOUNTS";
    }

    @Override protected List<Pair<String, Object>> getFields()
    {
        return new ArrayList<>(Arrays.asList(new Pair<>("TYPE_ID", this.type.identifier.getValue()),
                                             new Pair<>("FIRSTNAME", this.firstname),
                                             new Pair<>("USERNAME", this.username),
                                             new Pair<>("PASSWORD", this.password)));
    }

    @Override protected void setFields(Map<String, Object> fields) throws Exception
    {
        this.type = Entity.fromIdentifier(AccountType.class, (Integer)fields.get("TYPE_ID"));

        this.firstname = (String)fields.get("FIRSTNAME");
        this.username = (String)fields.get("USERNAME");
        this.password = (String)fields.get("PASSWORD");
    }

    public void setPending() throws SQLException
    {
        String query = "INSERT INTO LIBMAN_ACCOUNTS.PENDING_ACCOUNTS (ACCOUNT_ID) VALUES (?)";

        PreparedStatement statement = App.database.connection.prepareStatement(query);
        statement.setInt(1, this.identifier.getValue());

        statement.execute();
    }

    public void approve() throws SQLException
    {
        String query = "DELETE FROM LIBMAN_ACCOUNTS.PENDING_ACCOUNTS WHERE ACCOUNT_ID = ?";

        PreparedStatement statement = App.database.connection.prepareStatement(query);
        statement.setInt(1, this.identifier.getValue());

        statement.execute();
    };

    public void reject() throws SQLException
    {
        this.delete();
        
        String query = "DELETE FROM LIBMAN_ACCOUNTS.PENDING_ACCOUNTS WHERE ACCOUNT_ID = ?";

        PreparedStatement statement = App.database.connection.prepareStatement(query);
        statement.setInt(1, this.identifier.getValue());

        statement.execute();
    };
}
