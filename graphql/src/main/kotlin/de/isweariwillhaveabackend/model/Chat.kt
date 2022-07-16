package de.isweariwillhaveabackend.model

import io.github.graphglue.model.Direction
import io.github.graphglue.model.DomainNode
import io.github.graphglue.model.Node
import io.github.graphglue.model.NodeRelationship
import org.springframework.data.annotation.Transient

@DomainNode("chats")
class Chat(
    var name: String
) : Node() {
    companion object {
        const val MESSAGE = "MESSAGE"
    }

    @NodeRelationship(User.CHAT, Direction.INCOMING)
    @delegate:Transient
    val users by NodeSetProperty<ChatUser>()

    @NodeRelationship(FoodOffering.CHAT, Direction.INCOMING)
    @delegate:Transient
    val foodOffering by NodeProperty<FoodOffering>()
}