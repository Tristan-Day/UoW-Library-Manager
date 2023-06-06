package com.libman.display.strategy.search;

// clang-format off
import com.libman.App;
import com.libman.data.model.Loan;
import com.libman.data.model.Entity;

import com.libman.display.controller.activity.SearchActivity;
import com.libman.display.controller.widget.RecordWidget;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXMLLoader;
// clang-format on

public class SearchLoans extends SearchStrategy
{
    @Override public String getTitle()
    {
        return "Active Loans";
    }

    @Override public String getPrompt()
    {
        return "Search by Resource Name";
    }

    @Override public void buildResults(String query, SearchActivity search)
    {
        try
        {
            final String query_ = "SELECT LOAN_ID FROM LIBMAN_INVENTORY.LOANS L "
                            + "JOIN LIBMAN_INVENTORY.RESOURCES R ON R.RESOURCE_ID = L.RESOURCE_ID "
                            + "WHERE RETURNED IS NULL AND NAME LIKE ? "
                            + "ORDER BY NAME DESC";
            PreparedStatement statement = App.database.connection.prepareStatement(query_);

            statement.setString(1, "%" + query + "%");
            ResultSet results = statement.executeQuery();

            while (results.next())
            {
                FXMLLoader loader = App.getLoader("widget/RecordWidget");
                search.addResult(loader.load());

                RecordWidget record = loader.getController();
                Loan loan = Entity.fromIdentifier(Loan.class, results.getInt("LOAN_ID"));

                record.setStrategy(this);
                record.setData(loan);

                record.setTitle(loan.resource.name);
                record.setSubtitle(String.format("%s %s", loan.patron.firstname, loan.patron.lastname));
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
