#kart works

##Quick overview
- This is an API, standard play framework application
- The main (and only useful) controller is the `controllers.RaceReportController`
- A term that is extensively used in this doc is `report`. It's basically an abstraction of feature  asked in the test proposal. `Report Strategy`'s are the components that generate the reports. It's really easy for introducing new reports, just create one within the `reports.strategies` package and inject it in the `Module`.
- One can ask different or even multiple reports at the same time, just comma separate the names of the reports. Try the API with wrong report name and it will return the available reports you should use
- There's a middle data transforming layer, that layer is used for mapping and processing patterns that are common to multiple reports. All transformations are in the package `reports.transforms`



##Main proposed goals

####Fault tolerant by design
Taking advantage of the rich scala libs, the validations and error are considered as an expected flow, that way the code is explicitly showing failure prone transformations. An example of it in code is the error propagation on some key points of the application.
Of course there may be unhandled cases and uncovered scenarios, but the idea was not to focus only on this topic and make an ultimate failproof application 

####Low incidental code complexity
(https://pressupinc.com/blog/2014/05/root-causes-software-complexity/)
First of all I need to start stating that the problem to be solved itself was really simple and with a dozen lines of code all functional requirements could have been achieved.
Anyway, in order to demonstrate some design points, it have been considerably "over engineered". In a real life project, I'd definitely argue if all burden added to solve this simple problem was really needed.
Acknowledging that, my goal regarding not adding incidental code complexity is present in all the main `core business` related code.

#####Parsing the input file example
Parsing an input text can become really complex and mainly error prone. I used mainly regexes on parsing and validation. 
Well, in fact some developers may hate regexes and find it all too complex, and to avoid that I let specific patterns for each column, so one can read/understand/modify the pattern for each column without getting a massive headache.
![p](https://i.imgur.com/9I4HN0N.png)

###Simple and extensible reports 
This is the main design goal on this project. The idea was to have:
- A extensible architecture where reports could be easily added
- Avoid code duplication, and reuse common logic for all possible reports
- (again) Avoid incidental complexity

To achieve that there are three main concepts:
#####1) Shared Intermediate layer `transforms`
I added a composable intermediate layer, internally called `transforms`. Those transforms basically process/aggregate/enrich the input lap log entries so the report classes have more cleaner and easy to understand code
That way, reports can be composed with transforms and do some really simple logic, bellow an example:

#####2) Common interface for all reports
Reports are basically a transformation of some data into an output format, so is the `common report interface`. Using a common interface it becomes possible to add as many features as needed without having the need to add specific handling and merging code for each new one.

#####3) "End-to-end composability"
Well, no one likes having to refactor all layers of the application when a simple change or new feature is needed, right? To avoid this, the design allows a new report to be added simply doing two things:
- Extend the `ReportStrategy` trait
- Inject it using `Module`

That's it! The report will be available in the API using the name you defined for it.

Bellow one example, the "default" report, which have all the mandatory information asked in the test, which is:
`Posição Chegada, Código Piloto, Nome Piloto, Qtde Voltas Completadas e Tempo Total de Prova`
This is the whole code for this report
![p](https://i.imgur.com/PbT7OO4.png)
All reports classes have less than 30 LoC

##Running the application
- Import it in your IDE as an SBT project
- RUN (it will be available in the 9000 port)

##Testing
- Simply run `sbt test` in the project root directory

##Consuming the API
The only API in the project, apart from the `healthCheck` endpoint is the report creation one.

`http://localhost:9000/api/kart/report/{report_name_separated_by_comma}`

Here is an HTTP code for the test sample data:
(if you are using postman, just import this collection: https://www.getpostman.com/collections/185146a7f4f299301d95)

``` 
POST /api/kart/report/best-lap,time-after-winner,best-pilot-lap,average-speed,default HTTP/1.1
Host: localhost:9000
Cache-Control: no-cache

Hora                               Piloto             Nº Volta   Tempo Volta       Velocidade média da volta
23:49:08.277      038 – F.MASSA                           1		1:02.852                        44,275
23:49:10.858      033 – R.BARRICHELLO                     1		1:04.352                        43,243
23:49:11.075      002 – K.RAIKKONEN                       1             1:04.108                        43,408
23:49:12.667      023 – M.WEBBER                          1		1:04.414                        43,202
23:49:30.976      015 – F.ALONSO                          1		1:18.456			35,47
23:50:11.447      038 – F.MASSA                           2		1:03.170                        44,053
23:50:14.860      033 – R.BARRICHELLO                     2		1:04.002                        43,48
23:50:15.057      002 – K.RAIKKONEN                       2             1:03.982                        43,493
23:50:17.472      023 – M.WEBBER                          2		1:04.805                        42,941
23:50:37.987      015 – F.ALONSO                          2		1:07.011			41,528
23:51:14.216      038 – F.MASSA                           3		1:02.769                        44,334
23:51:18.576      033 – R.BARRICHELLO		          3		1:03.716                        43,675
23:51:19.044      002 – K.RAIKKONEN                       3		1:03.987                        43,49
23:51:21.759      023 – M.WEBBER                          3		1:04.287                        43,287
23:51:46.691      015 – F.ALONSO                          3		1:08.704			40,504
23:52:01.796      011 – S.VETTEL                          1		3:31.315			13,169
23:52:17.003      038 – F.MASS                            4		1:02.787                        44,321
23:52:22.586      033 – R.BARRICHELLO		          4		1:04.010                        43,474
23:52:22.120      002 – K.RAIKKONEN                       4		1:03.076                        44,118
23:52:25.975      023 – M.WEBBER                          4		1:04.216                        43,335
23:53:06.741      015 – F.ALONSO                          4		1:20.050			34,763
23:53:39.660      011 – S.VETTEL                          2		1:37.864			28,435
23:54:57.757      011 – S.VETTEL                          3		1:18.097			35,633
```

The result is a Json, with the following standard:
```
{ 
  "reports": [
    { "reportName": "XXX", "content" : { xContent },
    { "reportName": "YYY", "content" : { yContent }
   ]
}
```
The following is the result of the above request (some parts were cut to avoid being too big)
```
{
    "reports": [
        {
            "reportName": "best-lap",
            "content": {
                "name": "F.MASSA",
                "lap": 3,
                "time": "1:02.769"
            }
        },
        {
            "reportName": "time-after-winner",
            "content": [
                {
                    "pilotNumber": "038",
                    "name": "F.MASS",
                    "timeAfterWinner": "PT0S"
                }
                ...
                {
                    "pilotNumber": "011",
                    "name": "S.VETTEL",
                    "timeAfterWinner": "PT135.698S"
                }
            ]
        },
        {
            "reportName": "best-pilot-lap",
            "content": [
                {
                    "pilotNumber": "038",
                    "name": "F.MASSA",
                    "bestLap": 3,
                    "totalTime": "1:02.769"
                }
                ...
                {
                    "pilotNumber": "011",
                    "name": "S.VETTEL",
                    "bestLap": 3,
                    "totalTime": "1:18.097"
                }
            ]
        },
        {
            "reportName": "average-speed",
            "content": [
                {
                    "pilotNumber": "038",
                    "name": "F.MASS",
                    "averageRaceSpeed": 44.285
                },
                ...
                {
                    "pilotNumber": "011",
                    "name": "S.VETTEL",
                    "averageRaceSpeed": 28.2175
                }
            ]
        },
        {
            "reportName": "default",
            "content": [
                {
                    "position": 1,
                    "pilotNumber": "038",
                    "name": "F.MASS",
                    "totalLaps": 4,
                    "totalTime": "PT251.578S"
                },
                ...
                {
                    "position": 6,
                    "pilotNumber": "011",
                    "name": "S.VETTEL",
                    "totalLaps": 3,
                    "totalTime": "PT387.276S"
                }
            ]
        }
    ]
}

```