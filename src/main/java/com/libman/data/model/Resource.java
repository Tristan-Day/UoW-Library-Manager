package com.libman.data.model;

import com.libman.App;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import javafx.util.Pair;

public class Resource extends Entity
{
    public ResourceType type;

    public String name;
    public Blob image;
    public Integer copies;

    public List<Pair<TagType, String>> tags;

    public static Blob getDefaultImage() throws Exception
    {
        Blob image = App.database.connection.createBlob();

        InputStream inputStream = App.class.getResourceAsStream("/com/libman/resource-default.png");
        image.setBytes(1, inputStream.readAllBytes());

        return image;
    }

    public Resource(Integer identifier) throws Exception
    {
        this.identifier = new Pair<String, Integer>("RESOURCE_ID", identifier);

        this.type = Entity.createBlank(ResourceType.class);
        this.tags = new ArrayList<>();
    }

    @Override protected String getTableName()
    {
        return "LIBMAN_INVENTORY.RESOURCES";
    }

    @Override protected List<Pair<String, Object>> getFields()
    {
        return new ArrayList<>(Arrays.asList(new Pair<>("TYPE_ID", this.type.identifier.getValue()),
                                             new Pair<>("NAME", this.name),
                                             new Pair<>("IMAGE", this.image),
                                             new Pair<>("COPIES", this.copies)));
    }

    @Override protected void setFields(Map<String, Object> fields) throws Exception
    {
        try
        {
            this.type = Entity.fromIdentifier(ResourceType.class, (Integer)fields.get("TYPE_ID"));
        }
        catch (SQLException exception)
        {
            throw new SQLDataException("Resource is Missing Type");
        }

        this.name = (String)fields.get("NAME");
        this.image = (Blob)fields.get("IMAGE");
        this.copies = (Integer)fields.get("COPIES");
    }

    private void readTags() throws Exception
    {
        final String query = "SELECT TAG_TYPE_ID, VALUE FROM LIBMAN_INVENTORY.RESOURCE_TAGS WHERE RESOURCE_ID = ?";
        PreparedStatement statement = App.database.connection.prepareStatement(query);

        statement.setInt(1, this.identifier.getValue());
        ResultSet results = statement.executeQuery();

        while (results.next())
        {
            // Initialise each tag type and link associated value
            TagType tag = Entity.fromIdentifier(TagType.class, results.getInt("TAG_TYPE_ID"));
            this.tags.add(new Pair<TagType, String>(tag, results.getString("VALUE")));
        }
    }

    private void writeTags() throws Exception
    {
        final String query =
            "INSERT INTO LIBMAN_INVENTORY.RESOURCE_TAGS (RESOURCE_ID, TAG_TYPE_ID, VALUE) VALUES (?, ?, ?)";

        for (Pair<TagType, String> tag : this.tags)
        {
            try
            {
                // Attempt to create new tag
                tag.getKey().create();
            }
            catch (SQLIntegrityConstraintViolationException exception)
            {
                // Ignore exception raised when tag already exists
            }

            // Retrieve tag identifier
            tag = new Pair<>(TagType.fromName(tag.getKey().name), tag.getValue());

            // Write link to database
            PreparedStatement statement = App.database.connection.prepareStatement(query);

            statement.setInt(1, this.identifier.getValue());
            statement.setInt(2, tag.getKey().identifier.getValue());
            statement.setString(3, tag.getValue());

            statement.execute();
        }
    }

    private void clearTags() throws SQLException
    {
        final String query = "DELETE FROM LIBMAN_INVENTORY.RESOURCE_TAGS WHERE RESOURCE_ID = ?";
        PreparedStatement statement = App.database.connection.prepareStatement(query);

        statement.setInt(1, this.identifier.getValue());
        statement.execute();
    }

    public void create() throws Exception
    {
        super.create();
        this.writeTags();
    }

    public void read() throws Exception
    {
        super.read();
        this.readTags();
    }

    public void update() throws Exception
    {
        super.update();
        this.clearTags();
        this.writeTags();
    }

    public void delete() throws SQLException
    {
        super.delete();
        this.clearTags();
    }

    /**
     * Creates a new reservation instance for the specified patron.
     *
     * @param patron The patron reserving the resource.
     * @return The newly created Reservation object.
     * @throws Exception If there is an error querying the database or creating the Reservation object.
     */
    public Reservation reserve(Patron patron) throws Exception
    {
        Reservation reservation = Entity.createBlank(Reservation.class);

        reservation.resource = this;
        reservation.patron = patron;
        reservation.dateIssued = LocalDate.now();

        reservation.create();
        return reservation;
    }

    /**
     * Creates a new loan instance for the specified patron.
     *
     * @param patron The patron borrowing the resource.
     * @return The newly created Loan object.
     * @throws Exception If there is an error querying the database or creating the Loan object.
     */
    public Loan loan(Patron patron) throws Exception
    {
        Loan loan = Entity.createBlank(Loan.class);

        loan.resource = this;
        loan.patron = patron;
        loan.dateIssued = LocalDate.now();

        return loan;
    }

    public Integer getAvailableCopies() throws SQLException
    {
        final String query = "SELECT COUNT(*) FROM LIBMAN_INVENTORY.LOANS WHERE RESOURCE_ID = ? AND RETURNED IS NULL";
        PreparedStatement statement = App.database.connection.prepareStatement(query);

        statement.setInt(1, this.identifier.getValue());
        ResultSet results = statement.executeQuery();

        if (results.next())
        {
            return this.copies - results.getInt(1);
        }
        return 0;
    }

    public Integer getUnavailableCopies() throws SQLException
    {
        return this.copies - this.getAvailableCopies();
    }

    public LocalDate getNextReturnDate() throws SQLException
    {
        final String query =
            "SELECT ISSUED FROM LIBMAN_INVENTORY.LOANS WHERE RESOURCE_ID = ? AND RETURNED IS NULL ORDER BY ISSUED ASC";
        PreparedStatement statement = App.database.connection.prepareStatement(query);

        statement.setInt(1, this.identifier.getValue());
        ResultSet results = statement.executeQuery();

        if (results.next())
        {
            Integer period = (Integer)App.preferences.get("loan-period");
            return ((java.sql.Date)results.getDate("ISSUED")).toLocalDate().plusDays(period);
        }
        return null;
    }

    public Boolean hasReservation() throws Exception
    {
        final String query = "SELECT RESERVATION_ID FROM LIBMAN_INVENTORY.RESERVATIONS WHERE RESOURCE_ID = ?";
        PreparedStatement statement = App.database.connection.prepareStatement(query);

        statement.setInt(1, this.identifier.getValue());
        ResultSet results = statement.executeQuery();

        return results.next();
    }
}
