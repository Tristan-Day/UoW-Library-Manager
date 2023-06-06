package com.libman.data.model;

import com.libman.App;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import javafx.util.Pair;

public class Reservation extends Entity
{
    public Patron patron;
    public Resource resource;

    public LocalDate dateIssued;

    /**
     * Retrieves a given Resource using association.
     *
     * @param resource The resource object used to reterive the reservation.
     * @return The Reservation object associated with the resource.
     * @throws Exception If there is an error querying the database or creating the Reservation object.
     */
    public static Reservation fromResource(Resource resource) throws Exception
    {
        final String query = "SELECT RESERVATION_ID FROM LIBMAN_INVENTORY.RESERVATIONS WHERE RESOURCE_ID = ?";
        PreparedStatement statement = App.database.connection.prepareStatement(query);

        statement.setInt(1, resource.identifier.getValue());
        ResultSet results = statement.executeQuery();

        results.next();
        return Entity.fromIdentifier(Reservation.class, results.getInt("RESERVATION_ID"));
    }

    public Reservation(Integer identifier) throws Exception
    {
        this.identifier = new Pair<String, Integer>("RESERVATION_ID", identifier);

        this.patron = Entity.createBlank(Patron.class);
        this.resource = Entity.createBlank(Resource.class);
    }

    @Override protected String getTableName()
    {
        return "LIBMAN_INVENTORY.RESERVATIONS";
    }

    @Override protected List<Pair<String, Object>> getFields()
    {
        List<Pair<String, Object>> fields = new ArrayList<>();

        fields.add(new Pair<>("PATRON_ID", this.patron.identifier.getValue()));
        fields.add(new Pair<>("RESOURCE_ID", this.resource.identifier.getValue()));

        fields.add(new Pair<>("ISSUED", this.dateIssued != null ? java.sql.Date.valueOf(this.dateIssued) : null));

        return fields;
    }

    @Override protected void setFields(Map<String, Object> fields) throws Exception
    {
        this.patron = Entity.fromIdentifier(Patron.class, (Integer)fields.get("PATRON_ID"));
        this.resource = Entity.fromIdentifier(Resource.class, (Integer)fields.get("RESOURCE_ID"));
        this.dateIssued = ((java.sql.Date)fields.get("ISSUED")).toLocalDate();
    }

    public LocalDate getExpriyDate()
    {
        return this.dateIssued.plusDays((Integer)App.preferences.get(("reservation-period")));
    }

    public Boolean hasExpired()
    {
        if (LocalDate.now().isAfter(this.getExpriyDate()))
        {
            return true;
        }
        return false;
    }
}
