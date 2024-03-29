/**
 * The Sales Report JavaScript code handles the generation and display of sales reports on a web page.
 * It utilizes the Google Charts library to create charts and fetches sales report data from the server.
 */

var data;
var chartOptions;
var totalGrossSales;
var totalNetSales;
var totalOrders;
var startDateField;
var endDateField;

var MILLISECONDS_A_DAY = 24 * 60 * 60 * 1000;

$(document).ready(function () {
    divCustomDateRange = $("#divCustomDateRange");
    startDateField = document.getElementById('startDate');
    endDateField = document.getElementById('endDate');

    $(".button-sales-by-date").on("click", function () {

        $(".button-sales-by-date").each(function (e) {
            $(this).removeClass('btn-outline-green').addClass('btn-outline-light');
        });

        $(this).removeClass('btn-outline-light').addClass('btn-outline-green');

        period = $(this).attr("period");
        if (period) {
            loadSalesReportByDate(period);
            divCustomDateRange.addClass("d-none");
        } else {
            divCustomDateRange.removeClass("d-none");
        }
    });

    initCustomDateRange();

    $("#buttonViewReportByDateRange").on("click", function (e) {
        validateDateRange();
    });
});

/**
 * Validates the selected date range and loads the sales report if the range is valid.
 */
function validateDateRange() {
    days = calculateDays();

    startDateField.setCustomValidity("");

    if (days >= 7 && days <= 30) {
        loadSalesReportByDate("custom");
    } else {
        startDateField.setCustomValidity("Dates must be in the range of 7 and 30 days");
        startDateField.reportValidity();
    }
}

/**
 * Calculates the number of days between the selected start and end dates.
 *
 * @return the number of days between the selected dates
 */
function calculateDays() {
    startDate = startDateField.valueAsDate;
    endDate = endDateField.valueAsDate;

    differenceInMilliseconds = endDate - startDate;

    return differenceInMilliseconds / MILLISECONDS_A_DAY;
}

/**
 * Initializes the custom date range input fields.
 */
function initCustomDateRange() {
    toDate = new Date();
    endDateField.valueAsDate = toDate;

    fromDate = new Date();
    fromDate.setDate(toDate.getDate() - 30);
    startDateField.valueAsDate = fromDate;
}

/**
 * Loads the sales report for the specified period or custom date range.
 *
 * @param period the period or "custom" for a custom date range
 */
function loadSalesReportByDate(period) {
    if (period == "custom") {
        startDate = $("#startDate").val();
        endDate = $("#endDate").val();

        requestURL = contextPath + "reports/sales_by_date/" + startDate + "/" + endDate;
    } else {
        requestURL = contextPath + "reports/sales_by_date/" + period;
    }

    $.get(requestURL, function (responseJSON) {
        prepareChartData(responseJSON);
        customizeChart(period);
        drawChart(period);
    });
}

/**
 * Prepares the sales report data received from the server for chart visualization.
 *
 * @param responseJSON the sales report data received from the server
 */
function prepareChartData(responseJSON) {
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Date');
    data.addColumn('number', 'Gross Sales');
    data.addColumn('number', 'Net Sales');
    data.addColumn('number', 'Orders');

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalOrders = 0;

    $.each(responseJSON, function (index, reportItem) {
        data.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales, reportItem.ordersCount]]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalOrders += parseInt(reportItem.ordersCount);
    });
}

/**
 * Customizes the chart options based on the selected period.
 *
 * @param period the selected period
 */
function customizeChart(period) {
    chartOptions = {
        title: getChartTitle(period),
        'height': 360,
        backgroundColor: 'transparent', // Set the background color to transparent

        legend: {
            position: 'top',
            textStyle: {
                color: '#ffffff' // Set the legend text color to white
            }
        },

        series: {
            0: {
                targetAxisIndex: 0,
                color: '#3a9140' // Set the color for series 0 (lines) to #1deba8
            },
            1: {
                targetAxisIndex: 0,
                color: '#3a9191' // Set the color for series 1 (lines) to #1deba8
            },
            2: {
                targetAxisIndex: 1
            }
        },

        vAxes: {
            0: {
                title: 'Sales Amount',
                format: 'currency',
                textStyle: {
                    color: '#ffffff' // Set the vertical axis labels color to white
                },
                titleTextStyle: {
                    color: '#1deba8' // Set the title color to white
                },
                gridlines: {
                    color: '#ffffff',
                    count: 4
                }
            },
            1: {
                title: 'Number of Orders',
                textStyle: {
                    color: '#ffffff' // Set the vertical axis labels color to white
                },
                titleTextStyle: {
                    color: '#1deba8' // Set the title color to white
                },
                gridlines: {
                    color: 'transparent'
                }
            }
        },

        hAxis: {
            textStyle: {
                color: '#ffffff' // Set the horizontal axis labels color to white
            }
        },

        chartArea: {
            backgroundColor: 'transparent' // Set the chart area background color to transparent
        },

        titleTextStyle: {
            color: '#d79e12'
        }
    };

    var formatter = new google.visualization.NumberFormat({
        prefix: prefixCurrencySymbol,
        suffix: suffixCurrencySymbol,
        decimalSymbol: decimalPointType,
        groupingSymbol: thousandsPointType,
        fractionDigits: decimalDigits
    });

    formatter.format(data, 1);
    formatter.format(data, 2);
}

/**
 * Draws the sales chart on the web page.
 *
 * @param period the selected period
 */
function drawChart(period) {
    var salesChart = new google.visualization.ColumnChart(document.getElementById('chart_sales_by_date'));
    salesChart.draw(data, chartOptions);

    $("#textTotalGrossSales").text(formatCurrency(totalGrossSales));
    $("#textTotalNetSales").text(formatCurrency(totalNetSales));

    denominator = getDenominator(period);

    $("#textAvgGrossSales").text(formatCurrency(totalGrossSales / denominator));
    $("#textAvgNetSales").text(formatCurrency(totalNetSales / denominator));
    $("#textTotalOrders").text(totalOrders);
}

/**
 * Formats an amount as currency with the specified formatting options.
 *
 * @param amount the amount to format
 * @return the formatted currency string
 */
function formatCurrency(amount) {
    formattedAmount = $.number(amount, decimalDigits, decimalPointType, thousandsPointType);
    return prefixCurrencySymbol + formattedAmount + suffixCurrencySymbol;
}

/**
 * Retrieves the chart title based on the selected period.
 *
 * @param period the selected period
 * @return the chart title
 */
function getChartTitle(period) {
    if (period == "last_7_days") return "Sales in Last 7 Days";
    if (period == "last_28_days") return "Sales in Last 28 Days";
    if (period == "last_6_months") return "Sales in Last 6 Months";
    if (period == "last_1_year") return "Sales in Last 1 Year";
    if (period == "custom") return "Custom Date Range";

    return "";
}

/**
 * Retrieves the denominator for average sales calculation based on the selected period.
 *
 * @param period the selected period
 * @return the denominator
 */
function getDenominator(period) {
    if (period == "last_7_days") return 7;
    if (period == "last_28_days") return 28;
    if (period == "last_6_months") return 6;
    if (period == "last_1_year") return 12;
    if (period == "custom") return calculateDays();

    return 7;
}