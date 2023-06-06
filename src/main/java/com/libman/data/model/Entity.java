package com.libman.data.model;

import com.libman.App;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import javafx.util.Pair;

public abstract class Entity
{
    public Pair<String, Integer> identifier;

    /**
     * Instantiates a new Entity of the specified type with a null identifier.
     *
     * @param cls The type of object to be instantiated.
     * @return The new instance of the specified class.
     * @throws Exception If the specified class cannot be instantiated with a null identifier.
     */
    public static <Type extends Entity> Type createBlank(Class<Type> cls) throws Exception
    {
        try
        {
            return cls.getConstructor(Integer.class).newInstance((Integer)null);
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
               NoSuchMethodException | SecurityException exception)
        {
            throw new Exception("Failed to create instance of " + cls.getSimpleName(), exception);
        }
    }

    /**
     * Instantiates a new Entity of the specified type using the provided identifier.
     *
     * @param cls The type of object to be instantiated.
     * @param identifier The identifier of the entity to be read.
     * @return The new instance of the specified class.
     * @throws Exception If the specified class cannot be instantiated or if there is an error reading from the database.
     */
    public static <Type extends Entity> Type fromIdentifier(Class<Type> cls, Integer identifier) throws Exception
    {
        try
        {
            Type instance = cls.getConstructor(Integer.class).newInstance(identifier);
            instance.read();

            return instance;
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
               NoSuchMethodException | SecurityException | SQLException exception)
        {
            throw new Exception("Failed to create instance of " + cls.getSimpleName(), exception);
        }
    }

    protected abstract String getTableName();

    protected abstract List<Pair<String, Object>> getFields();

    protected abstract void setFields(Map<String, Object> fields) throws Exception;

    /**
     * Inserts a new record into the database table for this entity.
     *
     * @throws Exception If there is an error executing the SQL INSERT statement or retrieving the generated identifier
     *     from the database.
     */
    public void create() throws Exception
    {
        List<Pair<String, Object>> fields = this.getFields();
        StringBuilder query = new StringBuilder(String.format("INSERT INTO %s (", this.getTableName()));

        // Set target fields
        fields.forEach(field -> { query.append(field.getKey()).append(", "); });
        query.delete(query.length() - 2, query.length()).append(")");

        // Add placeholders for target field values
        query.append(" VALUES (");
        fields.forEach(field -> { query.append("?, "); });
        query.delete(query.length() - 2, query.length()).append(")");

        PreparedStatement statement =
            App.database.connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);

        // Set placeholder values
        for (Integer index = 0; index < fields.size(); index++)
        {
            statement.setObject(index + 1, fields.get(index).getValue());
        }
        statement.execute();

        ResultSet results = statement.getGeneratedKeys();
        results.next();

        this.identifier = new Pair<String, Integer>(this.identifier.getKey(), results.getInt(1));
    }

    /**
     * Reads the current entity from the database.
     *
     * @throws Exception If there is an error accessing the database or if no rows are returned.
     */
    public void read() throws Exception
    {
        List<Pair<String, Object>> fields = this.getFields();
        StringBuilder query = new StringBuilder("SELECT ");

        // Set target fields
        fields.forEach(field -> { query.append(field.getKey()).append(", "); });
        query.delete(query.length() - 2, query.length());

        // Set target entity placeholder
        query.append(String.format(" FROM %s WHERE %s = ?", this.getTableName(), this.identifier.getKey()));

        PreparedStatement statement = App.database.connection.prepareStatement(query.toString());
        statement.setInt(1, this.identifier.getValue());

        ResultSet results = statement.executeQuery();
        if (results.next())
        {
            // Process results into hashmap
            Map<String, Object> map = new HashMap<>();
            for (Pair<String, Object> field : fields)
            {
                map.put(field.getKey(), results.getObject(field.getKey()));
            }
            this.setFields(map);
        }
        else
        {
            throw new SQLException("No Rows Returned");
        }
    }

    /**
     * Updates the entity's record in the database with the current field values.
     *
     * @throws Exception If there is an error executing the SQL UPDATE statement.
     */
    public void update() throws Exception
    {
        List<Pair<String, Object>> fields = this.getFields();
        StringBuilder query = new StringBuilder(String.format("UPDATE %s SET ", this.getTableName()));

        // Set target fields
        fields.forEach(field -> { query.append(field.getKey()).append(" = ?, "); });
        query.delete(query.length() - 2, query.length());

        // Set target entity placeholder
        query.append(String.format(" WHERE %s = ?", this.identifier.getKey()));

        PreparedStatement statement = App.database.connection.prepareStatement(query.toString());

        Integer index = 0;
        for (; index < fields.size(); index++)
        {
            statement.setObject(index + 1, fields.get(index).getValue());
        }
        statement.setInt(index + 1, this.identifier.getValue());
        statement.execute();
    }

    /**
     * Deletes the current Entity instance from the database.
     *
     * @throws SQLException If an error occurs while executing the SQL DELETE statement.
     */
    public void delete() throws SQLException
    {
        // Set target entity placeholder
        String query = String.format("DELETE FROM %s WHERE %s = ?", this.getTableName(), this.identifier.getKey());
        PreparedStatement statement = App.database.connection.prepareStatement(query);

        statement.setInt(1, this.identifier.getValue());
        statement.execute();
    }
}
