package com.libman.data.model;

import java.util.*;
import javafx.util.Pair;

public class Patron extends Entity
{
    public String firstname;
    public String lastname;
    public String mobile;
    public String email;
    public String postcode;

    public Patron(Integer identifer)
    {
        this.identifier = new Pair<String, Integer>("PATRON_ID", identifer);
    }

    @Override protected String getTableName()
    {
        return "LIBMAN_INVENTORY.PATRONS";
    }

    @Override protected List<Pair<String, Object>> getFields()
    {
        return new ArrayList<>(Arrays.asList(new Pair<>("FIRSTNAME", this.firstname),
                                             new Pair<>("LASTNAME", this.lastname),
                                             new Pair<>("MOBILE", this.mobile),
                                             new Pair<>("EMAIL", this.email),
                                             new Pair<>("POSTCODE", this.postcode)));
    }

    @Override protected void setFields(Map<String, Object> fields)
    {
        this.firstname = (String)fields.get("FIRSTNAME");
        this.lastname = (String)fields.get("LASTNAME");
        this.mobile = (String)fields.get("MOBILE");
        this.email = (String)fields.get("EMAIL");
        this.postcode = (String)fields.get("POSTCODE");
    }
}
