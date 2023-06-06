package com.libman.data;

// clang-format off
import com.libman.App;
import com.libman.data.model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
// clang-format on

public class DocumentBuilder
{
    protected static Integer calculateWidth(String text)
    {
        // Find the longest line
        Integer width = 0;
        for (String line : text.split("\n"))
        {
            if (line.length() > width)
            {
                width = line.length();
            }
        }
        return width;
    }

    protected static StringBuilder generateHeader(String subtitle, StringBuilder document)
    {
        // Generate document subtitle
        document.insert(0, String.format("[%s]\n\n", subtitle));

        // Generate document title
        String organisation = (String)App.preferences.get("organisation");

        Integer width = DocumentBuilder.calculateWidth(document.toString());
        Integer padding = Math.max(0, width - organisation.length() / 2 - 10);

        String title = String.format("%s LIBMAN - %s %s\n\n", "#".repeat(padding), organisation, "#".repeat(padding));
        return document.insert(0, title);
    }

    protected static StringBuilder generateFooter(StringBuilder document)
    {
        String date = LocalDate.now().toString();

        // Calculate required padding
        Integer width = DocumentBuilder.calculateWidth(document.toString());
        Integer padding = Math.max(0, (width - date.length() - 2) / 2);

        String footer = String.format("\n%s %s %s", "#".repeat(padding), date, "#".repeat(padding));
        return document.append(footer);
    }

    public static void exportDocument(String document, String filename) throws IOException
    {
        FileWriter writer =
            new FileWriter(String.format("%s/%s", System.getProperty("user.home") + "/Documents", filename + ".txt"));
            
        writer.write(document);
        writer.close();
    }

    public static String generateTakeoutReceipt(Loan loan)
    {
        StringBuilder receipt = new StringBuilder();

        receipt.append(String.format("ITEM: %s", loan.resource.name));
        receipt.append("\n");

        receipt.append(String.format("ISSUED: %s", loan.dateIssued.toString()));
        receipt.append("\n");

        receipt.append(String.format("DUE: %s", loan.getDueDate().toString()));
        receipt.append("\n");

        receipt.append("\n");
        receipt.append(App.preferences.get("receipt-message"));
        receipt.append("\n");

        receipt = DocumentBuilder.generateHeader("Resource Takeout Receipt", receipt);
        receipt = DocumentBuilder.generateFooter(receipt);

        return receipt.toString();
    }

    public static String generateReturnReceipt(Loan loan) throws Exception
    {
        StringBuilder receipt = new StringBuilder();

        receipt.append(String.format("ITEM: %s", loan.resource.name));
        receipt.append("\n");

        receipt.append(String.format("ISSUED: %s", loan.dateIssued.toString()));
        receipt.append("\n");

        receipt.append(String.format("RETURNED: %s", loan.dateReturned.toString()));
        receipt.append("\n");

        // Convert to localised currency
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.UK);
        receipt.append(String.format("LATE FEE: %s", currency.format(loan.getLateFee())));
        receipt.append("\n");

        receipt.append("\n");
        receipt.append(App.preferences.get("receipt-message"));
        receipt.append("\n");

        receipt = DocumentBuilder.generateHeader("Resource Return Receipt", receipt);
        receipt = DocumentBuilder.generateFooter(receipt);

        return receipt.toString();
    }

    public static String generateResourceReport(Resource resource) throws SQLException
    {
        StringBuilder report = new StringBuilder();

        report.append(String.format("ITEM: %s | %s", resource.type.name, resource.name));
        report.append("\n");

        LocalDate nextReturnDate = resource.getNextReturnDate();
        if (nextReturnDate != null)
        {
            report.append(String.format("NEXT RETURN DATE: %s", nextReturnDate.toString()));
            report.append("\n");
        }

        report.append("\n");
        report.append(String.format("COPIES REGISTERED: %d", resource.copies));
        report.append("\n");

        report.append(String.format("COPIES AVAILABLE: %d", resource.getAvailableCopies()));
        report.append("\n");

        report.append(String.format("COPIES ON-LOAN: %d", resource.copies - resource.getAvailableCopies()));
        report.append("\n");

        report = DocumentBuilder.generateHeader("Resource Report", report);
        report = DocumentBuilder.generateFooter(report);

        return report.toString();
    }
}
