package com.mihai.whatsappclone.file;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j // Enables logging using SLF4J.
public class FileUtils {

    /**
     * Reads a file from the specified location and returns its content as a byte array.
     * If the file path is blank or the file doesn't exist, it returns an empty byte array.
     *
     * @param fileUrl The URL or path of the file to be read.
     * @return A byte array containing the content of the file, or an empty array if the file is not found.
     */
    public static byte[] readFileFromLocation(String fileUrl) {
        // Checks if the file URL is blank (null or empty), and returns an empty byte array if true.
        if(StringUtils.isBlank(fileUrl)){
            return new byte[0]; // Return an empty byte array if the path is blank.
        }

        try{
            // Convert the file URL to a Path object.
            Path file = new File(fileUrl).toPath();
            // Read all bytes from the file and return the byte array.
            return Files.readAllBytes(file);

        }catch (IOException e){
            // Logs a warning if an exception occurs while trying to read the file.
            log.warn("No file found in the path {}", fileUrl);
        }
        // Return an empty byte array if there was an error or the file was not found.
        return new byte[0];
    }

    // Private constructor to prevent instantiation of this utility class.
    private FileUtils(){}
}
