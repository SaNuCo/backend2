package de.isweariwillhaveabackend.schema

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.scalars.ID
import de.isweariwillhaveabackend.model.FoodOfferingReactionState

class FoodOfferingInput(
    val title: String,
    val description: String?,
    val picture: String?,
    val location: List<Double>,
    val category: String
)

class UpdateFoodOfferingInput(
    val id: ID,
    val title: String?,
    val description: String?,
    val picture: OptionalInput<String>,
    val location: List<Double>?,
    val category: String?
)

class FoodOfferingReactionInput(
    val offering: ID,
    val state: FoodOfferingReactionState
)

class MessageInput(
    val chat: ID,
    val content: String,
    val files: List<MessageFileInput>,
    val replyTo: ID?
)

class MessageFileInput(
    val name: String,
    val type: String,
    val audio: Boolean,
    val duration: Double,
    val file: String
)

class UpdateMessageInput(
    val id: ID,
    val content: String?,
    val addedFiles: List<MessageFileInput>?,
    val removedFiles: List<ID>?
)

class UpdateMessageReactionsInput(
    val id: ID,
    val addedEmoji: List<String>?,
    val removedEmoji: List<String>?
)

class ChatInput(
    val offering: ID,
    val name: String,
    val users: List<ID>
)

class UpdateChatInput(
    val id: ID,
    val name: String?,
    val addedUsers: List<ID>?,
    val removedUsers: List<ID>?
)

class UpdateUserInput(
    val displayName: String,
    val avatar: OptionalInput<String>
)