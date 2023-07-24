package com.babydocs.service;

import exceptions.BadRequestException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Service
public class ValidationService {
    public void isLoggedUserValid(String username, HttpServletRequest request) {
        // test comment
        if (request.getUserPrincipal() == null) {
            throw new BadRequestException("Bad Request with username:");
        }
        String loggedInUser = request.getUserPrincipal().getName();

        if (!loggedInUser.equals(username)) {
            throw new BadRequestException("Not permitted to perform the requested operation");
        }
    }

    public boolean isValidContentType(MultipartFile multipartFile) {
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        return isSupportedExtension(extension);
    }

    private boolean isSupportedExtension(String extension) {
        String[] validFormats = new String[]{"MOV", "MPEG-1", "MPEG-2", "MPEG4", "MP4", "MPG", "AVI", "WMV", "MPEGPS", "FLV",
                "3GPP", "WebM", "png", "jpeg", "jpg"
        };
        return Arrays.stream(validFormats).anyMatch(v -> v.equalsIgnoreCase(extension));

    }
}
