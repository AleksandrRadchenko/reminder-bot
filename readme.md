### About
Stupid simple task manager as Telegram bot @TimerReminderBot (http://t.me/TimerReminderBot). 

### Prerequisites
JDK 17

### How to run
Specify Telegram bot token via `BOT_TOKEN` environment variable.
For example: `java -DBOT_TOKEN=<your_token> -jar reminder-1.0-SNAPSHOT.jar`

### Database
Set password: `$env:POSTGRES_PASSWORD='...'`

Start compose: `docker-compose up .`

### Database migration
Set liquibase properties in maven settings file (ex: %MAVEN_HOME%\.m2\settings.xml):
```xml
<profiles>
    <profile>
        <id>local</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <liquibase.url>jdbc:postgresql://localhost:12013/reminder</liquibase.url>
            <liquibase.username>...</liquibase.username>
            <liquibase.password>...</liquibase.password>
        </properties>
    </profile>
    <profile>
        <id>serv</id>
        <properties>
            <liquibase.url>jdbc:postgresql://${server_path}:12013/reminder</liquibase.url>
            <liquibase.username>...</liquibase.username>
            <liquibase.password>...</liquibase.password>
        </properties>
    </profile>
</profiles>
```
#### Liquibase update
`mvn liquibase:update -P local`
#### Liquibase rollback
`mvn liquibase:rollback -Dliquibase.rollbackCount=1 -P local`