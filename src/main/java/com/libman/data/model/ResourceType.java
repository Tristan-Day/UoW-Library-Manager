package com.libman.data.model;

import com.libman.App;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import javafx.util.Pair;

public class ResourceType extends Entity
{
    public String name;

    private BigDecimal lateFee;

    public static List<ResourceType> getRootTypes() throws Exception
    {
        final String query = "SELECT TYPE_ID FROM LIBMAN_INVENTORY.RESOURCE_TYPES WHERE PARENT_ID IS NULL";
        PreparedStatement statement = App.database.connection.prepareStatement(query);

        ResultSet results = statement.executeQuery();

        List<ResourceType> internalTypes = new ArrayList<ResourceType>();
        while (results.next())
        {
            internalTypes.add(Entity.fromIdentifier(ResourceType.class, results.getInt("TYPE_ID")));
        }
        return internalTypes;
    }

    public ResourceType(Integer identifer)
    {
        this.identifier = new Pair<String, Integer>("TYPE_ID", identifer);
        this.lateFee = new BigDecimal(0);
    }

    @Override protected String getTableName()
    {
        return "LIBMAN_INVENTORY.RESOURCE_TYPES";
    }

    @Override protected List<Pair<String, Object>> getFields()
    {
        return new ArrayList<>(
            Arrays.asList(new Pair<>("NAME", this.name), new Pair<>("LATE_FEE", this.lateFee.floatValue())));
    }

    @Override protected void setFields(Map<String, Object> fields)
    {
        this.name = (String)fields.get("NAME");
        try
        {
            this.lateFee = new BigDecimal((Float)fields.get("LATE_FEE"));
        }
        catch (NullPointerException exception)
        {
            // Case where no late fee is set
            this.lateFee = null;
        }
    }

    /**
     * Returns the patrent resource type of this resource type.
     *
     * @return Parent ResourceType of this type, or null if it does not have a parent
     * @throws Exception If an error occurs while accessing the database
     */
    public ResourceType getParent() throws Exception
    {
        final String query = "SELECT PARENT_ID FROM LIBMAN_INVENTORY.RESOURCE_TYPES WHERE TYPE_ID = ?";
        PreparedStatement statement = App.database.connection.prepareStatement(query);

        statement.setInt(1, this.identifier.getValue());
        ResultSet results = statement.executeQuery();

        results.next();
        Object parent = results.getObject("PARENT_ID");

        if (parent != null)
        {
            return Entity.fromIdentifier(ResourceType.class, (Integer)parent);
        }
        return null;
    }

    /**
     * Returns a list of ResourceTypes that share the same parent ResourceType
     *
     * @return A list of ResourceTypes that are peers of this ResourceType
     * @throws Exception If there is an error accessing the database or creating ResourceType entities
     */
    public List<ResourceType> getPeers() throws Exception
    {
        ResourceType parent = this.getParent();

        final String query;
        if (parent != null)
        {
            query = "SELECT TYPE_ID FROM LIBMAN_INVENTORY.RESOURCE_TYPES WHERE PARENT_ID = ? AND NOT TYPE_ID = ?";
        }
        else
        {
            query = "SELECT TYPE_ID FROM LIBMAN_INVENTORY.RESOURCE_TYPES WHERE PARENT_ID IS NULL AND NOT TYPE_ID = ?";
        }

        PreparedStatement statement = App.database.connection.prepareStatement(query);

        if (parent != null)
        {
            statement.setObject(1, this.getParent().identifier.getValue());
            statement.setInt(2, this.identifier.getValue());
        }
        else
        {
            statement.setInt(1, this.identifier.getValue());
        }

        ResultSet results = statement.executeQuery();

        List<ResourceType> peers = new ArrayList<ResourceType>();
        while (results.next())
        {
            peers.add(Entity.fromIdentifier(ResourceType.class, results.getInt("TYPE_ID")));
        }
        return peers;
    }

    /**
     * Returns a list of ResourceType objects that are children of the current ResourceType.
     *
     * @return A List of ResourceType objects representing the children of the current ResourceType
     * @throws Exception If there is an error executing the SQL query.
     */
    public List<ResourceType> getChildren() throws Exception
    {
        final String query = "SELECT TYPE_ID FROM LIBMAN_INVENTORY.RESOURCE_TYPES WHERE PARENT_ID = ?";
        PreparedStatement statement = App.database.connection.prepareStatement(query);

        statement.setInt(1, this.identifier.getValue());
        ResultSet results = statement.executeQuery();

        List<ResourceType> children = new ArrayList<ResourceType>();
        while (results.next())
        {
            children.add(Entity.fromIdentifier(ResourceType.class, results.getInt("TYPE_ID")));
        }
        return children;
    }

    public BigDecimal getLateFee() throws Exception
    {
        if (this.lateFee != null)
        {
            return this.lateFee;
        }
        else
        {
            return this.getParent().getLateFee();
        }
    }

    @Override public String toString()
    {
        return this.name;
    }
}
