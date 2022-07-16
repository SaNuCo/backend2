package de.imageapi.repositories

import de.imageapi.model.entities.FileData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

/**
 * Repository for all ImageDatas
 */
@Repository
@Transactional
interface FileDataRepository : JpaRepository<FileData, Long>