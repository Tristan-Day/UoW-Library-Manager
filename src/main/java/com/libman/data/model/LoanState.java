package com.libman.data.model;

public enum LoanState
{
    ON_LOAN(0, "On Loan"),
    RETURNED(1, "Returned"),
    OVERDUE(2, "Overdue");

    private final Integer value;
    private final String label;

    private LoanState(Integer value, String label)
    {
        this.value = value;
        this.label = label;
    }

    public static LoanState fromValue(Integer value)
    {
        for (LoanState state : LoanState.values())
        {
            if (state.value.equals(value))
            {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid State Value: " + value);
    }

    @Override public String toString()
    {
        return this.label;
    }
}
