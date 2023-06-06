package com.libman;

// clang-format off
import com.libman.data.Database;
import com.libman.data.model.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.sql.SQLException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.*;
import org.yaml.snakeyaml.*;
import org.yaml.snakeyaml.constructor.Constructor;
// clang-format on

public class DatabaseRetrieval
{
    static void prepare() throws SQLException, FileNotFoundException
    {
        Yaml yaml = new Yaml(new Constructor(Map.class, new LoaderOptions()));
        App.preferences = yaml.load(new FileInputStream("./src/main/resources/com/libman/preferences.yml"));

        App.database = new Database();
        App.database.initialize();
    }

    @Test public void testAccountType() throws Exception
    {
        DatabaseRetrieval.prepare();

        AccountType accountType = Entity.fromIdentifier(AccountType.class, 1);
        Assert.assertEquals("Librarian", accountType.name);

        accountType = Entity.fromIdentifier(AccountType.class, 2);
        Assert.assertEquals("Curator", accountType.name);

        accountType = Entity.fromIdentifier(AccountType.class, 3);
        Assert.assertEquals("Administrator", accountType.name);
    }

    @Test public void testAccount() throws Exception
    {
        DatabaseRetrieval.prepare();

        Account account = Entity.fromIdentifier(Account.class, 1);
        Assert.assertEquals("tristan-librarian", account.username);
        Assert.assertEquals("password", account.password);
        Assert.assertEquals("Tristan", account.firstname);

        account = Entity.fromIdentifier(Account.class, 2);
        Assert.assertEquals("tristan-curator", account.username);

        account = Entity.fromIdentifier(Account.class, 3);
        Assert.assertEquals("tristan-admin", account.username);
    }

    @Test public void testLoan() throws Exception
    {
        DatabaseRetrieval.prepare();

        Loan loan = Entity.fromIdentifier(Loan.class, 1);
        Assert.assertEquals("Rachel", loan.patron.firstname);
        Assert.assertEquals("Electric Nights", loan.resource.name);
        Assert.assertTrue(loan.dateIssued.isEqual(LocalDate.of(2023, 4, 20)));

        loan = Entity.fromIdentifier(Loan.class, 2);
        Assert.assertEquals("Andrew", loan.patron.firstname);
        Assert.assertEquals("Cosmos", loan.resource.name);

        // Test State Retrieval
        Assert.assertEquals(loan.getState(), LoanState.RETURNED);
    }

    @Test public void testPatron() throws Exception
    {
        DatabaseRetrieval.prepare();

        Patron patron = Entity.fromIdentifier(Patron.class, 1);
        Assert.assertEquals("Rachel", patron.firstname);
        Assert.assertEquals("Brown", patron.lastname);
        Assert.assertEquals("555-1234", patron.mobile);
        Assert.assertEquals("rbrown@example.com", patron.email);
        Assert.assertEquals("AB12 3CD", patron.postcode);
    }

    @Test public void testResourceType() throws Exception
    {
        DatabaseRetrieval.prepare();

        ResourceType resourceType = Entity.fromIdentifier(ResourceType.class, 1);
        Assert.assertEquals("Compact Disk", resourceType.name);

        // Test Subtype Retrieval
        List<ResourceType> children = resourceType.getChildren();
        Assert.assertEquals("Audiobook", children.get(0).name);
        Assert.assertEquals("Documentary", children.get(1).name);

        // Test Parent Retrieval
        Assert.assertEquals(null, resourceType.getParent());
    }

    @Test public void testResource() throws Exception
    {
        DatabaseRetrieval.prepare();

        Resource resource = Entity.fromIdentifier(Resource.class, 2);
        Assert.assertEquals("Electric Nights", resource.name);
        Assert.assertEquals((Integer)2, resource.copies);

        Assert.assertEquals((Integer)1, resource.getAvailableCopies());
    }
}