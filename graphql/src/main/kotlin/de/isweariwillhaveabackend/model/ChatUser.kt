package de.isweariwillhaveabackend.model

import io.github.graphglue.model.Direction
import io.github.graphglue.model.DomainNode
import io.github.graphglue.model.Node
import io.github.graphglue.model.NodeRelationship
import java.time.OffsetDateTime
import org.springframework.data.annotation.Transient

@DomainNode
class ChatUser(
    var readUntil: Int,
    var isTyping: Boolean
) : Node() {
    companion object {
        const val USER = "USER"
    }

    @NodeRelationship(USER, Direction.OUTGOING)
    @delegate:Transient
    val user by NodeProperty<User>()
}