# mobilespoilers

![Java CI with Maven](https://github.com/markhenrick/mobilespoilers/workflows/Java%20CI%20with%20Maven/badge.svg) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This bot is a temporary solution for a minor UI problem on [Discord](https://discordapp.com/) - there is no way to mark an image as a spoiler on mobile

This bot adds a `!spoil` command to do that

I've deliberately over-engineered this a bit to make it more fun. The main complication is that it persists who owns what spoilers, so the user can delete it later

It is currently very much a WIP

# Invite

There is a public instance of this bot which can be invited with [this](https://discordapp.com/oauth2/authorize?client_id=699048830112366632&scope=bot&permissions=322624) link

# Setup

* You will need an empty PostgreSQL database
  * Security-lax quick start: `docker run -e POSTGRES_USER=mobilespoilers -e POSTGRES_PASSWORD=mobilespoilers -p 5432:5432 -d postgres:12` (docker-compose [coming soon](https://github.com/markhenrick/mobilespoilers/issues/11) (tm)!)
  * I would have used SQLite, but Spring-data doesn't support it at the moment
  * The user will need `create table`, `insert`, `select`, and `delete` privileges. It might be possible to seperate the first from the rest by using `spring.flyway.user/password` properties
  * Other databases will probably work as long as there is a JDBC driver on the classpath, Spring supports it, and it can execute the migrations (`src/main/resources/db/migration`)
* Mandatory configuration can be found in `application.example.yml`. Edit and rename to `application.yml` in the directory of execution
* Optional configuration can be found in `src/main/resources/application.yml`, or by reading `MobileSpoilersConfig.java`
* A fat jar can be generated by `./mvnw package`
