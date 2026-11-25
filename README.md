# Event booking: Spring Boot in practice

The project of the course modules 3 to 5, Java 21 and Spring Boot 4.1.

## Get started

```
git clone <course repository>
git config core.longpaths true        (Windows only, once)
```

In IntelliJ: File, Open, then the chapter folder, for example `3.1-from-zero`. It finds all Maven projects.
Start `EventBookingApp` (from 3.4 on, start `PaymentServiceApp` first). The requests in
`event-booking/requests/*.http` run with the arrow next to each request. New chapters arrive with `git pull`.

## One folder per chapter

| Folder | Chapter |
|---|---|
| `3.1-from-zero` | 3.1 From zero to running service |
| `3.2-rest-apis` | 3.2 REST APIs done right |
| `3.3-persistence` | 3.3 Persistence with Spring Data JPA |
| `3.4-calling-services` | 3.4 Calling other services |

Each folder is the project of its chapter: the code of the slides, the stand-ins and the requests of your lab.
Open the folder at the start of the chapter and follow along in your IDE. Compare a folder with the next one
to see what the chapter changed: in IntelliJ select both folders, right click, Compare Directories.

Inside a folder:

| Project | What it is | Port | Start class |
|---|---|---|---|
| `event-booking` | the service we build in the course | 8080 | `EventBookingApp` |
| `payment-service` | from 3.4: a stand-in for the payment system, you only start it | 8081 | `PaymentServiceApp` |
| `review-provided/event-service` | from 3.4: a stand-in for the event catalogue your review service asks | 8084 | `EventServiceApp` |

## The documents

`documents/` holds the slides, the lab booklet and the reference sheet of module 3 (`documents/module3`).

## Your labs

Your labs run in your own review service, created in lab 3.1 with the Initializr. Keep it next to this repository
and continue it chapter by chapter. In a chapter folder, what your labs need is in `review-provided/` and
`review-requests/`, the rest is the code of the slides. For lab 3.2, the table and the database settings are given in `3.2-rest-apis/review-provided/`.

The solutions of your labs are in `solutions/`, one folder per lab, after the review of the lab:
`lab-3.1-review-service`, `lab-3.2-review-service`.
Compare a solution with your own service the same way, with Compare Directories.

## Checking a lab

From lab 3.2 on, every lab ends with "Check it": start your service fresh and the stand-ins the lab names, then
send the requests of the table and compare each answer with the table. The requests are ready in `review-requests/`
of the chapter folder: in IntelliJ IDEA Ultimate the file `review-requests-X.Y.http`, in Postman the collection
`review-requests.postman_collection.json` with the environment `review-requests-local.postman_environment.json`,
one folder per lab. Where a stand-in needs a switch or must be stopped, the name of the request says so.

## Maven in the terminal

The slides and the lab booklet write `mvn test` or `mvn package`. No Maven installed, or an old one?
Every project brings the Maven Wrapper: in its folder, type `./mvnw` instead of `mvn` (Windows: `mvnw.cmd`,
in PowerShell `.\mvnw`), for example `./mvnw test` in `event-booking`. The first call downloads the right
Maven version once. `./mvnw -v` shows whether it works. Your own review service has one from the Initializr.
