package de.imageapi.rest

import de.imageapi.model.entities.FileData
import de.imageapi.model.services.FileDataService
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.transaction.Transactional


/**
 * RestController for the Image (Up- and Download feature)
 * @property fileDataService service to get and persist image data
 */
@RestController
class FileController(
    @Autowired private val fileDataService: FileDataService
) {

    @PutMapping("/files")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    @Throws(IOException::class)
    suspend fun uploadFile(@RequestPart("image") imageFile: FilePart, @RequestPart("type") type: String): String {
        val fileData = FileData()
        fileData.type = type
        val bytes = imageFile.toBytes()
        if (bytes.size > 16777216) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "File too large")
        }
        fileData.data = bytes
        val entry = fileDataService.save(fileData)

        return entry.id.toString()
    }

    /**
     * Gets a saved image
     * @param id the expected id
     */
    @GetMapping("/files/{id}")
    @Throws(IOException::class)
    fun getFile(@PathVariable("id") id: String): ResponseEntity<ByteArray> {
        val fileData = fileDataService.findById(id.toLong()).get()
        val parts = fileData.type!!.split("/")
        return ResponseEntity.ok()
            .contentType(MediaType(parts[0], parts[1]))
            .body(fileData.data)
    }

    /**
     * Converts the image to an byte array
     * @return the file as ByteArray
     */
    suspend fun FilePart.toBytes(): ByteArray {
        val bytesList: List<ByteArray> = this.content()
            .flatMap { Flux.just(it.asByteBuffer().array()) }
            .collectList()
            .awaitFirst()

        val byteStream = ByteArrayOutputStream()
        bytesList.forEach { bytes -> byteStream.write(bytes) }
        return byteStream.toByteArray()
    }
}