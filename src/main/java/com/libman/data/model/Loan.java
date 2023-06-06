package com.libman.data.model;

import com.libman.App;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import javafx.util.Pair;

public class Loan extends Entity
{
    public Patron patron;
    public Resource resource;

    public LocalDate dateIssued;
    public LocalDate dateReturned;

    public Loan(Integer identifier) throws Exception
    {
        this.identifier = new Pair<String, Integer>("LOAN_ID", identifier);

        this.patron = Entity.createBlank(Patron.class);
        this.resource = Entity.createBlank(Resource.class);
    }

    @Override protected String getTableName()
    {
        return "LIBMAN_INVENTORY.LOANS";
    }

    @Override protected List<Pair<String, Object>> getFields()
    {
        List<Pair<String, Object>> fields = new ArrayList<>();

        fields.add(new Pair<>("PATRON_ID", this.patron.identifier.getValue()));
        fields.add(new Pair<>("RESOURCE_ID", this.resource.identifier.getValue()));

        fields.add(new Pair<>("ISSUED", this.dateIssued != null ? java.sql.Date.valueOf(this.dateIssued) : null));
        fields.add(new Pair<>("RETURNED", this.dateReturned != null ? java.sql.Date.valueOf(this.dateReturned) : null));

        return fields;
    }

    @Override protected void setFields(Map<String, Object> fields) throws Exception
    {
        this.patron = Entity.fromIdentifier(Patron.class, (Integer)fields.get("PATRON_ID"));
        this.resource = Entity.fromIdentifier(Resource.class, (Integer)fields.get("RESOURCE_ID"));
        this.dateIssued = ((java.sql.Date)fields.get("ISSUED")).toLocalDate();

        try
        {
            this.dateReturned = ((java.sql.Date)fields.get("RETURNED")).toLocalDate();
        }
        catch (NullPointerException exception)
        {
            // Case where return date is not yet set
            this.dateReturned = null;
        }
    }
    
    public LoanState getState()
    {
        Integer loanPeriod = (Integer)App.preferences.get("loan-period");

        if (this.dateReturned != null)
        {
            return LoanState.RETURNED;
        }
        else if (LocalDate.now().isAfter(this.dateIssued.plusDays(loanPeriod)))
        {
            return LoanState.OVERDUE;
        }
        return LoanState.ON_LOAN;
    }

    public LocalDate getDueDate()
    {
        Integer period = (Integer)App.preferences.get("loan-period");
        return this.dateIssued.plusDays(period);
    }

    public BigDecimal getLateFee() throws Exception
    {
        return this.resource.type.getLateFee().multiply(new BigDecimal(this.getDaysLate()));
    }

    public Long getDaysLate()
    {
        if (this.getState() == LoanState.RETURNED)
        {
            return java.time.temporal.ChronoUnit.DAYS.between(this.dateIssued, this.dateReturned);
        }
        else if (this.getState() == LoanState.OVERDUE)
        {
            return java.time.temporal.ChronoUnit.DAYS.between(this.getDueDate(), LocalDate.now());
        }
        else
        {
            return (long)0;
        }
    }
}
