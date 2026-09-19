/**
 * ReceiptStorageService handles receipt file uploads using local filesystem storage.
 *
 * Stores receipt files in a configurable local directory organized by userId and expenseId.
 *
 * @author SpendSmart Development Team
 * @version 2.0
 */
package com.spendsmart.expense.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class ReceiptStorageService {

    @Value("${receipt.storage.path:./receipts}")
    private String storagePath;

    /**
     * Upload a receipt file to local storage and return the relative path.
     *
     * @param userId    the user's ID
     * @param expenseId the expense ID
     * @param file      the receipt file
     * @return the relative storage path
     * @throws IOException if file storage fails
     */
    public String uploadReceipt(Long userId, Long expenseId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Receipt file cannot be empty");
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new IllegalArgumentException("Receipt file size cannot exceed 10MB");
        }

        // Generate unique file name
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + fileExtension;
        Path directory = Paths.get(storagePath, String.valueOf(userId), String.valueOf(expenseId));

        // Create directory if it doesn't exist
        Files.createDirectories(directory);

        // Save file
        Path filePath = directory.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String relativePath = String.format("receipts/%d/%d/%s", userId, expenseId, fileName);
        log.info("Receipt stored at: {}", relativePath);

        return relativePath;
    }

    /**
     * Delete a receipt file from local storage.
     *
     * @param receiptPath the relative path of the receipt
     */
    public void deleteReceipt(String receiptPath) {
        if (receiptPath == null || receiptPath.isEmpty()) return;

        try {
            Path filePath = Paths.get(storagePath).getParent().resolve(receiptPath);
            Files.deleteIfExists(filePath);
            log.info("Receipt deleted: {}", receiptPath);
        } catch (IOException e) {
            log.warn("Failed to delete receipt: {}", receiptPath, e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }
}
