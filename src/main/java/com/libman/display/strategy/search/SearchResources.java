package com.libman.display.strategy.search;

// clang-format off
import com.libman.App;
import com.libman.data.model.Resource;
import com.libman.data.model.Entity;

import com.libman.display.controller.activity.SearchActivity;
import com.libman.display.controller.widget.RecordWidget;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXMLLoader;
// clang-format on

public class SearchResources extends SearchStrategy
{
    @Override public String getTitle()
    {
        return "Library Inventory";
    }

    @Override public String getPrompt()
    {
        return "Search by Resource Name";
    }

    @Override public void buildResults(String query, SearchActivity search)
    {
        try
        {
            final String query_ = "SELECT RESOURCE_ID FROM LIBMAN_INVENTORY.RESOURCES "
                            + "WHERE NAME LIKE ? "
                            + "ORDER BY NAME DESC";
            PreparedStatement statement = App.database.connection.prepareStatement(query_);

            statement.setString(1, "%" + query + "%");
            ResultSet results = statement.executeQuery();

            while (results.next())
            {
                FXMLLoader loader = App.getLoader("widget/RecordWidget");
                search.addResult(loader.load());

                RecordWidget record = loader.getController();
                Resource resource = Entity.fromIdentifier(Resource.class, results.getInt("RESOURCE_ID"));

                record.setStrategy(this);
                record.setData(resource);

                record.setTitle(resource.name);
                record.setSubtitle(resource.type.name.toUpperCase());
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
