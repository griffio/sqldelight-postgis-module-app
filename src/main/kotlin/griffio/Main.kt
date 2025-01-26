package griffio

import app.cash.sqldelight.dialect.api.SqlDelightModule
import app.cash.sqldelight.dialects.postgresql.PostgreSqlDialect
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import org.postgresql.ds.PGSimpleDataSource

private fun getSqlDriver() = PGSimpleDataSource().apply {
    setURL("jdbc:postgresql://localhost:5432/geo")
    applicationName = "App Main"
    user = "geo"
    password = "geo"
}.asJdbcDriver()

fun main() {
    val driver = getSqlDriver()
}
