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
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
        // Get all drawings ordered by last modified date descending
        get<Drawings> {
            call.respond(
                newSuspendedTransaction(Dispatchers.IO) {
                    Drawing
                        .selectAll()
                        .orderBy(Drawing.lastModifiedDate, SortOrder.DESC)
                        .map {
                            mapRowToDrawingObject(it)
                        }
                }
            )
        }

        // get drawings created by user id order by last modified date descending
        get<Drawings.User.UserId.History> {
            val userId = it.parent.userId
            val drawings = newSuspendedTransaction(Dispatchers.IO) {
                Drawing.select { Drawing.creatorId eq userId }
                    .orderBy(Drawing.lastModifiedDate, SortOrder.DESC)
                    .map { resultRow ->
                        mapRowToDrawingObject(resultRow)
                    }
            }
            call.respond(drawings)
        }

        // get feed by user id, aka every other drawing from other users order by last modified date descending
        get<Drawings.User.UserId.Feed> {
            val userId = it.parent.userId
            val drawings = newSuspendedTransaction(Dispatchers.IO) {
                Drawing.select { Drawing.creatorId neq userId }.map { resultRow ->
                    mapRowToDrawingObject(resultRow)
                }
            }
            call.respond(drawings)
        }

        // post a new drawing
        post<Drawings.Create> {
            val drawingData = call.receive<DrawingData>()
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

            call.respond(
                HttpStatusCode.Created,
                "Drawing $drawingId created with title: ${drawingData.title}, by: ${drawingData.creatorId}"
            )
        }

        // get drawing by id
        get<Drawings.Drawing.DrawingId> {
            val drawingId = it.drawingId
            val drawing = newSuspendedTransaction(Dispatchers.IO) {
                Drawing.select { Drawing.id eq drawingId }.map { resultRow ->
                    mapRowToDrawingObject(resultRow)
                }
            }
            call.respond(drawing)
        }

        // update a drawing by id
        put<Drawings.Drawing.DrawingId> { it ->
            val drawingId = it.drawingId
            val drawingData = call.receive<DrawingData>()
            newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                Drawing.update({ Drawing.id eq drawingId }) { updateStatement ->
                    updateStatement[title] = drawingData.title
                    updateStatement[lastModifiedDate] = System.currentTimeMillis()
                    // it[thumbnail] = ExposedBlob(thumbnailData)
                }
            }
            call.respond(HttpStatusCode.OK, "Drawing $drawingId updated")
        }

        // delete a drawing by id
        delete<Drawings.Drawing.DrawingId> { it ->
            val drawingId = it.drawingId
            newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                Drawing.deleteWhere { Drawing.id eq drawingId }
            }
            call.respond(HttpStatusCode.OK, "Drawing $drawingId deleted")
        }
    }
}

// TODO: make thumbnail a Blob work
@Serializable
data class DrawingData(val creatorId: String, val title: String, val imagePath: String)

@Resource("/drawings")
class Drawings {

    @Resource("user")
    class User(val parent: Drawings = Drawings()) {
        @Resource("{userId}")
        class UserId(val parent: User = User(), val userId: String) {
            @Resource("history")
            class History(val parent: UserId = UserId(parent = User(), userId = ""))
            @Resource("feed")
            class Feed(val parent: UserId = UserId(parent = User(), userId = ""))
        }
    }

    @Resource("drawing")
    class Drawing(val parent: Drawings = Drawings()) {
        @Resource("{drawingId}")
        class DrawingId(val parent: Drawing = Drawing(), val drawingId: Int)
    }

    @Resource("create")
    class Create(val parent: Drawings = Drawings())
}