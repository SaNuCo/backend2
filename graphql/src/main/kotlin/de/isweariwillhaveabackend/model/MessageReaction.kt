package de.isweariwillhaveabackend.model

import io.github.graphglue.model.Direction
import io.github.graphglue.model.DomainNode
import io.github.graphglue.model.Node
import io.github.graphglue.model.NodeRelationship
import org.springframework.data.annotation.Transient

@DomainNode
class MessageReaction(
    val emoji: String
) : Node() {
    companion object {
        const val USER = "USER"
    }

    @NodeRelationship(USER, Direction.OUTGOING)
    @delegate:Transient
    val users by NodeSetProperty<User>()

    @NodeRelationship(Message.REACTION, Direction.INCOMING)
    @delegate:Transient
    val message by NodeProperty<Message>()
}