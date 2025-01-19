package com.mihai.whatsappclone.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;

@Service // Marks the class as a Spring service.
@Slf4j // Enables logging with SLF4J.
@RequiredArgsConstructor // Generates a constructor with required arguments (in this case, for final fields).
public class FileService {

    // Injects the file upload path from application properties.
    @Value("${application.file.uploads.media-output-path}")
    private String fileUploadPath;

    /**
     * Saves a file uploaded by the user.
     *
     * @param sourceFile The file to be uploaded.
     * @param userId The user ID associated with the file.
     * @return The file path where the file is saved, or null if the file couldn't be saved.
     */
    public String saveFile(
            @NonNull MultipartFile sourceFile, // Ensures the file is not null.
            @NonNull String userId) { // Ensures the user ID is not null.

        // Creates a subdirectory path based on the user ID.
        final String fileUploadSubPath = "users" + separator + userId;

        // Calls the helper method to upload the file.
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    /**
     * Uploads the file to the specified directory.
     *
     * @param sourceFile The file to be uploaded.
     * @param fileUploadSubPath The subpath where the file will be saved.
     * @return The full path of the uploaded file, or null if it couldn't be saved.
     */
    private String uploadFile(
            @NonNull MultipartFile sourceFile, // Ensures the file is not null.
            @NonNull String fileUploadSubPath) { // Ensures the subpath is not null.

        // Combines the base upload path with the user's subpath.
        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);

        // Creates the target folder if it doesn't exist.
        if(!targetFolder.exists()){
            boolean folderCreated = targetFolder.mkdirs();

            if(!folderCreated){
                // Logs a warning if folder creation fails.
                log.warn("Failed to create the target folder, {}", targetFolder);
                return null;
            }
        }

        // Extracts the file extension.
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        // Generates a unique file name by appending the current timestamp.
        String targetFilePath = finalUploadPath + separator + currentTimeMillis() + fileExtension;
        Path targetPath = Paths.get(targetFilePath);

        try{
            // Writes the file bytes to the target path.
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved at: {}", targetFilePath);
            return targetFilePath;
        }catch (IOException e) {
            // Logs an error if the file couldn't be saved.
            log.error("File was not saved", e);
        }

        // Returns null if the file couldn't be saved.
        return null;
    }

    /**
     * Extracts the file extension from the file name.
     *
     * @param fileName The name of the file.
     * @return The file extension (e.g., ".jpg"), or an empty string if no extension is found.
     */
    private String getFileExtension(String fileName) {

        // Checks if the file name is empty or null.
        if(fileName == null || fileName.isEmpty()){
            return "";
        }

        // Finds the position of the last dot in the file name.
        int lastDotIndex = fileName.lastIndexOf('.');
        if(lastDotIndex == -1){
            return "";
        }

        // Extracts and returns the file extension in lowercase.
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}
