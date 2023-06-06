package com.libman.data.model;

import com.libman.App;
import java.sql.*;
import java.util.*;
import javafx.util.Pair;

public class AccountType extends Entity
{
    public String name;

    public static List<AccountType> getAccountTypes() throws Exception
    {
        String query = "SELECT TYPE_ID, NAME FROM LIBMAN_ACCOUNTS.ACCOUNT_TYPES";
        PreparedStatement statement = App.database.connection.prepareStatement(query);
        ResultSet results = statement.executeQuery();

        List<AccountType> types = new ArrayList<>();
        while (results.next())
        {
            AccountType type = Entity.createBlank(AccountType.class);

            type.identifier = new Pair<String, Integer>("TYPE_ID", results.getInt("TYPE_ID"));
            type.name = results.getString("NAME");
            
            types.add(type);
        }
        return types;
    }

    public AccountType(Integer identifer)
    {
        this.identifier = new Pair<String, Integer>("TYPE_ID", identifer);
    }

    @Override protected String getTableName()
    {
        return "LIBMAN_ACCOUNTS.ACCOUNT_TYPES";
    }

    @Override protected List<Pair<String, Object>> getFields()
    {
        return new ArrayList<>(Arrays.asList(new Pair<>("NAME", this.name)));
    }

    @Override protected void setFields(Map<String, Object> fields)
    {
        this.name = (String)fields.get("NAME");
    }

    @Override public String toString()
    {
        return this.name;
    }
}