package cs6018.lydiayuan.models

import cs6018.lydiayuan.DBSettings
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob

fun exposedBlobToBlob(exposedBlob: ExposedBlob): Blob {
    return SerialBlob(exposedBlob.bytes)
}

fun mapRowToDrawingObject(row: ResultRow): DrawingObject {
    return DrawingObject(
        id = row[Drawing.id].value,
        creatorId = row[Drawing.creatorId],
        title = row[Drawing.title],
        lastModifiedDate = row[Drawing.lastModifiedDate],
        createdDate = row[Drawing.createdDate],
        imagePath = row[Drawing.imagePath],
        // thumbnail = exposedBlobToBlob(row[Drawing.thumbnail])
    )
}

fun Application.configureResources() {
    install(Resources)
    routing {
        // Get all drawings
        get<Drawings> {
            call.respond(
                newSuspendedTransaction(Dispatchers.IO) {
                    Drawing
                        .selectAll()
                        .map {
                            mapRowToDrawingObject(it)
                        }
                }
            )
        }

        // get drawings by user id
        get<Drawings.UserId> {
            val userId = it.userId
            val drawings = newSuspendedTransaction(Dispatchers.IO) {
                Drawing.select { Drawing.creatorId eq userId }.map { resultRow ->
                    mapRowToDrawingObject(resultRow)
                }
            }
            call.respond(drawings)
        }

        // post a new drawing
        post<Drawings.Create> {
            val drawingData = call.receive<DrawingData>()

            val thumbnailData: ByteArray = byteArrayOf(0x48, 0x65, 0x6C, 0x6C, 0x6F)

            val drawingId = newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                Drawing.insertAndGetId {
                    it[creatorId] = drawingData.creatorId
                    it[title] = drawingData.title
                    it[lastModifiedDate] = System.currentTimeMillis()
                    it[createdDate] = System.currentTimeMillis()
                    it[imagePath] = drawingData.imagePath
                    // it[thumbnail] = ExposedBlob(thumbnailData)
                }
            }

            call.respond(HttpStatusCode.Created, "Drawing created with title: ${drawingData.title}, by: ${drawingData.creatorId}")
        }
    }
}

// TODO: make thumbnail a Blob work
@Serializable
data class DrawingData(val creatorId: String, val title: String, val imagePath: String)

@Resource("/drawings")
class Drawings {
    @Resource("{userId}")
    class UserId(val parent: Drawings = Drawings(), val userId: String)

    @Resource("create")
    class Create(val parent: Drawings = Drawings())
}