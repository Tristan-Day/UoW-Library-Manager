package com.libman.display.strategy.search;

// clang-format off
import com.libman.App;
import com.libman.data.model.Account;
import com.libman.data.model.Entity;

import com.libman.display.controller.activity.SearchActivity;
import com.libman.display.controller.widget.RecordWidget;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXMLLoader;
// clang-format on

public class SearchAccounts extends SearchStrategy
{
    @Override public String getTitle()
    {
        return "System Users";
    }

    @Override public String getPrompt()
    {
        return "Search by Username or Firstname";
    }

    @Override public void buildResults(String query, SearchActivity search)
    {
        try
        {
            final String query_ = "SELECT ACCOUNT_ID FROM LIBMAN_ACCOUNTS.ACCOUNTS "
                            + "WHERE ACCOUNT_ID NOT IN(SELECT ACCOUNT_ID FROM LIBMAN_ACCOUNTS.PENDING_ACCOUNTS) "
                            + "AND (USERNAME LIKE ? OR FIRSTNAME LIKE ?) "
                            + "ORDER BY FIRSTNAME DESC";
            PreparedStatement statement = App.database.connection.prepareStatement(query_);

            statement.setString(1, "%" + query + "%");
            statement.setString(2, "%" + query + "%");

            ResultSet results = statement.executeQuery();

            while (results.next())
            {
                FXMLLoader loader = App.getLoader("widget/RecordWidget");
                search.addResult(loader.load());

                RecordWidget record = loader.getController();
                Account account = Entity.fromIdentifier(Account.class, results.getInt("ACCOUNT_ID"));

                record.setStrategy(this);
                record.setData(account);

                record.setTitle(account.firstname);
                record.setSubtitle(String.format("%s  (%s)", account.username, account.type.name));
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
