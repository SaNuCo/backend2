package de.isweariwillhaveabackend.model

import io.github.graphglue.model.*
import org.springframework.data.annotation.Transient

@DomainNode("foodOfferingReactions")
class FoodOfferingReaction(
    var state: FoodOfferingReactionState
) : Node() {

    @NodeRelationship(FoodOffering.REACTION, Direction.INCOMING)
    @delegate:Transient
    val offering by NodeProperty<FoodOffering>()

    @NodeRelationship(User.FOOD_OFFERING_REACTION, Direction.INCOMING)
    @FilterProperty
    @delegate:Transient
    val user by NodeProperty<User>()

}