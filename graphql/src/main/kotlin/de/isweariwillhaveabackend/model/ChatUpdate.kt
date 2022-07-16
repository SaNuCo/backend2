package de.isweariwillhaveabackend.model

import com.expediagroup.graphql.generator.scalars.ID

class ChatUpdate(
    val newMessages: List<Message>,
    val updatedMessages: List<Message>,
    val deletedMessages: List<ID>,
    val addedUsers: List<ChatUser>,
    val updatedUsers: List<ChatUser>,
    val removedUsers: List<ID>
    )