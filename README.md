#kart works

##Quick overview
- This is an API, standard play framework application
- The main (and only useful) controller is the `controllers.KartReportController`
- Each feature in the test proposal is a `Report Strategy`, each strategy generate a report. It's really easy for introducing new reports, just create one within the `reports.strategies` package and inject it in the `KartReportController`.
- One can ask different or even multiple reports at the same time, just comma separate the names of the reports. Try the API with wrong report name and it will return the available reports you should use
- There's a middle data transforming layer, that layer is used for mapping and processing patterns that are common to multiple reports. All transformations are in the package `reports.transforms`



##Main proposed goals

####Fault tolerant by design
Taking advantage of the rich scala libs, the validations and error are considered as an expected flow, that way the code is explicitly showing failure prone transformations. An example of it in code is the error propagation on the `middle transformation layer`, within the package `transforms`. Another is the `Either` usage on the endpoint validation. 

####