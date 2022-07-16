package de.imageapi.model.entities

import javax.persistence.*

/**
 * Class to store the data of an image
 * @property type the content type of the image
 * @property id the id of the ImageData
 * @property data the bytes of the image
 */
@Entity
open class FileData(
    @GeneratedValue
    @Id
    open val id: Long? = null,
    open var type: String? = null,
    @Column(length = 16777216)
    open var data: ByteArray? = null

)