# SqlDelight 2.1.x Postgresql Postgis module support prototype 

https://github.com/cashapp/sqldelight

**Experimental**

Use with SqlDelight 2.1.0
---

Instead of a new dialect or adding PostgreSql extensions into the core PostgreSql grammar e.g. https://postgis.net/ and
https://github.com/pgvector/pgvector

Use a custom SqlDelight module to implement grammar and type resolvers for simple PostGIS operations

```kotlin
sqldelight {
    databases {
        create("Sample") {
            deriveSchemaFromMigrations.set(true)
            migrationOutputDirectory = file("$buildDir/generated/migrations")
            migrationOutputFileFormat = ".sql"
            packageName.set("griffio.queries")
            dialect(libs.sqldelight.postgresql.dialect)
            module(project(":postgis-module")) // module can be local project
           // or external module("io.github.griffio:sqldelight-postgis:0.0.1")
        }
    }
}
```

`postgis-module` published in Maven Central https://central.sonatype.com/artifact/io.github.griffio/sqldelight-postgis/versions

`io.github.griffio:sqldelight-postgis:0.0.1`

**NOTES**
There are problems extending an existing grammar through more than one level of inheritance. This would require fixes to
https://github.com/sqldelight/Grammar-Kit-Composer.

Duplication of PostgreSql data types are required unless external parser rules are created manually.

PostgreSqlTypeResolver uses inheritance rather than use delegation as needs polymorphic calls.

```shell
createdb geo && 
./gradlew build &&
./gradlew flywayMigrate
```
