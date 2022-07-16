package de.imageapi.model.services

import de.imageapi.repositories.FileDataRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

/**
 * Service used to access ImageDatas
 * Also provides access to the ImageDataRepository
 * @param repository the imageData repository which is used for persistence
 */
@Service
@Transactional
class FileDataService(
    repository: FileDataRepository
) : FileDataRepository by repository