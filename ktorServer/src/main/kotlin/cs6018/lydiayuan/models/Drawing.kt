package cs6018.lydiayuan.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import java.sql.Blob

@Serializable
data class DrawingObject(
    val id: Int,
    val creatorId: String,
    val title: String,
    val lastModifiedDate: Long,
    val createdDate: Long,
    val imagePath: String,
    // val thumbnail: Blob
)

object Drawing : IntIdTable() {
    val creatorId = varchar("creatorId", 255)
    val title = varchar("title", 255)
    val lastModifiedDate = long("lastModifiedDate")
    val createdDate = long("createdDate")
    val imagePath = varchar("imagePath", 255)
    // val thumbnail = blob("thumbnail")
}