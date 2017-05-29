# active-toggle

A Clojure program that obtains time entries via [Toggl](https://toggl.com)'s
REST API and outputs time entries as CSV.

## Usage

To access your Toggle account you need your account's API token, see your [Toggle
profile](https://toggl.com/app/profile).

Then you can run the program like this:

    lein run <api-token> <start-date> [<end-date> <project>]

The dates for `start-date` and optional `end-date` need to be specified as
`yyyy-mm-dd`.  If no end date is specified, today is assumed.  `project` is an
optional project name that the program uses to filter time entries.

For example, running

    lein run 0123456789ABCDEF 2017-05-01 2017-05-31 cleaning
    
returns all time entries that are in a project called "cleaning" for the month
of May 2017:

    "date","duration","description"
    "2017-05-02","1.00","Kitchen"
    "2017-05-08","2.00","Bathroom"
    "2017-05-15","5.00","Office"
    "2017-05-16","0.75","Garage"
    "2017-05-22","1.50","Basement"

## License

Copyright © 2017 Active Group GmbH

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
