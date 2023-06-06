package com.libman.data.model;

import com.libman.App;
import java.sql.*;
import java.util.*;
import javafx.util.Pair;

public class TagType extends Entity
{
    public String name;

    public static TagType fromName(String name) throws Exception
    {
        final String query = "SELECT TAG_TYPE_ID FROM LIBMAN_INVENTORY.TAG_TYPES WHERE NAME = ?";
        PreparedStatement statement = App.database.connection.prepareStatement(query);

        statement.setString(1, name);
        ResultSet results = statement.executeQuery();

        results.next();
        return Entity.fromIdentifier(TagType.class, results.getInt("TAG_TYPE_ID"));
    }

    public TagType(Integer identifier)
    {
        this.identifier = new Pair<String, Integer>("TAG_TYPE_ID", identifier);
    }

    @Override protected String getTableName()
    {
        return "LIBMAN_INVENTORY.TAG_TYPES";
    }

    @Override protected List<Pair<String, Object>> getFields()
    {
        return new ArrayList<>(Arrays.asList(new Pair<>("NAME", this.name)));
    }

    @Override protected void setFields(Map<String, Object> fields)
    {
        this.name = (String)fields.get("NAME");
    }
}
