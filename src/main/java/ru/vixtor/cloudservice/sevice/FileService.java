package ru.vixtor.cloudservice.sevice;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.vixtor.cloudservice.dto.FileResponse;
import ru.vixtor.cloudservice.entity.File;
import ru.vixtor.cloudservice.entity.User;
import ru.vixtor.cloudservice.exceptions.InputDataException;
import ru.vixtor.cloudservice.exceptions.UnauthorizedException;
import ru.vixtor.cloudservice.repository.AuthorizationRepository;
import ru.vixtor.cloudservice.repository.FileRepository;
import ru.vixtor.cloudservice.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class FileService {

    FileRepository fileRepository;
    AuthorizationRepository authorizationRepository;
    UserRepository userRepository;

    public void uploadFile(String authToken, String filename, MultipartFile file) throws IOException {
        final User user = getUser(authToken);
        if (user == null) {
            throw new UnauthorizedException("Unauthorized error");
        }
        fileRepository.save(new File(filename, file.getSize(), file.getContentType(), file.getBytes(), user));
        log.info("User {} upload file {}", user.getLogin(), filename);
    }

    public void deleteFile(String authToken, String filename) {
        final User user = getUser(authToken);
        if (user == null) {
            log.error("Delete file error");
            throw new UnauthorizedException("Unauthorized error");
        }
        log.info("User {} delete file {}", user.getLogin(), filename);
        fileRepository.removeByUserAndFilename(user, filename);
    }

    public File downloadFile(String authToken, String filename) {
        final User user = getUser(authToken);
        if (user == null) {
            throw new UnauthorizedException("Unauthorized error");
        }
        final File file = fileRepository.findByUserAndFilename(user, filename);
        if (file == null) {
            log.error("Download file error");
            throw new InputDataException("Error input data");
        }
        log.info("User {} download file {}", user.getLogin(), filename);
        return file;
    }

    public void editFileName(String authToken, String filename, String newFileName) {
        final User user = getUser(authToken);
        if (user == null) {
            log.error("Edit file error");
            throw new UnauthorizedException("Unauthorized error");
        }

        if (newFileName == null) throw new InputDataException("Error input data");

        fileRepository.editFileNameByUser(user, filename, newFileName);
        log.info("User {} edit file {}", user.getLogin(), filename);
    }

    public List<File> getAllFiles(String authToken, Integer limit) {
        final User user = getUser(authToken);
        if (user == null) {
            log.error("Get all files error");
            throw new UnauthorizedException("Unauthorized error");
        }
        log.info("User {} get all files", user.getLogin());
        return fileRepository.findAllByUser(user, Sort.by("filename"));
    }

    private User getUser(String authToken) {
        if (authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }
        final String username = authorizationRepository.getUserNameByToken(authToken);
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized error"));
    }
}
