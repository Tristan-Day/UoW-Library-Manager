package com.libman;

// clang-format off
import static org.junit.Assert.assertTrue;

import com.libman.data.DocumentBuilder;
import com.libman.data.model.*;

import java.io.FileWriter;
import java.util.regex.Pattern;
import org.junit.*;
// clang-format on

public class DocumentGeneration
{
    static void prepare()
    {
        App.main(null);
    }

    @Test public void testResourceReport() throws Exception
    {
        DatabaseRetrieval.prepare();

        Resource resource = Entity.fromIdentifier(Resource.class, 2);
        FileWriter writer = new FileWriter("./exports/test-report.txt");

        String document = DocumentBuilder.generateResourceReport(resource);

        Pattern pattern =
            Pattern.compile("NEXT RETURN DATE:\\s+(19|20)\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])");
        assertTrue(pattern.matcher(document).find());

        pattern = Pattern.compile(String.format("COPIES REGISTERED: %d", resource.copies));
        assertTrue(pattern.matcher(document).find());

        pattern = Pattern.compile(String.format("COPIES AVAILABLE: %d", resource.getAvailableCopies()));
        assertTrue(pattern.matcher(document).find());

        pattern = Pattern.compile(String.format("COPIES ON-LOAN: %d", resource.getUnavailableCopies()));
        assertTrue(pattern.matcher(document).find());

        writer.write(document);
        writer.close();
    }

    @Test public void testTakeoutReceipt() throws Exception
    {
        DatabaseRetrieval.prepare();

        Loan loan = Entity.fromIdentifier(Loan.class, 1);
        FileWriter writer = new FileWriter("./exports/test-takeout-receipt.txt");

        String document = DocumentBuilder.generateTakeoutReceipt(loan);

        Pattern pattern = Pattern.compile(String.format("ITEM:\\s+%s", loan.resource.name));
        assertTrue(pattern.matcher(document).find());

        pattern = Pattern.compile("ISSUED:\\s\\d{4}-\\d{2}-\\d{2}");
        assertTrue(pattern.matcher(document).find());

        pattern = Pattern.compile("DUE:\\s\\d{4}-\\d{2}-\\d{2}");
        assertTrue(pattern.matcher(document).find());

        writer.write(document);
        writer.close();
    }

    @Test public void testReturnReceipt() throws Exception
    {
        DatabaseRetrieval.prepare();

        Loan loan = Entity.fromIdentifier(Loan.class, 2);
        FileWriter writer = new FileWriter("./exports/test-return-receipt.txt");

        System.out.println(loan.dateReturned.toString());
        System.out.println(loan.dateIssued.toString());

        String document = DocumentBuilder.generateReturnReceipt(loan);

        Pattern pattern = Pattern.compile("ISSUED:\\s\\d{4}-\\d{2}-\\d{2}");
        assertTrue(pattern.matcher(document).find());

        pattern = Pattern.compile("RETURNED:\\s\\d{4}-\\d{2}-\\d{2}");
        assertTrue(pattern.matcher(document).find());

        pattern = Pattern.compile("LATE FEE: £\\d+\\.\\d{2}");
        assertTrue(pattern.matcher(document).find());

        writer.write(document);
        writer.close();
    }
}
