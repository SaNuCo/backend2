package de.isweariwillhaveabackend.schema

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.scalars.ID
import de.isweariwillhaveabackend.graphql.user
import de.isweariwillhaveabackend.model.*
import de.isweariwillhaveabackend.repositories.NodeRepository
import de.isweariwillhaveabackend.repositories.UserRepository
import de.isweariwillhaveabackend.repositories.findById
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import java.time.OffsetDateTime


@org.springframework.stereotype.Component
class Mutation(val nodeRepository: NodeRepository, val userRepository: UserRepository) :
    com.expediagroup.graphql.server.operations.Mutation {

    suspend fun createFoodOffering(input: FoodOfferingInput, dfe: DataFetchingEnvironment): FoodOffering {
        val offering = FoodOffering(input.title, input.description ?: "", input.picture, input.location, input.category)
        offering.offeredBy().value = dfe.user
        return nodeRepository.save(offering).awaitSingle()
    }

    suspend fun updateFoodOffering(input: UpdateFoodOfferingInput, dfe: DataFetchingEnvironment): FoodOffering {
        val offering = nodeRepository.findById(input.id) as FoodOffering
        if (offering.offeredBy().value != dfe.user) {
            throw IllegalArgumentException("other user than expected")
        }
        if (input.title != null) {
            offering.title = input.title
        }
        if (input.description != null) {
            offering.description = input.description
        }
        if (input.category != null) {
            offering.category = input.category
        }
        if (input.picture is OptionalInput.Defined) {
            offering.picture = input.picture.value
        }
        if (input.location != null) {
            offering.location = input.location
        }
        return nodeRepository.save(offering).awaitSingle()
    }

    suspend fun setFoodOfferingReaction(
        input: FoodOfferingReactionInput,
        dfe: DataFetchingEnvironment
    ): FoodOfferingReaction {
        val offering = nodeRepository.findById(input.offering) as FoodOffering
        val existingReaction = offering.reactions().firstOrNull { it.user().value == dfe.user }
        return if (existingReaction == null) {
            if (offering.offeredBy().value == dfe.user) {
                throw IllegalArgumentException("Muste be reacted by other user")
            }
            val reaction = FoodOfferingReaction(input.state)
            reaction.user().value = dfe.user
            reaction.offering().value = offering
            nodeRepository.save(reaction).awaitSingle()
        } else {
            if (existingReaction.state == FoodOfferingReactionState.LIKE || existingReaction.state == FoodOfferingReactionState.DISLIKE) {
                existingReaction.state = input.state
            }
            nodeRepository.save(existingReaction).awaitSingle()
        }
    }

    suspend fun createChat(input: ChatInput, dfe: DataFetchingEnvironment): Chat {
        val offering = nodeRepository.findById(input.offering) as FoodOffering
        if (offering.offeredBy().value != dfe.user) {
            throw IllegalArgumentException("Chat must be created by same person as offering")
        }
        val users = findUsersById(input.users)
        val chat = Chat(input.name)
        chat.foodOffering().value = offering
        chat.users().addAll((users + dfe.user).map {
            val chatUser = ChatUser(0, false)
            chatUser.user().value = it
            chatUser
        })
        return nodeRepository.save(chat).awaitSingle()
    }

    suspend fun updateChat(input: UpdateChatInput, dfe: DataFetchingEnvironment): Chat {
        val chat = nodeRepository.findById(input.id) as Chat
        if (chat.foodOffering().value.offeredBy().value != dfe.user) {
            throw IllegalArgumentException("Chat can only be updated by creating user")
        }
        if (input.name != null) {
            chat.name = input.name
        }
        if (input.addedUsers != null) {
            chat.users().addAll(findUsersById(input.addedUsers).map {
                val chatUser = ChatUser(0, false)
                chatUser.user().value = it
                chatUser
            })
        }
        if (input.removedUsers != null) {
            val usersToRemove = findUsersById(input.removedUsers).toMutableSet()
            usersToRemove.remove(dfe.user)
            val chatUserLookup = chat.users().associateBy { it.user().value }
            val chatUsersToRemove = usersToRemove.mapNotNull { chatUserLookup[it] }
            chat.users().removeAll(chatUsersToRemove.toSet())
        }
        return nodeRepository.save(chat).awaitSingle()
    }

    suspend fun createMessage(input: MessageInput, dfe: DataFetchingEnvironment): Message {
        val chat = nodeRepository.findById(input.chat) as Chat
        if (chat.users().none { it.user().value == dfe.user }) {
            throw IllegalArgumentException("User not a participant of the specified chat")
        }
        val message = Message(input.content, OffsetDateTime.now(), false, false)
        message.sender().value = dfe.user
        message.files().addAll(createMessageFiles(input.files))
        if (input.replyTo != null) {
            message.replyTo().value = nodeRepository.findById(input.replyTo) as Message
        }
        return nodeRepository.save(message).awaitSingle()
    }

    suspend fun updateMessage(input: UpdateMessageInput, dfe: DataFetchingEnvironment): Message {
        val message = nodeRepository.findById(input.id) as Message
        if (message.sender().value != dfe.user) {
            throw IllegalArgumentException("A message can only be edited by its sender")
        }
        if (input.content != null) {
            message.content = input.content
        }
        if (input.addedFiles != null) {
            message.files().addAll(createMessageFiles(input.addedFiles))
        }
        if (input.removedFiles != null) {
            message.files()
                .removeAll(nodeRepository.findAllById(input.removedFiles.map { it.value }).collectList().awaitSingle())
        }
        return nodeRepository.save(message).awaitSingle()
    }

    suspend fun deleteMessage(message: ID, dfe: DataFetchingEnvironment): Message {
        val messageToDelete = nodeRepository.findById(message) as Message
        if (messageToDelete.sender().value != dfe.user) {
            throw IllegalArgumentException("Message can only be deleted by its sender")
        }
        nodeRepository.delete(messageToDelete).awaitSingleOrNull()
        return messageToDelete
    }

    suspend fun updateMessageReactions(input: UpdateMessageReactionsInput, dfe: DataFetchingEnvironment): Message {
        val message = nodeRepository.findById(input.id) as Message
        val reactionsByEmoji = message.reactions().associateBy { it.emoji }
        val toSave = mutableListOf<MessageReaction>()
        for (emoji in input.addedEmoji?.distinct() ?: emptyList()) {
            val reactionToUpdate = reactionsByEmoji.getOrElse(emoji) {
                val newReaction = MessageReaction(emoji)
                newReaction.message().value = message
                newReaction
            }
            toSave += reactionToUpdate
            reactionToUpdate.users() += dfe.user
        }
        for (emoji in input.removedEmoji ?: emptyList()) {
            reactionsByEmoji[emoji]?.let {
                toSave += it
                it.users() -= dfe.user
            }
        }
        nodeRepository.saveAll(toSave).collectList().awaitSingle()
        return nodeRepository.findById(input.id) as Message
    }

    suspend fun markChatAsRead(chat: ID, until: Int, dfe: DataFetchingEnvironment): Boolean {
        val chat = nodeRepository.findById(chat) as Chat
        val chatUser = chat.users().first {
            it.user().value == dfe.user
        }
        chatUser.readUntil = until
        nodeRepository.save(chatUser).awaitSingle()
        return true
    }

    suspend fun updateUser(input: UpdateUserInput, dfe: DataFetchingEnvironment): User {
        if (input.displayName != null) {
            dfe.user.displayName = input.displayName
        }
        if (input.avatar is OptionalInput.Defined) {
            dfe.user.avatar = input.avatar.value
        }
        return userRepository.save(dfe.user).awaitSingle()
    }


    private suspend fun findUsersById(addedUsers: List<ID>) =
        userRepository.findAllById(addedUsers.map { it.value }).collectList().awaitSingle()!!

    private fun createMessageFiles(input: List<MessageFileInput>): List<MessageFile> {
        return input.map {
            MessageFile(it.name, it.type, it.audio, it.duration, it.file)
        }
    }
}