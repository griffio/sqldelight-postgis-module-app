package griffio

import app.cash.sqldelight.dialect.api.DialectType
import app.cash.sqldelight.dialect.api.IntermediateType
import app.cash.sqldelight.dialect.api.PrimitiveType.BOOLEAN
import app.cash.sqldelight.dialect.api.SqlDelightModule
import app.cash.sqldelight.dialect.api.TypeResolver
import app.cash.sqldelight.dialects.postgresql.PostgreSqlTypeResolver
import com.alecstrong.sql.psi.core.psi.SqlFunctionExpr
import com.alecstrong.sql.psi.core.psi.SqlTypeName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import griffio.grammar.PostgisParserUtil
import griffio.grammar.psi.PostGisTypeName

class PostGisModule : SqlDelightModule {
    override fun typeResolver(parentResolver: TypeResolver): TypeResolver = PostGisTypeResolver(parentResolver)

    override fun setup() {
        PostgisParserUtil.reset()
        PostgisParserUtil.overridePostgreSqlParser()
    }
}

enum class PostGisSqlType(override val javaType: TypeName) : DialectType {
    GEOMETRY(STRING), GEOGRAPHY(STRING);

    override fun prepareStatementBinder(columnIndex: CodeBlock, value: CodeBlock): CodeBlock {
        return when (this) {
            GEOMETRY, GEOGRAPHY -> CodeBlock.of("bindString(%L, %L)\n", columnIndex, value)
        }
    }

    override fun cursorGetter(columnIndex: Int, cursorName: String): CodeBlock {
        return CodeBlock.of(
            when (this) {
                GEOMETRY, GEOGRAPHY -> "$cursorName.getString($columnIndex)"
            },
            javaType,
        )
    }
}

// Change to inheritance so that definitionType can be called by polymorphism - not possible with delegation
private class PostGisTypeResolver(private val parentResolver: TypeResolver) : PostgreSqlTypeResolver(parentResolver) {

    override fun definitionType(typeName: SqlTypeName): IntermediateType = with(typeName) {
        check(this is PostGisTypeName)
        val type = (when {
            geometryDataType != null -> IntermediateType(PostGisSqlType.GEOMETRY)
            geographyDataType != null -> IntermediateType(PostGisSqlType.GEOGRAPHY)
            else -> super.definitionType(typeName)
        })
        return type
    }

    override fun functionType(functionExpr: SqlFunctionExpr): IntermediateType? =
        when (functionExpr.functionName.text.lowercase()) {
            "st_geographyfromtext" -> IntermediateType(PostGisSqlType.GEOGRAPHY)
            "st_dwithin" -> IntermediateType(BOOLEAN)
            "st_force2d" -> IntermediateType(PostGisSqlType.GEOMETRY)
            else -> parentResolver.functionType(functionExpr)
        }
}
