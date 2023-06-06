package com.libman.display.strategy.search;

// clang-format off
import com.libman.App;
import com.libman.data.model.Patron;
import com.libman.data.model.Entity;

import com.libman.display.controller.activity.SearchActivity;
import com.libman.display.controller.widget.RecordWidget;

import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXMLLoader;
// clang-format on

public class SearchPatrons extends SearchStrategy
{
    @Override public String getTitle()
    {
        return "Patron Database";
    }

    @Override public String getPrompt()
    {
        return "Search by Patron Name";
    }

    @Override public void buildResults(String query, SearchActivity search)
    {
        String firstname = "";
        String lastname = "";
        
        if (query.split(" ").length >= 2)
        {
            String[] words = query.split(" ");
            
            firstname = (String)Array.get(words, 0);
            lastname = (String)Array.get(words, 1);
        }
        else
        {
            firstname = query;
            lastname = query;
        }

        try
        {
            final String query_ = "SELECT PATRON_ID FROM LIBMAN_INVENTORY.PATRONS "
                                  + "WHERE FIRSTNAME LIKE ? OR LASTNAME LIKE ? "
                                  + "ORDER BY FIRSTNAME DESC";
            PreparedStatement statement = App.database.connection.prepareStatement(query_);

            statement.setString(1, "%" + firstname + "%");
            statement.setString(2, "%" + lastname + "%");

            ResultSet results = statement.executeQuery();

            while (results.next())
            {
                FXMLLoader loader = App.getLoader("widget/RecordWidget");
                search.addResult(loader.load());

                RecordWidget record = loader.getController();
                Patron patron = Entity.fromIdentifier(Patron.class, results.getInt("PATRON_ID"));

                record.allowInspection(false);
                record.setStrategy(this);
                record.setData(patron);

                record.setTitle(String.format("%s %s", patron.firstname, patron.lastname));
                record.setSubtitle(String.format("ID: #%d  |  %s  |  %s  |  %s",
                                                 patron.identifier.getValue(),
                                                 patron.mobile,
                                                 patron.email,
                                                 patron.postcode));
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
