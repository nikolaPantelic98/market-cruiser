package com.marketcruiser.admin.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportItem {

    private String identifier;
    private float grossSales;
    private float netSales;
    private int ordersCount;


    public ReportItem(String identifier) {
        this.identifier = identifier;
    }

    public ReportItem(String identifier, float grossSales, float netSales) {
        this.identifier = identifier;
        this.grossSales = grossSales;
        this.netSales = netSales;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportItem that = (ReportItem) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    public void addGrossSales(float amount) {
        this.grossSales += amount;
    }

    public void addNetSales(float amount) {
        this.netSales += amount;
    }

    public void increaseOrderCount() {
        this.ordersCount++;
    }
}
