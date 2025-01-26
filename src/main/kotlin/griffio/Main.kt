package griffio

import app.cash.sqldelight.dialect.api.SqlDelightModule
import app.cash.sqldelight.dialects.postgresql.PostgreSqlDialect
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import org.postgresql.ds.PGSimpleDataSource
import java.util.ServiceLoader

private fun getSqlDriver() = PGSimpleDataSource().apply {
    setURL("jdbc:postgresql://localhost:5432/srs")
    applicationName = "App Main"
    user = "srs"
    password = "srs"
}.asJdbcDriver()

fun main() {
    val driver = getSqlDriver()
}
