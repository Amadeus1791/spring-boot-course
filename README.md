# Event booking: Spring Boot in practice

The project of the course modules 3 to 5, Java 21 and Spring Boot 4.1.

## Get started

```
git clone <course repository>
git config core.longpaths true        (Windows only, once)
```

In IntelliJ: File, Open, then the chapter folder, for example `3.1-from-zero`. It finds all Maven projects.
Start `PaymentServiceApp`, then `EventBookingApp`. The requests in `event-booking/requests/*.http`
run with the arrow next to each request. New chapters arrive with `git pull`.

## One folder per chapter

| Folder | Chapter |
|---|---|
| `3.1-from-zero` | 3.1 From zero to running service |

Each folder is the project of its chapter: the code of the slides, the stand-ins and the checks of your lab.
Open the folder at the start of the chapter and follow along in your IDE. Compare a folder with the next one
to see what the chapter changed: in IntelliJ select both folders, right click, Compare Directories.

Inside a folder:

| Project | What it is | Port | Start class |
|---|---|---|---|
| `event-booking` | the service we build in the course | 8080 | `EventBookingApp` |
| `payment-service` | a stand-in for the payment system, you only start it | 8081 | `PaymentServiceApp` |

## The documents

`documents/` holds the slides, the lab booklet and the reference sheet of module 3 (`documents/module3`).

## Your labs

Your labs run in your own review service, created in lab 3.1 with the Initializr. Keep it next to this repository
and continue it chapter by chapter. 

The reference solution of your labs comes after each module, as the project `review-service`:
the labs of module 3 in `4.1-testing`, of module 4 in `5.1-production`, of module 5 in `5.4-final`.
Compare it with your own service the same way, with Compare Directories.

## Checking a lab

From lab 3.2 on, every lab ends with "Check it": start your service and the stand-ins the lab names, then run
the checks of the lab from `review-checks/` of the current chapter folder. In IntelliJ IDEA Ultimate the file
`review-checks-X.Y.http` (Run All Requests in File), in Postman the collection `review-checks.postman_collection.json`
with the environment `review-checks-local.postman_environment.json`, in the folders the lab names. Each chapter
folder brings the checks up to its chapter: import the collection of the new folder, it replaces the older one.

## Maven in the terminal

The slides and the lab booklet write `mvn test` or `mvn package`. No Maven installed, or an old one?
Every project brings the Maven Wrapper: in its folder, type `./mvnw` instead of `mvn` (Windows: `mvnw.cmd`,
in PowerShell `.\mvnw`), for example `./mvnw test` in `event-booking`. The first call downloads the right
Maven version once. `./mvnw -v` shows whether it works. Your own review service has one from the Initializr.
