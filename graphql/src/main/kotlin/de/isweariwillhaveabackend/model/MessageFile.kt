package de.isweariwillhaveabackend.model

import io.github.graphglue.model.DomainNode
import io.github.graphglue.model.Node

@DomainNode
class MessageFile(
    val name: String,
    val type: String,
    val audio: Boolean,
    val duration: Double?,
    val file: String
) : Node() {
    suspend fun size(): Int {
        return 0
    }
}