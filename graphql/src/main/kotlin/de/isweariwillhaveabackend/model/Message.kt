package de.isweariwillhaveabackend.model

import io.github.graphglue.model.Direction
import io.github.graphglue.model.DomainNode
import io.github.graphglue.model.Node
import io.github.graphglue.model.NodeRelationship
import java.time.OffsetDateTime
import org.springframework.data.annotation.Transient

@DomainNode
class Message(
    var content: String,
    var timestamp: OffsetDateTime,
    var distributed: Boolean,
    var seen: Boolean,
) : Node() {
    companion object {
        const val SENDER = "SENDER"
        const val FILE = "FILE"
        const val REACTION = "REACTION"
        const val REPLY_TO = "REPLY_TO"
    }

    @NodeRelationship(SENDER, Direction.OUTGOING)
    @delegate:Transient
    val sender by NodeProperty<User>()

    @NodeRelationship(FILE, Direction.OUTGOING)
    @delegate:Transient
    val files by NodeSetProperty<MessageFile>()

    @NodeRelationship(REACTION, Direction.OUTGOING)
    @delegate:Transient
    val reactions by NodeSetProperty<MessageReaction>()

    @NodeRelationship(REPLY_TO, Direction.OUTGOING)
    @delegate:Transient
    val replyTo by NodeProperty<Message>()
}