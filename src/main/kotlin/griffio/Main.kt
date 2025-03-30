package griffio

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import griffio.migrations.Locations
import griffio.queries.Sample
import net.postgis.jdbc.geometry.Point
import net.postgis.jdbc.geometry.binary.BinaryParser
import net.postgis.jdbc.geometry.binary.BinaryWriter
import org.postgresql.ds.PGSimpleDataSource

private fun getSqlDriver() = PGSimpleDataSource().apply {
    setURL("jdbc:postgresql://localhost:5432/geo")
    applicationName = "App Main"
    user = "postgres"
    password = ""
}.asJdbcDriver()

private val pointAdapter = object : ColumnAdapter<Point, String> {
    override fun encode(value: Point) = BinaryWriter().writeHexed(value)
    override fun decode(databaseValue: String) = BinaryParser().parse(databaseValue) as Point
}

private val adapters = Locations.Adapter(pointAdapter, pointAdapter, pointAdapter, pointAdapter)

fun main() {
    val driver = getSqlDriver()
    val sample = Sample(driver, adapters)

    // Insert points using different PostGIS functions
    sample.geoQueries.insertMakePoint(
        x = 40.7128,
        y = -74.0060,
        srid = 4326
    )

    sample.geoQueries.insertPointZM(
        x = 40.7128,
        y = -74.0060,
        z = 10.0,
        m = 1.0,
        srid = 4326
    )

    // Query all locations
    sample.geoQueries.select().executeAsList().forEach { location ->
        println("Location: ${location.name}")
    }

    // Check if two points are within a certain distance
    val withinDistance = sample.geoQueries.selectWithin(
        distanceMeters = 1000000.0,
        useSpheroid = true
    ).executeAsOne()
    println("Points are within distance: $withinDistance")

    // Query locations within distance of a geometry
    sample.geoQueries.selectLocationByDistance(
        geometry = "POINT(40.7128 -74.0060)",
        distanceMeters = 5000.0,
        useSpheroid = true
    ).executeAsList().forEach { (id, name) ->
        println("Nearby location: $name (id: $id)")
    }

    // Get 2D representation of points
    val nullPoint2d = sample.geoQueries.selectSingleForce2d().executeAsOneOrNull()
    println("2D point: $nullPoint2d")

    sample.geoQueries.insertPointZM(-71.104, 42.315, 3.4, 4.5, 4326)

    sample.geoQueries.selectSTAsText().executeAsList().forEach {
        println("$it")
    }
}
